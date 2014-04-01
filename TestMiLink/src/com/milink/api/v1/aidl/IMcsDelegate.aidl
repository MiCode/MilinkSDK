
package com.milink.api.v1.aidl;

interface IMcsDelegate {

    void onConnected();

    void onConnectedFailed();

    void onDisconnected();

    void onLoading();

    void onPlaying();

    void onStopped();

    void onPaused();

    void onVolume(int volume);

    void onNextAudio(boolean isAuto);

    void onPrevAudio(boolean isAuto);
}