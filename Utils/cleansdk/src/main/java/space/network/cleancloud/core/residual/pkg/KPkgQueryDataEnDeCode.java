//=============================================================================
/**
 * @file KPreInstallPkgQueryDataEnDeCode.java
 * @brief
 */
//=============================================================================
package space.network.cleancloud.core.residual.pkg;

import android.text.TextUtils;

import com.hawkclean.framework.log.NLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.zip.CRC32;

import space.network.cleancloud.KResidualCloudQuery;
import space.network.cleancloud.KResidualCloudQuery.PkgQueryData;
import space.network.cleancloud.KResidualCloudQuery.PkgQueryResult;
import space.network.cleancloud.core.base.BaseQueryDataEnDecode;
import space.network.cleancloud.core.residual.KResidualCommonData;
import space.network.cleancloud.core.residual.dir.KDirQueryDataEnDeCode;
import space.network.util.ConvertUtil;
import space.network.util.compress.EnDeCodeUtils;
import space.network.util.net.KJsonUtils;


public class KPkgQueryDataEnDeCode extends BaseQueryDataEnDecode {
    /*
    协议格式 http://beha.cloud.duba.net/aps
        PostHead：请求的协议头信息，任何请求都需要有该信息
        PostBody：请求的内容，业务不同，请求内容不一样

        PostHead：type PostHead struct {
            dataSize  uint16
            crc      uint32
            channelId  uint16
            version   uint32
            languageCode  byte[6]
            uuid      byte[16]
        }

        PostBody：type PostBody struct {
        count byte
        packageMd51 byte[16]
        packageMd52 byte[16]
        ......
        }

        参数说明
        dataSize：PostHead+PostBody总大小
        crc：用来校验数据的crc信息，crc(channelId字节+xor(version字节+language字节+uuid字节+PostBody字节,channelKey)+channelKey)
        version：客户端的版本号
        uuid：客户端的32位字符串uuid值转换成16位
        languageCode：6字符语言代码，如zh-CN、syr-SY，少于6字符末尾填充空格
        count：查询目录数
        packageMd51：包1的Md5
        packageMd52：包2的Md5
        从version开始，后面所有的内容传上来的都是异或过的内容

        返回值
        xor(result, ResponseKey)
        返回值异或后是json格式，如果客户端获取到不是json格式，则可以认为服务端有问题
        错误的返回：{‘e’: ‘错误描述’}
        正确的返回：{"s":[
        {"i":123,"d":"[\”aaaaaaaaaaaaaaaaaaa+bbbbbbbbbbbbbbbbb+abc/aaa\”, \”aaaaaaaaaaaaaaaaaaa+bbbbbbbbbbbbbbbbb+abc/bbb\”]"},
         {"i":124,"d":"[\”aaaaaaaaaaaaaaaaaaa+bbbbbbbbbbbbbbbbb+abc/aaa\”, \”aaaaaaaaaaaaaaaaaaa+bbbbbbbbbbbbbbbbb+abc/bbb\”]"}]}
        服务器异常：解析json会失败

        备注
        服务端分配给不同产品不同的查询渠道信息，每个渠道号有不同的channelKey
        比如：
        channelId		1002
        channelKey	BcpjBhC*8kZ&=0Oo
        ResponseKey	%^ZHGrLSqV=ZLWv
        i: packageId
        d: 目录列表
        */
    private static final String TAG = KDirQueryDataEnDeCode.class.getSimpleName();

    public static final int QUERY_POST_DATA_HEAD_SIZE =
            2 + //dataSize  uint16
                    4 + //crc       uint32
                    2 + //channelId uint16
                    4 + //version   uint32
                    6 + //languageCode  byte[6]
                    16; //uuid      byte[16]
    //total 34

    public static final int DATA_BODY_ITEM_SIZE = 16;//md5

