
package com.milink.uniplay;

import android.content.Context;
import android.util.Log;

import com.milink.api.v1.MilinkClientManager;
import com.milink.api.v1.MilinkClientManagerDataSource;
import com.milink.api.v1.MilinkClientManagerDelegate;
import com.milink.api.v1.type.DeviceType;
import com.milink.api.v1.type.ErrorCode;
import com.milink.uniplay.audio.IAudioCallback;
import com.milink.uniplay.image.IImageCallback;

import java.util.ArrayList;

public class MilinkClient implements MilinkClientManagerDelegate, MilinkClientManagerDataSource {
    private final String TAG = this.getClass().getSimpleName();

    private ICallback mICallback = null;

    private static MilinkClient mMilinkClient = null; // singleton
    private static MilinkClientManager mMgr = null;
    private static ArrayList<Device> mDeviceList = null;

    private MilinkClient() {
    }

    public static MilinkClient newMilinkClient(Context context) {
        if (mMilinkClient == null) {
            mMilinkClient = new MilinkClient();
            mMgr = new MilinkClientManager(context);
            mDeviceList = new ArrayList<Device>();
        }
        return mMilinkClient;
    }

    public static MilinkClient getMilinkClientInstance() {
        return mMilinkClient;
    }

    public static ArrayList<Device> getDeviceList() {
        return mDeviceList;
    }

    public static MilinkClientManager getManagerInstance() {
        return mMgr;
    }

    public void setCallback(ICallback mICallback) {
        this.mICallback = mICallback;
    }

    @Override
    public String getPrevPhoto(String uri, boolean isRecyle) {
        Log.d(TAG, "getPrevPhoto");
        String s = null;
        if (mICallback instanceof IImageCallback) {
            s = ((IImageCallback) mICallback).getPrevPhoto(uri, isRecyle);
        }
        return s;
    }

    @Override
    public String getNextPhoto(String uri, boolean isRecyle) {
        Log.d(TAG, "getNextPhoto");
        String s = null;
        if (mICallback instanceof IImageCallback) {
            s = ((IImageCallback) mICallback).getNextPhoto(uri, isRecyle);
        }
        return s;
    }

    @Override
    public void onOpen() {
        Log.d(TAG, "MilinkClientManager onOpen");
    }

    @Override
    public void onClose() {
        Log.d(TAG, "MilinkClientManager onClose");
    }

    @Override
    public void onDeviceFound(String deviceId, String name, DeviceType type) {
        Log.d(TAG, String.format("onDeviceFound: %s -> %s -> %s", deviceId, name, type));
        synchronized (mDeviceList) {
            Device device = new Device(deviceId, name, type);
            if (!mDeviceList.contains(device)) {
                mDeviceList.add(device);
            }
        }
    }

    @Override
    public void onDeviceLost(String deviceId) {
        Log.d(TAG, String.format("onDeviceLost: %s", deviceId));
        synchronized (mDeviceList) {
            for (Device device : mDeviceList) {
                if (device.id.equals(deviceId)) {
                    mDeviceList.remove(device);
                    break;
                }
            }
        }
    }

    @Override
    public void onConnected() {
        Log.d(TAG, "onConnected");
        mICallback.onConnected();
    }

    @Override
    public void onConnectedFailed(ErrorCode errorCode) {
        Log.d(TAG, "onConnectedFailed");
        mICallback.onConnectedFailed(errorCode);
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "onDisconnected");
        mICallback.onDisconnected();
    }

    @Override
    public void onLoading() {
        Log.d(TAG, "onLoading");
        mICallback.onLoading();
    }

    @Override
    public void onPlaying() {
        Log.d(TAG, "onPlaying");
        mICallback.onPlaying();
    }

    @Override
    public void onStopped() {
        Log.d(TAG, "onStopped");
        mICallback.onStopped();
    }

    @Override
    public void onPaused() {
        Log.d(TAG, "onPaused");
        mICallback.onPaused();
    }

    @Override
    public void onVolume(int volume) {
        Log.d(TAG, "onVolume");
        mICallback.onVolume(volume);
    }

    @Override
    public void onNextAudio(boolean isAuto) {
        Log.d(TAG, "onNextAudio");

        if (mICallback instanceof IAudioCallback) {
            ((IAudioCallback) mICallback).onNextAudio(isAuto);
        }
    }

    @Override
    public void onPrevAudio(boolean isAuto) {
        Log.d(TAG, "onPrevAudio");

        if (mICallback instanceof IAudioCallback) {
            ((IAudioCallback) mICallback).onPrevAudio(isAuto);
        }
    }

}
