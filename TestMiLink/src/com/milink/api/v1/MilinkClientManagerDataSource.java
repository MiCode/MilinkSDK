
package com.milink.api.v1;

public interface MilinkClientManagerDataSource {

    String getPrevPhoto(String uri, boolean isRecyle);

    String getNextPhoto(String uri, boolean isRecyle);
}
