
package com.xiaomi.milinksdk.audio;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.milink.api.v1.MilinkClientManager;
import com.milink.api.v1.type.ErrorCode;
import com.milink.api.v1.type.MediaType;
import com.milink.api.v1.type.ReturnCode;
import com.xiaomi.milinksdk.MainActivity;
import com.xiaomi.milinksdk.Device;
import com.xiaomi.milinksdk.R;
import com.xiaomi.milinksdk.video.IVideoCallback;

public class AudioActivity extends Activity implements IAudioCallback {
    private Button backButton;
    private TextView titleTextView;
    private Button playPauseButton;
    private Button prevButton; // TODO
    private Button nextButton; // TODO
    private Button stopButton; // TODO
    private SeekBar timeSeekBar;
    private Button castButton;
    private int position = -1; // 歌曲的位置
    private AudioData audioData;
    private boolean isPlaying = false;
    private Button modeButton; // TODO
    private int CurrentTime; // TODO
    private int totalTime; // TODO
    private Timer mTimer = null;
    private String timeout = "5000";
    private MilinkClientManager mMilinkClientManager;

    void findViews() {
        backButton = (Button) findViewById(R.id.back_button);
        titleTextView = (TextView) findViewById(R.id.title_textView);
        timeSeekBar = (SeekBar) findViewById(R.id.time_seekBar);
        playPauseButton = (Button) findViewById(R.id.playPause_button);
        playPauseButton.setBackgroundResource(R.drawable.icon_music_stop);
        prevButton = (Button) findViewById(R.id.prev_audio_button);
        nextButton = (Button) findViewById(R.id.next_audio_button);
        stopButton = (Button) findViewById(R.id.stop_audio_button);
        castButton = (Button) findViewById(R.id.cast_button);
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
        getActionBar().hide();
        if (mMilinkClientManager == null) {
            mMilinkClientManager = MainActivity.mMilinkClient.getInstance();
        }
        MainActivity.mMilinkClient.setCallback(this);
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
            mMilinkClientManager.stopPlay();
            mMilinkClientManager.disconnect();
        }
    }

    class timeSeekBarListener implements OnSeekBarChangeListener {
        private boolean isPlaying = false;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progresecond, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if (AudioUtil.mediaPlayer.isPlaying()) {
                isPlaying = true;
            } else {
                isPlaying = false;
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int dest = seekBar.getProgress();
            if (AudioUtil.mediaPlayer != null) {
                int minuteax = AudioUtil.mediaPlayer.getDuration();
                int sMax = seekBar.getMax();
                AudioUtil.mediaPlayer.seekTo(minuteax * dest / sMax);
                if (isPlaying) {
                    AudioUtil.mediaPlayer.start();
                }
            }
        }
    }

    class playPauseButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (isPlaying) {
                playPauseButton.setBackgroundResource(R.drawable.icon_music_stop);
                isPlaying = false;
                mMilinkClientManager.setPlaybackRate(0);
            } else {
                playPauseButton.setBackgroundResource(R.drawable.icon_music_start);
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
            synchronized (MainActivity.mDeviceList) {
                deviceList = (ArrayList<Device>) MainActivity.mDeviceList.clone();
            }
            final ArrayList<Device> finalDeviceList = deviceList;
            int size = finalDeviceList.size();
            if (size <= 0) {
                new AlertDialog.Builder(AudioActivity.this)
                        .setTitle("No Device Found!")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .create().show();
            } else {
                final ArrayList<String> names = new ArrayList<String>();
                for (Device device : finalDeviceList) {
                    names.add(device.name);
                }
                String[] deviceNames = new String[names.size()];
                names.toArray(deviceNames);
                new AlertDialog.Builder(AudioActivity.this).setTitle("Devices").setItems(
                        deviceNames,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int pos) {
                                String deviceId = finalDeviceList.get(pos).id;
                                mMilinkClientManager.disconnect();
                                if (mTimer != null) {
                                    mTimer.cancel();
                                }
                                ReturnCode retcode = mMilinkClientManager.connect(deviceId,
                                        Integer.valueOf(timeout));
                                Log.d("MilinkClient", "returncode: " + retcode);
                            }
                        })
                        .create().show();
            }
        }
    }

    @Override
    public void onConnected() {
        play();
    }

    @Override
    public void onConnectedFailed(ErrorCode errorCode) {
        // TODO
    }

    @Override
    public void onDisconnected() {
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
    private String TAG = "AudioActivity";
    private void play() {
        ReturnCode retCode = mMilinkClientManager.startPlay(audioData.getUri(), audioData.getTitle(), 0, 0,
                MediaType.Audio);
        Log.d(TAG, "play: " + audioData.getTitle());
    }

    private void prevPlay() {
        if (position == 0) {
            position = AudioUtil.musicCount - 1;
        } else {
            position--;
        }
        audioData = AudioUtil.audioList.get(position);
        titleTextView.setText(audioData.getTitle());
        mMilinkClientManager.startPlay(audioData.getUri(), audioData.getTitle(), 0, 0,
                MediaType.Audio);
    }

    private void nextPlay() {
        if (position == AudioUtil.musicCount - 1) {
            position = 0;
        } else {
            position++;
        }
        audioData = AudioUtil.audioList.get(position);
        titleTextView.setText(audioData.getTitle());
        mMilinkClientManager.startPlay(audioData.getUri(), audioData.getTitle(), 0, 0,
                MediaType.Audio);
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
            audioData = AudioUtil.audioList.get(position);
            titleTextView.setText(audioData.getTitle());
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
        // 查询详情
            case Menu.FIRST:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("歌曲详情");
                builder.setItems(new String[] {
                        "歌名: " + audioData.getTitle(),
                        "歌手: " + audioData.getSinger(),
                        "专辑: " + audioData.getAlbum(),
                        "时间: " + AudioUtil.formatTime(audioData.getTime()),
                        "文件地址: " + audioData.getUri()
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
