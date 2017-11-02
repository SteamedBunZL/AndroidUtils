package space.network.cleancloud.core.base;

import android.text.TextUtils;

import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import space.network.util.compress.EnDeCodeUtils;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/20 10:15
 * @copyright TCL-MIG
 */
public class BaseQueryDataEnDecode {

    /**
     *
      * @param respone
     * @param decodekey
     * @return
     */
    public static String getResultString(byte[] respone, byte[] decodekey) {
        if (null == respone)
            return null;

        EnDeCodeUtils.xorEncodeBytes(respone, 0, respone.length, decodekey);

        String strResult = null;

        try {
            strResult = new String(respone, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            strResult = null;
        }
        return strResult;
    }


    public static JsonResult checkResult(String strResult){
        JsonResult result = new JsonResult();
        result.code = "-1";
        if (TextUtils.isEmpty(strResult)){
            return result;
        }
        JSONObject jsonObject;
        try{
            jsonObject = new JSONObject(strResult);
            if (jsonObject.has("code")){
                result.code = jsonObject.getString("code");
            }
            if (jsonObject.has("msg")){
                result.msg = jsonObject.getString("msg");
            }
            //success
            if ("0".equals(result.code)){
                if (jsonObject.has("data")){
                    result.data = (JSONObject) jsonObject.get("data");
                }
                return result;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    public static class JsonResult{
        public String  code;
        public String msg;
        public JSONObject data;
    }




    public static byte[] encrypt(String key, byte[] origData) {
        try {
            byte[] keyBytes = key.getBytes();
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(origData);
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt(String key,byte[] crypted) {
        try {
            byte[] keyBytes = key.getBytes();
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(crypted);
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
