package com.clean.spaceplus.cleansdk.util;

import com.hawkclean.framework.log.NLog;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * @author zengtao.kuang
 * @Description: 字节大小字符串格式化工具
 * @date 2016/4/13 20:28
 * @copyright TCL-MIG
 */
public class SizeUtil {
    private final static String TAG=SizeUtil.class.getSimpleName();
//    public static final long TB = 1024 * 1024 * 1024 * 1024l;
//    public static final long GB = 1024 * 1024 * 1024;
//    public static final long MB = 1024 * 1024;
//    public static final long KB = 1024;

//    /**
//     * 转换Size值，小数点后保留一位小数
//     * @param size
//     * @return
//     */
//    public static String formatSizeFloatSingle(long size){
//        return formatSize(size, 1);
//    }

//    /**
//     * 转换Size值
//     * @param size 需要转换的值
//     * @param floatSize 小数点后保留的位数
//     * @return
//     */
//    public static String formatSize(long size,int floatSize) {
//
//        // 保留小数点后两位，四舍五入
//        if (size < 0){
//            return "";
//        }
////            return "" + size;
//
//        if (size >= GB) {
////            long size_ = size % GB;
////            long size__ = size_ * 10 % GB;
////            long size___ = size__ * 10 % GB;
////            String s = String.valueOf(size / GB) + "."
////                    + String.valueOf(size_ * 10 / GB)
////                    + String.valueOf(size__ * 10 / GB)
////                    + String.valueOf(size___ * 10 / GB);
//            double sz = (double) size / GB;
//            BigDecimal bd = new BigDecimal(sz);
//            bd = bd.setScale(floatSize, BigDecimal.ROUND_HALF_UP);
//            return String.format("%sGB", bd.toString());
//        }
//        if (size >= MB) {
////            long size_ = size % MB;
////            long size__ = size_ * 10 % MB;
////            long size___ = size__ * 10 % MB;
////            String s = String.valueOf(size / MB) + "."
////                    + String.valueOf(size_ * 10 / MB)
////                    + String.valueOf(size__ * 10 / MB)
////                    + String.valueOf(size___ * 10 / MB);
//            double sz = (double) size / MB;
//            BigDecimal bd = new BigDecimal(sz);
//            bd = bd.setScale(floatSize, BigDecimal.ROUND_HALF_UP);
//
//            return String.format("%sMB", bd.toString());
//        }
//        if (size >= KB) {
////            long size_ = size % KB;
////            long size__ = size_ * 10 % KB;
////            long size___ = size__ * 10 % KB;
////            String s = String.valueOf(size / KB) + "."
////                    + String.valueOf(size_ * 10 / KB)
////                    + String.valueOf(size__ * 10 / KB)
////                    + String.valueOf(size___ * 10 / KB);
//            double sz = (double) size / KB;
//            BigDecimal bd = new BigDecimal(sz);
//            bd = bd.setScale(floatSize, BigDecimal.ROUND_HALF_UP);
//            return String.format("%sKB", bd.toString());
//        }
//
//        if (size != 0)
//            return "< 1 KB";
//        return "0 KB";
//
//    }

//    /**
//     * 转换Size值，小数点后保留两位小数
//     * @param size
//     * @return
//     */
//    //SizeUtil
//    public static String formatSize_1(long size) {
//        return formatSize(size, 2);
//    }
//
//    /**
//     * @param size
//     * @return 将long转换为流量单位并以String类型返回
//     */
//    public static String formatSizeInt(long size) {
//
//
//        if (size < 0)
//            return "" + size;
//
//        if(size >= TB)
//            return String.format(Locale.US, "%d.%dTB", size / TB, size % TB * 10
//                    / TB);
//        if (size >= GB)
//            return String.format(Locale.US, "%d.%dGB", size / GB, size % GB * 10
//                    / GB);
//        if (size >= MB)
//            return String.format(Locale.US, "%d.%dMB", size / MB, size % MB * 10
//                    / MB);
//        if (size >= KB)
//            return String.format(Locale.US, "%d.%dKB", size / KB, size % KB * 10
//                    / KB);
//        if (size != 0)
//            return "< 1KB";
//        return "0KB";
//    }
//
//    /**
//     * 只保留三位数
//     * @param size
//     * @return
//     */
//    public static String formatSize3(long size) {
//
//        float fSize = 0;
//        String formatString;
//        String formatLastString = "";
//        if (size >= 1024 * 1024* 1000 ) {
//            formatLastString = "GB";
//            fSize = (float) (size / (1024*1024*1024.0) );
//        } else if (size >= 1024 * 1000 ) {
//            formatLastString = "MB";
//            fSize = (float) (size / (1024*1024.0) );
//        } else {
//            formatLastString = "KB";
//            fSize = (float) (size / 1024.0 );
//        }
//        if(fSize > 100){
//            formatString = "#0";
//        }else if(fSize > 10){
//            formatString = "#0.0";
//        }else{
//            formatString = "#0.00";
//        }
//
//        DecimalFormat df = new DecimalFormat(formatString);
//        DecimalFormatSymbols symbols=df.getDecimalFormatSymbols();
//        symbols.setDecimalSeparator('.');
//        df.setDecimalFormatSymbols(symbols);
//        String result = df.format(fSize);
//        result = result.replaceAll("-", ".")+formatLastString;
//        return result;
//    }
//
//    //ViewUtil
//    public static String formatSize_2(long size) {
//
//        float fSize = 0;
//        String formatString;
//        String unit;
//        if (size >= 1024 * 1024* 1000 ) {
//            unit = "GB";
//            fSize = (float) (size / (1024*1024*1024.0) );
//        } else if (size >= 1024 * 1000 ) {
//            unit = "MB";
//            fSize = (float) (size / (1024*1024.0) );
//        } else {
//            unit = "KB";
//            fSize = (float) (size / 1024.0 );
//        }
//        if(fSize >= 100){
//            formatString = "#0";
//        }else if(fSize >= 10){
//            formatString = "#0.0";
//        }else{
//            formatString = "#0.00";
//        }
//
//        DecimalFormat df = new DecimalFormat(formatString);
//        DecimalFormatSymbols symbols=df.getDecimalFormatSymbols();
//        symbols.setDecimalSeparator('.');
//        df.setDecimalFormatSymbols(symbols);
//        String result = df.format(fSize);
//        result = result.replaceAll("-", ".");
//        return result + unit;
//    }
//
//    public static String formatSizeSmallestMBUnit(long size) {
//
//        float fSize = 0;
//        String formatString;
//        String unit;
//        if (size >= 1024 * 1024* 1000 ) {
//            unit = "GB";
//            fSize = (float) (size / (1024*1024*1024.0) );
//        } else{
//            unit = "MB";
//            fSize = (float) (size / (1024*1024.0) );
//        }
//        if(fSize >= 100){
//            formatString = "#0";
//        }else if(fSize >= 10){
//            formatString = "#0.0";
//        }else{
//            formatString = "#0.00";
//        }
//
//        DecimalFormat df = new DecimalFormat(formatString);
//        DecimalFormatSymbols symbols=df.getDecimalFormatSymbols();
//        symbols.setDecimalSeparator('.');
//        df.setDecimalFormatSymbols(symbols);
//        String result = df.format(fSize);
//        result = result.replaceAll("-", ".");
//        return result + unit;
//    }

