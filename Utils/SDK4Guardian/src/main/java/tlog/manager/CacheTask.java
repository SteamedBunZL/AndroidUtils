package tlog.manager;



import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import tlog.IReportCallback;
import tlog.PostTaskProvider;

/**
 * Created by hui.zhu on 2016/5/3.
 */

public class CacheTask extends PostTaskProvider {
    private final static String TAG = "CacheTask";
    String tmpPath;


    public CacheTask(IReportCallback callback, String appKey,String defautStr) {
        super(callback, appKey,defautStr);
    }

    public void setTemPath(String path){
        this.tmpPath = path;
    }

    @Override
    public String getURL() {
        return UrlConfig.getAbsoluteURI();   // TODO 离线数据可以令用api
    }


    @Override
    public Map<String, byte[]> getPostEntities() {
        Map<String, byte[]> dataMap = new HashMap<String, byte[]>();
        FileInputStream is = null;
        try {
            is = new FileInputStream(tmpPath);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            //os.write(JSON_HEAD.getBytes());
            byte[] buf = new byte[1024];
            int lastbyte = -256;
            int byteread = -1;
            while ((byteread = is.read(buf)) != -1) {
                if (lastbyte != -256){
                    os.write((byte)lastbyte);
                }
                lastbyte = buf[byteread - 1];
                os.write(buf, 0, byteread - 1);
            }
            //os.write(JSON_END.getBytes());
            byte[] data = GZipUtils.compress(os.toByteArray());
            if(data !=null){
                dataMap.put("behaviors", data);
                return dataMap;
            }

        } catch (Exception e) {
            return null;
        }finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                }
        }
        return null;
    }
}