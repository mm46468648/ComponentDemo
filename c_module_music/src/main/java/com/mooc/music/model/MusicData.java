package com.mooc.music.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Observable;

public class MusicData extends Observable implements Parcelable {


    private volatile long progress;

    private volatile long duration;

    private volatile int buffer;

    public MusicData() {
    }

    protected MusicData(Parcel in) {
        progress = in.readLong();
        duration = in.readLong();
        buffer = in.readInt();
    }

    public static final Creator<MusicData> CREATOR = new Creator<MusicData>() {
        @Override
        public MusicData createFromParcel(Parcel in) {
            return new MusicData(in);
        }

        @Override
        public MusicData[] newArray(int size) {
            return new MusicData[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(progress);
        dest.writeLong(duration);
        dest.writeInt(buffer);
    }

    @Override
    public int describeContents() {
        return 0;
    }



    public int getBuffer() {
        return buffer;
    }

    public void setBuffer(int buffer) {
        this.buffer = buffer;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
