package com.clean.spaceplus.cleansdk.junk.engine.util;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/10 16:29
 * @copyright TCL-MIG
 */
public class RubbishUtil {

    public static String getRubbishTypeString(int type){
        String rubbishType = "unkonw";
        if (type == 0){
            rubbishType = "残留文件";
        }else if (type == 1){
            rubbishType = "临时文件";
        }else if (type == 2){
            rubbishType = "广告文件";
        }else if (type == 3){
            rubbishType = "大文件";
        }
        return rubbishType;
    }
}
