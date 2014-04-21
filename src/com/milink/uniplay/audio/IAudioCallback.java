package com.milink.uniplay.audio;

import com.milink.uniplay.ICallback;

public interface IAudioCallback extends ICallback {
    public void onNextAudio(boolean isAuto);
    public void onPrevAudio(boolean isAuto);
}
