//=============================================================================
/**
 * @file KJsonUtils.java
 * @brief 用来放一些json相关的公共函敿 *
 */
//=============================================================================
package space.network.util.net;

import android.text.TextUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collection;

public class KJsonUtils {
//	public static ArrayList<String> getStringArrayFromJsonArray(JSONArray array) throws JSONException {
//		int arraylen = array.length();
//		if (0 == arraylen)
//			return null;
//
//		ArrayList<String> strArray = new ArrayList<String>(arraylen);
//		for (int i = 0; i < arraylen; ++i) {
//			strArray.add(array.getString(i));
//		}
//		return strArray;
//	}

    /**
     * 10536,11348
     * @param str
     * @return
     */
    public static ArrayList<String> getStringArrayFromArrayString(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }

        int len = str.length();
        char c;
        int bufCnt = 0;
        int bufMax = 0;
        int resultCnt = 0;
        for (int i = 0; i < len; ++i) {
            c = str.charAt(i);
            switch (c) {
                case ',' :
                    if (bufCnt > bufMax)
                        bufMax = bufCnt;

                    bufCnt = 0;
                    ++resultCnt;
                    break;
                default :
                    ++bufCnt;
                    break;
            }
        }

        ++resultCnt;
        if (bufCnt > bufMax) {
            bufMax = bufCnt;
        }

        ArrayList<String> result = new ArrayList<>(resultCnt);
        StringBuilder sb = new StringBuilder(bufMax);
        for (int i = 0; i < len; ++i) {
            c = str.charAt(i);
            switch (c) {
                case ',' :
                    result.add(sb.toString());
                    sb.delete(0, sb.length());
                    break;
                default :
                    sb.append(c);
                    break;
            }
        }
        result.add(sb.toString());
        return result;
    }
    /*
     * ࠉ可以兼容最外层有'[' 或者没有'['的情况
     */
    public static ArrayList<String> getStringArrayFromNoBracketJsonArrayString(String strJsonArray) {
        if (TextUtils.isEmpty(strJsonArray)) {
            return null;
        }
        String strFixJsonArray = null;
        if (strJsonArray.charAt(0) == '[') {
            strFixJsonArray = strJsonArray;
        } else {
            StringBuilder sb = new StringBuilder(strJsonArray.length() + 2);
            sb.append('[');
            sb.append(strJsonArray);
            sb.append(']');
            strFixJsonArray = sb.toString();
        }
        return getStringArrayFromJsonArrayString(strFixJsonArray);
    }

    public static ArrayList<String> getStringArrayFromJsonArrayString(String strJsonArray) {
        if (isEmpty(strJsonArray)) {
            return null;
        }
        try {
            JSONArray array = new JSONArray(strJsonArray);
            int arraylen = array.length();
            if (0 == arraylen) {
                return null;
            }
            ArrayList<String> strArray = new ArrayList<>(arraylen);
            for (int i = 0; i < arraylen; ++i) {
                strArray.add(array.getString(i));
            }
            return strArray;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getJsonArrayStringFromStringArray(Collection<String> strArray)  {
        JSONArray array = new JSONArray();
        if (strArray.isEmpty())
            return null;

        for (String str : strArray) {
            array.put(str);
        }
        return array.toString();
    }

//	public static int[] getIntArrayFromJsonArray(JSONArray array) throws JSONException {
//		int arraylen = array.length();
//		int[] ids = new int[arraylen];
//		for (int i = 0; i < arraylen; ++i) {
//			ids[i] = array.getInt(i);
//		}
//		return ids;
//	}
//
//	public static int[] getIntArrayFromJsonArrayString(String strJsonArray) throws JSONException {
//		if (TextUtils.isEmpty(strJsonArray))
//			return null;
//
//		JSONArray array = new JSONArray(strJsonArray);
//		int arraylen = array.length();
//		int[] ids = new int[arraylen];
//		for (int i = 0; i < arraylen; ++i) {
//			ids[i] = array.getInt(i);
//		}
//		return ids;
//	}



    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        boolean result = false;
        if ("null".equals(str) || TextUtils.isEmpty(str)) {
            result = true;
        } else {
            if (TextUtils.isEmpty(str.trim())) {
                result = true;
            } else {
                result = false;
            }
        }
        return result;
    }
}
