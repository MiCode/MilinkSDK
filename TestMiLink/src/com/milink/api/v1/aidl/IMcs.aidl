
package com.milink.api.v1.aidl;

import com.milink.api.v1.aidl.IMcsDataSource;
import com.milink.api.v1.aidl.IMcsDelegate;
import com.milink.api.v1.aidl.IMcsDeviceListener;

interface IMcs {

    void setDeviceListener(IMcsDeviceListener listener);

    void unsetDeviceListener(IMcsDeviceListener listener);

    void setDataSource(IMcsDataSource dataSource);

    void unsetDataSource(IMcsDataSource dataSource);

    void setDelegate(IMcsDelegate delegate);

    void unsetDelegate(IMcsDelegate delegate);

    void setDeviceName(String deviceName);

    // connection
    int connect(String deviceId, int timeout);

    int disconnect();

    // photo
    int startShow();

    int show(String photoUri);

    int stopShow();

    // slide
    int startSlideshow(int duration, boolean isRecyle);

    int stopSlideshow();

    // audio & video
    int startPlayVideo(String url, String title, int iPosition, double dPosition);

    int startPlayAudio(String url, String title, int iPosition, double dPosition);

    int stopPlay();

    int setPlaybackRate(int rate);

    int getPlaybackRate();

    int setPlaybackProgress(int position);

    int getPlaybackProgress();

    int getPlaybackDuration();

    int setVolume(int volume);

    int getVolume();
}
