
package com.milink.api.v1;

import android.os.Handler;
import android.os.RemoteException;

import com.milink.api.v1.type.ErrorCode;
import com.milink.api.v1.aidl.IMcsDelegate;

public class McsDelegate extends IMcsDelegate.Stub {

    private Handler mHandler = new Handler();
    private MilinkClientManagerDelegate mDelegate = null;

    public void setDelegate(MilinkClientManagerDelegate delegate) {
        mDelegate = delegate;
    }

    @Override
    public void onConnected() throws RemoteException {
        if (mDelegate == null)
            return;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDelegate.onConnected();
            }
        });
    }

    @Override
    public void onConnectedFailed() throws RemoteException {
        if (mDelegate == null)
            return;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDelegate.onConnectedFailed(ErrorCode.ConnectTimeout);
            }
        });
    }

    @Override
    public void onDisconnected() throws RemoteException {
        if (mDelegate == null)
            return;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDelegate.onDisconnected();
            }
        });
    }

    @Override
    public void onLoading() throws RemoteException {
        if (mDelegate == null)
            return;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDelegate.onLoading();
            }
        });
    }

    @Override
    public void onPlaying() throws RemoteException {
        if (mDelegate == null)
            return;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDelegate.onPlaying();
            }
        });
    }

    @Override
    public void onStopped() throws RemoteException {
        if (mDelegate == null)
            return;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDelegate.onStopped();
            }
        });
    }

    @Override
    public void onPaused() throws RemoteException {
        if (mDelegate == null)
            return;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDelegate.onPaused();
            }
        });
    }

    @Override
    public void onVolume(final int volume) throws RemoteException {
        if (mDelegate == null)
            return;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDelegate.onVolume(volume);
            }
        });
    }

    @Override
    public void onNextAudio(final boolean isAuto) throws RemoteException {
        if (mDelegate == null)
            return;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDelegate.onNextAudio(isAuto);
            }
        });
    }

    @Override
    public void onPrevAudio(final boolean isAuto) throws RemoteException {
        if (mDelegate == null)
            return;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDelegate.onPrevAudio(isAuto);
            }
        });
    }

}
