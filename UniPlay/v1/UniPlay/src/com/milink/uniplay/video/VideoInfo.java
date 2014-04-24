
package com.milink.uniplay.video;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoInfo implements Parcelable {
    public String id;
    public String title;
    public String album;
    public String artist;
    public String description;
    public String data;
    public String MIME_TYPE;
    public String duration;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel arg0, int arg1) {
    }

    public static final Parcelable.Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {

        @Override
        public VideoInfo[] newArray(int size) {
            return null;
        }

        @Override
        public VideoInfo createFromParcel(Parcel source) {
            return null;
        }
    };

}
