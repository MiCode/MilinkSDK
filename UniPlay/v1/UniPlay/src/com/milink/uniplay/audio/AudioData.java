
package com.milink.uniplay.audio;

public class AudioData {
    private int mId;
    private String mTitle;
    private String mName;
    private String mSinger;
    private String mAlbum;
    private String mUri;
    private long mSize;
    private long mTime;
    private long mDateModified;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getSinger() {
        return mSinger;
    }

    public void setSinger(String singer) {
        mSinger = singer;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        mAlbum = album;
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String uri) {
        mUri = uri;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        mSize = size;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public long getDateModified() {
        return mDateModified;
    }

    public void setDateModified(long mDateModified) {
        this.mDateModified = mDateModified;
    }
}
