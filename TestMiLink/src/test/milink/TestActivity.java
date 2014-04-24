
package test.milink;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.milink.api.v1.MilinkClientManager;
import com.milink.api.v1.MilinkClientManagerDataSource;
import com.milink.api.v1.MilinkClientManagerDelegate;
import com.milink.api.v1.type.DeviceType;
import com.milink.api.v1.type.ErrorCode;
import com.milink.api.v1.type.MediaType;
import com.milink.api.v1.type.ReturnCode;
import com.milink.api.v1.type.SlideMode;

import java.util.ArrayList;

import test.milinksdk.R;

public class TestActivity extends Activity implements MilinkClientManagerDelegate,
        MilinkClientManagerDataSource {

    private static final String TAG = TestActivity.class.getSimpleName();

    private MilinkClientManager mMgr = null;

    private String mPhotos[] = {
            "/sdcard/DCIM/Camera/00.jpg",
            "/sdcard/DCIM/Camera/01.jpg",
            "/sdcard/DCIM/Camera/02.jpg",
            "/sdcard/DCIM/Camera/03.jpg",
            "/sdcard/DCIM/Camera/04.jpg",
            "/sdcard/DCIM/Camera/05.jpg",
            "/sdcard/DCIM/Camera/06.jpg",
            "/sdcard/DCIM/Camera/07.jpg",
            "/sdcard/DCIM/Camera/08.jpg",
            "/sdcard/DCIM/Camera/09.jpg",
            "/sdcard/DCIM/Camera/10.jpg",
            "/sdcard/DCIM/Camera/11.jpg",
            "/sdcard/DCIM/Camera/12.jpg",
            "/sdcard/DCIM/Camera/13.jpg",
            "/sdcard/DCIM/Camera/14.jpg",
            "/sdcard/DCIM/Camera/15.jpg",
            "/sdcard/DCIM/Camera/16.jpg",
            "/sdcard/DCIM/Camera/17.jpg",
            "/sdcard/DCIM/Camera/18.jpg",
            "/sdcard/DCIM/Camera/19.jpg",
            "/sdcard/DCIM/Camera/20.jpg",
            "/sdcard/DCIM/Camera/21.jpg",
            "/sdcard/DCIM/Camera/22.jpg",
            "/sdcard/DCIM/Camera/23.jpg",
    };

    private int mRate = 0;
    private int mVolume = 50;
    private int mPosition = 0;
    private int mMusicIndex = 0;

    private Spinner mSpinner = null;
    private TextView mTextView = null;

    private ArrayList<Device> mDeviceList = new ArrayList<Device>();
    private int mSelectedDeviceIndex = 0;

    private class Device {
        public String id;
        public String name;
        public DeviceType type;

        public Device(String deviceId, String name, DeviceType type) {
            this.id = deviceId;
            this.name = name;
            this.type = type;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mTextView = (TextView) findViewById(R.id.textView1);

        // Spinner
        mSpinner = (Spinner) findViewById(R.id.spinner1);

        Device nullDevice = new Device("127.0.0.1", "Please Select device", DeviceType.Unknown);
        mDeviceList.add(nullDevice);

        MyAdapter myAdapter = new MyAdapter(this);

        mSpinner.setAdapter(myAdapter);
        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedDeviceIndex = position;

                synchronized (mDeviceList) {
                    Device device = mDeviceList.get(position);
                    if (device != null) {
                        Toast.makeText(TestActivity.this, "你选中了:" + device.name, 2000).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mMgr = new MilinkClientManager(this);
        mMgr.setDeviceName("OuyangMiPhone");
        mMgr.setDelegate(this);
        mMgr.setDataSource(this);
    }

    @Override
    protected void onDestroy() {
        mMgr.close();
        super.onDestroy();
    }

    public class MyAdapter extends BaseAdapter {
        private Context mContext;

        public MyAdapter(Context pContext) {
            this.mContext = pContext;
        }

        @Override
        public int getCount() {
            int size = 0;

            synchronized (mDeviceList) {
                size = mDeviceList.size();
            }

            return size;
        }

        @Override
        public Object getItem(int position) {
            Device device = null;

            synchronized (mDeviceList) {
                device = mDeviceList.get(position);
            }

            return device;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout ll = new LinearLayout(mContext);
            ll.setOrientation(LinearLayout.HORIZONTAL);

            Device device = null;

            synchronized (mDeviceList) {
                device = mDeviceList.get(position);
            }

            TextView tv = new TextView(mContext);
            tv.setText(device.name);
            tv.setTextSize(24);

            ll.addView(tv);

            return ll;
        }
    }

    public void onOpen(View button) {
        Log.d(TAG, "Button onOpen");
        mMgr.open();
    }

    public void onClose(View button) {
        Log.d(TAG, "Button onClose");
        mMgr.close();

        mDeviceList.clear();
        Device nullDevice = new Device("127.0.0.1", "Please Select device", DeviceType.Unknown);
        mDeviceList.add(nullDevice);
    }

    public void onConnect(View button) {
        if (mDeviceList.size() == 1)
            return;

        if (mSelectedDeviceIndex == 0)
            return;

        Device device = mDeviceList.get(mSelectedDeviceIndex);
        if (device == null)
            return;

        ReturnCode code = mMgr.connect(device.id, 1000 * 10);
        Log.d(TAG, "Button onConnect: " + code);
    }

    public void onDisconnect(View button) {
        ReturnCode code = mMgr.disconnect();
        Log.d(TAG, "Button onDisconnect: " + code);
    }

    public void onStartShow(View button) {
        ReturnCode code = mMgr.startShow();
        Log.d(TAG, "Button onStartShow: " + code);
    }

    public void onStopShow(View button) {
        ReturnCode code = mMgr.stopShow();
        Log.d(TAG, "Button onStopShow: " + code);
    }

    private void show(int index) {
        ReturnCode code = mMgr.show(mPhotos[index]);
        Log.d(TAG, String.format("show: %s - %s", mPhotos[index], code));
    }

    public void onP0(View button) {
        show(0);
    }

    public void onP1(View button) {
        show(1);
    }

    public void onP2(View button) {
        show(2);
    }

    public void onP3(View button) {
        show(3);
    }

    public void onP4(View button) {
        show(4);
    }

    public void onP5(View button) {
        show(5);
    }

    public void onP6(View button) {
        show(6);
    }

    public void onP7(View button) {
        show(7);
    }

    public void onP8(View button) {
        show(8);
    }

    public void onP9(View button) {
        show(9);
    }

    public void onP10(View button) {
        show(10);
    }

    public void onP11(View button) {
        show(11);
    }

    public void onP12(View button) {
        show(12);
    }

    public void onP13(View button) {
        show(13);
    }

    public void onP14(View button) {
        show(14);
    }

    public void onP15(View button) {
        show(15);
    }

    public void onP16(View button) {
        show(16);
    }

    public void onP17(View button) {
        show(17);
    }

    public void onP18(View button) {
        show(18);
    }

    public void onP19(View button) {
        show(19);
    }

    public void onP20(View button) {
        show(20);
    }

    public void onP21(View button) {
        show(21);
    }

    public void onP22(View button) {
        show(22);
    }

    public void onP23(View button) {
        show(23);
    }

    public void onStartSlide(View button) {
        ReturnCode code = mMgr.startSlideshow(3000, SlideMode.Recyle);
        Log.d(TAG, "Button onStartSlide: " + code);
    }

    public void onStopSlide(View button) {
        ReturnCode code = mMgr.stopSlideshow();
        Log.d(TAG, "Button onStopSlide: " + code);
    }

    public void onPlayVideo(View button) {
        // String url =
        // "http://video19.ifeng.com/video07/2013/11/28/744134-102-007-1612.mp4";
        // String url =
        // "http://v.youku.com/player/getRealM3U8/vid/XNjU1NDU5NTQw/type/mp4/v.m3u8";
//      String url = "file:///sdcard/Movies/IceAge4.mp4";
        String url = "file://sdcard/Movies/test.rmvb";
        // String url = "file:///sdcard/Movies/demo.avi";
        // String url = "file:///sdcard/DCIM/Camera/VID_20140228_141803.mp4";
        // String url = "/sdcard/Movies/hello.mp4";

        String title = "demo";
        int iPosition = 0;
        double dPosition = 0.0;

        ReturnCode code = mMgr.startPlay(url, title, iPosition, dPosition, MediaType.Video);
        Log.d(TAG, "Button onPlayVideo: " + code);
    }

    public void onPlayAudio(View button) {
        mMusicIndex = (mMusicIndex + 1) % 4;
//        String url = String.format("/sdcard/Music/demo%d.mp3", mMusicIndex);
        String url = String.format("music://baidu/113333032");

        String title = "demo";
        int iPosition = 0;
        double dPosition = 0.0;

        ReturnCode code = mMgr.startPlay(url, title, iPosition, dPosition, MediaType.Audio);
        Log.d(TAG, "Button onPlayAudio: " + code);
    }

    public void onPlayPause(View button) {
        mRate = (mRate == 0) ? 1 : 0;
        ReturnCode code = mMgr.setPlaybackRate(mRate);
        Log.d(TAG, "Button onPlayPause: " + code);
    }

    public void onStopPlay(View button) {
        ReturnCode code = mMgr.stopPlay();
        Log.d(TAG, "Button onStopPlay: " + code);
    }

    public void onGetPosition(View button) {
        mPosition = mMgr.getPlaybackProgress();
        Log.d(TAG, "Button onGetPosition: " + mPosition);
        mTextView.setText("getPlaybackProgress: " + mPosition);
    }

    public void onGetDuration(View button) {
        int duration = mMgr.getPlaybackDuration();
        Log.d(TAG, "Button onGetDuration: " + duration);
        mTextView.setText("getDuration: " + duration);
    }

    public void onGetVolume(View button) {
        int volume = mMgr.getVolume();
        Log.d(TAG, "Button onGetVolume: " + volume);
        mTextView.setText("getVolume: " + volume);
    }

    public void onVolDec(View button) {
        mVolume = (mVolume - 10) % 100;
        if (mVolume < 0)
            mVolume = 0;

        ReturnCode code = mMgr.setVolume(mVolume);
        Log.d(TAG, "Button onVolDec: " + code);
    }

    public void onVolInc(View button) {
        mVolume = (mVolume + 10) % 100;

        ReturnCode code = mMgr.setVolume(mVolume);
        Log.d(TAG, "Button onVolDec: " + code);
    }

    public void onSeek(View button) {
        mPosition += 10;

        ReturnCode code = mMgr.setPlaybackProgress(mPosition);
        Log.d(TAG, "Button onSeek: " + code);
    }

    // MilinkClientManagerDelegate

    @Override
    public void onOpen() {
        Log.d(TAG, "on open");
    }

    @Override
    public void onClose() {
        Log.d(TAG, "on Close");
    }

    @Override
    public void onDeviceFound(String deviceId, String name, DeviceType type) {
        Log.d(TAG, String.format("onDeviceFound: %s -> %s -> %s", deviceId, name, type));

        Toast.makeText(this, "发现设备: " + name, 1000).show();

        synchronized (mDeviceList) {
            Device device = new Device(deviceId, name, type);
            mDeviceList.add(device);
        }
    }

    @Override
    public void onDeviceLost(String deviceId) {
        Log.d(TAG, String.format("onDeviceLost: %s", deviceId));

        Toast.makeText(this, "丢失设备: " + deviceId, 1000).show();

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
        mTextView.setText("连接成功");
    }

    @Override
    public void onConnectedFailed(ErrorCode errorCode) {
        Log.d(TAG, "onConnectedFailed");
        mTextView.setText("连接失败");
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "onDisconnected");
        mTextView.setText("断开连接");
    }

    @Override
    public void onLoading() {
        Log.d(TAG, "onLoading");
        mRate = 0;
        mTextView.setText("Loading");
    }

    @Override
    public void onPlaying() {
        Log.d(TAG, "onPlaying");
        mRate = 1;
        mTextView.setText("onPlaying");
    }

    @Override
    public void onStopped() {
        Log.d(TAG, "onStopped");
        mRate = 0;
        mTextView.setText("onStopped");
    }

    @Override
    public void onPaused() {
        Log.d(TAG, "onPaused");
        mRate = 0;
        mTextView.setText("onPaused");
    }

    @Override
    public void onVolume(int volume) {
        Log.d(TAG, "onVolume: " + volume);
        mTextView.setText("onVolume: " + volume);
    }

    @Override
    public void onNextAudio(boolean isAuto) {
        Log.d(TAG, "onNextAudio: " + isAuto);

        mMusicIndex = (mMusicIndex + 1) % 4;
        String url = String.format("/sdcard/Music/demo%d.mp3", mMusicIndex);
        String title = "demo";
        int iPosition = 0;
        double dPosition = 0.0;

        ReturnCode code = mMgr.startPlay(url, title, iPosition, dPosition, MediaType.Audio);
    }

    @Override
    public void onPrevAudio(boolean isAuto) {
        Log.d(TAG, "onPrevAudio: " + isAuto);
    }

    // MilinkClientManagerDataSource

    @Override
    public String getPrevPhoto(String uri, boolean isRecyle) {
        Log.d(TAG, String.format("getPrevPhoto: %s %s", uri, isRecyle));

        int index = getPhotoIndex(uri);
        if (index > 0) {
            return mPhotos[index - 1];
        }

        if (isRecyle) {
            if (index == 0) {
                return mPhotos[mPhotos.length - 1];
            }
        }

        return null;
    }

    @Override
    public String getNextPhoto(String uri, boolean isRecyle) {
        Log.d(TAG, String.format("getNextPhoto: %s %s", uri, isRecyle));

        int index = getPhotoIndex(uri);
        if (index < (mPhotos.length - 1)) {
            return mPhotos[index + 1];
        }

        if (isRecyle) {
            if (index == (mPhotos.length - 1)) {
                return mPhotos[0];
            }
        }

        return null;
    }

    public int getPhotoIndex(String uri) {
        for (int i = 0; i < mPhotos.length; ++i) {
            if (mPhotos[i].equalsIgnoreCase(uri)) {
                return i;
            }
        }

        return -1;
    }
}
