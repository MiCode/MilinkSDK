
package com.milink.uniplay.audio;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

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
import android.view.View.OnClickListener;
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

public class AudioActivity extends Activity implements IAudioCallback {
    private String TAG = AudioActivity.class.getSimpleName();

    private TextView mTextViewTitle;
    private TextView mTextViewAlbum;
    private TextView mTextViewArtist;
    private TextView mTextViewData;
    private TextView mTextView_MIME_TYPE;
    private Button playPauseButton;
    private Button prevButton;
    private Button nextButton;
    private Button stopButton;
    private Button volumeInc;
    private Button volumeDec;
    private TextView timeTextView;

    private int mDeviceCurrentPosition;
    private int position = -1;
    private AudioData mAudioData;
    private int volumeValue = 0;

    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private int AUDIO_SEP_TIME = 1000;
    private int AUDIO_DURATION = 1;
    private int CONNECT_TIME_OUT = 5000;

    private Automata mCurrentState;

    private MilinkClientManager mMilinkClientManager;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == AUDIO_DURATION) {
                timeTextView.setText((String) msg.obj);
            }
        };
    };

    void findViews() {
        mTextViewTitle = (TextView) findViewById(R.id.title);
        mTextViewAlbum = (TextView) findViewById(R.id.album);
        mTextViewArtist = (TextView) findViewById(R.id.artist);
        mTextViewData = (TextView) findViewById(R.id.data);
        mTextView_MIME_TYPE = (TextView) findViewById(R.id.MIME_TYPE);
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
        mMilinkClientManager = MilinkClient.mMilinkClient.getManagerInstance();
        MilinkClient.mMilinkClient.setCallback(this);

        position = this.getIntent().getIntExtra("Position", 0);
        mAudioData = AudioUtil.audioList.get(position);
        mTextViewTitle.setText("title: " + mAudioData.getTitle());
        mTextViewAlbum.setText("album: " + mAudioData.getAlbum());
        mTextViewArtist.setText("artist: " + mAudioData.getSinger());
        mTextViewData.setText("data: " + mAudioData.getUri());
        mTextView_MIME_TYPE.setText("MIME_TYPE: mp3");

        getActionBar().setTitle(R.string.localDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        switchState(Automata.START);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlay();
        disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem mMenuItem = menu.add(R.string.push);
        mMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        mMenuItem.setIcon(android.R.drawable.ic_menu_share);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        } else if (item.getTitle().equals(getString(R.string.push))) {
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
                                mDeviceCurrentPosition = 0;
                                stopPlay();
                                disconnect();

                                getActionBar().setTitle(R.string.localDeviceName);
                            } else if (pos == mDeviceCurrentPosition) {
                                if (mCurrentState == Automata.START) {
                                    String deviceId = finalDeviceList.get(pos).id;
                                    String deviceName = finalDeviceList.get(pos).name;

                                    getActionBar().setTitle(deviceName);
                                    connect(deviceId, CONNECT_TIME_OUT);
                                } else if (mCurrentState == Automata.STOPPED) {
                                    switchState(Automata.DEVICE_READY);
                                    play();
                                }
                            } else {
                                if (mDeviceCurrentPosition != 0) {
                                    switchState(Automata.START);
                                    stopPlay();
                                    disconnect();
                                }
                                mDeviceCurrentPosition = pos;
                                String deviceId = finalDeviceList.get(pos).id;
                                String deviceName = finalDeviceList.get(pos).name;

                                getActionBar().setTitle(deviceName);
                                connect(deviceId, CONNECT_TIME_OUT);
                            }
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
            playPause();
        }
    }

    class prevButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            prevPlay(true);
        }
    }

    class nextButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            nextPlay(true);
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

    public void setVolumn() {
        if (mCurrentState == Automata.PAUSED || mCurrentState == Automata.PLAYING) {
            volumeValue = mMilinkClientManager.getVolume();
        }
    }

    private void startTimerTask() {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimerTask = new TimerTask() {

                @Override
                public void run() {
                    int len = mMilinkClientManager.getPlaybackDuration();
                    int pos = mMilinkClientManager.getPlaybackProgress();
                    len = len <= 0 ? 0 : len;
                    pos = pos <= 0 ? 0 : pos;
                    Log.d(TAG, String.format("timer len = %d, pos = %d", len, pos));

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

            mTimer.schedule(mTimerTask, AUDIO_SEP_TIME, AUDIO_SEP_TIME);
        }
    }

    private void stopTimerTask() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
            mTimerTask = null;
        }
    }

    private void connect(String deviceId, int timeout) {
        if (switchState(Automata.CONNECTING)) {
            Log.d(TAG, "new state: " + mCurrentState);
            ReturnCode retcode = mMilinkClientManager.connect(deviceId, timeout);
            Log.d(TAG, "connect ret code: " + retcode);
        }
    }

    private void disconnect() {
        if (switchState(Automata.START)) {
            ReturnCode ret = mMilinkClientManager.disconnect();
            Log.d(TAG, "disconnect ret code: " + ret);
        }
    }

    private void playPause() {
        if (mCurrentState == Automata.PLAYING) {
            switchState(Automata.PAUSED);
            mMilinkClientManager.setPlaybackRate(0);
        } else if (mCurrentState == Automata.PAUSED) {
            switchState(Automata.PLAYING);
            mMilinkClientManager.setPlaybackRate(1);
        } else {
            switchState(Automata.PLAYING);
            play();
        }
        Log.d(TAG, "new state: " + mCurrentState);
    }

    private void stopPlay() {
        switchState(Automata.STOPPED);
        ReturnCode ret = mMilinkClientManager.stopPlay();
        Log.d(TAG, "stop play ret code: " + ret);

        setVisible(View.INVISIBLE);
        stopTimerTask();
    }

    private void play() {
        if (switchState(Automata.PLAYING)) {
            stopTimerTask();
            ReturnCode ret = mMilinkClientManager.startPlay(mAudioData.getUri(),
                    mAudioData.getTitle(),
                    0, 0, MediaType.Audio);
            Log.d(TAG, "start play ret code: " + ret);
            startTimerTask();
        }
    }

    private void prevPlay(boolean isRecycle) {
        if (mCurrentState == Automata.DEVICE_READY || mCurrentState == Automata.PAUSED
                || mCurrentState == Automata.PLAYING) {
            if (isRecycle) {
                if (position == 0) {
                    position = AudioUtil.musicCount - 1;
                } else {
                    position--;
                }
            } else {
                return;
            }

            switchState(Automata.PLAYING);
            mAudioData = AudioUtil.audioList.get(position);
            mTextViewTitle.setText("title: " + mAudioData.getTitle());
            mTextViewAlbum.setText("album: " + mAudioData.getAlbum());
            mTextViewArtist.setText("artist: " + mAudioData.getSinger());
            mTextViewData.setText("data: " + mAudioData.getUri());
            mTextView_MIME_TYPE.setText("MIME_TYPE: mp3");

            play();
        }
    }

    private void nextPlay(boolean isRecycle) {
        if (mCurrentState == Automata.DEVICE_READY || mCurrentState == Automata.PAUSED
                || mCurrentState == Automata.PLAYING) {
            if (isRecycle) {
                if (position == AudioUtil.musicCount - 1) {
                    position = 0;
                } else {
                    position++;
                }
            } else {
                return;
            }

            switchState(Automata.PLAYING);
            mAudioData = AudioUtil.audioList.get(position);
            mTextViewTitle.setText("title: " + mAudioData.getTitle());
            mTextViewAlbum.setText("album: " + mAudioData.getAlbum());
            mTextViewArtist.setText("artist: " + mAudioData.getSinger());
            mTextViewData.setText("data: " + mAudioData.getUri());
            mTextView_MIME_TYPE.setText("MIME_TYPE: mp3");

            play();
        }
    }

    private boolean switchState(Automata state) {
        Log.d(TAG, "current state: " + mCurrentState);
        switch (state) {
            case START:
            case CONNECT_FAILED:
                setVisible(View.INVISIBLE);
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
                setVisible(View.VISIBLE);
                playPauseButton.setText(R.string.playVideo);
                mCurrentState = state;
                return true;
            case PLAYING:
                if (mCurrentState != Automata.DEVICE_READY && mCurrentState != Automata.PAUSED
                        && mCurrentState != Automata.PLAYING && mCurrentState != Automata.LOADING) {
                    return false;
                }
                setVisible(View.VISIBLE);
                playPauseButton.setText(R.string.pauseVideo);
                mCurrentState = state;
                return true;
            case STOPPED:
                if (mCurrentState != Automata.PLAYING && mCurrentState != Automata.PAUSED
                        && mCurrentState != Automata.LOADING) {
                    return false;
                }
                setVisible(View.INVISIBLE);
                playPauseButton.setText(R.string.playVideo);
                mCurrentState = state;
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onConnected() {
        switchState(Automata.DEVICE_READY);
        Log.d(TAG, "new state: " + mCurrentState);
        play();
        Toast.makeText(this, R.string.connected, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectedFailed(ErrorCode errorCode) {
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
        startTimerTask();
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

    @Override
    public void onPrevAudio(boolean isAuto) {
        prevPlay(true);
    }

    @Override
    public void onNextAudio(boolean isAuto) {
        nextPlay(true);
    }
}
