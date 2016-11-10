package com.tcl.security.virusengine.network;

/**
 * Created by Steve on 2016/6/22.
 */
public class URLData {
    private String key;
    private String url;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public String toString() {
        return "URLData{" +
                "key='" + key + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
