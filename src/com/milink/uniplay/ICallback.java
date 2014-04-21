
package com.milink.uniplay;

import com.milink.api.v1.type.ErrorCode;

public interface ICallback {
    public void onConnected();

    public void onConnectedFailed(ErrorCode errorCode);

    public void onDisconnected();

    public void onLoading();

    public void onPlaying();

    public void onStopped();

    public void onPaused();

    public void onVolume(int volume);
}
