package com.clean.spaceplus.cleansdk.boost.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * @author zengtao.kuang
 * @Description:process的oom_adj相关信息
 * @date 2016/4/6 10:26
 * @copyright TCL-MIG
 */
public class ProcessOOMHelper {

    public static final int CACHED_APP_MIN_ADJ = 9;

    public static int getProcessOOM(int pid) {
        try {
            File stat = new File("/proc/" + Integer.valueOf(pid) + "/oom_adj");
            FileInputStream ins = new FileInputStream(stat);
            DataInputStream inReader = new DataInputStream(ins);

            String xLine = inReader.readLine();
            xLine = xLine.trim();

            inReader.close();

            return Integer.valueOf(xLine);

        } catch (Exception e) {
        }

        return 0;
    }

    public static int getProcessOOMFromOOMScore(int pid) {
        try {
            File stat = new File("/proc/" + Integer.valueOf(pid) + "/oom_score_adj");
            FileInputStream ins = new FileInputStream(stat);
            DataInputStream inReader = new DataInputStream(ins);

            String xLine = inReader.readLine();
            xLine = xLine.trim();

            inReader.close();

            int oom_score_adj = Integer.valueOf(xLine);
            return (oom_score_adj * 17 + 500) / 1000;

        } catch (Exception e) {
        }

        return 0;
    }
}
