package com.clean.spaceplus.cleansdk.base.db;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shunyou.huang
 * @Description:字符串辅助类
 * @date 2016/4/23 11:19
 * @copyright TCL-MIG
 */

public class StringUtils {

    /**
     * 判断是否有效的邮箱地址
     * @param s 邮箱地址
     * @return 返回判断结果
     */
    public static boolean isValidEmail(String s) {

        if (TextUtils.isEmpty(s)) {
            return false;
        }

        String strPattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(s);
        return m.matches();
    }

    /**
     * 判断是否有效的手机号
     * @param s 手机号码
     * @return 返回判断结果
     */
    public static boolean isValidMobilePhone(String s) {
        if (TextUtils.isEmpty(s)) {
            return false;
        }

        Pattern pattern = Pattern.compile("1[0-9]{10}");
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }
    /**
     * 判断字符串是否为数字
     * @param str 字符串
     * @return 返回判断结果
     */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches() ;
    }

    /**
     * 字符串串联
     * @param sb
     * @param format
     * @param args
     * @return
     */
    public static StringBuffer appendFormat(StringBuffer sb, String format, Object...args) {
        if (sb == null){
            sb = new StringBuffer();
        }

        if (!TextUtils.isEmpty(format))
        {
        String s = String.format(format, args);
        sb.append(s);
        }else
        {
            return null;
        }
        return sb;
    }


    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String formatFileSize(long fileS) {
        final int K = 1024;
        final int M = 1024 * 1024;
        final int G = 1024 * 1024 * 1024;
        StringBuilder builder = new StringBuilder();
        String fileSizeString = "";
        if (fileS < K/2) {
            fileSizeString = String.valueOf(fileS) + "B";
        }

        else if (fileS < M/2) {
            long k = fileS/K;
            builder.append(k);
            builder.append(".");
            fileS = fileS % K;
            fileS *= 10;
            k = fileS / K;
            builder.append(k);

            fileS = fileS % K;
            fileS *= 10;
            k = fileS / K;
            builder.append(k);

            builder.append("KB");

            fileSizeString = builder.toString();

        } else if (fileS < G/2) {

            long k = fileS/M;
            builder.append(k);
            builder.append(".");
            fileS = fileS % M;
            fileS *= 10;
            k = fileS / M;
            builder.append(k);

            fileS = fileS % M;
            fileS *= 10;
            k = fileS / M;
            builder.append(k);

            builder.append("MB");

            fileSizeString = builder.toString();
        } else {
            long k = fileS/G;
            builder.append(k);
            builder.append(".");
            fileS = fileS % G;
            fileS *= 10;
            k = fileS / G;
            builder.append(k);

            fileS = fileS % G;
            fileS *= 10;
            k = fileS / G;
            builder.append(k);

            builder.append("GB");

            fileSizeString = builder.toString();
        }

        return fileSizeString;
    }


    public static boolean isSameOrEmpty(String s1, String s2) {
        if (TextUtils.isEmpty(s1)) {
            return TextUtils.isEmpty(s2);
        }

        else {
            return s1.equals(s2);
        }
    }

    public static int stringCompareTo(String s1, String s2) {
        boolean empty1 = TextUtils.isEmpty(s1);
        boolean empty2 = TextUtils.isEmpty(s2);

        if ( empty1 && empty2)
            return 0;

        else if (empty1)
            return -1;

        else if (empty2)
            return 1;

        return s1.compareTo(s2);
    }

    /**
     * 字符串转换成小写
     * @param strSrc
     * @return
     */
    public static String toLowerCase( String strSrc ) {
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
}
