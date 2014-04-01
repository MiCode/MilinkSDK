
package com.milink.api.v1;

import android.os.Handler;
import android.os.RemoteException;

import com.milink.api.v1.aidl.IMcsDeviceListener;
import com.milink.api.v1.type.DeviceType;

public class McsDeviceListener extends IMcsDeviceListener.Stub {

    private Handler mHandler = new Handler();
    private MilinkClientManagerDelegate mDelegate = null;

    public void setDelegate(MilinkClientManagerDelegate delegate) {
        mDelegate = delegate;
    }

    @Override
    public void onDeviceFound(final String deviceId, final String name, final String type)
            throws RemoteException {
        if (mDelegate == null)
            return;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDelegate.onDeviceFound(deviceId, name, DeviceType.create(type));
            }
        });
    }

    @Override
    public void onDeviceLost(final String deviceId) throws RemoteException {
        if (mDelegate == null)
            return;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDelegate.onDeviceLost(deviceId);
            }
        });
    }

}
