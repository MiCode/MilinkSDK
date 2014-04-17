
package com.xiaomi.milinksdk;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import com.milink.api.v1.MilinkClientManager;
import com.milink.api.v1.type.DeviceType;
import com.xiaomi.milinksdk.audio.AudioTabContentFragment;
import com.xiaomi.milinksdk.image.ImageTabContentFragment;
import com.xiaomi.milinksdk.video.VideoTabContentFragment;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private final String TAG = this.getClass().getSimpleName();

    public final static MilinkClient mMilinkClient = new MilinkClient();
    public final static ArrayList<Device> mDeviceList = new ArrayList<Device>();

    private MilinkClientManager mMilinkClientManager = null;

    private String imageTabName = "image";
    private String audioTabName = "audio";
    private String videoTabName = "video";
    
    //tstskfdslj

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar mActionBar = getActionBar();
        mActionBar.addTab(mActionBar
                .newTab()
                .setText(imageTabName)
                .setTabListener(new TabListener(new ImageTabContentFragment(this))));

        mActionBar.addTab(mActionBar
                .newTab()
                .setText(audioTabName)
                .setTabListener(new TabListener(new AudioTabContentFragment(this))));

        mActionBar.addTab(mActionBar
                .newTab()
                .setText(videoTabName)
                .setTabListener(new TabListener(new VideoTabContentFragment(this))));

        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        Log.d(TAG, "onCreate");

        mMilinkClient.setContext(this);
        mMilinkClientManager = mMilinkClient.getInstance();
        mMilinkClientManager.setDelegate(mMilinkClient);
        mMilinkClientManager.setDataSource(mMilinkClient);
        mMilinkClientManager.setDeviceName("zhgnphone");
        mMilinkClientManager.open();

        Device nullDevice = new Device("127.0.0.1", "Local Device", DeviceType.Unknown);
        mDeviceList.add(nullDevice);

    }

    @Override
    protected void onDestroy() {
        mMilinkClientManager.close();
        mDeviceList.clear();
        super.onDestroy();
    }

    private class TabListener implements ActionBar.TabListener {
        private Fragment mFragment;

        public TabListener(Fragment fragment) {
            mFragment = fragment;
        }

        @Override
        public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
            Log.d(TAG, "onTabReselected");
        }

        @Override
        public void onTabSelected(Tab arg0, FragmentTransaction ft) {
            Log.d(TAG, "onTabSelected");
            ft.add(R.id.fragment_content, mFragment, null);
        }

        @Override
        public void onTabUnselected(Tab arg0, FragmentTransaction ft) {
            Log.d(TAG, "onTabUnselected");
            ft.remove(mFragment);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
