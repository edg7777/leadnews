package com.heima.utils.thread;

import com.heima.model.user.pojo.ApUser;
import com.heima.model.wemedia.pojos.WmUser;

/**
 * @author fzj
 * @date 2023-08-20 11:49
 */
public class AppThreadLocalUtil {
    private final static ThreadLocal<ApUser> WM_USER_THREAD_LOCAL=new ThreadLocal<>();

    //存入线程
    public static void setUser(ApUser apUser){
        WM_USER_THREAD_LOCAL.set(apUser);
    }
    //从线程中获取
    public static ApUser getUser(){
        return WM_USER_THREAD_LOCAL.get();
    }
    //清理
    public static void clear(){
        WM_USER_THREAD_LOCAL.remove();
    }

}
