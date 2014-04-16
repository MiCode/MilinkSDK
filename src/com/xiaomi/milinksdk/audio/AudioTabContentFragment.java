package com.xiaomi.milinksdk.audio;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AudioTabContentFragment extends Fragment {
    private Context mContext;
    
    public AudioTabContentFragment(Context context) {
        mContext = context;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}