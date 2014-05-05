
package com.milink.uniplay;

import android.os.Parcel;
import android.os.Parcelable;

import com.milink.api.v1.type.DeviceType;

public class Device implements Parcelable {
    public String id;
    public String name;
    public DeviceType type;

    public Device(String deviceId, String name, DeviceType type) {
        this.id = deviceId;
        this.name = name;
        this.type = type;
    }

    public Device(Parcel source) {
        id = source.readString();
        name = source.readString();
        type = DeviceType.valueOf(source.readString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Device)) {
            return false;
        }
        Device d = (Device) o;
        return id.equals(d.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(type.name());
    }

    public static final Parcelable.Creator<Device> CREATOR = new Creator<Device>() {

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }

        @Override
        public Device createFromParcel(Parcel source) {
            return new Device(source);
        }
    };
}
