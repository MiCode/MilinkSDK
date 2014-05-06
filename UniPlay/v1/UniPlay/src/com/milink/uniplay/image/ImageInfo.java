
package com.milink.uniplay.image;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageInfo implements Parcelable {
    public int id;
    public String title;
    public String data;
    public long size;

    public ImageInfo() {
    }

    public ImageInfo(Parcel source) {
        id = source.readInt();
        title = source.readString();
        data = source.readString();
        size = source.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(data);
        dest.writeLong(size);
    }

    public final static Parcelable.Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {

        @Override
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }

        @Override
        public ImageInfo createFromParcel(Parcel source) {
            return new ImageInfo(source);
        }
    };

}
