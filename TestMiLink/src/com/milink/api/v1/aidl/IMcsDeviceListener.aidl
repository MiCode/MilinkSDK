
package com.milink.api.v1.aidl;

interface IMcsDeviceListener {

    void onDeviceFound(String deviceId, String name, String type);

    void onDeviceLost(String deviceId);
}