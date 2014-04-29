
package com.milink.uniplay.audio;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.milink.uniplay.R;

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
                new String[] {
                        "name",
                        "date",
                        "size"
                },
                new int[] {
                        R.id.displayName,
                        R.id.dateModified,
                        R.id.size
                });
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

        return fragView;
    }

    private ArrayList<Map<String, Object>> getData() {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;
        ArrayList<AudioData> audioList = AudioUtil.getAudioData(mContext);
        for (AudioData mAudioData : audioList) {
            map = new HashMap<String, Object>();
            map.put("name", mAudioData.getTitle());
            map.put("date",
                    DateFormat.format("yyyy-MM-dd kk:mm", 1000 * mAudioData.getDateModified()));
            map.put("size", AudioUtil.formatSize(mAudioData.getSize()));
            list.add(map);
        }
        return list;
    }

}
