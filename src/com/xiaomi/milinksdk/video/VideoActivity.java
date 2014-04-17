
package com.xiaomi.milinksdk.video;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.milink.api.v1.MilinkClientManager;
import com.milink.api.v1.type.ErrorCode;
import com.milink.api.v1.type.MediaType;
import com.milink.api.v1.type.ReturnCode;
import com.xiaomi.milinksdk.Device;
import com.xiaomi.milinksdk.MainActivity;
import com.xiaomi.milinksdk.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class VideoActivity extends Activity implements IVideoCallback {
    private String TAG = this.getClass().getSimpleName();

    private String timeout = "5000";
    private int VIDEO_SEP_TIME = 1000;
    private int VIDEO_DURATION = 0;

    private boolean isVideoPlaying = false;

    private HashMap<String, Object> mVideoInfoHashMap = null;
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == VIDEO_DURATION) {
                TextView tv = (TextView) findViewById(R.id.playtime);
                tv.setText((String) msg.obj);
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_details);
        Log.d(TAG, "VideoActivity onCreate");

        Bundle mBundle = getIntent().getExtras();
        mVideoInfoHashMap = (HashMap<String, Object>) mBundle.get("videoInfo");

        TextView tv1 = (TextView) findViewById(R.id.title);
        TextView tv2 = (TextView) findViewById(R.id.album);
        TextView tv3 = (TextView) findViewById(R.id.artist);
        TextView tv4 = (TextView) findViewById(R.id.discription);
        TextView tv5 = (TextView) findViewById(R.id.data);
        TextView tv6 = (TextView) findViewById(R.id.MIME_TYPE);

        tv1.setText("Title: " + (String) mVideoInfoHashMap.get("TITLE"));
        tv2.setText("Album: " + (String) mVideoInfoHashMap.get("ALBUM"));
        tv3.setText("Artist: " + (String) mVideoInfoHashMap.get("ARTIST"));
        tv4.setText("Description: " + (String) mVideoInfoHashMap.get("DESCRIPTION"));
        tv5.setText("Data: " + (String) mVideoInfoHashMap.get("DATA"));
        tv6.setText("MIME_TYPE: " + (String) mVideoInfoHashMap.get("MIME_TYPE"));

        setVisible(false);

        MainActivity.mMilinkClient.setCallback(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopVideo(getCurrentFocus());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem mMenuItem = menu.add("push");
        mMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        mMenuItem.setIcon(android.R.drawable.ic_menu_share);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("push")) {
            Log.d(TAG, "push");

            ArrayList<Device> deviceList = null;
            synchronized (MainActivity.mDeviceList) {
                deviceList = (ArrayList<Device>) MainActivity.mDeviceList.clone();
            }
            final ArrayList<Device> finalDeviceList = deviceList;
            final ArrayList<String> names = new ArrayList<String>();
            for (Device device : finalDeviceList) {
                names.add(device.name);
            }
            String[] deviceNames = new String[names.size()];
            names.toArray(deviceNames);

            new AlertDialog.Builder(this).setTitle("").setItems(
                    deviceNames,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            if (pos == 0) {
                                return;
                            }
                            String deviceId = finalDeviceList.get(pos).id;
                            MilinkClientManager mMilinkClientManager = MainActivity.mMilinkClient
                                    .getInstance();
                            ReturnCode retcode = mMilinkClientManager.connect(deviceId,
                                    Integer.valueOf(timeout));
                            Log.d(TAG, "ret code: " + retcode);
                        }

                    })
                    .create().show();

            return true;
        }

        return false;
    }

    private synchronized void setPlaying(boolean playing) {
        isVideoPlaying = playing;
    }

    public void setVisible(boolean visible) {
        View view0 = findViewById(R.id.playtime);
        View view1 = findViewById(R.id.btnPause);
        View view2 = findViewById(R.id.btnStop);
        View view3 = findViewById(R.id.btnPrev);
        View view4 = findViewById(R.id.btnNext);
        if (!visible) {
            view0.setVisibility(View.INVISIBLE);
            view1.setVisibility(View.INVISIBLE);
            view2.setVisibility(View.INVISIBLE);
            view3.setVisibility(View.INVISIBLE);
            view4.setVisibility(View.INVISIBLE);
        } else {
            view0.setVisibility(View.VISIBLE);
            view1.setVisibility(View.VISIBLE);
            view2.setVisibility(View.VISIBLE);
            view3.setVisibility(View.VISIBLE);
            view4.setVisibility(View.VISIBLE);
        }
    }

    private void startTimerTask() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                MilinkClientManager mMgr = MainActivity.mMilinkClient.getInstance();
                int len = mMgr.getPlaybackDuration();
                int pos = mMgr.getPlaybackProgress();
                len = len <= 0 ? 0 : len;
                pos = pos <= 0 ? 0 : pos;
                Log.d(TAG, String.format("timer len = %d, pos = %d", len, pos));

                String text = convertTime(pos) + "/" + convertTime(len);
                Message msg = Message.obtain();
                msg.obj = text;
                msg.what = VIDEO_DURATION;
                handler.sendMessage(msg);
            }

            private String convertTime(int time) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                return format.format(time);
            }
        };

        mTimer.schedule(mTimerTask, VIDEO_SEP_TIME, VIDEO_SEP_TIME);
    }

    private void stopTimerTask() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void playVideo(View view) {
        MilinkClientManager mMilinkClientManager = MainActivity.mMilinkClient.getInstance();
        String title = (String) mVideoInfoHashMap.get("TITLE");
        String url = (String) mVideoInfoHashMap.get("DATA");

        Log.d(TAG, "url: " + url);
        Log.d(TAG, "title: " + title);

        ReturnCode retcode = mMilinkClientManager.startPlay(url, title, 0, 0.0, MediaType.Video);
        Log.d(TAG, "startPlay ret code: " + retcode);

        startTimerTask();

    }

    public void pauseVideo(View view) {
        MilinkClientManager mMilinkClientManager = MainActivity.mMilinkClient.getInstance();
        ReturnCode retcode;
        if (isVideoPlaying) {
            retcode = mMilinkClientManager.setPlaybackRate(0);
        } else {
            retcode = mMilinkClientManager.setPlaybackRate(1);
        }
        Log.d(TAG, "pause ret code: " + retcode);
    }

    public void stopVideo(View view) {
        MilinkClientManager mMilinkClientManager = MainActivity.mMilinkClient.getInstance();
        ReturnCode retcode = mMilinkClientManager.stopPlay();
        ReturnCode retcode1 = mMilinkClientManager.disconnect();
        Log.d(TAG, "stop ret code: " + retcode);
        Log.d(TAG, "disconnect ret code: " + retcode1);

        stopTimerTask();
        setVisible(false);
        setPlaying(false);
    }

    @Override
    public void onConnected() {
        playVideo(getCurrentFocus());
        setVisible(true);
        Toast.makeText(this, R.string.connected, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectedFailed(ErrorCode errorCode) {
        setVisible(false);
        Toast.makeText(this, R.string.connectFailed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnected() {
        setVisible(false);
    }

    @Override
    public void onLoading() {
    }

    @Override
    public void onPlaying() {
        Button btn = (Button) findViewById(R.id.btnPause);
        btn.setText(R.string.pauseVideo);
        setPlaying(true);
        setVisible(true);
        Toast.makeText(this, R.string.playing, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStopped() {
        stopTimerTask();
        setVisible(false);
        setPlaying(false);
        Toast.makeText(this, R.string.stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaused() {
        Button btn = (Button) findViewById(R.id.btnPause);
        btn.setText(R.string.playVideo);
        setPlaying(false);
        Toast.makeText(this, R.string.paused, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onVolume(int volume) {
    }
}
