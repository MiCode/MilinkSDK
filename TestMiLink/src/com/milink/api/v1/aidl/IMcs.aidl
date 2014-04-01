
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
    boolean connect(String deviceId, int timeout);

    boolean disconnect();

    // photo
    boolean startShow();

    boolean show(String photoUri);

    boolean stopShow();

    // slide
    boolean startSlideshow(int duration, boolean isRecyle);

    boolean stopSlideshow();

    // audio & video
    boolean startPlayVideo(String url, String title, int iPosition, double dPosition);

    boolean startPlayAudio(String url, String title, int iPosition, double dPosition);

    boolean stopPlay();

    boolean setPlaybackRate(int rate);

    int getPlaybackRate();

    boolean setPlaybackProgress(int position);

    int getPlaybackProgress();

    int getPlaybackDuration();

    boolean setVolume(int volume);

    int getVolume();
}
