package space.network.util;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/20 11:11
 * @copyright TCL-MIG
 */
public class ConvertUtil {
//    /**
//     * 对象转整数
//     *
//     * @param obj
//     * @return 转换异常返回 0
//     */
//    public static int toInt(Object obj) {
//        if (obj == null) return 0;
//        return toInt(obj.toString(), -1);
//    }

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
}
