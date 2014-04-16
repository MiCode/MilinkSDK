package com.xiaomi.milinksdk.image;

import com.xiaomi.milinksdk.ICallback;

public interface IImageCallback extends ICallback {
    public String getPrevPhoto(String uri, boolean isRecyle);
    public String getNextPhoto(String uri, boolean isRecyle);
}
