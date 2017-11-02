package com.clean.spaceplus.cleansdk.boost.util;

/**
 * @author zengtao.kuang
 * @Description: 进程白名单标识辅助类
 * @date 2016/4/19 20:03
 * @copyright TCL-MIG
 */
public class ProcessWhiteListMarkHelper {


    public static final int FLAG_UNKNOWN	 = 0;
    public static final int FLAG_PROTECTED 	= 1 << 4;		//全部不杀,全部忽略，在哪里都不显示

    /**
     * 白名单 4
     * */
    public static final int FLAG_WHITELIST 	= 1 << 2;		//默认在白名单中显示
    public static final int FLAG_UNCHECKED   	= 1 << 1;		//默认不勾选

    private static final int SPLITER 		=  8;
    private static final int MARK_DEFAULT 	=  (1 << SPLITER) -1 ;		//区分用户设置和系统设置的界限 , 0x00FF
    private static final int MARK_USER 		=  MARK_DEFAULT << SPLITER;	//用户配置Mask，0xFF00

    /**
     * 用户加白 256
     * */
    private static final int FLAG_USER_WHITELIST = 1 << (SPLITER + 0);
    private static final int FLAG_USER_UNCHECKED 	= 1 << (SPLITER + 1);    //用户不勾选
    private static final int FLAG_USER_NORMAL 	= 1 << (SPLITER + 2);    //用户非白，仅仅用于用户移除非白的时候才用得上
    private static final int FLAG_USER_CHECKED 	= 1 << (SPLITER + 3);    //用户CHECKED

    /**
     * 用户没手动修改过状态
     * */
    public static boolean isUserModified(int mark) {
        return (mark & MARK_USER) != 0;
    }



    public static boolean isProtected(int mark) {
        return (mark & FLAG_PROTECTED) != 0;
    }

    public static boolean isInWhiteList(int mark) {
        if (isProtected(mark)) {
            return false;
        }
        // User
        if (ProcessWhiteListMarkHelper.isUserModified(mark)) {
            if ((mark & FLAG_USER_NORMAL) != 0) {
                return false;
            }
            if ((mark & FLAG_USER_WHITELIST) != 0) {
                return true;
            }
        }
        // Default
        return (mark & FLAG_WHITELIST) != 0;
    }

    /**
     * 默认 Mark过的
     * @param mark
     * @return
     */
    public static boolean isDefaultIgnore(int mark) {
        return (mark & MARK_DEFAULT) != 0;
    }

/*		public static boolean isDefaultInWhiteList(int mark){
			return (mark & FLAG_WHITELIST) != 0;
		}*/

    // -------user -------------

    public static boolean isUserWhiteList(int mark) {
        return (mark & FLAG_USER_WHITELIST) != 0;
    }

    public static boolean isUserUnchecked(int mark) {
        return (mark & FLAG_USER_UNCHECKED) != 0;
    }

    public static boolean isUserChecked(int mark) {
        return (mark & FLAG_USER_CHECKED) != 0;
    }

    public static boolean isUserUnwhite(int mark) {
        return (mark & FLAG_USER_NORMAL) != 0;
    }

    // ----------


    // ChangeUser
    public static int setUserChecked(int mark, boolean checked) {
        if (checked) { //选中状态
            mark = mark & (~FLAG_USER_UNCHECKED); //
            mark = mark | FLAG_USER_CHECKED; //
        } else {
            mark = mark & (~ FLAG_USER_CHECKED);
            mark = mark | FLAG_USER_UNCHECKED;
        }
        return mark;
    }

    public static int addToWhiteListByUser(int mark) {
        // 清空Normal位
        if ((mark & FLAG_USER_NORMAL) != 0) {
            mark = mark & (~FLAG_USER_NORMAL);
        }
        // 置1，WhiteList位
        mark = mark | FLAG_USER_WHITELIST;
        return mark;
    }

    /**
     * 	 * 1. 如果是原始就是白名单的，直接去掉FLAG_WHITELIST属性，加FLAG_USER_UNCHECKED
     * 2. 如果没FLAG_WHITELIST属性的不额外处理
     * */
    public static int removeFromWhiteList(int mark) {
        // 置1，Normal位
        mark = mark | FLAG_USER_NORMAL;

        mark = mark & (~FLAG_USER_WHITELIST);

        if ((mark & FLAG_WHITELIST) != 0) {
            mark = mark | FLAG_USER_UNCHECKED;
        } else {

        }

        // 置0，WhiteList位
        //mark = mark & (~FLAG_USER_WHITELIST);
        // 置1，Checked位
        //mark = mark | FLAG_USER_UNCHECKED;
        return mark;
    }
}
