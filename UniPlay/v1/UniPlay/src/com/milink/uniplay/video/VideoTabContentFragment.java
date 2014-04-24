
package com.milink.uniplay.video;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.milink.uniplay.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoTabContentFragment extends Fragment {
    private String TAG = this.getClass().getSimpleName();
    private Context mContext;

    public VideoTabContentFragment(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View fragView = inflater.inflate(R.layout.video_content, container, false);
        Log.d(TAG, "inflate video_content");

        List<Map<String, Object>> videoList = new ArrayList<Map<String, Object>>();

        String[] mediaColumns = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.ALBUM,
                MediaStore.Video.Media.ARTIST,
                MediaStore.Video.Media.DESCRIPTION,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DURATION
        };
        String[] from = new String[] {
                // "THUMBNAIL",
                "DISPLAY_NAME",
                "DATE_MODIFIED",
                "SIZE"
        };
        int[] to = new int[] {
                // R.id.thumbnail,
                R.id.displayName,
                R.id.dateModified,
                R.id.size
        };

        Cursor mCursor = mContext.getContentResolver()
                .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null,
                        null, null);

        while (mCursor.moveToNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("_ID", mCursor.getString(mCursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media._ID)));
            map.put("TITLE", mCursor.getString(mCursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
            map.put("DISPLAY_NAME", mCursor.getString(mCursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
            map.put("DATE_MODIFIED", DateFormat.format("yyyy-MM-dd kk:mm", 1000 * mCursor
                    .getLong(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED))));
            map.put("SIZE", formatSize(mCursor.getLong(mCursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))));
            map.put("ALBUM", mCursor.getString(mCursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM)));
            map.put("ARTIST", mCursor.getString(mCursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST)));
            map.put("DESCRIPTION", mCursor.getString(mCursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DESCRIPTION)));
            map.put("DATA", mCursor.getString(mCursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
            map.put("MIME_TYPE", mCursor.getString(mCursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)));
            map.put("DURATION", mCursor.getString(mCursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));

            videoList.add(map);
        }
        mCursor.close();
        Log.d(TAG, "List view size: " + videoList.size());

        ListView mListView = (ListView) fragView.findViewById(R.id.listView1);
        SimpleAdapter mSimpleAdapter = new SimpleAdapter(mContext, videoList,
                R.layout.video_content_list, from, to);
        mListView.setAdapter(mSimpleAdapter);

        final ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) videoList;
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent mIntent = new Intent(mContext, VideoActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("videoInfoList", list);
                mBundle.putInt("position", position);
                mIntent.putExtras(mBundle);
                startActivity(mIntent);
            }
        });

        Log.d(TAG, "loading list view.");

        return fragView;
    }

    private String formatSize(long l) {
        String s = "GB";
        if (l > 1024 * 1024 * 1024) {
            l /= 1024 * 1024 * 1024;
        } else if (l > 1024 * 1024) {
            l /= 1024 * 1024;
            s = "MB";
        } else if (l > 1024) {
            l /= 1024;
            s = "KB";
        } else {
            s = "B";
        }

        return String.valueOf(l) + s;

    }
}
