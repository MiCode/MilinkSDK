
package com.milink.api.v1;

import com.milink.api.v1.type.DeviceType;
import com.milink.api.v1.type.ErrorCode;

public interface MilinkClientManagerDelegate {

    void onOpen();

    void onClose();

    void onDeviceFound(String deviceId, String name, DeviceType type);

    void onDeviceLost(String deviceId);

    void onConnected();

    void onConnectedFailed(ErrorCode errorCode);

    void onDisconnected();

    void onLoading();

    void onPlaying();

    void onStopped();

    void onPaused();

    void onVolume(int volume);

    void onNextAudio(boolean isAuto);

    void onPrevAudio(boolean isAuto);
}
