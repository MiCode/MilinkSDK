
package com.milink.uniplay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;

import com.milink.api.v1.MilinkClientManager;
import com.milink.api.v1.type.DeviceType;
import com.milink.uniplay.audio.AudioTabContentFragment;
import com.milink.uniplay.image.ImageTabContentFragment;
import com.milink.uniplay.video.VideoTabContentFragment;
import com.milink.uniplay.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private final String TAG = this.getClass().getSimpleName();
    private final String SERVICE_NAME = "com.milink.service";

    private MilinkClientManager mMilinkClientManager = null;

    List<Fragment> mFragmentList = new ArrayList<Fragment>();
    List<String> mTabTitleList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager vp = (ViewPager) findViewById(R.id.viewPager);

        mFragmentList.add(new ImageTabContentFragment(this));
        mFragmentList.add(new AudioTabContentFragment(this));
        mFragmentList.add(new VideoTabContentFragment(this));

        mTabTitleList.add(getString(R.string.imageTabName));
        mTabTitleList.add(getString(R.string.audioTabName));
        mTabTitleList.add(getString(R.string.videoTabName));

        vp.setAdapter(new SimplePagerAdapter(getSupportFragmentManager(), mFragmentList,
                mTabTitleList));
        // set the number of cached pages.
        vp.setOffscreenPageLimit(mFragmentList.size());

        MilinkClient client = MilinkClient.newMilinkClient(this);
        mMilinkClientManager = MilinkClient.getManagerInstance();
        mMilinkClientManager.setDelegate(client);
        mMilinkClientManager.setDataSource(client);
        mMilinkClientManager.setDeviceName("My Phone");
        mMilinkClientManager.open();

        checkServiceAvailable(this);

        Device nullDevice = new Device("127.0.0.1", getString(R.string.localDeviceName),
                DeviceType.Unknown);
        MilinkClient.getDeviceList().add(nullDevice);

        Log.d(TAG, "app start.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMilinkClientManager.close();
        MilinkClient.getDeviceList().clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class SimplePagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList;
        private List<String> mTabTitleList;

        public SimplePagerAdapter(FragmentManager fm, List<Fragment> flist, List<String> slist) {
            super(fm);
            mFragmentList = flist;
            mTabTitleList = slist;
        }

        @Override
        public Fragment getItem(int pos) {
            return mFragmentList.get(pos);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitleList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

    private void checkServiceAvailable(final Activity mActivity) {
        try {
            getPackageManager().getApplicationInfo(SERVICE_NAME,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            Log.d(TAG, "service found.");
            return;
        } catch (NameNotFoundException e) {
        }

        Log.d(TAG, "service not found.");
        new AlertDialog.Builder(this)
                .setTitle(R.string.serviceUnavailable)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                mActivity.finish();
                            }
                        }).create().show();
    }

}