    public static byte[] getPostData(
            Collection<PkgQueryData> datas,
            short channelId,
            int version,
            byte[] lang,
            byte[] uuid,
            byte[] encodekey) {
        if (      null == datas || datas.isEmpty()
                || null == lang || lang.length < 6
                || null == uuid || uuid.length < 16
                || null == encodekey || encodekey.length < 1)
            return null;

        int count =  datas.size();
        if (count > 255)
            return null;

        short datasize = (short)(QUERY_POST_DATA_HEAD_SIZE + 1 + DATA_BODY_ITEM_SIZE * count);
        byte[] data = new byte[datasize];
        EnDeCodeUtils.copyShortToBytes(datasize, data, 0);
        //skip crc value offset 2 ,length 4 :
        EnDeCodeUtils.copyShortToBytes(channelId, data, 6);
        EnDeCodeUtils.copyIntToBytes(version, data, 8);
        System.arraycopy(lang, 0,                 data, 12, 6);
        System.arraycopy(uuid, 0,                 data, 18, 16);
        data[QUERY_POST_DATA_HEAD_SIZE] = (byte)(count);
        int md5pos = QUERY_POST_DATA_HEAD_SIZE + 1;
        String dirNameMd5;
        for (PkgQueryData item : datas) {
            dirNameMd5 = ((KResidualCommonData.PkgQueryInnerData)item.mInnerData).mPkgNameMd5;
            EnDeCodeUtils.copyHexStringtoBytes(dirNameMd5, data, md5pos, 16);
            md5pos += 16;
        }

        EnDeCodeUtils.xorEncodeBytes(
                data,
                8, //begin from section 'version'
                data.length - 8,
                encodekey);

        CRC32 crc32 = new CRC32();
        crc32.update(
                data,
                6, //begin from section 'channelId'
                data.length - 6);

        crc32.update(encodekey);
        int crcvalue = (int)crc32.getValue();
        EnDeCodeUtils.copyIntToBytes(crcvalue, data, 2);

        //在之前的加密基础上再做一层AES加密
       // data = encrypt(KCleanCloudEnv.DEFAULT_CHANNEL_KEY_AES,data );
        return data;
    }

    public static class PkgBatchQuryResult {
        public int mErrorCode;       ///< 0成功， 其他失败
        public String mErrorMsg;     ///< 服务段返回的错误码，现在没用先不开放
        Collection<PkgQueryResult> mResults;///< 结果列表

        @Override
        public String toString() {
            return "PkgBatchQuryResult{" +
                    "mErrorCode=" + mErrorCode +
                    ", mErrorMsg='" + mErrorMsg + '\'' +
                    ", mResults=" + mResults +
                    '}';
        }
    }

