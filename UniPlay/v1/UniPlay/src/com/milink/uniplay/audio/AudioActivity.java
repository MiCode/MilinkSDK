
package com.milink.uniplay.audio;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.milink.api.v1.MilinkClientManager;
import com.milink.api.v1.type.DeviceType;
import com.milink.api.v1.type.ErrorCode;
import com.milink.api.v1.type.MediaType;
import com.milink.api.v1.type.ReturnCode;
import com.milink.uniplay.Device;
import com.milink.uniplay.MilinkClient;
import com.milink.uniplay.R;

public class AudioActivity extends Activity implements IAudioCallback {
    private TextView titleTextView;
    private TextView detailTextView;
    private Button playPauseButton;
    private Button prevButton;
    private Button nextButton;
    private Button stopButton;
    private Button volumeInc;
    private Button volumeDec;
    private TextView timeTextView;
    private int position = -1;
    private AudioData mAudioData;
    private int volumeValue = 0;
    private boolean isPlaying = true;
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private String timeout = "5000";
    private int VIDEO_SEP_TIME = 1000;
    private int AUDIO_DURATION = 1;
    private MilinkClientManager mMilinkClientManager;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == AUDIO_DURATION) {
                timeTextView.setText((String) msg.obj);
            }
        };
    };

    void findViews() {
        titleTextView = (TextView) findViewById(R.id.title);
        detailTextView = (TextView) findViewById(R.id.discription);
        playPauseButton = (Button) findViewById(R.id.btnPause);
        prevButton = (Button) findViewById(R.id.btnPrev);
        nextButton = (Button) findViewById(R.id.btnNext);
        stopButton = (Button) findViewById(R.id.btnStop);
        volumeInc = (Button) findViewById(R.id.btnVolInc);
        volumeDec = (Button) findViewById(R.id.btnVolDec);

        timeTextView = (TextView) findViewById(R.id.playtime);
        timeTextView.setText("00:00/00:00");
    }

    void setupActions() {
        playPauseButton.setOnClickListener(new playPauseButtonListener());
        prevButton.setOnClickListener(new prevButtonListener());
        nextButton.setOnClickListener(new nextButtonListener());
        stopButton.setOnClickListener(new stopButtonListener());
        volumeInc.setOnClickListener(new volumeIncListener());
        volumeDec.setOnClickListener(new volumeDecListener());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_details);
        findViews();
        setupActions();
        setVisible(View.INVISIBLE);
        if (mMilinkClientManager == null) {
            mMilinkClientManager = MilinkClient.mMilinkClient.getManagerInstance();
        }
        MilinkClient.mMilinkClient.setCallback(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem mMenuItem = menu.add("push");
        mMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        mMenuItem.setIcon(android.R.drawable.ic_menu_share);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("push")) {
            final ArrayList<Device> finalDeviceList = new ArrayList<Device>();
            synchronized (MilinkClient.mDeviceList) {
                finalDeviceList.add(MilinkClient.mDeviceList.get(0));
                for (int i = 1; i < MilinkClient.mDeviceList.size(); ++i) {
                    if (MilinkClient.mDeviceList.get(i).type == DeviceType.Speaker
                            || MilinkClient.mDeviceList.get(i).type == DeviceType.TV) {
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
            new AlertDialog.Builder(AudioActivity.this).setTitle(R.string.deviceListName).setItems(
                    deviceNames,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            if (pos == 0) {
                                return;
                            }
                            String deviceId = finalDeviceList.get(pos).id;
                            ReturnCode retcode = mMilinkClientManager.connect(deviceId,
                                    Integer.valueOf(timeout));
                            Log.d("MilinkClient", "returncode: " + retcode);
                        }
                    })
                    .create().show();
        }

        return true;
    }

    class stopButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            stopPlay();
        }
    }

    class playPauseButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (isPlaying) {
                playPauseButton.setText("play");
                isPlaying = false;
                mMilinkClientManager.setPlaybackRate(0);
            } else {
                playPauseButton.setText("pause");
                isPlaying = true;
                mMilinkClientManager.setPlaybackRate(1);
            }
        }
    }

    class prevButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            prevPlay();
        }
    }

    class nextButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            nextPlay();
        }
    }

    class volumeIncListener implements OnClickListener {
        @Override
        public void onClick(View arg0) {
            volumeValue += 10;
            volumeValue = volumeValue > 100 ? 100 : volumeValue;
            mMilinkClientManager.setVolume(volumeValue);
        }
    }

    class volumeDecListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            volumeValue -= 10;
            volumeValue = volumeValue < 0 ? 0 : volumeValue;
            mMilinkClientManager.setVolume(volumeValue);
        }
    }

    public void setVisible(int visible) {
        playPauseButton.setVisibility(visible);
        prevButton.setVisibility(visible);
        nextButton.setVisibility(visible);
        stopButton.setVisibility(visible);
        volumeInc.setVisibility(visible);
        volumeDec.setVisibility(visible);
        timeTextView.setVisibility(visible);
    }

    private void startTimerTask() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                long len = mMilinkClientManager.getPlaybackDuration();
                long pos = mMilinkClientManager.getPlaybackProgress();
                len = len <= 0 ? 0 : len;
                pos = pos <= 0 ? 0 : pos;
                String text = AudioUtil.formatTime(pos) + "/" + AudioUtil.formatTime(len);
                Message msg = Message.obtain();
                msg.obj = text;
                msg.what = AUDIO_DURATION;
                handler.sendMessage(msg);
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

    private void stopPlay() {
        mMilinkClientManager.stopPlay();
        mMilinkClientManager.disconnect();
        stopTimerTask();
        setVisible(View.INVISIBLE);
    }

    private void play() {
        mMilinkClientManager.startPlay(mAudioData.getUri(), mAudioData.getTitle(), 0, 0,
                MediaType.Audio);
        isPlaying = true;
        setVisible(View.VISIBLE);
        startTimerTask();
    }

    private void prevPlay() {
        if (position == 0) {
            position = AudioUtil.musicCount - 1;
        } else {
            position--;
        }
        mAudioData = AudioUtil.audioList.get(position);
        titleTextView.setText(mAudioData.getTitle());
        detailTextView.setText(mAudioData.getUri());
        play();
    }

    private void nextPlay() {
        if (position == AudioUtil.musicCount - 1) {
            position = 0;
        } else {
            position++;
        }
        mAudioData = AudioUtil.audioList.get(position);
        titleTextView.setText(mAudioData.getTitle());
        detailTextView.setText(mAudioData.getUri());
        play();
    }

    @Override
    protected void onResume() {
        super.onResume();
        position = this.getIntent().getIntExtra("Position", 0);
        if (AudioUtil.audioList == null) {
            titleTextView.setText("没有歌曲存在");
        } else {
            if (position == -1) {
                position = 0;
            }
            mAudioData = AudioUtil.audioList.get(position);
            titleTextView.setText(mAudioData.getTitle());
            detailTextView.setText(mAudioData.getUri());
        }
        getActionBar().setTitle(mAudioData.getTitle());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConnected() {
        setVisible(View.VISIBLE);
        play();
    }

    @Override
    public void onConnectedFailed(ErrorCode errorCode) {
    }

    @Override
    public void onDisconnected() {
        setVisible(View.INVISIBLE);
    }

    @Override
    public void onLoading() {
    }

    @Override
    public void onPlaying() {
    }

    @Override
    public void onStopped() {
    }

    @Override
    public void onPaused() {
    }

    @Override
    public void onVolume(int volume) {
    }

    @Override
    public void onPrevAudio(boolean isAuto) {
        if (isAuto) {
            prevPlay();
        }
    }

    @Override
    public void onNextAudio(boolean isAuto) {
        if (isAuto) {
            nextPlay();
        }
    }
}
