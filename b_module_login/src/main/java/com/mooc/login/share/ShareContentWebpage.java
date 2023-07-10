package com.mooc.login.share;


import com.intelligent.reader.module.share.api.bean.BaseShareContent;
import com.intelligent.reader.module.share.api.constans.ShareConstant;

import org.jetbrains.annotations.NotNull;

/**
 * 链接分享
 * Created by Administrator on 2019/1/11.
 */

public class ShareContentWebpage extends BaseShareContent {
    private String title;
    private String content;
    private String url;
    private String pictureResource;
    private int defaultThumbnail;

    public ShareContentWebpage(String title, String content, String url, String pictureResource, int defaultThumbnail) {
        this.title = title;
        this.content = content;
        this.url = url;
        this.pictureResource = pictureResource;
        this.defaultThumbnail = defaultThumbnail;
    }

    @Override
    public int getShareWay() {
        return ShareConstant.INSTANCE.getSHARE_WAY_WEBPAGE();
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getTitle() {
        return title;
    }



    @Override
    public String getPictureResource() {
        return pictureResource;
    }

    @Override
    public void setPictureResource(@NotNull String s) {
        this.pictureResource = s;
    }


    @Override
    public String miniProgramPagePath() {
        return null;
    }

    @NotNull
    @Override
    public String getUrl() {
        return url;
    }


}
