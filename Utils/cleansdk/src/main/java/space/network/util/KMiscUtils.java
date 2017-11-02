//=============================================================================
/**
 * @file KMiscUtils.java
 * @brief 用来放一些不好归类的公共函数
 */
//=============================================================================
package space.network.util;


public final class KMiscUtils {

    public static final String LANG_TW = "tw";
    public static final String LANG_CN = "cn";
    public static final String LANG_EN = "en";

    public static byte[] getLanguageBytes(String lang) {
        if (lang == null)
            return null;

        byte langBytes[] = null;
        int langLen = lang.length();
        if (langLen < 6) {
            //如果语言字符小于六个，用空格补齐
            StringBuffer strbuffer = new StringBuffer();
            int appendCount = 6 - langLen;
            strbuffer.append(lang);
            for (int i = 0; i < appendCount; ++i) {
                strbuffer.append(" ");
            }
            langBytes = strbuffer.toString().getBytes();
        } else {
            langBytes = lang.getBytes();
        }
        return langBytes;
    }

    public static String toSupportedLanguage(String lang) {
        if (lang == null)
            return null;

        String result;
        if (0 == lang.compareToIgnoreCase("zh-cn")) {
            result = LANG_CN;
        } else if (0 == lang.compareToIgnoreCase("zh-tw")) {
            result = LANG_TW;
        } else if (0 == lang.compareToIgnoreCase("zh-hk")) {
            result = LANG_TW;
        }else {
            int pos = lang.indexOf('-');
            if (-1 == pos) {
                result = lang;
            } else {
                result = lang.substring(0, pos);
            }
            if (0 == result.compareToIgnoreCase("zh")) {
                result = LANG_CN;
            }
        }
        return result;
    }

//    public static String getInfocReportEscapeString(String str) {
//        if (TextUtils.isEmpty(str))
//            return str;
//
//        char c;
//        boolean needChange = false;
//        int len = str.length();
//        for (int i = 0; i < len; ++i) {
//            c = str.charAt(i);
//            switch(c) {
//                case '^':
//                case '*':
//                case '&':
//                case '=':
//                    needChange = true;
//                    break;
//                default:
//            }
//        }
//        if (!needChange)
//            return str;
//
//        //由于infoc使用里&和=号，所以就用其他字符来转义一下
//        String trans1 = str.replace("^", "^^").replace("*", "**");
//        String trans2 = trans1.replace('=', '^').replace('&', '*');
//        return trans2;
//    }

//	public static String toSupportedDefaultLanguage(String lang) {
//		if (lang == null)
//			return null;
//
//		String result;
//		if (0 == lang.compareToIgnoreCase("zh-tw")) {
//			result = LANG_CN;
//		} else if (0 == lang.compareToIgnoreCase("zh-hk")) {
//			result = LANG_CN;
//		} else {
//			result = LANG_EN;
//		}
//		return result;
//	}
}