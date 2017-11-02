package com.clean.spaceplus.cleansdk.util;

import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author wangtianbao
 * @Description: 日期类工具
 * @date 2016/5/9 13:49
 * @copyright TCL-MIG
 */
public class DateUtils {

    public static final SimpleDateFormat SIMPLE_FORMAT=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static String simpleFormatLong(long mis){
        return SIMPLE_FORMAT.format(new Date(mis));
    }

    public static final SimpleDateFormat ONLY_DATE =new SimpleDateFormat("yyyy-MM-dd");

    public static String simpleFormat(Date date){
        return ONLY_DATE.format(date);
    }

    public static String simpleFormat(long ms){
        return ONLY_DATE.format(new Date(ms));
    }

    public static Date simpleParse(String str){
        try {
            return ONLY_DATE.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Date();
    }


    public static Date dbParse(String str){
        try {
            return ONLY_DATE.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static String dbFormat(Date date){
        return ONLY_DATE.format(date);
    }


    public static boolean isSameDay(Date date1, Date date2)
    {
        if ((date1 == null) || (date2 == null)) {
            if (PublishVersionManager.isTest()) {
                throw new IllegalArgumentException("The date must not be null");
            }
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2)
    {
        if ((cal1 == null) || (cal2 == null)) {
            if (PublishVersionManager.isTest()) {
                throw new IllegalArgumentException("The date must not be null");
            }
        }
        return (cal1.get(0) == cal2.get(0)) && (cal1.get(1) == cal2.get(1)) && (cal1.get(6) == cal2.get(6));
    }

    /**
     * 清队时间信息，只留日期
     * @param date
     * @return
     */
    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

//    public static int getDaysBetweenTwoDate(Date date1,Date date2){
//        long diff = date2.getTime() - date1.getTime();
//        long days= TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
//        return (int) days;
//    }

    /**
     * 获取某一日期到今天相隔的天数
     * @return
     */
    public  static int getDaysToNow(Date date1){
        Date now=DateUtils.removeTime(new Date());
        return getDaysBetweenTwoDate(date1,now);
    }

    public static int getDaysBetweenTwoDate(Date fDate, Date oDate) {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(fDate);
        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
        aCalendar.setTime(oDate);
        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
        return day2 - day1;
    }

    /**
     * 时间戳获取日
     * @param time
     * @return
     */
    public static String getDay(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
        return simpleDateFormat.format(new Date(time * 1000));
    }
    /**
     * 时间戳获取月
     * @param time
     * @return
     */
    public static String getMonth(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
        return simpleDateFormat.format(new Date(time * 1000));
    }
    /**
     * 时间戳获取年
     * @param time
     * @return
     */
    public static String getyear(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        return simpleDateFormat.format(new Date(time * 1000));
    }

    /**
     * 获取详细日期信息
     * @param time
     * @return
     */
    public static String getDateTime(long time){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        return simpleDateFormat.format(new Date(time));
    }
}