    static DecimalFormat mSizeFormat;

    public static String formatSizeSmallestMBUnit2(long size) {
        if(size<=0){
            return "0";
        }
        float fSize = 0;
        fSize = (float) (size / (1024*1024.0) );
        String result = mSizeFormat.format(fSize);
        result = result.replaceAll("-", "");
        return result;
    }

    static {
        String formatString = "#0.00";
        mSizeFormat= new DecimalFormat(formatString,new DecimalFormatSymbols(Locale.ENGLISH));
        DecimalFormatSymbols symbols1=mSizeFormat.getDecimalFormatSymbols();
        symbols1.setDecimalSeparator('.');
        mSizeFormat.setDecimalFormatSymbols(symbols1);
    }

    //common
//    public static String formatSize_3(long size) {
//        return formatSize(size, "#0.00");
//    }
//
//    public static String formatSizeDecimalPartOnly(long size) {
//        return formatSize(size, "#0");
//    }

//    public static String _formatSizeDecimalPartOnly(long size) {
//        return size > (1024 * 1024 * 1024) ? formatSize_3(size) : formatSizeDecimalPartOnly(size);
//    }
//
//    public static String formatSize2(long size) {
//        return formatSize(size, "#0.0");
//    }

//    public static String accuracy(double num, double total, int scale){
//            DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
//             //可以设置精确几位小数
//            df.setMaximumFractionDigits(scale);
//            //模式 例如四舍五入
//            df.setRoundingMode(RoundingMode.HALF_UP);
//            double accuracy_num = num / total * 100;
//            return df.format(accuracy_num);
//    }


