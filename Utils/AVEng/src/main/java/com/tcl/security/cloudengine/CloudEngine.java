package com.tcl.security.cloudengine;


import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.steve.commonlib.DebugLog;
import com.tcl.security.daemon.DaemonStartup;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CloudEngine {
    private static final String TAG = ProjectEnv.bDebug ? "CloudEngine" : CloudEngine.class.getSimpleName();
    private static volatile int inited = -1;
    private static final String MEDIA_JSON = "application/json; charset=utf-8";
    private static final String MEDIA_STREAM = "application/octet-stream; charset=utf-8";
    private static String keyPwd = "h2PMy83Jzsr8//vR/sk=";
    private static String keystore = "h2O0sdLV/v/76P5Q";
    private static String keyType = "h2OMDfH5//5N/x4=";
    private static String accessKey = "h2O0s7Gx0tFxMLFS+//p5Pvk";
    private static String pkgName = "h2PUN7FwMLQzsvr/8Sv8vA==";
    private static final String dumpDir = "/sdcard/"; // for debug
    private static final String dataReqFile = "req.txt"; // for debug
    private static final String dataRspFile = "rsp.txt"; // for debug
    private Error error;
    private static int errorBase = 10000;
    private Error errorIOException = new Error(601, "network IO error.");
    private Error errorFatal = new Error(902, "network unreachable.");
    private Error errorInvalidResponse = new Error(701, "invalid response data");
    private Error errorFromServer = new Error(801, "server inner error.");
    private Error errorLogic = new Error(901, "logic error.");
    private Error errorUnknown = new Error(1001, "unknown error.");
    private static Map<String, String> gzipHeader = new HashMap<String, String>();
    private static Map<String, String> cacheHeaders = new HashMap<String, String>();
    private Context c;
    private static Object okClientLock = new Object();
    private static OkHttpClient okhttp;
    private ArrayList<RttProfile> rttList = new ArrayList<RttProfile>();
    private static WeakReference<CallbackInit> cbInit;
    private long tsEnter;//快扫云查开始时间
    private long tsHttpEnd;
    private long rttBefore;
    private long rttAfter;

    static {
        toPlain();
        gzipHeader.put("Content-Encoding", "gzip");//gzipHead map
        cacheHeaders.put("Pragma", "no-cache");
        cacheHeaders.put("Cache-Control", "no-cache");
    }

    public enum HttpMethod {
        GET,
        POST
    }

    private CloudEngine(Context c) {
        this.c = c.getApplicationContext();
    }

    private static final Runnable runInit = new Runnable() {
        @Override
        public void run() {
            CloudRequest.init();
            CloudRequest.MetaInfo.init();
            CloudResponse.init();

            //DaemonStartup.start(App.c); 和云引擎没啥关系，好像拉活用到

            if (cbInit != null) {
                inited = 2;
                CallbackInit callbackInit = cbInit.get();
                if (callbackInit != null) {
                    inited = 0;
                    callbackInit.getResult(inited);
                }
            } else {
                inited = 0;
            }
            if (ProjectEnv.bDebug) {
                Log.i(TAG, "inited result:" + inited);
            }
        }
    };

    public interface CallbackInit {
        void getResult(int result);
    }

    /**
     * 初始化，解码一些key和包名
     *
     keyPwd = 123456,
     keystore = cert,
     keyType = BKS,
     accessKey = access_key,
     pkgName = pkg_name
     */
    private static void toPlain() {
        keyPwd = Utils.xde(keyPwd);//得到keypwd TODO 这个是多少？
        keystore = Utils.xde(keystore);
        keyType = Utils.xde(keyType);
        accessKey = Utils.xde(accessKey);
        pkgName = Utils.xde(pkgName);
        DebugLog.w("------------CloudEngine 解码-------------");
        DebugLog.d("keyPwd = %s,\nkeystore = %s,\nkeyType = %s,\naccessKey = %s,\npkgName = %s\n",
                keyPwd,keystore,keyType,accessKey,pkgName);
    }

    /**
     * 初始化调用
     * @param c
     * @param accessKey
     * @param callback
     */
    public static void init(Context c, String accessKey, CallbackInit callback) {
        if (inited >= 0) {//值判断，避免重复初始化
            if (ProjectEnv.bDebug) {
                Log.w(TAG, "already inited!");
            }
            if (callback != null) {
                callback.getResult(inited);
            }
            return;
        }
        inited = 1;
        if (callback != null) {
            cbInit = new WeakReference<CallbackInit>(callback);
        }
        App.init(c, accessKey);//记录传入的包名和accessKey
        new Thread(runInit).start();
    }

    public static CloudEngine getInstance(Context c) {
        if (inited < 0) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "Please init first.");
            }
            return null;
        }

        CloudEngine obj = new CloudEngine(c);
        if (obj.extractKeystore() != 0) {
            return null;
        }
        try {
            obj.setHttpsIfNecessary();
        } catch (Exception e) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "config http(s) error.");
            }
            return null;
        }
        obj.resetError();
        return obj;
    }

    public static class Error {
        public int code;
        public String msg;

        public Error(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }

    private void resetError() {
        if (error != null) {
            error.code = 0;
            error.msg = "";
        } else {
            error = new Error(0, "");
        }
    }

    private void setError(Error e) {
        error.code = e.code;
        error.msg = e.msg;
    }

    private void updateErrorMsg(Error e, String msg) {
        e.msg = msg;
    }

    public Error getError() {
        if (error.code != 0) {
            return new Error(error.code + errorBase, error.msg);
        }
        return new Error(0, "");
    }

    public boolean isFatalError() {
        if (error.code == errorFatal.code) {
            return true;
        }
        return false;
    }

    private void serializeOneQuery(JsonWriter writer, CloudRequest.MetaInfo info) throws IOException {
        writer.beginObject();
        writer.name(CloudRequest.MetaInfo.keyTag).value(info.key);
        writer.name(CloudRequest.MetaInfo.pkgNameTag).value(info.pkgName);
        writer.name(CloudRequest.MetaInfo.versionCodeTag).value(info.versionCode);
        writer.name(CloudRequest.MetaInfo.versionNameTag).value(info.versionName);
        writer.name(CloudRequest.MetaInfo.sigNameTag).value(info.sigName);
        writer.name(CloudRequest.MetaInfo.sigHashTag).value(info.sigHash);
        writer.name(CloudRequest.MetaInfo.apkHashTag).value(info.apkHash);
        writer.name(CloudRequest.MetaInfo.apkSizeTag).value(info.apkSize);
        writer.endObject();
    }

    /**
     *{
     *  apkList[
     *              {
     *                  key = "",
     *                  pkgName = "",
     *                  versionCode = "",
     *                  versionName = "",
     *                  ....
     *
     *              },
     *
     *              {
     *                  key = "",
     *                  pkgName = "",
     *                  ...
     *              }
     *              ...
     *  ]
     *}
     * @param infos
     * @param extra
     * @return
     */
    private ByteArrayOutputStream serializeQuery(List<CloudRequest.MetaInfo> infos, Map<String, String> extra) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
        try {
            JsonWriter writer = new JsonWriter(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")));
            if (ProjectEnv.bDebug) {
                writer.setIndent("  ");
            }
            writer.beginObject();
            writer.name(CloudRequest.apkListTag);
            writer.beginArray();
            for (CloudRequest.MetaInfo info : infos) {
                serializeOneQuery(writer, info);
            }
            writer.endArray();
            if (ProjectEnv.bQueryOOB) {
                writer.name(CloudRequest.basicTag);
                writer.beginObject();
                DeviceInfo.write(c, writer);
                writer.endObject();
            }
            if (extra != null) {
                writer.name(CloudRequest.extraTag);
                writer.beginObject();
                Set<Map.Entry<String, String>> set = extra.entrySet();
                for (Map.Entry<String, String> s : set) {
                    String k = s.getKey();
                    String v = s.getValue();
                    writer.name(k).value(v);
                }
                writer.endObject();
            }
            writer.endObject();
            writer.close();
        } catch (Exception e) {
            updateErrorMsg(errorLogic, "serialize error.");
            setError(errorLogic);
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "serialize error:\n");
                e.printStackTrace();
            }
            return null;
        }

        return out;
    }

    // write to local file before http request.
    private void dumpQuery(ByteArrayOutputStream out) {
        try {
            OutputStream file = new BufferedOutputStream(new FileOutputStream(new File(dumpDir, dataReqFile)));
            file.write(out.toByteArray());
            file.flush();
            file.close();
        } catch (Exception e) {
            try {
                String s = new String(out.toByteArray(), "utf-8");
                Log.i(TAG, "----------------query data begin---------------------");
                Log.i(TAG, s);
                Log.i(TAG, "----------------query data end-----------------------");
            } catch (Exception e2) {

            }
        }
    }

    // write response body to local file.
    private void dumpResponse(ByteArrayOutputStream out) {
        try {
            OutputStream file = new BufferedOutputStream(new FileOutputStream(new File(dumpDir, dataRspFile)));
            file.write(out.toByteArray());
            file.flush();
            file.close();
        } catch (Exception e) {
            try {
                String s = new String(out.toByteArray(), "utf-8");
                Log.i(TAG, "----------------rsp data begin---------------------");
                Log.i(TAG, s);
                Log.i(TAG, "----------------rsp data end-----------------------");
            } catch (Exception e2) {

            }
        }
    }

    // print response headers.
    private void printRspHeaders(Response response) {
        if (response.headers() != null) {
            Log.i(TAG, response.headers().toString());
        } else {
            Log.w(TAG, "response headers is null.");
        }
    }

    private void printRspContentType(MediaType mt) {
        Log.i(TAG, "Content-Type:" + mt.type() + "/" + mt.subtype() + "," + mt.charset());
    }

    private CloudResponse deserializeOneResponse(JsonReader reader) {
        String key = null;
        int from = -1;
        int result = -1;
        String virusName = null;
        String virusDescription = null;
        String policy = null;
        String cloudCache = null;
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                int index = CloudResponse.tagMap.get(name);
                switch (index) {
                    case 3: //CloudResponse.keyTag:
                        key = reader.nextString();
                        break;
                    case 4: //CloudResponse.fromTag:
                        from = reader.nextInt();
                        break;
                    case 9: //CloudResponse.resultTag:
                        result = reader.nextInt();
                        break;
                    case 5: //CloudResponse.virusNameTag:
                        virusName = reader.nextString();
                        break;
                    case 6: //CloudResponse.virusDescriptionTag:
//                        virusDescription = reader.skipAndGetValue();
                        reader.beginArray();
                        StringBuilder builder = new StringBuilder();
                        builder.append("[");
                        while (reader.hasNext()) {
                            int i = reader.nextInt();
                            builder.append(i);
                            builder.append(",");
                        }
                        virusDescription = builder.substring(0, builder.length()-1) + "]";
                        reader.endArray();
                        break;
                    case 7: //CloudResponse.policyTag:
//                        policy = reader.skipAndGetValue();
                        reader.skipValue();
                        break;
                    case 8: //CloudResponse.cloudCacheTag:
                        cloudCache = reader.nextString();
                        break;
                }
            }
            reader.endObject();
            JSONObject jobj = null;
            if (policy != null) {
                jobj = new JSONObject(policy);
            }
            return new CloudResponse(key, from, result, virusName, virusDescription, cloudCache, jobj);
        } catch (Exception e) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "deserialize one error:");
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 返回结果 json解析
     * @param inputStream
     * @return
     */
    private List<CloudResponse> deserializeResponse(CloudResponse.ResponseStream inputStream) {
        JsonReader reader = null;
        try {
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

            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            reader = new JsonReader(new BufferedReader(new InputStreamReader(in, "UTF-8")));
            reader.beginObject();
            String name = reader.nextName();
            if (CloudResponse.returnTag.equals(name)) {
                Gson g = new Gson();
                CloudResponse.ReturnCode returnCode = g.fromJson(reader, CloudResponse.ReturnCode.class);
                if (returnCode.code == 0) {
                    name = reader.nextName();
                    if (CloudResponse.resultsTag.equals(name)) {
                        List<CloudResponse> list = new ArrayList<CloudResponse>();
                        reader.beginArray();
                        while (reader.hasNext()) {
                            CloudResponse rsp = deserializeOneResponse(reader);
                            if (rsp != null) {
                                list.add(rsp);
                            }
                        }
                        reader.endArray();
                        if (list.size() == 0) {
                            setError(errorInvalidResponse);
                            return null;
                        }
                        return list;
                    } else {
                        setError(errorInvalidResponse);
                        if (ProjectEnv.bDebug) {
                            Log.e(TAG, "invalid data. expected:" + CloudResponse.resultsTag + " current:" + name);
                        }
                        return null;
                    }
                } else {
                    updateErrorMsg(errorFromServer, returnCode.msg);
                    setError(errorFromServer);
                    if (ProjectEnv.bDebug) {
                        Log.e(TAG, "server error code:" + returnCode.code + " " + returnCode.msg);
                    }
                    return null;
                }
            } else {
                setError(errorInvalidResponse);
                if (ProjectEnv.bDebug) {
                    Log.e(TAG, "invalid data. expected:" + CloudResponse.returnTag + " current:" + name);
                }
                return null;
            }
        } catch (Exception e) {
            updateErrorMsg(errorLogic, e.getMessage());
            setError(errorLogic);
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "deserialize error:\n");
                e.printStackTrace();
            }
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private static String getKeyPwd() {
        return keyPwd;
    }

    private static String getKeyType() {
        return keyType;
    }

    private KeyStore loadKeyStore() throws Exception {
        String pwd = getKeyPwd();//12345
        String type = getKeyType();//BKS
        String name = getKeystore();//cert
        final KeyStore keyStore = KeyStore.getInstance(type);
        final InputStream inputStream = new FileInputStream(new File(c.getFilesDir(), name));
        try {
            keyStore.load(inputStream, pwd.toCharArray());
            return keyStore;
        } catch (Exception e) {
            throw new Exception("");
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {

            }
        }
    }

    private void copyStream(InputStream source, OutputStream target) throws IOException {
        final int BUF_SIZE = 4096;
        byte[] buffer = new byte[BUF_SIZE];
        int length = 0;
        while ((length = source.read(buffer)) > 0) {
            target.write(buffer, 0, length);
        }
        target.flush();
    }

    private static String getKeystore() {
        return keystore;
    }

    private int extractKeystore() {
        String name = getKeystore();
        InputStream in = null;
        OutputStream out = null;
        File f = new File(c.getFilesDir(), name);
        if (!f.exists()) {
            try {
                in = c.getAssets().open(name);
                out = new BufferedOutputStream(new FileOutputStream(f));
                copyStream(in, out);
                return 0;
            } catch (Exception e) {
                if (ProjectEnv.bDebug) {
                    Log.e(TAG, "extract error:");
                    e.printStackTrace();
                }
                return -1;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception e) {
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e) {
                    }
                }
            }
        } else {
            return 0;
        }
    }

    public void setTimeout(int t) {
        if (ProjectEnv.bUseOkhttp) {
//            okhttp.setTimeout(t, t, t);
        }
    }

    public void setTimeout(int t1, int t2, int t3) {
        if (ProjectEnv.bUseOkhttp) {
//            okhttp.setTimeout(t1, t2, t3);
        }
    }

    private void setHttpsIfNecessary() throws Exception {
        if (ProjectEnv.bUseOkhttp) {
            synchronized (okClientLock) {
                if ((okhttp == null) || ProjectEnv.encTypeReseted) {
                    if (ProjectEnv.encType == ProjectEnv.EncType.HTTPS) {
                        try {
                            if (ProjectEnv.bDebug) {
                                Log.i(TAG, "init okhttps.");
                            }
                            SSLContext sslContext = SSLContext.getInstance("TLS");
                            KeyStore keyStore = loadKeyStore();
                            sslContext.init(null, new TrustManager[]{new LocalTrustManager(keyStore)}, new SecureRandom());
                            okhttp = new OkHttpClient.Builder().sslSocketFactory(sslContext.getSocketFactory()).hostnameVerifier(new LocalHostnameVerifier()).build();
                        } catch (Exception e) {
                            try {
                                SSLContext sslContext = SSLContext.getInstance("TLS");
                                sslContext.init(null, new TrustManager[]{new LocalTrustManager(null)}, new SecureRandom());
                                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                                builder.sslSocketFactory(sslContext.getSocketFactory());
                                builder.hostnameVerifier(new LocalHostnameVerifier());
                                okhttp = builder.build();
                            } catch (Exception e2) {
                                if (ProjectEnv.bDebug) {
                                    Log.e(TAG, "https init error:");
                                    e2.printStackTrace();
                                }
                                throw new Exception("");
                            }
                        }
                    } else {
                        okhttp = new OkHttpClient();
                    }
                }
            }
        }
    }

    private void saveRtt(RttProfile rtt) {
        if (rttList.size() > ProjectEnv.rttCountMax) {
            rttList.remove(0);
        }
        rttList.add(rtt);
    }

    public List<RttProfile> getRtt() {
        ArrayList<RttProfile> list = new ArrayList<RttProfile>();
        for (RttProfile rtt : rttList) {
            RttProfile r = RttProfile.dup(rtt);
            list.add(r);
        }
        rttList.clear();
        return list;
    }

    /**
     * 其他函数调用，云查接口
     * @param url
     * @param data
     * @param headers
     * @param method
     * @return
     */
    public CloudResponse.ResponseStream visitWithAccessKey(String url, byte[] data, Map<String, String> headers, HttpMethod method) {
        if ((HttpMethod.GET != method && HttpMethod.POST != method)) {
            String msg = "invalid http method:" + method;
            if (ProjectEnv.bDebug) {
                Log.e(TAG, msg);
            }
            updateErrorMsg(errorLogic, msg);
            setError(errorLogic);
            return null;
        }
        RttProfile rtt = new RttProfile();
        if (ProjectEnv.bRttTrace) {
            rtt.itemType = RttProfile.TYPE_COMMON;
            rtt.itemCount = 0; // XXX:
        }
        if (HttpMethod.POST == method) {
            if (headers != null) {
                headers.putAll(gzipHeader);
            } else {
                headers = gzipHeader;
            }
        } else {
            if (headers != null) {
                headers.putAll(cacheHeaders);
            } else {
                headers = cacheHeaders;
            }
        }
        return visitHelper(url, data, rtt, headers, method, true);
    }

    public CloudResponse.ResponseStream visitWithoutAccessKey(String url, byte[] data, Map<String, String> headers, HttpMethod method) {
        if ((HttpMethod.GET != method && HttpMethod.POST != method)) {
            String msg = "invalid http method:" + method;
            if (ProjectEnv.bDebug) {
                Log.e(TAG, msg);
            }
            updateErrorMsg(errorLogic, msg);
            setError(errorLogic);
            return null;
        }
        RttProfile rtt = new RttProfile();
        if (ProjectEnv.bRttTrace) {
            rtt.itemType = RttProfile.TYPE_COMMON;
            rtt.itemCount = 0; // XXX:
        }
        if (HttpMethod.POST == method) {
            if (headers != null) {
                headers.putAll(gzipHeader);
            } else {
                headers = gzipHeader;
            }
        } else {
            if (headers != null) {
                headers.putAll(cacheHeaders);
            } else {
                headers = cacheHeaders;
            }
        }
        return visitHelper(url, data, rtt, headers, method, false);
    }

    private String makeTailForGet(String url) {
        if (url.indexOf("?") == -1) {
            return url + "?ts=" + System.currentTimeMillis();
        } else if (url.indexOf("#") == -1) {
            return url + "&ts=" + System.currentTimeMillis();
        }
        return url;
    }

    /**
     * 通过url进行拼接，组成服务器可较验的url
     * @param url
     * @return
     */
    private String appendUrlAccessKey(String url) {
        StringBuilder builder = new StringBuilder(128);
        builder.append(url);
        if (url.indexOf("?") == -1) {//没有？
            builder.append("?");
        } else {
            builder.append("&");
        }
        builder.append(accessKey);
        builder.append("=");
        try {
            builder.append(URLEncoder.encode(App.accessKey, "UTF-8"));
        } catch (Exception e) {
            if (ProjectEnv.bDebug) {
                e.printStackTrace();
            }
            return url;
        }
        builder.append("&");
        builder.append(pkgName);
        builder.append("=");
        try {
            builder.append(URLEncoder.encode(App.pkgName, "UTF-8"));
        } catch (Exception e) {
            if (ProjectEnv.bDebug) {
                e.printStackTrace();
            }
            return url;
        }
        return builder.toString();
    }

    /**
     * 实际网络请求？帮助类
     * @param url
     * @param data
     * @param rtt
     * @param headers
     * @param method
     * @param withAccessKey
     * @return
     */
    private CloudResponse.ResponseStream visitHelper(String url, byte[] data, RttProfile rtt, Map<String, String> headers, HttpMethod method, boolean withAccessKey) {
        resetError();
        if (ProjectEnv.bUseOkhttp) {
            boolean usePost = true;
            if (HttpMethod.POST != method) {
                usePost = false;
            }
            //okhttp3 header builder类
            Request.Builder builder = new Request.Builder();
            boolean withGzipHeaders = false;
            if (headers != null) {
                Set<Map.Entry<String, String>> set = headers.entrySet();
                for (Map.Entry<String, String> item : set) {
                    String key = item.getKey();
                    String value = item.getValue();
                    if ("Content-Encoding".equalsIgnoreCase(key) && "gzip".equalsIgnoreCase(value)) {
                        if (ProjectEnv.bUseGzip && usePost) {
                            withGzipHeaders = true;
                            builder.addHeader(key, value);
                        } else {
                            withGzipHeaders = false;
                        }
                    }
                }
            }

            if (withGzipHeaders) {//使用gzip压缩
                try {
                    ByteArrayOutputStream source = new ByteArrayOutputStream();
                    source.write(data);
                    ByteArrayOutputStream target = new ByteArrayOutputStream();
                    GZIPOutputStream gzStream = new GZIPOutputStream(target);
                    source.writeTo(gzStream);
                    gzStream.finish();
                    data = target.toByteArray();
                } catch (Exception e) {
                    if (ProjectEnv.bDebug) {
                        e.printStackTrace();
                    }
                    updateErrorMsg(errorLogic, e.getMessage());
                    setError(errorLogic);
                    return null;
                }
            }

            if (ProjectEnv.bUseAccessKey && withAccessKey) {//使用accesskey加密后的url
                url = appendUrlAccessKey(url);
            }

            if (ProjectEnv.bDebug) {
                Log.i(TAG, "okhttp visit:" + url + " method:" + method + " gzip:" + withGzipHeaders);
            }
            RequestBody reqBody = null;
            if (usePost) {//使用post
                MediaType mt = MediaType.parse(MEDIA_STREAM);
                reqBody = RequestBody.create(mt, data);
            } else {
                url = makeTailForGet(url);
            }
            Request request = builder.url(url).method(method.toString(), reqBody).build();
            if (ProjectEnv.bRttTrace) {
                rtt.reqSize = (data != null) ? data.length : 0; // not include headers.
                rtt.netType = NetworkUtils.getConnectionType(c);
                rtt.rtt = System.currentTimeMillis();
                rtt.rspSize = -1;
                rttBefore = rtt.rtt - tsEnter;
            }

            CloudResponse.ResponseStream rstream = null;
            int rspLen = 0;
            try {
                Response response = okhttp.newCall(request).execute();//执行同步的请求
                if (response.isSuccessful()) {//请求成功
                    ResponseBody rspBody = response.body();
                    long len = rspBody.contentLength();
                    try {
                        rspLen = (int) len + response.headers().toString().getBytes("UTF-8").length;
                    } catch (Exception e) {
                    }
                    if (ProjectEnv.bDebug) {
                        printRspHeaders(response);
                    }
                    //把ResponseBody转成输入流 传入CloudResponse里
                    rstream = new CloudResponse.ResponseStream(rspBody.byteStream());
                } else {
                    error.code = response.code();//拿到错误信息code
                    error.msg = response.message();//拿到错误信息message
                    if (ProjectEnv.bDebug) {
                        Log.e(TAG, "okhttp error code:" + error.code + " msg:" + error.msg);
                    }
                }
            } catch (MalformedURLException e) {
                updateErrorMsg(errorFatal, e.getMessage());
                setError(errorFatal);
                if (ProjectEnv.bDebug) {
                    Log.e(TAG, "okhttp malformed URL:\n");
                    e.printStackTrace();
                }
            } catch (ProtocolException e) {
                updateErrorMsg(errorFatal, e.getMessage());
                setError(errorFatal);
                if (ProjectEnv.bDebug) {
                    Log.e(TAG, "okhttp protocol exception:\n");
                    e.printStackTrace();
                }
            } catch (SocketException e) {
                updateErrorMsg(errorFatal, e.getMessage());
                setError(errorFatal);
                if (ProjectEnv.bDebug) {
                    Log.e(TAG, "okhttp socket exception:\n");
                    e.printStackTrace();
                }
            } catch (UnknownHostException e) {
                updateErrorMsg(errorFatal, e.getMessage());
                setError(errorFatal);
                if (ProjectEnv.bDebug) {
                    Log.e(TAG, "okhttp unknown host:\n");
                    e.printStackTrace();
                }
            } catch (UnknownServiceException e) {
                updateErrorMsg(errorFatal, e.getMessage());
                setError(errorFatal);
                if (ProjectEnv.bDebug) {
                    Log.e(TAG, "okhttp unknown service:\n");
                    e.printStackTrace();
                }
            } catch (SecurityException e) {
                updateErrorMsg(errorFatal, e.getMessage());
                setError(errorFatal);
                if (ProjectEnv.bDebug) {
                    Log.e(TAG, "okhttp security exception:\n");
                    e.printStackTrace();
                }
            } catch (IOException e) {
                updateErrorMsg(errorIOException, e.getMessage());
                setError(errorIOException);
                if (ProjectEnv.bDebug) {
                    Log.e(TAG, "okhttp IO exception:\n");
                    e.printStackTrace();
                }
            } catch (Exception e) {
                updateErrorMsg(errorUnknown, e.getMessage());
                setError(errorUnknown);
                if (ProjectEnv.bDebug) {
                    Log.e(TAG, "okhttp unknown exception:\n");
                    e.printStackTrace();
                }
            }
            if (ProjectEnv.bRttTrace) {
                tsHttpEnd = System.currentTimeMillis();
                rtt.rtt = tsHttpEnd - rtt.rtt;
                rtt.rspSize = rspLen;
                saveRtt(rtt);
            }
            return rstream;
        }
        updateErrorMsg(errorLogic, "unsupported http client.");
        setError(errorLogic);
        return null;
    }

    /**
     * metainfo 得到唯一  apkhash - list info.key 这里为啥是hash 对应List ??
     * 本质啊，是为了更新map，重新添加
     * @param infos
     * @return
     */
    private Map<String, List<String>> uniqueApkHashList(List<CloudRequest.MetaInfo> infos) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        List<CloudRequest.MetaInfo> dup = new ArrayList<CloudRequest.MetaInfo>();
        for (CloudRequest.MetaInfo info : infos) {
            String apkHash = info.apkHash;
            if (apkHash != null) {
                List<String> value = map.get(apkHash);
                if (value == null) {
                    value = new ArrayList<String>();
                    value.add(info.key);
                    map.put(apkHash, value);
                } else {
                    value.add(info.key);
                    dup.add(info);
                }
            }
        }

        for (CloudRequest.MetaInfo info : dup) {
            infos.remove(info);
        }
        return map;
    }

    /**
     * 快扫函数
     * @param pkgs
     * @param url
     * @param extra
     * @return
     */
    public List<CloudResponse> queryPkgs(List<String> pkgs, String url, Map<String, String> extra) {
        if (ProjectEnv.bRttTrace) {
            tsEnter = System.currentTimeMillis();
        }
        List<CloudResponse> result = null;
        List<CloudRequest.MetaInfo> infos = new ArrayList<CloudRequest.MetaInfo>();
        Map<String, CloudRequest.MetaInfo> map1 = new HashMap<String, CloudRequest.MetaInfo>();// 包名-Metainfo
        Map<String, List<String>> map2;//由metainfo 得到  apkhash - info.key
        for (String pkg : pkgs) {//遍历传入的待查pkgs 逐一生成metainfo
            CloudRequest.MetaInfo info = CloudRequest.getMetaInfoForPkg(c, pkg);
            if (info != null) {
                infos.add(info);
                map1.put(pkg, info);
            } else {//为空的话，new一个
                map1.put(pkg, new CloudRequest.MetaInfo());
            }
        }
        if (infos.size() > 0) {
            map2 = uniqueApkHashList(infos);
            ByteArrayOutputStream out = serializeQuery(infos, extra);
            if (out != null) {
                RttProfile rtt = new RttProfile();
                if (ProjectEnv.bRttTrace) {
                    rtt.itemType = RttProfile.TYPE_QUERY;
                    rtt.itemCount = infos.size();
                }
                //实际触发请求   使用加密的accesskey 访问
                CloudResponse.ResponseStream inputStream = visitHelper(url, out.toByteArray(), rtt, gzipHeader, HttpMethod.POST, true);
                if (inputStream != null) {
                    List<CloudResponse> list = deserializeResponse(inputStream);
                    if (list != null) {
                        result = fillResponseResult(map1, map2, list);
                    }
                }
            }
        } else {
            result = getAllUnknown(pkgs);
        }

        if (ProjectEnv.bRttTrace) {
            rttAfter = System.currentTimeMillis() - tsHttpEnd;
            if (ProjectEnv.bDebug) {
                StringBuilder builder = new StringBuilder(64);
                builder.append("query pkgs. ");
                builder.append("before RTT:");
                builder.append(rttBefore);
                int size = rttList.size();
                if (size > 0) {
                    builder.append(" RTT:");
                    builder.append(rttList.get(size - 1).rtt);
                }
                builder.append(" after RTT:");
                builder.append(rttAfter);
                Log.d(TAG, builder.toString());
            }
        }
        return result;
    }

    private List<CloudResponse> getAllUnknown(List<String> keys) {
        List<CloudResponse> list = new ArrayList<CloudResponse>();
        for (String key : keys) {
            list.add(getOneUnknown(key));
        }
        return list;
    }

    private CloudResponse getOneUnknown(String key) {
        return CloudResponse.getOneUnknown(key);
    }

    private List<CloudResponse> fillResponseResult(Map<String, CloudRequest.MetaInfo> map1, Map<String, List<String>> map2, List<CloudResponse> list) {
		ArrayList<CloudResponse> dupAdd = new ArrayList<CloudResponse>();
		
        for (CloudResponse rsp : list) {
            rsp.metaInfo = map1.get(rsp.key);
            map1.remove(rsp.key);

            String apkHash = rsp.metaInfo.apkHash;
            if (apkHash != null) {
                List<String> keys = map2.get(apkHash);
                if (keys.size() > 1) {
                    for (String key : keys) {
                        if (!rsp.key.equals(key)) {
                            CloudResponse dup = CloudResponse.dupResponseWithKey(key, rsp);
                            dup.metaInfo = map1.get(key);
							dupAdd.add(dup);
                            map1.remove(key);
                        }
                    }
                }
            }
        }
		
		if (dupAdd.size() > 0) {
            list.addAll(dupAdd);
        }
        if (map1.size() > 0) {
            Set<String> set = map1.keySet();
            for (String key : set) {
                list.add(getOneUnknown(key));
            }
        }
        return list;
    }

    /**
     * 深扫函数
     * @param path
     * @param url
     * @param extra
     * @return
     */
    public List<CloudResponse> queryFiles(List<String> path, String url, Map<String, String> extra) {
        if (ProjectEnv.bRttTrace) {
            tsEnter = System.currentTimeMillis();
        }
        List<CloudResponse> result = null;
        List<CloudRequest.MetaInfo> infos = new ArrayList<CloudRequest.MetaInfo>();
        Map<String, CloudRequest.MetaInfo> map1 = new HashMap<String, CloudRequest.MetaInfo>();
        Map<String, List<String>> map2;
        for (String p : path) {
            File file = new File(p);
            if (file.exists() && file.isFile() && file.canRead()) {
                CloudRequest.MetaInfo info = CloudRequest.getMetaInfoForFile(file.getAbsolutePath());
                if (info != null) {
                    infos.add(info);
                    map1.put(p, info);
                } else {
                    map1.put(p, new CloudRequest.MetaInfo());
                }
            } else {
                map1.put(p, new CloudRequest.MetaInfo());
                if (ProjectEnv.bDebug) {
                    Log.e(TAG, "file deny from:" + file.getAbsolutePath());
                }
            }
        }
        if (infos.size() > 0) {
            map2 = uniqueApkHashList(infos);
            ByteArrayOutputStream out = serializeQuery(infos, extra);
            if (out != null) {
                RttProfile rtt = new RttProfile();
                if (ProjectEnv.bRttTrace) {
                    rtt.itemType = RttProfile.TYPE_QUERY;
                    rtt.itemCount = infos.size();
                }
                CloudResponse.ResponseStream inputStream = visitHelper(url, out.toByteArray(), rtt, gzipHeader, HttpMethod.POST, true);
                if (inputStream != null) {
                    List<CloudResponse> list = deserializeResponse(inputStream);
                    if (list != null) {
                        result = fillResponseResult(map1, map2, list);
                    }
                }
            }
        } else {
            result = getAllUnknown(path);
        }

        if (ProjectEnv.bRttTrace) {
            rttAfter = System.currentTimeMillis() - tsHttpEnd;
            if (ProjectEnv.bDebug) {
                StringBuilder builder = new StringBuilder(64);
                builder.append("query files. ");
                builder.append("before RTT:");
                builder.append(rttBefore);
                int size = rttList.size();
                if (size > 0) {
                    builder.append(" RTT:");
                    builder.append(rttList.get(size - 1).rtt);
                }
                builder.append(" after RTT:");
                builder.append(rttAfter);
                Log.d(TAG, builder.toString());
            }
        }
        return result;
    }

    private void serializeOneRecord(JsonWriter writer, CloudUpdate.RecordInfo record) throws IOException {
        writer.beginObject();
        writer.name(CloudUpdate.RecordInfo.typeTag).value(record.type);
        writer.name(CloudUpdate.RecordInfo.nameTag).value(record.name);
        writer.name(CloudUpdate.RecordInfo.levelTag).value(record.level);
        writer.name(CloudUpdate.RecordInfo.variantTag).value(record.variant);
        writer.name(CloudUpdate.RecordInfo.descriptionTag).value(record.description);
        writer.endObject();
    }

    private void serializeOneUpdate(JsonWriter writer, CloudUpdate update) throws IOException {
        writer.beginObject();
        writer.name(CloudUpdate.pkgInfoTag);
        serializeOneQuery(writer, update.pkgInfo);
        writer.name(CloudUpdate.engineTag).value(update.engine);
        writer.name(CloudUpdate.subengineTag).value(update.subengine);
        writer.name(CloudUpdate.recordTag);
        serializeOneRecord(writer, update.record);
        writer.endObject();
    }

    private ByteArrayOutputStream serializeUpdate(List<CloudUpdate> pkgs, Map<String, String> extra) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        try {
            JsonWriter writer = new JsonWriter(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")));
            if (ProjectEnv.bDebug) {
                writer.setIndent("  ");
            }
            writer.beginObject();
            writer.name(CloudRequest.apkListTag);
            writer.beginArray();
            for (CloudUpdate pkg : pkgs) {
                serializeOneUpdate(writer, pkg);
            }
            writer.endArray();
            if (ProjectEnv.bQueryOOB) {
                writer.name(CloudRequest.basicTag);
                writer.beginObject();
                DeviceInfo.write(c, writer);
                writer.endObject();
            }
            if (extra != null) {
                writer.name(CloudRequest.extraTag);
                writer.beginObject();
                Set<Map.Entry<String, String>> set = extra.entrySet();
                for (Map.Entry<String, String> s : set) {
                    String k = s.getKey();
                    String v = s.getValue();
                    writer.name(k).value(v);
                }
                writer.endObject();
            }
            writer.endObject();
            writer.close();
        } catch (Exception e) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "serialize update error:\n");
                e.printStackTrace();
            }
            return null;
        }

        return out;
    }

    private int getUpdateResult(CloudResponse.ResponseStream inputStream) {
        byte[] buffer = new byte[1024];
        int len = -1;
        try {
            while (true) {
                len = inputStream.read(buffer);
                if (len < 0) {
                    break;
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                inputStream.close();
            } catch (Exception e2) {
            }
        }
        return 0;
    }

    public int update(List<CloudUpdate> pkgs, String url, Map<String, String> extra) {
        if (pkgs.isEmpty()) {
            return 0;
        }

        ByteArrayOutputStream out = serializeUpdate(pkgs, extra);
        if (out != null) {
            RttProfile rtt = new RttProfile();
            if (ProjectEnv.bRttTrace) {
                rtt.itemType = RttProfile.TYPE_UPDATE;
                rtt.itemCount = pkgs.size();
            }
            CloudResponse.ResponseStream inputStream = visitHelper(url, out.toByteArray(), rtt, gzipHeader, HttpMethod.POST, true);
            if (inputStream != null) {
                return getUpdateResult(inputStream);
            } else {
                return -1;
            }
        }
        return 0;
    }


    public interface OnDownloadListener {
        void onFailure(String message);

        void onLoading(int percent);

        void onLoaded();
    }

    /**
     * 文件下载
     *
     * @param url  下载地址
     * @param file 下载后的文件名
     */
    public void downLoadFile(String url, final File file, final OnDownloadListener listener) {
        Request request = new Request.Builder().url(url).build();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(6,TimeUnit.SECONDS);
        builder.writeTimeout(6,TimeUnit.SECONDS);
        builder.build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (listener != null) {
                    listener.onFailure(e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    InputStream inputStream = response.body().byteStream();
                    long available = response.body().contentLength();
                    if(!file.getParentFile().exists()){
                        file.getParentFile().mkdir();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[2048];
                    int len = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                        final int percent = (int) (((float) file.length()) / available * 100);
                        if (listener != null) {
                            listener.onLoading(percent);
                        }
                    }
                    fileOutputStream.flush();
                    if (listener != null) {
                        listener.onLoaded();
                    }
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onFailure(e.getMessage());
                    }
                }

            }
        });
    }

}
