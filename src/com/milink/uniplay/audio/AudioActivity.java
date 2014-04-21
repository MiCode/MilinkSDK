
package com.milink.uniplay.audio;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;
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
import com.milink.api.v1.type.ErrorCode;
import com.milink.api.v1.type.MediaType;
import com.milink.api.v1.type.ReturnCode;
import com.milink.uniplay.Device;
import com.milink.uniplay.MilinkClient;
import com.milink.uniplay.R;

public class AudioActivity extends Activity implements IAudioCallback {
    private Button backButton;
    private TextView titleTextView;
    private TextView detailTextView;
    private Button playPauseButton;
    private Button prevButton; // TODO
    private Button nextButton; // TODO
    private Button stopButton; // TODO
    private SeekBar timeSeekBar;
    private Button castButton;
    private TextView timeTextView;
    private int position = -1; // 歌曲的位置
    private AudioData mAudioData;
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
        backButton = (Button) findViewById(R.id.back_button);
        titleTextView = (TextView) findViewById(R.id.audio_title_textView);
        castButton = (Button) findViewById(R.id.cast_button);
        detailTextView = (TextView) findViewById(R.id.audio_detail_textView);
        timeSeekBar = (SeekBar) findViewById(R.id.time_seekBar);
        playPauseButton = (Button) findViewById(R.id.playPause_button);
        prevButton = (Button) findViewById(R.id.prev_audio_button);
        nextButton = (Button) findViewById(R.id.next_audio_button);
        stopButton = (Button) findViewById(R.id.stop_audio_button);
        timeTextView = (TextView) findViewById(R.id.audio_time_textview);
        timeTextView.setText("00:00/00:00");
    }

    void setupActions() {
        backButton.setOnClickListener(new backButtonListener());
        timeSeekBar.setOnSeekBarChangeListener(new timeSeekBarListener());
        playPauseButton.setOnClickListener(new playPauseButtonListener());
        prevButton.setOnClickListener(new prevButtonListener());
        nextButton.setOnClickListener(new nextButtonListener());
        stopButton.setOnClickListener(new stopButtonListener());
        castButton.setOnClickListener(new castButtonListener());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_details);
        findViews();
        setupActions();
        setVisible(View.INVISIBLE);
        getActionBar().hide();
        if (mMilinkClientManager == null) {
            mMilinkClientManager = MilinkClient.mMilinkClient.getManagerInstance();
        }
        MilinkClient.mMilinkClient.setCallback(this);
    }

    class backButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            mMilinkClientManager.stopPlay();
            mMilinkClientManager.disconnect();
            finish();
            return;
        }
    }

    class stopButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            stopPlay();

        }
    }

    class timeSeekBarListener implements OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progresecond, boolean fromUser) {
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    class playPauseButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (isPlaying) {
                playPauseButton.setBackgroundResource(R.drawable.icon_audio_play);
                isPlaying = false;
                mMilinkClientManager.setPlaybackRate(0);
            } else {
                playPauseButton.setBackgroundResource(R.drawable.icon_audio_pause);
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

    class castButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            ArrayList<Device> deviceList = null;
            synchronized (MilinkClient.mDeviceList) {
                deviceList = (ArrayList<Device>) MilinkClient.mDeviceList.clone();
            }
            final ArrayList<Device> finalDeviceList = deviceList;
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
    }
    public void setVisible(int visible) {
        timeSeekBar.setVisibility(View.INVISIBLE);
        playPauseButton.setVisibility(visible);
        prevButton.setVisibility(visible);
        nextButton.setVisibility(visible);
        stopButton.setVisibility(visible);
        timeTextView.setVisibility(visible);
    }
    @Override
    public void onConnected() {
        setVisible(View.VISIBLE);
        play();
    }

    @Override
    public void onConnectedFailed(ErrorCode errorCode) {
        // TODO
    }

    @Override
    public void onDisconnected() {
        setVisible(View.INVISIBLE);
        //TODO
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
    private void startTimerTask() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                int len = mMilinkClientManager.getPlaybackDuration();
                int pos = mMilinkClientManager.getPlaybackProgress();
                len = len <= 0 ? 0 : len;
                pos = pos <= 0 ? 0 : pos;
                String text = convertTime(pos) + "/" + convertTime(len);
                Message msg = Message.obtain();
                msg.obj = text;
                msg.what = AUDIO_DURATION;
                handler.sendMessage(msg);
            }

            private String convertTime(int time) {
                DateFormat format = new SimpleDateFormat("mm:ss");
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
    private void stopPlay() {
        mMilinkClientManager.stopPlay();
        playPauseButton.setBackgroundResource(R.drawable.icon_audio_play);
        mMilinkClientManager.disconnect();
        stopTimerTask();
        setVisible(View.INVISIBLE);
    }
    private void play() {
        mMilinkClientManager.startPlay(mAudioData.getUri(), mAudioData.getTitle(), 0, 0,
                MediaType.Audio);
        playPauseButton.setBackgroundResource(R.drawable.icon_audio_pause);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, Menu.FIRST, 0, "详情");
        menu.add(0, Menu.FIRST + 1, 0, "退出程序");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("歌曲详情");
                builder.setItems(new String[] {
                        "歌名: " + mAudioData.getTitle(),
                        "歌手: " + mAudioData.getSinger(),
                        "专辑: " + mAudioData.getAlbum(),
                        "时间: " + AudioUtil.formatTime(mAudioData.getTime()),
                        "文件地址: " + mAudioData.getUri()
                }, null);
                builder.setNegativeButton("确定", null).show();
                break;
            case Menu.FIRST + 1:
                exitSystem(this);
                break;
            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public void exitSystem(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage("确定退出");
        builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("退出", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.show();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
