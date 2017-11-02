package com.upload.library.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zeming_liu
 * @Description:
 * @date 2016/9/7.
 * @copyright TCL-MIG
 */

public class DirInfo {
    public String dir;
    public List<String> subDir = new ArrayList<>();

    @Override
    public String toString() {
        return "data{" +
                "dir='" + dir + '\'' +
                ", subDir=" + subDir +
                '}';
    }
}
