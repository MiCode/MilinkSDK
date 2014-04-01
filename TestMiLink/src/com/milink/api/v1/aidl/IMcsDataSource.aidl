
package com.milink.api.v1.aidl;

interface IMcsDataSource {

    String getPrevPhoto(String uri, boolean isRecyle);

    String getNextPhoto(String uri, boolean isRecyle);
}