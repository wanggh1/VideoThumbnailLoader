package com.xuie.videothumbnailloaderdemo;

import android.os.Parcel;
import android.os.Parcelable;

public class Media implements Parcelable {

    public Media() {
    }

    private long id;
    private long duration;
    private long size;
    private String path;
    private String artist;
    private String title;
    private String displayName;
    private long abulmId;

    protected Media(Parcel in) {
        id = in.readLong();
        duration = in.readLong();
        size = in.readLong();
        path = in.readString();
        artist = in.readString();
        title = in.readString();
        displayName = in.readString();
        abulmId = in.readLong();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getAbulmId() {
        return abulmId;
    }

    public void setAbulmId(long abulmId) {
        this.abulmId = abulmId;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(duration);
        dest.writeLong(size);
        dest.writeString(path);
        dest.writeString(artist);
        dest.writeString(title);
        dest.writeString(displayName);
        dest.writeLong(abulmId);
    }

    @Override public String toString() {
        return "Media{" +
                "id=" + id +
                ", duration=" + duration +
                ", size=" + size +
                ", path='" + path + '\'' +
                ", artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", displayName='" + displayName + '\'' +
                ", abulmId=" + abulmId +
                '}';
    }
}
