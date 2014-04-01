
package com.milink.api.v1;

import com.milink.api.v1.type.MediaType;
import com.milink.api.v1.type.ReturnCode;
import com.milink.api.v1.type.SlideMode;

public interface IMilinkClientManager {

    void setDeviceName(String selfName);

    void setDataSource(MilinkClientManagerDataSource dataSource);

    void setDelegate(MilinkClientManagerDelegate delegate);

    void open();

    void close();

    ReturnCode connect(String deviceId, int timeout);

    ReturnCode disconnect();

    // photo
    ReturnCode startShow();

    ReturnCode show(String photoUri);

    ReturnCode stopShow();

    // slide
    ReturnCode startSlideshow(int duration, SlideMode Type);

    ReturnCode stopSlideshow();

    // audio & video
    ReturnCode startPlay(String url, String title, int iPosition, double dPosition, MediaType type);

    ReturnCode stopPlay();

    ReturnCode setPlaybackRate(int rate);

    int getPlaybackRate();

    ReturnCode setPlaybackProgress(int position);

    int getPlaybackProgress();

    int getPlaybackDuration();

    ReturnCode setVolume(int volume);

    int getVolume();
}
