package com.mooc.discover.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EventTaskBean implements Parcelable {
    private long start_time;
    private long end_time;
    private long apply_start_time;
    private long apply_end_time;
    private boolean is_title;
    private String font_color;
    private String background_color;
    private String url;
    private String title;
    private String share_desc;
    private String share_button_picture;
    private int share_status;
    private String share_picture;
    private String share_link;
    private String share_title;

    public EventTaskBean() {
    }


    protected EventTaskBean(Parcel in) {
        start_time = in.readLong();
        end_time = in.readLong();
        apply_start_time = in.readLong();
        apply_end_time = in.readLong();
        is_title = in.readByte() != 0;
        font_color = in.readString();
        background_color = in.readString();
        url = in.readString();
        title = in.readString();
        share_desc = in.readString();
        share_button_picture = in.readString();
        share_status = in.readInt();
        share_picture = in.readString();
        share_link = in.readString();
        share_title = in.readString();
    }

    public static final Creator<EventTaskBean> CREATOR = new Creator<EventTaskBean>() {
        @Override
        public EventTaskBean createFromParcel(Parcel in) {
            return new EventTaskBean(in);
        }

        @Override
        public EventTaskBean[] newArray(int size) {
            return new EventTaskBean[size];
        }
    };

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public long getApply_start_time() {
        return apply_start_time;
    }

    public void setApply_start_time(long apply_start_time) {
        this.apply_start_time = apply_start_time;
    }

    public String getApplyDuration() {

        if (apply_start_time == 0 || apply_end_time == 0) {
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");

        return simpleDateFormat.format(new Date(apply_start_time * 1000)) + "-" + simpleDateFormat.format(new Date(apply_end_time * 1000));
    }

    public long getApply_end_time() {
        return apply_end_time;
    }

    public void setApply_end_time(long apply_end_time) {
        this.apply_end_time = apply_end_time;
    }

    public boolean isIs_title() {
        return is_title;
    }

    public void setIs_title(boolean is_title) {
        this.is_title = is_title;
    }

    public String getFont_color() {
        return font_color;
    }

    public void setFont_color(String font_color) {
        this.font_color = font_color;
    }

    public String getBackground_color() {
        return background_color;
    }

    public void setBackground_color(String background_color) {
        this.background_color = background_color;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShare_desc() {
        return share_desc;
    }

    public void setShare_desc(String share_desc) {
        this.share_desc = share_desc;
    }

    public String getShare_button_picture() {
        return share_button_picture;
    }

    public void setShare_button_picture(String share_button_picture) {
        this.share_button_picture = share_button_picture;
    }

    public int getShare_status() {
        return share_status;
    }

    public void setShare_status(int share_status) {
        this.share_status = share_status;
    }

    public String getShare_picture() {
        return share_picture;
    }

    public void setShare_picture(String share_picture) {
        this.share_picture = share_picture;
    }

    public String getShare_link() {
        return share_link;
    }

    public void setShare_link(String share_link) {
        this.share_link = share_link;
    }

    public String getShare_title() {
        return share_title;
    }

    public void setShare_title(String share_title) {
        this.share_title = share_title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(start_time);
        dest.writeLong(end_time);
        dest.writeLong(apply_start_time);
        dest.writeLong(apply_end_time);
        dest.writeByte((byte) (is_title ? 1 : 0));
        dest.writeString(font_color);
        dest.writeString(background_color);
        dest.writeString(url);
        dest.writeString(title);
        dest.writeString(share_desc);
        dest.writeString(share_button_picture);
        dest.writeInt(share_status);
        dest.writeString(share_picture);
        dest.writeString(share_link);
        dest.writeString(share_title);
    }
}
