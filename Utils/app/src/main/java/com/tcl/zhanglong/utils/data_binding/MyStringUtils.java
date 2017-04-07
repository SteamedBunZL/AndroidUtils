package com.tcl.zhanglong.utils.data_binding;

/**
 * Created by Steve on 17/2/5.
 */

public class MyStringUtils {

    public static String capitalize(final String word){
        if (word.length()>1){
            return String.valueOf(word.charAt(0));
        }
        return word;
    }
}
