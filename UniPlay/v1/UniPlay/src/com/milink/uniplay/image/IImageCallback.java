
package com.milink.uniplay.image;

import com.milink.uniplay.ICallback;

public interface IImageCallback extends ICallback {
    public String getPrevPhoto(String uri, boolean isRecyle);

    public String getNextPhoto(String uri, boolean isRecyle);
}
