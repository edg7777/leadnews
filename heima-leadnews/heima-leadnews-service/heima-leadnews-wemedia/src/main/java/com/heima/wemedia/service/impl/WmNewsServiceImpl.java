package com.heima.wemedia.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.WemediaConstants;
import com.heima.common.constants.WmNewsMessageConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.config.DelayedQueueConfig;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.heima.wemedia.service.WmNewsService;
import com.heima.wemedia.service.WmNewsTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fzj
 * @date 2023-08-21 11:18
 */
@Service
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {
    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Autowired
    private WmMaterialMapper wmMaterialMapper;
    /**
     * 条件查询文章列表
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findList(WmNewsPageReqDto dto) {
        //检查参数
        dto.checkParam();
        //分页条件查询
        IPage page=new Page(dto.getPage(),dto.getSize());
        LambdaQueryWrapper<WmNews> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        //状态查询
        if(dto.getStatus()!=null){
            lambdaQueryWrapper.eq(WmNews::getStatus,dto.getStatus());
        }
        //频道查询
        if(dto.getChannelId()!=null){
            lambdaQueryWrapper.eq(WmNews::getChannelId,dto.getChannelId());
        }
        //时间范围查询
        if(dto.getBeginPubDate()!=null&&dto.getEndPubDate()!=null){
            lambdaQueryWrapper.between(WmNews::getPublishTime,dto.getBeginPubDate(),dto.getEndPubDate());
        }
        //关键字的模糊查询
        if(StringUtils.isNotBlank(dto.getKeyword())){
            lambdaQueryWrapper.like(WmNews::getTitle,dto.getKeyword());
        }
        //查询当前登录人的文章
        lambdaQueryWrapper.eq(WmNews::getUserId, WmThreadLocalUtil.getUser().getId());
        //按照发布时间倒序查询
        lambdaQueryWrapper.orderByDesc(WmNews::getPublishTime);
        page(page,lambdaQueryWrapper);
        //结果返回
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());

        responseResult.setData(page.getRecords());
        return responseResult;
    }
    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;
    @Autowired
    private WmNewsTaskService wmNewsTaskService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 上传文章
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult submitNews(WmNewsDto dto) {
        //条件判断
        if (dto == null || dto.getContent()==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //保存或者修改文章
        WmNews wmNews = new WmNews();
        //属性拷贝
        BeanUtil.copyProperties(dto,wmNews);
        //封面图片
        if(dto.getImages()!=null&& dto.getImages().size()>0) {
            String imagStr = StringUtils.join(dto.getImages(), ",");
            wmNews.setImages(imagStr);
        }
        //如果当前封面类型为自动 -1
        if(dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)){
            wmNews.setType(null);
        }
        saveOrUpdateWmNews(wmNews);
        //判断是否为草稿，如果是草稿则结束当前方法
        if(dto.getStatus().equals(WmNews.Status.NORMAL.getCode())){
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
        //不是草稿就保存文章内容图片与素材的关系
        //获取到文章内容中的图片
        List<String> materials= ectractUrlInfo(dto.getContent());
        saveRelativeInfoForContent(materials,wmNews.getId());
        //不是草稿，保存文章封面图片与素材的关系
        saveRelativeInfoForCover(dto,wmNews,materials);
        long duration = wmNews.getPublishTime().getTime() - System.currentTimeMillis();
        log.info("duration:{}"+duration);
        if(duration<=6){
            wmNewsAutoScanService.autoScanNews(wmNews.getId());
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
        //审核文章
        rabbitTemplate.convertAndSend(DelayedQueueConfig.NORMAL_EXCHANGE_NAME,DelayedQueueConfig.NORMAL_ROUTING_KEY, JSONUtil.toJsonStr(wmNews),
                correlationData->{
            correlationData.getMessageProperties().setExpiration(String.valueOf(duration));
            return correlationData;
                });
//        wmNewsTaskService.addNewsToTask(wmNews.getId(),wmNews.getPublishTime());
//        wmNewsAutoScanService.autoScanNews(wmNews.getId());
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 当前封面类型为自动则设置封面类型的数据
     * 如果内容图片大于等于1，小于3，单图 type1
     * 如果内容图片大于等于3  多图 type 3
     * 如果内容没有图片，无图，type 0
     * @param dto
     * @param wmNews
     * @param materials
     */
    private void saveRelativeInfoForCover(WmNewsDto dto, WmNews wmNews, List<String> materials) {
        List<String> images=dto.getImages();
        //如果封面类型为自动，则设置封面类型的数据
        if(dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)){
            //多图
            if(materials.size()>=3){
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images= materials.stream().limit(3).collect(Collectors.toList());
            }
            //单图
            else if(materials.size()>1&&materials.size()<3){
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images=materials.stream().limit(1).collect(Collectors.toList());
            }
            //无图
            else{
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }
            //修改文章
            if(images!=null&&images.size()>0){
                wmNews.setImages(StringUtils.join(images,","));
            }
            updateById(wmNews);
        }
        if(images!=null&&images.size()>0){
            saveRelativeInfo(images,wmNews.getId(),WemediaConstants.WM_COVER_REFERENCE);
        }
    }

    /**
     * 处理文章内容与图片的素材关系
     * @param materials
     * @param id
     */
    private void saveRelativeInfoForContent(List<String> materials, Integer id) {
        saveRelativeInfo(materials,id,WemediaConstants.WM_CONTENT_REFERENCE);
    }

    /**
     * 保存文章图片与素材的关系到数据库中
     * @param materials
     * @param id
     * @param type
     */
    private void saveRelativeInfo(List<String> materials, Integer id, Short type) {
        if(materials!=null){
        //通过图片的url查询素材的id
        List<WmMaterial> dbMaterials = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, materials));
         //判断素材是否有效
        if(dbMaterials==null||dbMaterials.size()==0){
            //手动抛出异常 能够提示调用者素材失效 ，进行数据的回滚
            throw new CustomException(AppHttpCodeEnum.MATERIAL_REFERENCE_FAIL);
        }

        if(materials.size()!=dbMaterials.size()){
            throw new CustomException(AppHttpCodeEnum.MATERIAL_REFERENCE_FAIL);
        }
        List<Integer> idList = dbMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());

        wmNewsMaterialMapper.saveRelations(idList,id,type);
        }
    }

    /**
     * 提取文章内容中的图片信息
     * @param content
     * @return
     */
    private List<String> ectractUrlInfo(String content) {
        List<String> materials=new ArrayList<>();
        List<Map> maps = JSON.parseArray(content, Map.class);
        for (Map map : maps) {
            if(map.get("type").equals("image")){
                String imgUrl = (String) map.get("value");
                materials.add(imgUrl);
            }
        }
        return materials;
    }

    /**
     * 保存或修改文章
     * @param wmNews
     */
    private void saveOrUpdateWmNews(WmNews wmNews) {
        //补全属性
        wmNews.setUserId(WmThreadLocalUtil.getUser().getId());
        wmNews.setCreatedTime(new Date());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable((short)1);
        if(wmNews.getId()==null){
            //保存
            save(wmNews);
        }
        else{
            //修改
            //删除文章图片与素材的关系
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId,wmNews.getId()));
            updateById(wmNews);
        }
    }
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;
    /**
     * 上下架文章
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult downOrUp(WmNewsDto dto) {
        //检查参数
        if (dto.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //查询文章
        WmNews wmNews = getById(dto.getId());
        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"文章不存在");
        }
        //判断文章是否发布
        if(!wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"当前文章不是发布状态无法下架");
        }
        //修改文章enable
        if(dto.getEnable()!=null && dto.getEnable()>-1 &&dto.getEnable()<2){
            update(Wrappers.<WmNews>lambdaUpdate().set(WmNews::getEnable,dto.getEnable())
                    .eq(WmNews::getId,dto.getId()));
            if (wmNews.getArticleId() != null) {
                Map<String,Object> map=new HashMap<>();
                map.put("articleId",wmNews.getArticleId());
                map.put("enable",dto.getEnable());
                kafkaTemplate.send(WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC,JSON.toJSONString(map));
            }

        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

}
