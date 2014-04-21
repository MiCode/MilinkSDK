package com.milink.uniplay.audio;

import java.io.IOException;
import java.util.ArrayList;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;

public class AudioUtil {
    public static ArrayList<AudioData> audioList = null;
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    public static int musicCount = 0 ;
    public static String mode = "顺序播放";

    public static ArrayList<AudioData> getAudioData(Context context) {
        audioList = new ArrayList<AudioData>();
        ContentResolver cr = context.getContentResolver();
        if (cr != null) {
            Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null, null, null, null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
                String title = cursor.getString(cursor.getColumnIndex(MediaColumns.TITLE));
                String name = cursor.getString(cursor.getColumnIndex(MediaColumns.DISPLAY_NAME));
                String singer = cursor.getString(cursor.getColumnIndex(AudioColumns.ARTIST));
                if (singer.equals("<unknown>")) {
                    singer = "未知歌手";
                }
                String album = cursor.getString(cursor.getColumnIndex(AudioColumns.ALBUM));
                long size = cursor.getLong(cursor.getColumnIndex(MediaColumns.SIZE));
                long time = cursor.getLong(cursor.getColumnIndex(AudioColumns.DURATION));
                String uri = cursor.getString(cursor.getColumnIndex(MediaColumns.DATA));
                if (name.endsWith(".mp3") || name.endsWith(".MP3")) {
                    AudioData AudioData = new AudioData();
                    AudioData.setId(id);
                    AudioData.setName(name);
                    AudioData.setSinger(singer);
                    AudioData.setSize(size);
                    AudioData.setTime(time);
                    AudioData.setTitle(title);
                    AudioData.setAlbum(album);
                    AudioData.setUri(uri);
                    audioList.add(AudioData);
                }
            }
        }
        musicCount = audioList.size();
        return audioList;
    }

    public static void delete(Context context, int position) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(audioList.get(position).getUri());
        cr.delete(uri, BaseColumns._ID + "=" + audioList.get(position).getId(), null);
    }

    public static MediaPlayer play(int position) {
        AudioData AudioData = audioList.get(position);
        if (mediaPlayer == null) {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(AudioData.getUri());
                mediaPlayer.prepare();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mediaPlayer.start();
        return mediaPlayer;
    }

    public static void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public static void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static String formatTime(long time) {
        long totalSec = time / 1000;
        long min = totalSec / 60;
        long sec = totalSec % 60;
        String minute = "";
        String second = "";
        if (min < 10) {
            minute = "0" + min;
        } else {
            minute = min + "";
        }
        if (sec < 10) {
            second = "0" + sec;
        } else {
            second = sec + "";
        }
        return minute + ":" + second;
    }
}
