package com.xiaomi.milinksdk.audio;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.xiaomi.milinksdk.R;
public class AudioTabContentFragment extends Fragment {
    private Context mContext;
    public AudioTabContentFragment(Context context) {
        mContext = context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.audio_content, container, false);
        SimpleAdapter adapter = new SimpleAdapter(mContext, getData(), R.layout.audio_content_list,
                new String[] {"name", "singer", "time"},
                new int[] {R.id.name, R.id.singer, R.id.time});
        ListView mListView = (ListView) fragView.findViewById(R.id.listView);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Intent playIntent = new Intent(mContext, AudioActivity.class);
                playIntent.putExtra("Position", position);
                startActivity(playIntent);
            }
        });
        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO
                return false;
            }
        });
        return fragView;
    }
    private ArrayList<Map<String, Object>> getData() {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;
        ArrayList<AudioData> musicList = AudioUtil.getAudioData(mContext);
        for (AudioData mAudioData : musicList) {
            map = new HashMap<String, Object>();
            map.put("name", mAudioData.getTitle());
            map.put("singer", mAudioData.getSinger());
            map.put("time", AudioUtil.formatTime(mAudioData.getTime()));
            list.add(map);
        }
        return list;
    }
}
