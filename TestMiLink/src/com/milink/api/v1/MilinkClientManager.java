
package com.milink.api.v1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.milink.api.v1.type.MediaType;
import com.milink.api.v1.type.MilinkConfig;
import com.milink.api.v1.type.ReturnCode;
import com.milink.api.v1.type.SlideMode;
import com.milink.api.v1.aidl.IMcs;

public class MilinkClientManager implements IMilinkClientManager {

    private static final String TAG = MilinkClientManager.class.getSimpleName();

    private MilinkClientManagerDelegate mDelegate = null;

    private Context mContext = null;
    private IMcs mService = null;
    private boolean mIsbound = false;

    private String mDeviceName = null;
    private McsDataSource mMcsDataSource = null;
    private McsDelegate mMcsDelegate = null;
    private McsDeviceListener mMcsDeviceListener = null;

    public MilinkClientManager(Context context) {
        mContext = context;
        mMcsDelegate = new McsDelegate();
        mMcsDataSource = new McsDataSource();
        mMcsDeviceListener = new McsDeviceListener();
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    @Override
    public void setDeviceName(String selfName) {
        mDeviceName = selfName;
    }

    @Override
    public void setDataSource(MilinkClientManagerDataSource dataSource) {
        mMcsDataSource.setDataSource(dataSource);
    }

    @Override
    public void setDelegate(MilinkClientManagerDelegate delegate) {
        mDelegate = delegate;
        mMcsDelegate.setDelegate(delegate);
        mMcsDeviceListener.setDelegate(delegate);
    }

    @Override
    public void open() {
        bindMilinkClientService();
    }

    @Override
    public void close() {
        unbindMilinkClientService();
    }

    private void bindMilinkClientService() {
        if (!mIsbound) {
            Intent intent = new Intent(IMcs.class.getName());
            intent.setPackage(MilinkConfig.PACKAGE_NAME);
            mIsbound = mContext.bindService(intent,
                    mServiceConnection,
                    Context.BIND_AUTO_CREATE);
        }
    }

    private void unbindMilinkClientService() {
        if (mIsbound) {
            mContext.unbindService(mServiceConnection);
            mIsbound = false;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");

            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mDelegate != null) {
                        mDelegate.onOpen();
                    }
                }
            });

            mService = IMcs.Stub.asInterface(service);
            try {
                mService.setDeviceName(mDeviceName);
                mService.setDelegate(mMcsDelegate);
                mService.setDataSource(mMcsDataSource);
                mService.setDeviceListener(mMcsDeviceListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");

            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mDelegate != null) {
                        mDelegate.onClose();
                    }

                }
            });

            try {
                mService.unsetDeviceListener(mMcsDeviceListener);
                mService.unsetDataSource(mMcsDataSource);
                mService.unsetDelegate(mMcsDelegate);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mService = null;
        }
    };

    @Override
    public ReturnCode connect(String deviceId, int timeout) {
        if (mService == null)
            return ReturnCode.NotConnected;

        ReturnCode code = ReturnCode.OK;

        try {
            code = mService.connect(deviceId, timeout) ? ReturnCode.OK : ReturnCode.Error;
        } catch (RemoteException e) {
            e.printStackTrace();
            code = ReturnCode.ServiceException;
        }

        return code;
    }

    @Override
    public ReturnCode disconnect() {
        if (mService == null)
            return ReturnCode.NotConnected;

        ReturnCode code = ReturnCode.OK;

        try {
            code = mService.disconnect() ? ReturnCode.OK : ReturnCode.Error;
        } catch (RemoteException e) {
            e.printStackTrace();
            code = ReturnCode.ServiceException;
        }

        return code;
    }

    @Override
    public ReturnCode startShow() {
        if (mService == null)
            return ReturnCode.NotConnected;

        ReturnCode code = ReturnCode.OK;

        try {
            code = mService.startShow() ? ReturnCode.OK : ReturnCode.NotConnected;
        } catch (RemoteException e) {
            e.printStackTrace();
            code = ReturnCode.ServiceException;
        }

        return code;
    }

    @Override
    public ReturnCode show(String photoUri) {
        if (mService == null)
            return ReturnCode.NotConnected;

        ReturnCode code = ReturnCode.OK;

        try {
            code = mService.show(photoUri) ? ReturnCode.OK : ReturnCode.NotConnected;
        } catch (RemoteException e) {
            e.printStackTrace();
            code = ReturnCode.ServiceException;
        }

        return code;
    }

    @Override
    public ReturnCode stopShow() {
        if (mService == null)
            return ReturnCode.NotConnected;

        ReturnCode code = ReturnCode.OK;

        try {
            code = mService.stopShow() ? ReturnCode.OK : ReturnCode.NotConnected;
        } catch (RemoteException e) {
            e.printStackTrace();
            code = ReturnCode.ServiceException;
        }

        return code;
    }

    @Override
    public ReturnCode startSlideshow(int duration, SlideMode type) {
        if (mService == null)
            return ReturnCode.NotConnected;

        ReturnCode code = ReturnCode.OK;

        try {
            boolean isRecyle = (type == SlideMode.Recyle);
            code = mService.startSlideshow(duration, isRecyle) ? ReturnCode.OK
                    : ReturnCode.NotConnected;
        } catch (RemoteException e) {
            e.printStackTrace();
            code = ReturnCode.ServiceException;
        }

        return code;
    }

    @Override
    public ReturnCode stopSlideshow() {
        if (mService == null)
            return ReturnCode.NotConnected;

        ReturnCode code = ReturnCode.OK;

        try {
            code = mService.stopSlideshow() ? ReturnCode.OK : ReturnCode.NotConnected;
        } catch (RemoteException e) {
            e.printStackTrace();
            code = ReturnCode.ServiceException;
        }

        return code;
    }

    @Override
    public ReturnCode startPlay(String url, String title, int iPosition, double dPosition,
            MediaType type) {
        if (mService == null)
            return ReturnCode.NotConnected;

        ReturnCode code = ReturnCode.OK;

        try {
            switch (type) {
                case Audio:
                    code = mService.startPlayAudio(url, title, iPosition, dPosition) ? ReturnCode.OK
                            : ReturnCode.NotConnected;
                    break;

                case Photo:
                    code = ReturnCode.InvalidParams;

                case Undefined:
                    code = ReturnCode.InvalidParams;

                case Video:
                    code = mService.startPlayVideo(url, title, iPosition, dPosition) ? ReturnCode.OK
                            : ReturnCode.NotConnected;
                    break;

                default:
                    code = ReturnCode.InvalidParams;
            }

        } catch (RemoteException e) {
            e.printStackTrace();
            code = ReturnCode.ServiceException;
        }

        return code;
    }

    @Override
    public ReturnCode stopPlay() {
        if (mService == null)
            return ReturnCode.NotConnected;

        ReturnCode code = ReturnCode.OK;

        try {
            code = mService.stopPlay() ? ReturnCode.OK : ReturnCode.NotConnected;
        } catch (RemoteException e) {
            e.printStackTrace();
            code = ReturnCode.ServiceException;
        }

        return code;
    }

    @Override
    public ReturnCode setPlaybackRate(int rate) {
        if (mService == null)
            return ReturnCode.NotConnected;

        ReturnCode code = ReturnCode.OK;

        try {
            code = mService.setPlaybackRate(rate) ? ReturnCode.OK : ReturnCode.NotConnected;
        } catch (RemoteException e) {
            e.printStackTrace();
            code = ReturnCode.ServiceException;
        }

        return code;
    }

    @Override
    public int getPlaybackRate() {
        if (mService == null)
            return 0;

        int rate = 0;

        try {
            rate = mService.getPlaybackRate();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return rate;
    }

    @Override
    public ReturnCode setPlaybackProgress(int position) {
        if (mService == null)
            return ReturnCode.NotConnected;

        ReturnCode code = ReturnCode.OK;

        try {
            code = mService.setPlaybackProgress(position) ? ReturnCode.OK : ReturnCode.NotConnected;
        } catch (RemoteException e) {
            e.printStackTrace();
            code = ReturnCode.ServiceException;
        }

        return code;
    }

    @Override
    public int getPlaybackProgress() {
        if (mService == null)
            return 0;

        int position = 0;

        try {
            position = mService.getPlaybackProgress();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return position;
    }

    @Override
    public int getPlaybackDuration() {
        if (mService == null)
            return 0;

        int duration = 0;

        try {
            duration = mService.getPlaybackDuration();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return duration;
    }

    @Override
    public ReturnCode setVolume(int volume) {
        if (mService == null)
            return ReturnCode.NotConnected;

        ReturnCode code = ReturnCode.OK;

        try {
            code = mService.setVolume(volume) ? ReturnCode.OK : ReturnCode.NotConnected;
        } catch (RemoteException e) {
            e.printStackTrace();
            code = ReturnCode.ServiceException;
        }

        return code;
    }

    @Override
    public int getVolume() {
        if (mService == null)
            return 0;

        int volume = 0;

        try {
            volume = mService.getVolume();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return volume;
    }

}
