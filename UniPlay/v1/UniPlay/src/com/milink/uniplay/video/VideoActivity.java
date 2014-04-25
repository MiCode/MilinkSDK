
package com.milink.uniplay.video;

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
import com.milink.api.v1.type.DeviceType;
import com.milink.api.v1.type.ErrorCode;
import com.milink.api.v1.type.MediaType;
import com.milink.api.v1.type.ReturnCode;
import com.milink.uniplay.Automata;
import com.milink.uniplay.Device;
import com.milink.uniplay.MilinkClient;
import com.milink.uniplay.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class VideoActivity extends Activity implements IVideoCallback {
    private String TAG = this.getClass().getSimpleName();

    private MilinkClientManager mMilinkClientManager = null;

    private int CONNECT_TIME_OUT = 5000;
    private int VIDEO_SEP_TIME = 1000;
    private int VIDEO_DURATION = 0;

    private Automata mCurrentState = Automata.START;
    private int volumeValue = 0;

    private List<Map<String, Object>> mVideoList = null;
    private int mDeviceCurrentPosition = 0;
    private int mCurrentPosition = 0;

    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private int mVideoLenght = 0;

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

        mMilinkClientManager = MilinkClient.mMilinkClient.getManagerInstance();
        MilinkClient.mMilinkClient.setCallback(this);

        Bundle mBundle = getIntent().getExtras();
        mVideoList = (List<Map<String, Object>>) mBundle.get("videoInfoList");
        mCurrentPosition = (Integer) mBundle.get("position");

        setVideoInfo(mVideoList, mCurrentPosition);
        switchState(Automata.START);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopVideo(getCurrentFocus());
        disconnect();
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

            final ArrayList<Device> finalDeviceList = new ArrayList<Device>();
            synchronized (MilinkClient.mDeviceList) {
                finalDeviceList.add(MilinkClient.mDeviceList.get(0));
                for (int i = 1; i < MilinkClient.mDeviceList.size(); ++i) {
                    if (MilinkClient.mDeviceList.get(i).type == DeviceType.TV) {
                        finalDeviceList.add(MilinkClient.mDeviceList.get(i));
                    }
                }
            }
            final ArrayList<String> names = new ArrayList<String>();
            for (Device device : finalDeviceList) {
                names.add(device.name);
            }
            String[] deviceNames = new String[names.size()];
            names.toArray(deviceNames);

            new AlertDialog.Builder(this).setTitle(R.string.deviceListName).setItems(
                    deviceNames,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            if (pos == 0) {
                                mDeviceCurrentPosition = 0;
                                stopVideo(getCurrentFocus());
                                disconnect();
                            } else if (pos == mDeviceCurrentPosition) {
                                if (mCurrentState == Automata.START) {
                                    String deviceId = finalDeviceList.get(pos).id;
                                    connect(deviceId, CONNECT_TIME_OUT);
                                } else if (mCurrentState == Automata.STOPPED) {
                                    switchState(Automata.DEVICE_READY);
                                    playVideo(getCurrentFocus());
                                }
                            } else {
                                if (mDeviceCurrentPosition != 0) {
                                    stopVideo(getCurrentFocus());
                                    disconnect();
                                }
                                mDeviceCurrentPosition = pos;
                                String deviceId = finalDeviceList.get(pos).id;
                                connect(deviceId, CONNECT_TIME_OUT);
                            }
                        }

                    })
                    .create().show();
        }

        return true;
    }

    private void setVideoInfo(List<Map<String, Object>> list, int pos) {
        Map<String, Object> map = list.get(pos);

        TextView tv1 = (TextView) findViewById(R.id.title);
        TextView tv2 = (TextView) findViewById(R.id.album);
        TextView tv3 = (TextView) findViewById(R.id.artist);
        TextView tv4 = (TextView) findViewById(R.id.discription);
        TextView tv5 = (TextView) findViewById(R.id.data);
        TextView tv6 = (TextView) findViewById(R.id.MIME_TYPE);

        tv1.setText("Title: " + (String) map.get("TITLE"));
        tv2.setText("Album: " + (String) map.get("ALBUM"));
        tv3.setText("Artist: " + (String) map.get("ARTIST"));
        tv4.setText("Description: " + (String) map.get("DESCRIPTION"));
        tv5.setText("Data: " + (String) map.get("DATA"));
        tv6.setText("MIME_TYPE: " + (String) map.get("MIME_TYPE"));

        getActionBar().setTitle((String) map.get("TITLE"));
    }

    private void setVideoPlaying(boolean playing) {
        Button btn = (Button) findViewById(R.id.btnPause);
        if (playing) {
            btn.setText(R.string.pauseVideo);
        } else {
            btn.setText(R.string.playVideo);
        }
    }

    private void setVideoStopped(boolean stop) {
        if (stop) {
            Button btn = (Button) findViewById(R.id.btnPause);
            btn.setText(R.string.playVideo);
        }
    }

    private void setVolumn() {
        if (mCurrentState == Automata.PAUSED || mCurrentState == Automata.PLAYING) {
            volumeValue = mMilinkClientManager.getVolume();
        }
    }

    public void setVisible(boolean visible) {
        View view0 = findViewById(R.id.playtime);
        View view1 = findViewById(R.id.btnPause);
        View view2 = findViewById(R.id.btnStop);
        View view3 = findViewById(R.id.btnVolInc);
        View view4 = findViewById(R.id.btnVolDec);
        View view5 = findViewById(R.id.btnPrev);
        View view6 = findViewById(R.id.btnNext);
        if (!visible) {
            view0.setVisibility(View.INVISIBLE);
            view1.setVisibility(View.INVISIBLE);
            view2.setVisibility(View.INVISIBLE);
            view3.setVisibility(View.INVISIBLE);
            view4.setVisibility(View.INVISIBLE);
            view5.setVisibility(View.INVISIBLE);
            view6.setVisibility(View.INVISIBLE);
        } else {
            view0.setVisibility(View.VISIBLE);
            view1.setVisibility(View.VISIBLE);
            view2.setVisibility(View.VISIBLE);
            view3.setVisibility(View.VISIBLE);
            view4.setVisibility(View.VISIBLE);
            view5.setVisibility(View.VISIBLE);
            view6.setVisibility(View.VISIBLE);
        }
    }

    private void startTimerTask() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                if (mVideoLenght <= 0) {
                    mVideoLenght = mMilinkClientManager.getPlaybackDuration();
                    mVideoLenght = mVideoLenght <= 0 ? 0 : mVideoLenght;
                }
                int pos = mMilinkClientManager.getPlaybackProgress();
                pos = pos <= 0 ? 0 : pos;
                // Log.d(TAG, String.format("timer len = %d, pos = %d",
                // mVideoLenght, pos));

                String text = convertTime(pos) + "/" + convertTime(mVideoLenght);
                Message msg = Message.obtain();
                msg.obj = text;
                msg.what = VIDEO_DURATION;
                handler.sendMessage(msg);
            }

            private String convertTime(int time) {
                DateFormat format = new SimpleDateFormat("HH:mm:ss");
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

    private boolean switchState(Automata state) {
        Log.d(TAG, "current state: " + mCurrentState);
        switch (state) {
            case START:
            case CONNECT_FAILED:
                setVisible(false);
                setVideoPlaying(false);
                setVideoStopped(true);
                mCurrentState = state;
                return true;
            case CONNECTING:
                if (mCurrentState != Automata.START) {
                    return false;
                }
                mCurrentState = state;
                return true;
            case DEVICE_READY:
                if (mCurrentState != Automata.CONNECTING && mCurrentState != Automata.STOPPED) {
                    return false;
                }
                mCurrentState = state;
                return true;
            case LOADING:
                if (mCurrentState != Automata.DEVICE_READY && mCurrentState != Automata.PLAYING) {
                    return false;
                }
                mCurrentState = state;
                return true;
            case PAUSED:
                if (mCurrentState != Automata.PLAYING && mCurrentState != Automata.LOADING) {
                    return false;
                }
                setVisible(true);
                setVideoPlaying(false);
                setVideoStopped(false);
                mCurrentState = state;
                return true;
            case PLAYING:
                if (mCurrentState != Automata.DEVICE_READY && mCurrentState != Automata.PAUSED
                        && mCurrentState != Automata.PLAYING && mCurrentState != Automata.LOADING) {
                    return false;
                }
                setVisible(true);
                setVideoPlaying(true);
                setVideoStopped(false);
                mCurrentState = state;
                return true;
            case STOPPED:
                if (mCurrentState != Automata.PLAYING && mCurrentState != Automata.PAUSED
                        && mCurrentState != Automata.LOADING) {
                    return false;
                }
                setVisible(false);
                setVideoPlaying(false);
                setVideoStopped(true);
                mCurrentState = state;
                return true;
            default:
                return false;
        }
    }

    public void connect(String deviceId, int timeout) {
        if (switchState(Automata.CONNECTING)) {
            Log.d(TAG, "new state: " + mCurrentState);
            ReturnCode retcode = mMilinkClientManager.connect(deviceId, timeout);
            Log.d(TAG, "connect ret code: " + retcode);
        }
    }

    public void disconnect() {
        if (switchState(Automata.START)) {
            Log.d(TAG, "new state: " + mCurrentState);
            ReturnCode retcode = mMilinkClientManager.disconnect();
            Log.d(TAG, "disconnect ret code: " + retcode);
        }
    }

    public void playVideo(View view) {
        if (switchState(Automata.PLAYING)) {
            Log.d(TAG, "new state: " + mCurrentState);
            Map<String, Object> map = mVideoList.get(mCurrentPosition);
            String title = (String) map.get("TITLE");
            String url = (String) map.get("DATA");

            ReturnCode retcode = mMilinkClientManager
                    .startPlay(url, title, 0, 0.0, MediaType.Video);
            Log.d(TAG, "startPlay ret code: " + retcode);

            startTimerTask();
        }

    }

    public void pauseVideo(View view) {
        if (mCurrentState == Automata.PLAYING) {
            switchState(Automata.PAUSED);
            ReturnCode retcode = null;
            retcode = mMilinkClientManager.setPlaybackRate(0);
            Log.d(TAG, "rate 0 ret code: " + retcode);
        } else if (mCurrentState == Automata.PAUSED) {
            switchState(Automata.PLAYING);
            ReturnCode retcode = null;
            retcode = mMilinkClientManager.setPlaybackRate(1);
            Log.d(TAG, "rate 1 ret code: " + retcode);
        } else {
            switchState(Automata.PLAYING);
            playVideo(view);
        }
        Log.d(TAG, "new state: " + mCurrentState);
    }

    public void stopVideo(View view) {
        if (switchState(Automata.STOPPED)) {
            Log.d(TAG, "new state: " + mCurrentState);
            ReturnCode retcode = mMilinkClientManager.stopPlay();
            Log.d(TAG, "stop ret code: " + retcode);

            stopTimerTask();
        }
    }

    public void volumeInc(View view) {
        if (mCurrentState == Automata.PAUSED || mCurrentState == Automata.PLAYING) {
            volumeValue += 10;
            volumeValue = volumeValue > 100 ? 100 : volumeValue;
            ReturnCode retcode = mMilinkClientManager.setVolume(volumeValue);
            Log.d(TAG, "vol inc ret code: " + retcode);
        }
    }

    public void volumeDec(View view) {
        if (mCurrentState == Automata.PAUSED || mCurrentState == Automata.PLAYING) {
            volumeValue -= 10;
            volumeValue = volumeValue < 0 ? 0 : volumeValue;
            ReturnCode retcode = mMilinkClientManager.setVolume(volumeValue);
            Log.d(TAG, "vol dec ret code: " + retcode);
        }
    }

    public void prevVideo(View view) {
        if (mCurrentState == Automata.DEVICE_READY || mCurrentState == Automata.PAUSED
                || mCurrentState == Automata.PLAYING) {
            if (mCurrentPosition == 0) {
                return;
            }
            mCurrentPosition--;
            Map<String, Object> map = mVideoList.get(mCurrentPosition);
            String title = (String) map.get("TITLE");
            String url = (String) map.get("DATA");

            switchState(Automata.PLAYING);
            Log.d(TAG, "new state: " + mCurrentState);
            setVideoInfo(mVideoList, mCurrentPosition);
            ReturnCode retcode = mMilinkClientManager
                    .startPlay(url, title, 0, 0.0, MediaType.Video);
            Log.d(TAG, "startPlay ret code: " + retcode);
        }
    }

    public void nextVideo(View view) {
        if (mCurrentState == Automata.DEVICE_READY || mCurrentState == Automata.PAUSED
                || mCurrentState == Automata.PLAYING) {
            if (mCurrentPosition == mVideoList.size() - 1) {
                return;
            }
            mCurrentPosition++;
            Map<String, Object> map = mVideoList.get(mCurrentPosition);
            String title = (String) map.get("TITLE");
            String url = (String) map.get("DATA");

            switchState(Automata.PLAYING);
            Log.d(TAG, "new state: " + mCurrentState);
            setVideoInfo(mVideoList, mCurrentPosition);
            ReturnCode retcode = mMilinkClientManager
                    .startPlay(url, title, 0, 0.0, MediaType.Video);
            Log.d(TAG, "startPlay ret code: " + retcode);
        }
    }

    @Override
    public void onConnected() {
        switchState(Automata.DEVICE_READY);
        Log.d(TAG, "new state: " + mCurrentState);
        playVideo(getCurrentFocus());
        Toast.makeText(this, R.string.connected, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectedFailed(ErrorCode errorCode) {
        switchState(Automata.CONNECT_FAILED);
        Log.d(TAG, "new state: " + mCurrentState);
        Toast.makeText(this, R.string.connectFailed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnected() {
        switchState(Automata.START);
        Log.d(TAG, "new state: " + mCurrentState);
    }

    @Override
    public void onLoading() {
        switchState(Automata.LOADING);
    }

    @Override
    public void onPlaying() {
        switchState(Automata.PLAYING);
        Log.d(TAG, "new state: " + mCurrentState);
        setVolumn();
        Toast.makeText(this, R.string.playing, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStopped() {
        stopTimerTask();
         switchState(Automata.STOPPED);
         Log.d(TAG, "new state: " + mCurrentState);
         Toast.makeText(this, R.string.stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaused() {
        switchState(Automata.PAUSED);
        Toast.makeText(this, R.string.paused, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onVolume(int volume) {
        setVolumn();
    }

}
