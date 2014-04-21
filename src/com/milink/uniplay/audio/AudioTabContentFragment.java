package com.milink.uniplay.audio;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
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
        for (AudioData m : musicList) {
            map = new HashMap<String, Object>();
            map.put("name", m.getTitle());
            map.put("singer", m.getSinger());
            map.put("time", AudioUtil.formatTime(m.getTime()));
            list.add(map);
        }
        return list;
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, Menu.FIRST, 0, "退出程序");
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST:
                exitSystem(mContext);
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
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                context.finish();
                int nPid = android.os.Process.myPid();
                android.os.Process.killProcess(nPid);
            }
        });
        builder.show();
    }
*/
}
