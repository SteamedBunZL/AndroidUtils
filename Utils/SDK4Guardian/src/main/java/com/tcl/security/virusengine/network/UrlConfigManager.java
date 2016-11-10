package com.tcl.security.virusengine.network;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steve on 2016/6/22.
 */
public class UrlConfigManager {

    public static List<URLData> urlList;

    public static URLData findURL(Context context,String packageName,final String findKey) {
        if (urlList == null || urlList.isEmpty())
            fetchUrlDataFromXml(context,packageName);

        for (URLData data : urlList) {
            if (findKey.equals(data.getKey()))
                return data;
        }
        return null;
    }

    private static void fetchUrlDataFromXml(Context context, String packageName) {
        try {
            Resources res = context.getPackageManager().getResourcesForApplication(packageName);
            int resId = res.getIdentifier("url", "xml", packageName);
            XmlResourceParser xmlResParser = res.getXml(resId);
            int eventType = xmlResParser.getEventType();
            URLData data = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        urlList = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        if ("Node".equals(xmlResParser.getName())){
                            data = new URLData();
                            data.setKey(xmlResParser.getAttributeValue(0));
                            data.setUrl(xmlResParser.getAttributeValue(1));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("Node".equals(xmlResParser.getName())){
                            urlList.add(data);
                            data = null;
                        }
                        break;
                }
                eventType = xmlResParser.next();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