    public static String getResultString(byte[] respone, byte[] decodekey) {
        if (null == respone)
            return null;

        EnDeCodeUtils.xorEncodeBytes(
                respone,
                0,
                respone.length,
                decodekey);

        String strResult = null;

        try {
            strResult = new String(respone, "utf-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            strResult = null;
        }
        return strResult;
    }




    public static PkgBatchQuryResult getResultDataFromJsonString(String strResult) {
        PkgBatchQuryResult result = new PkgBatchQuryResult();
        result.mErrorCode = -1;
        if (TextUtils.isEmpty(strResult))
            return result;

        JSONObject jsonObject = null;
        ArrayList<PkgQueryResult> queryResults;

        try {
            jsonObject = new JSONObject(strResult);
            if (jsonObject.has("e")) {
                result.mErrorCode = -2;
                result.mErrorMsg = jsonObject.getString("e");
            } else if (jsonObject.has("s")) {
                JSONArray array = jsonObject.getJSONArray("s");
                int arraylen;
                if (array != null) {
                    arraylen = array.length();
                    queryResults = new ArrayList<>(arraylen);
                    for (int i = 0; i < arraylen; ++i) {

                        PkgQueryResult queryResult = new PkgQueryResult();
                        JSONObject jsonResult = array.getJSONObject(i);

                        if (jsonResult.has("i")) {
                            queryResult.mSignId      = jsonResult.getInt("i");
                        }

                        ArrayList<String> dirs = null;
                        if (jsonResult.has("d")) {
                            dirs = KJsonUtils.getStringArrayFromJsonArrayString(jsonResult.getString("d"));

                            if (dirs != null && !dirs.isEmpty()) {
                                queryResult.mPkgQueryDirItems = new ArrayList<>(dirs.size());
                                for (String dir : dirs) {
                                    KResidualCloudQuery.PkgQueryDirItem pkgQueryDirItem = new KResidualCloudQuery.PkgQueryDirItem();
                                    pkgQueryDirItem.mDirString = dir;
                                    queryResult.mPkgQueryDirItems.add(pkgQueryDirItem);
                                }
                            }
                        }

                        if (null == dirs || dirs.isEmpty()) {
                            queryResult.mQueryResult = KResidualCloudQuery.PkgResultType.NOT_FOUND;
                        } else {
                            queryResult.mQueryResult = KResidualCloudQuery.PkgResultType.DIR_LIST;
                        }

                        queryResults.add(queryResult);
                    }
                    result.mResults = queryResults;
                    result.mErrorCode = 0;
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return result;
        }
        return result;
    }
    public static PkgBatchQuryResult getResultDataFromJsonString_V2(String strResult) {
        PkgBatchQuryResult result = new PkgBatchQuryResult();
        result.mErrorCode = -1;

        if (TextUtils.isEmpty(strResult))
            return result;
        JSONObject jsonObject = null;
        ArrayList<PkgQueryResult> queryResults;

        try {
            JsonResult tempjsonResult = checkResult(strResult);
            result.mErrorCode = ConvertUtil.toInt(tempjsonResult.code, -1);
            result.mErrorMsg = tempjsonResult.msg;
            if (tempjsonResult != null && result.mErrorCode == 0){
                jsonObject = tempjsonResult.data;
                if (jsonObject.has("e")) {
                    result.mErrorCode = -2;
                    result.mErrorMsg = jsonObject.getString("e");
                } else if (jsonObject.has("s")) {
                    JSONArray array = jsonObject.getJSONArray("s");
                    int arraylen;
                    if (array != null) {
                        arraylen = array.length();
                        queryResults = new ArrayList<>(arraylen);
                        for (int i = 0; i < arraylen; ++i) {

                            PkgQueryResult queryResult = new PkgQueryResult();
                            JSONObject jsonResult = array.getJSONObject(i);

                            if (jsonResult.has("i")) {
                                queryResult.mSignId      = jsonResult.getInt("i");
                            }

                            ArrayList<String> dirs = null;
                            if (jsonResult.has("d")) {
                                dirs = KJsonUtils.getStringArrayFromJsonArrayString(jsonResult.getString("d"));

                                if (dirs != null && !dirs.isEmpty()) {
                                    queryResult.mPkgQueryDirItems = new ArrayList<>(dirs.size());
                                    for (String dir : dirs) {
                                        KResidualCloudQuery.PkgQueryDirItem pkgQueryDirItem = new KResidualCloudQuery.PkgQueryDirItem();
                                        pkgQueryDirItem.mDirString = dir;
                                        queryResult.mPkgQueryDirItems.add(pkgQueryDirItem);
                                    }
                                }
                            }

                            if (null == dirs || dirs.isEmpty()) {
                                queryResult.mQueryResult = KResidualCloudQuery.PkgResultType.NOT_FOUND;
                            } else {
                                queryResult.mQueryResult = KResidualCloudQuery.PkgResultType.DIR_LIST;
                            }

                            queryResults.add(queryResult);
                        }
                        result.mResults = queryResults;
                        result.mErrorCode = 0;
                    }
            }


            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return result;
        }
        return result;
    }

    public static boolean setResultToQueryData(PkgBatchQuryResult result, Collection<PkgQueryData> datas) {
        if (null == datas)
            return false;

        Iterator<PkgQueryResult> iter = null;
        if (result.mResults != null) {
            iter = result.mResults.iterator();
        }
        for (PkgQueryData data : datas) {
            if (null == result) {
                data.mErrorCode = -100;
            }else if (null == result.mResults){
                if (result.mErrorCode != 0) {
                    data.mErrorCode = result.mErrorCode;
                }
                else {
                    data.mErrorCode = -101;
                }
            } else if (iter != null && iter.hasNext()) {
                setPkgQueryResult(data, iter.next());
            } else {
                data.mErrorCode = -110;
            }
        }
        return true;
    }

    private static void setPkgQueryResult(PkgQueryData data, PkgQueryResult result) {
        data.mErrorCode = 0;
        data.mResultExpired = false;
        data.mResultSource  = KResidualCloudQuery.ResultSourceType.CLOUD;

        if (null == data.mResult) {
            data.mResult = result;
        } else {
            data.mResult.mQueryResult = result.mQueryResult;
            data.mResult.mSignId = result.mSignId;
            if (null == data.mResult.mPkgQueryDirItems) {
                data.mResult.mPkgQueryDirItems = result.mPkgQueryDirItems;
            } else if (result.mPkgQueryDirItems != null){
                data.mResult.mPkgQueryDirItems.addAll(result.mPkgQueryDirItems);
            }
        }
    }

    static public boolean decodeAndsetResultToQueryData(byte[] respone, byte[] decodekey, Collection<PkgQueryData> datas) {
        //服务端返回的数据新作了一次AES加密 需要先做AES界面 再解析byte数组
        if (respone == null || respone.length == 0){
            NLog.d(TAG, "AES解密数组为空 ");
            return false;
        }
        //服务端返回的数据新作了一次AES加密 需要先做AES界面 再解析byte数组
        //respone = decrypt(KCleanCloudEnv.DEFAULT_RESPONSE_KEY_AES, respone);

        String resultString = getResultString(respone, decodekey);
//        PkgBatchQuryResult result = getResultDataFromJsonString(resultString);
        PkgBatchQuryResult result = getResultDataFromJsonString_V2(resultString);
        return setResultToQueryData(result, datas);
    }
}