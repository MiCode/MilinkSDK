package com.xiaomi.milinksdk.audio;

import com.xiaomi.milinksdk.ICallback;

public interface IAudioCallback extends ICallback {
    public void onNextAudio(boolean isAuto);
    public void onPrevAudio(boolean isAuto);
}
