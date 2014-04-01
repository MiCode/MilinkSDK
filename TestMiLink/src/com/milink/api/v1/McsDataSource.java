
package com.milink.api.v1;

import android.os.RemoteException;

import com.milink.api.v1.aidl.IMcsDataSource;

public class McsDataSource extends IMcsDataSource.Stub {

    MilinkClientManagerDataSource mDataSource = null;

    public void setDataSource(MilinkClientManagerDataSource dataSource) {
        mDataSource = dataSource;
    }

    @Override
    public String getPrevPhoto(String uri, boolean isRecyle) throws RemoteException {
        if (mDataSource == null)
            return null;

        return mDataSource.getPrevPhoto(uri, isRecyle);
    }

    @Override
    public String getNextPhoto(String uri, boolean isRecyle) throws RemoteException {
        if (mDataSource == null)
            return null;

        return mDataSource.getNextPhoto(uri, isRecyle);
    }

}
