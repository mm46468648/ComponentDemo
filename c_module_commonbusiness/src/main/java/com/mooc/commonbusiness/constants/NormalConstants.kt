package com.mooc.commonbusiness.constants

/**
 * 普通的业务常量
 */
class NormalConstants {

    companion object {
        const val VideoQuality20 = 20    //高清视频质量20
        const val SHARE_SOURCE_TYPE_APP_SHARE = 2 //app分享
        const val SHARE_SOURCE_TYPE_MEDAL = 3 //荣耀，勋章分享链接
        const val SHARE_SOURCE_TYPE_APP_DATABOARD = 4 //数据面板
        const val SHARE_SOURCE_TYPE_E_BOOK = 6 //电子书



        const val SHARE_NUM_TIP = "您需要分享5本最喜欢的书给您的朋友"  //分享书籍数量不足提示
        const val SHARE_NUM_TIP2 = "已选中5个电子书资源"  //搜索中超过5本提示


        //大图预览，去除微信超链接
        const val JS_FUNCTION = "javascript:(function(){" +
                "var obs = document.getElementsByTagName('img'); " +
                "var urls=[];" +
                "for(var i=0;i<obs.length;i++)  " +
                "{" + "urls[i]=obs[i].getAttribute('data-src')||obs[i].src;" +
                "function getA(el){ if(el.parentElement==null){return false;}else if(el.parentElement.tagName.toLowerCase() =='a') {return ture;}else{getA(el.parentElement);} }" +
                "if(!getA(obs[i])){" +
                "(function(i){" +
                "    obs[i].onclick=function()  " +
                "    {  " +
                "window.mobile.openImage(i,urls,window.location.origin);  " +
                "    } " +
                "})(i)}" +
                "}" +
                "})()"

        //微信文章通过js注入的方法去掉文章下方的拓展模块
        const val JS_REMOVE_ADVERT_FUNCTION = "javascript:(function(){" +
                "var el=document.querySelector('.rich_media_area_extra');" +
                "if(el){" +
                "el.style.display = 'none';}" +
                "})()"

    }
}