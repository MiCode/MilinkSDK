package com.xiaomi.milinksdk;

import com.milink.api.v1.type.DeviceType;

public class Device {
    public String id;
    public String name;
    public DeviceType type;

    public Device(String deviceId, String name, DeviceType type) {
        this.id = deviceId;
        this.name = name;
        this.type = type;
    }
}
