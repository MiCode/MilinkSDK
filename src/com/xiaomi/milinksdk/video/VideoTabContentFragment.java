package com.xiaomi.milinksdk.video;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.xiaomi.milinksdk.R;
import com.xiaomi.milinksdk.R.id;
import com.xiaomi.milinksdk.R.layout;

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
                MediaStore.Video.Media.ALBUM,
                MediaStore.Video.Media.ARTIST,
                MediaStore.Video.Media.DESCRIPTION,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DURATION
        };
        String[] from = new String[] {
                "TITLE"
        };
        int[] to = new int[] {
                R.id.title
        };

        Cursor mCursor = mContext.getContentResolver()
                .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null,
                        null, null);

        while (mCursor.moveToNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("TITLE", mCursor.getString(mCursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
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
        Log.d(TAG, "List view size: " + videoList.size());

        ListView mListView = (ListView) fragView.findViewById(R.id.listView1);
        SimpleAdapter mSimpleAdapter = new SimpleAdapter(mContext, videoList,
                R.layout.video_content_list, from, to);
        mListView.setAdapter(mSimpleAdapter);

        final List<Map<String, Object>> list = videoList;
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Log.d(TAG, "position: " + position);
                HashMap<String, Object> map = (HashMap<String, Object>) list.get(position);

                Intent mIntent = new Intent(mContext, VideoActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("videoInfo", map);
                mIntent.putExtras(mBundle);
                startActivity(mIntent);

                // String path = (String) map.get("data");
                // Intent mIntent = new Intent(Intent.ACTION_VIEW);
                // mIntent.setDataAndType(Uri.parse(path), "video/mp4");
                // startActivity(mIntent);
            }
        });

        Log.d(TAG, "loading list view.");

        return fragView;
    }
}
