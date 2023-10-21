package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.wemedia.IWemediaClient;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.HotArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.pojo.ApArticle;
import com.heima.model.article.vos.HotArticleVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fzj
 * @date 2023-08-31 21:41
 */
@Service
@Slf4j
@Transactional
public class HotArticleServiceImpl implements HotArticleService {
    @Autowired
    private ApArticleMapper apArticleMapper;
    @Autowired
    private IWemediaClient wemediaClient;

    @Autowired
    private CacheService cacheService;
    /**
     * 计算热点文章
     */
    @Override
    public void computerHotArticle() {
        //查询前5天的文章数据
        Date dayParam = DateTime.now().minusDays(10).toDate();
        List<ApArticle> apArticleList = apArticleMapper.findArticleListByLast5Days(dayParam);
        //计算文章的分值
        List<HotArticleVo> hotArticleVoList=calculateHotArticle(apArticleList);
        //为每个频道缓存30条分值较高的文章
        cacheTagToRedis(hotArticleVoList);
    }

    /**
     * 为每个频道缓存30条分值较高的文章
     * @param hotArticleVoList
     */
    private void cacheTagToRedis(List<HotArticleVo> hotArticleVoList) {
        //为每个频道缓存30条分值较高的文章
        ResponseResult responseResult = wemediaClient.getChannels();
        if(responseResult.getCode().equals(200)){
            String channelJson = JSON.toJSONString(responseResult.getData());
            List<WmChannel> wmChannels = JSON.parseArray(channelJson, WmChannel.class);
            //检索出每个频道的文章
            if(wmChannels!=null && wmChannels.size()>0){
                for (WmChannel wmChannel : wmChannels) {
                    List<HotArticleVo> hotArticleVos = hotArticleVoList.stream().filter(x -> x.getChannelId().equals(wmChannel.getId())).collect(Collectors.toList());
                    //给文章进行排序，取30条分值较高的文章存入redis，key：频道id value：文章
                    sortAndCache(hotArticleVos, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + wmChannel.getId());
                }
            }
        }
        //设置推荐数据
        //给文章进行排序，取30条分值较高的文章存入redis，key：频道id value：文章
        sortAndCache(hotArticleVoList, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + ArticleConstants.DEFAULT_TAG);
    }

    /**
     * 排序并且缓存数据
     * @param hotArticleVos
     * @param key
     */
    private void sortAndCache(List<HotArticleVo> hotArticleVos, String key) {
        hotArticleVos = hotArticleVos.stream().sorted(Comparator.comparing(HotArticleVo::getScore).reversed()).collect(Collectors.toList());
        if (hotArticleVos.size() > 30) {
            hotArticleVos = hotArticleVos.subList(0, 30);
        }
        cacheService.set(key, JSON.toJSONString(hotArticleVos));
    }

    private List<HotArticleVo> calculateHotArticle(List<ApArticle> apArticleList) {
        List<HotArticleVo> hotArticleVoList=new ArrayList<>();
        if (apArticleList != null && apArticleList.size()>0) {
            for (ApArticle apArticle : apArticleList) {
                HotArticleVo hot=new HotArticleVo();
                BeanUtils.copyProperties(apArticle,hot);
                Integer score= calculateScore(apArticle);
                hot.setScore(score);
                hotArticleVoList.add(hot);
            }
        }
        return hotArticleVoList;
    }

    /**
     * 计算文章的分值
     * @param apArticle
     * @return
     */
    private Integer calculateScore(ApArticle apArticle) {
        Integer score=0;
        if(apArticle.getLikes()!=null){
            score+=apArticle.getLikes()* ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
        }
        if(apArticle.getViews()!=null){
            score+=apArticle.getViews();
        }
        if(apArticle.getComment()!=null){
            score+=apArticle.getComment()* ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT;
        }
        if(apArticle.getCollection()!=null){
            score+=apArticle.getCollection()* ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
        }
        return score;
    }
}
