package com.clean.spaceplus.cleansdk.util;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;

import java.io.File;


public class StringUtils {

    public static String[] slipString(String strValue, String regularExpression)
    {
        if (TextUtils.isEmpty(strValue) || TextUtils.isEmpty(regularExpression))
        {
            return null;
        }

        try
        {
            String[] arrayStr = strValue.split(regularExpression);
            if (null == arrayStr || arrayStr.length <= 0)
            {
                arrayStr = new String[]{strValue};
            }
            return arrayStr;
        }
        catch (Exception e)
        {
            return null;
        }
    }
	
	public static String getPrintableString(String string) {
		if (TextUtils.isEmpty(string)) {
			return "";
		}
		char[] nameArray = string.toCharArray();
		StringBuilder builder = new StringBuilder();
		for (char x : nameArray) {
			if (TextUtils.isGraphic(x)) {
				builder.append(x);
			}
		}
		return builder.toString();
	}

    public static boolean isExistArray(final String[] arrayStr, final String str)
    {
        if (null == arrayStr || arrayStr.length <= 0 || TextUtils.isEmpty(str))
        {
            return false;
        }

        int nSize = arrayStr.length;
        for (int nIdx = 0; nIdx < nSize; ++nIdx)
        {
            if (!TextUtils.isEmpty(arrayStr[nIdx]) && str.equals(arrayStr[nIdx]))
            {
                return true;
            }
        }
        return false;
    }

    public static CharSequence safeFormatCloudString(Context context, String cloud, int defaultRes, Object... args) {
        CharSequence result = "";
        try {
            result = Html.fromHtml(String.format(cloud, args));
        } catch (Exception e) {
            e.printStackTrace();
            result = context.getString(defaultRes, args);
        }
        return result;
    }

    public static CharSequence safeFormatCloudHtmlString(String cloud){
        try {
            return Html.fromHtml(cloud);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

	/**
	 * String类型的数字转换为整型
	 * */
	public static long string2Long(String sData, long def){
		long data = def;
		if(sData != null){
			try {
				data  = Long.valueOf(sData);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return data;
	}

    // 获取文件后缀名
    public static String getExtFromFilename(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }
        int index = fileName.lastIndexOf(".");
        if (-1 == index || fileName.length() - 1 == index) {
            return "";
        }
        return fileName.substring(index + 1, fileName.length());
    }

    public static String getNameFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(0, dotPosition);
        }
        return "";
    }

    public static String getPathFromFilepath(String filepath) {
        int pos = filepath.lastIndexOf('/');
        if (pos != -1) {
            return filepath.substring(0, pos);
        }
        return "";
    }

    public static String getNameFromFilepath(String filepath) {
        int pos = filepath.lastIndexOf('/');
        if (pos != -1) {
            return filepath.substring(pos + 1);
        }
        return "";
    }

    // 获取给定路径的父路径
    public static String getParentFolderPath(String fullPath) {
        if (null == fullPath) {
            return null;
        }
        int index = fullPath.lastIndexOf(File.separatorChar);
        if (-1 == index || 0 == index) {
            return null;
        }
        return fullPath.substring(0, index + 1);
    }

    //获取给定包名对应的cache路径
/*	public static String getCacheDirPath(Context context, String pkgName) {
		String cacheDirPath = null;
		String cmCacheDirPath = context.getCacheDir().getPath();
		String cmPkgName = context.getPackageName();
		if (cmCacheDirPath.contains(cmPkgName) && !TextUtils.isEmpty(pkgName)) {
			cacheDirPath = cmCacheDirPath.replace(cmPkgName, pkgName);
		}
		return cacheDirPath;
	}*/

    /**
     * 格式化字符串
     * @param defaultString
     * @param cloudString
     * @param args
     * @return
     */
    public static CharSequence formatCloudString(
            String defaultString, String cloudString, Object... args) {

        String resultString = null;
        if (!TextUtils.isEmpty(cloudString)) {
            resultString = cloudString;
        } else {
            resultString = defaultString;
        }

        if (!TextUtils.isEmpty(resultString)) {
            try {
                resultString = String.format(resultString, args);
            } catch (Exception e) {
                try {
                    resultString = String.format(defaultString, args);
                } catch (Exception e2) {
                    resultString = defaultString;
                }
            }
        }

        if (!TextUtils.isEmpty(resultString)) {
            CharSequence result = "";
            try {
                result = Html.fromHtml(resultString);
            } catch (Exception e) {
                
            }
            return result;
        }

        return defaultString;
    }

    public static String toLowerCase(String strSrc ) {
        if ( TextUtils.isEmpty(strSrc) ) {
            return strSrc;
        }
        char[] chars = strSrc.toCharArray();
        if ( chars == null ) {
            return strSrc;
        }
        for ( int i=0; i<chars.length; i++ ) {
            char c = chars[i];
            if ('A' <= c && c <= 'Z') {
                chars[i] = (char) (c + 'a' - 'A');
            }
        }
        return String.valueOf(chars);
    }



    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null) return 0;
        return toInt(obj.toString(), 0);
    }
    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }
    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return -1;
    }

    public static double toDouble(Object obj) {
        if (obj == null) return 0;
        return toDouble(obj.toString(), 0);
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static double toDouble(String str, float defValue) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
        }
        return defValue;
    }


    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        boolean result = false;
        if (TextUtils.isEmpty(str)) {
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