    // 保证三位数，和垃圾清理同步规则一致
    public static String formatSizeForJunkHeader(long size) {
        try{
        String suffix = null;
        float fSize = 0;
        if (size >= 1000) {
            suffix = "KB";
            fSize = (float) (size / 1024.0);
            if (fSize >= 1000) {
                suffix = "MB";
                fSize /= 1024;
            }
            if (fSize >= 1000) {
                suffix = "GB";
                fSize /= 1024;
            }
        } else {
            fSize = (float) (size / 1024.0);
            suffix = "KB";
        }

        String formatString = null;
        if(fSize > 100){
            formatString = "#0";
        }else if(fSize > 10){
            formatString = "#0.0";
        }else{
            formatString = "#0.00";
        }

        DecimalFormat df = new DecimalFormat(formatString);
        DecimalFormatSymbols symbols=df.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(symbols);
        StringBuilder resultBuffer = new StringBuilder(df.format(fSize));
        resultBuffer.append(suffix);
        String size1=resultBuffer.toString().replaceAll("-", ".");
        NLog.i(TAG,"size1 %s",size1);
            return size1;
        }catch (Exception e){
            return "";
        }
    }

////    public static String[] formatSizeReturnArray(long size) {
////        String suffix = null;
////        float fSize = 0;
////        if (size >= 1000) {
////            suffix = "KB";
////            fSize = (float) (size / 1024.0);
////            if (fSize >= 1000) {
////                suffix = "MB";
////                fSize /= 1024;
////            }
////            if (fSize >= 1000) {
////                suffix = "GB";
////                fSize /= 1024;
////            }
////        } else {
////            fSize = (float) (size / 1024.0);
////            suffix = "KB";
////        }
////
////        String formatString = null;
////        if(fSize > 100){
////            formatString = "#0";
////        }else if(fSize > 10){
////            formatString = "#0.0";
////        }else{
////            formatString = "#0.00";
////        }
////
////        DecimalFormat df = new DecimalFormat(formatString);
////        DecimalFormatSymbols symbols=df.getDecimalFormatSymbols();
////        symbols.setDecimalSeparator('.');
////        df.setDecimalFormatSymbols(symbols);
////
////        return new String[]{df.format(fSize), suffix};
////    }
////
////    public static String formatSizeGB(long lSizeMB) {
////
////        String strFormat = "";
////        if (lSizeMB > 1024) {
////            float gb = (float)lSizeMB / 1024.0f;
////            DecimalFormat decimalFormat = new DecimalFormat("#0.0");
////            strFormat = decimalFormat.format(gb) + "GB";
////        } else {
////            strFormat = lSizeMB + "MB";
////        }
////        return strFormat;
////    }
////
////    public static String formatSize2MB(long size) {
////        String mbStr = "0MB";
////        if (size > 0) {
////            float mb = size / (1024.0f * 1024.0f);
////            DecimalFormat decimalFormat = new DecimalFormat("#0.##");
////            mbStr = decimalFormat.format(mb) + "MB";
////        }
////        return mbStr;
////    }
////
////    public static float formatSizeMB(long size) {
////        float mb = 0.0f;
////        if (size > 0) {
////            float temp = size / (1024.0f * 1024.0f);
////            BigDecimal b = new BigDecimal(temp);
////            mb = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
////        }
////        return mb;
////    }
////
////    public static String formatSizeForMiui(long size) {
////        try {
////            Class.forName("miui.text.util.MiuiFormatter");
////            return miuiFormatSize(size, "#0.0");
////        } catch (Exception e) {
////        }
////        return formatSize(size, "#0.0");
////    }
//
//    private static String miuiFormatSize(long size, String formatString) {
//        String suffix = null;
//        float fSize = 0;
//        if (size >= 1000) {
//            suffix = "KB";
//            fSize = (float) (size / 1000.0);
//            if (fSize >= 1000) {
//                suffix = "MB";
//                fSize /= 1000;
//            }
//            if (fSize >= 1000) {
//                suffix = "GB";
//                fSize /= 1000;
//            }
//        } else {
//            fSize = size;
//        }
//        DecimalFormat df = new DecimalFormat(formatString);
//        StringBuilder resultBuffer = new StringBuilder(df.format(fSize));
//        if (suffix != null) {
//            resultBuffer.append(suffix);
//        } else {
//            suffix = "B";
//            resultBuffer.append(suffix);
//        }
//        return resultBuffer.toString();
//    }

//    public static String formatSize(long size, String formatString) {
//        String suffix = null;
//        float fSize = 0;
//        if (size >= 1024) {
//            suffix = "KB";
//            fSize = (float) (size / 1024.0);
//            if (fSize >= 1024) {
//                suffix = "MB";
//                fSize /= 1024;
//            }
//            if (fSize >= 1024) {
//                suffix = "GB";
//                fSize /= 1024;
//            }
//        } else {
//            fSize = size;
//        }
//        DecimalFormat df = new DecimalFormat(formatString);
//        StringBuilder resultBuffer = new StringBuilder(df.format(fSize));
//        if (suffix != null) {
//            resultBuffer.append(suffix);
//        } else {
//            suffix = "B";
//            resultBuffer.append(suffix);
//        }
//        return resultBuffer.toString();
//    }


//    public static String formatSizeGetUnit(long size) {
//        String suffix = null;
//        float fSize = 0;
//        if (size >= 1024) {
//            suffix = "KB";
//            fSize = (float) (size / 1024.0);
//            if (fSize >= 1024) {
//                suffix = "MB";
//                fSize /= 1024;
//            }
//            if (fSize >= 1024) {
//                suffix = "GB";
//                fSize /= 1024;
//            }
//        } else {
//            suffix = "B";
//        }
//        return suffix;
//    }
//
//
//
//    public static String formatSizeWithoutSuffix2(long size) {
//        float fSize = 0;
//        if (size >= 1024) {
//            fSize = (float) (size / 1024.0);
//            if (fSize >= 1024) {
//                fSize /= 1024;
//            }
//            if (fSize >= 1024) {
//                fSize /= 1024;
//            }
//        } else {
//            fSize = size;
//        }
//        DecimalFormat df = new DecimalFormat("#0.0");
//        StringBuilder resultBuffer = new StringBuilder(df.format(fSize));
//        return resultBuffer.toString();
//    }
//
//    public static String formatSizeWithoutSuffix(long size) {
//        float fSize = 0;
//        if (size >= 1024) {
//            fSize = (float) (size / 1024.0);
//            if (fSize >= 1024) {
//                fSize /= 1024;
//            }
//        } else {
//            fSize = size;
//        }
//        DecimalFormat df = new DecimalFormat("#0.0");
//        StringBuilder resultBuffer = new StringBuilder(df.format(fSize));
//        return resultBuffer.toString();
//    }
//
//    public static String[] getSizeShow(long size){
//        if(size < 0){
//            size = 0;
//        }
//
//        String[] values = new String[]{"0", "KB"};
//        String total = SizeUtil.formatSizeForJunkHeader(size);
//        String value="";
//        String unit="";
//        NLog.i(TAG,"total %s %s",total,unit);
//        if (!TextUtils.isEmpty(total) ) {
//            if (total.contains("KB")){
//                unit="KB";
//                value=total.replace("KB","");
//            }else if(total.contains("MB"))
//            {
//                unit="MB";
//                value=total.replace("MB","");
//
//            }
//            else if(total.contains("GB"))
//            {
//                unit="GB";
//                value=total.replace("GB","");
//            }
//
//            NLog.i(TAG,"value %s unit %s",value,unit);
//            try {
//                String p =SizeUtil.accuracy(size,1024*1024*1024D,0);
//                //  mCleanLayout.setPercent(Integer.parseInt(p));
//            }catch (Exception e){
//                NLog.i(TAG,"Exception %s",e.getLocalizedMessage());
//            }
//
//            values[0] = value;
//            values[1] = unit;
//        }
//
//        return values;
//    }
//
//
//    public static String formatSizeLimitMinmum(Context context, long size) {
//        String suffix = null;
//        float fSize = 0;
//        if (size >= 1024) {
//            suffix = "KB";
//            fSize = (float) (size / 1024.0);
//            if (fSize >= 1024) {
//                suffix = "MB";
//                fSize /= 1024;
//            }
//            if (fSize >= 1024) {
//                suffix = "GB";
//                fSize /= 1024;
//            }
//        } else {
//            fSize = size;
//        }
//        DecimalFormat df = new DecimalFormat("#0.0");
//        StringBuilder resultBuffer = new StringBuilder(df.format(fSize));
//        if (suffix != null) {
//            resultBuffer.append(suffix);
//        } else {
//            resultBuffer = new StringBuilder();
//            resultBuffer.append(context.getString(R.string.settings_cm_app_move_file_less_than_minmum, "1KB"));
//        }
//        return resultBuffer.toString();
//    }
}
