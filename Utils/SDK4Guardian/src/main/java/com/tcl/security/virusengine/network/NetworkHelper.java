package com.tcl.security.virusengine.network;

import com.tcl.security.cloudengine.CloudResponse;

import java.io.ByteArrayOutputStream;

/**
 * Created by Steve on 2016/8/18.
 */
public class NetworkHelper {

    public static String getResponseString(CloudResponse.ResponseStream inputStream){
        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
            byte[] buffer = new byte[1024];
            int total = 0;
            while (true) {
                int count = -1;
                count = inputStream.read(buffer);
                if (count < 0) {
                    break;
                }
                if (count > 0) {
                    out.write(buffer, 0, count);
                    total += count;
                }
            }
            return new String(out.toByteArray());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
