package com.tcl.security.cloudengine;


import com.steve.commonlib.DebugLog;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class CloudResponse {
    public String key;
    public int from;
    public int result;
    public String virusName;
    public String virusDescription;
    public String cloudCache;
    public JSONObject policy;
    public CloudRequest.MetaInfo metaInfo;

    static String returnTag = "h2PUtdLW0jX8//bT/V4=";
    static String resultsTag = "h2PUtdLRMtbW+f/zyvzs";
    static String keyTag = "h2M0sVL7//14/rU=";
    static String fromTag = "h2O01DUw+v/7wv5K";
    static String resultTag = "h2PUtdLRMtb+//bd/V8=";
    static String virusNameTag = "h2PUNNPV0gm0M7L6/+zT/EQ=";
    static String virusDescriptionTag = "h2PUNNPV0om20rHRNdPXNjMw/P/Gu/lB";
    static String policyTag = "h2PUNzA2s1H7//cE/W4=";
    static String cloudCacheTag = "h2O0MTbQso6xszG3+v/qF/wT";

    static HashMap<String, Integer> tagMap = new HashMap<String, Integer>();

    static void init() {
        toTags();
    }

    /**
     returnTag = return,
     resultTag = results,
     keyTag = key,
     fromTag = from,
     virusNameTag = virusName,
     virusDescriptionTag = virusDescription,
     policyTag = policy,
     cloudCacheTag = cloudCache,
     resultTag = result
     */
    private static void toTags() {
        returnTag = Utils.xde(returnTag);
        tagMap.put(returnTag, 1);
        resultsTag = Utils.xde(resultsTag);
        tagMap.put(resultsTag, 2);
        keyTag = Utils.xde(keyTag);
        tagMap.put(keyTag, 3);
        fromTag = Utils.xde(fromTag);
        tagMap.put(fromTag, 4);
        virusNameTag = Utils.xde(virusNameTag);
        tagMap.put(virusNameTag, 5);
        virusDescriptionTag = Utils.xde(virusDescriptionTag);
        tagMap.put(virusDescriptionTag, 6);
        policyTag = Utils.xde(policyTag);
        tagMap.put(policyTag, 7);
        cloudCacheTag = Utils.xde(cloudCacheTag);
        tagMap.put(cloudCacheTag, 8);
        resultTag = Utils.xde(resultTag);
        tagMap.put(resultTag, 9);
        DebugLog.w("------------CloudResponse 解码-------------");
        DebugLog.d("returnTag = %s,\nresultTag = %s,\nkeyTag = %s,\nfromTag = %s,\nvirusNameTag = %s,\nvirusDescriptionTag = %s,\npolicyTag = %s,\ncloudCacheTag = %s,\nresultTag = %s\n",
                returnTag,resultsTag,keyTag,fromTag,virusNameTag,virusDescriptionTag,policyTag,cloudCacheTag,resultTag);
    }

    public CloudResponse(String key, int from, int result, String virusName, String virusDescription, String cloudCache, JSONObject policy) {
        this.key = key;
        this.from = from;
        this.result = result;
        this.virusName = virusName;
        this.virusDescription = virusDescription;
        this.cloudCache = cloudCache;
        this.policy = policy;
    }

    public static CloudResponse getOneUnknown(String key) {
        return new CloudResponse(key, 1, -1, null, null, null, null);
    }

    public static CloudResponse dupResponseWithKey(String key, CloudResponse rsp) {
        return new CloudResponse(key, rsp.from, rsp.result, rsp.virusName, rsp.virusDescription, rsp.cloudCache, rsp.policy);
    }

    @Override
    public String toString() {
        if (ProjectEnv.bDebug) {
            StringBuilder builder = new StringBuilder(256);
            builder.append("key:");
            builder.append(key);
            builder.append("|");
            builder.append("from:");
            builder.append(from);
            builder.append("|");
            builder.append("result:");
            builder.append(result);
            builder.append("|");
            builder.append("virusName:");
            builder.append(virusName);
            builder.append("|");
            builder.append("virusDescription:");
            builder.append(virusDescription);
            builder.append("|");
            builder.append("cloudCache:");
            builder.append(cloudCache);
            builder.append("|");
            builder.append("metoinfo:");
            builder.append((metaInfo != null) ? metaInfo.toString() : "null");
            builder.append("|");
            builder.append("policy:\n");
            builder.append((policy != null) ? policy.toString() : "null");
            return builder.toString();
        } else {
            return super.toString();
        }
    }

    public static class ResponseStream {
        private InputStream stream;

        public ResponseStream(InputStream stream) {
            this.stream = stream;
        }

        public int read() throws IOException {
            return stream.read();
        }

        public int read(byte[] buffer) throws IOException {
            return read(buffer, 0, buffer.length);
        }

        public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            return stream.read(buffer, byteOffset, byteCount);
        }

        public void close() throws IOException {
            stream.close();
        }
    }

    public static class ReturnCode {
        int code;
        String msg;
        static final String codeTag = "code";
        static final String msgTag = "text";
    }

}