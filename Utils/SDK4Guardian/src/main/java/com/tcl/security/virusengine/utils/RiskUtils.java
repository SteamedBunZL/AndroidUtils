package com.tcl.security.virusengine.utils;

import android.content.Context;
import android.text.TextUtils;

import com.tcl.security.virusengine.R;


/**
 * Created by Rain on 2016/5/12 0012.
 */
public class RiskUtils {
    final static int RISK_HIGH = 0;
    final static int RISK_MEDIUM = 1;
    final static int RISK_LOW = 2;
    final static int TYPE_MALWARE = 1;
    final static int TYPE_SPAM = 2;
    final static int TYPE_PUP = 3;
    final static int TYPE_PHISHING = 4;
    final static int TYPE_VIRUS = 5;
    final static int TYPE_TROJAN = 6;
    final static int TYPE_EXPLOIT = 7;
    final static int TYPE_SUSPICIOUS = 8;
    final static int TYPE_RANSOMWARE = 10;
    final static int TYPE_PUP_ADWARE = 11;
    final static int TYPE_PUP_SPYWARE = 12;
    final static int TYPE_UNCLASSIFIED = 9999;
    final static int TYPE_DEFAULT = -10000;

    public static String levelToString(int level) {
        switch (level) {
            case RISK_HIGH:
                return "Risk High";
            case RISK_MEDIUM:
                return "Risk Medium";
            case RISK_LOW:
                return "Risk Low";
            default:
                break;
        }
        return "Risk";
    }

    public static String threatTypeToString(int type) {

        switch (type) {
            case TYPE_MALWARE:
                return "Malware";
            case TYPE_SPAM:
                return "Spam";
            case TYPE_PUP:
                return "Pub";
            case TYPE_PHISHING:
                return "Phishing";
            case TYPE_VIRUS:
                return "Virus";
            case TYPE_TROJAN:
                return "Trojan";
            case TYPE_EXPLOIT:
                return "Exploit";
            case TYPE_SUSPICIOUS:
                return "Suspicious";
            case TYPE_RANSOMWARE:
                return "Ransomware";
            case TYPE_PUP_ADWARE:
                return "Pup_adware";
            case TYPE_PUP_SPYWARE:
                return "Pup_spyware";
            case TYPE_UNCLASSIFIED:
                return "Unclassified";
            case TYPE_DEFAULT:
                return "";
            default:
                break;
        }
        return "" + type;
    }

    /**
     * 适配TCL云返回的病毒名，由此得到risk_type;
     *
     * @param virusName
     * @return
     */
    public static String obtainRiskTypeByVirusName(String virusName) {
        if (TextUtils.isEmpty(virusName))
            return null;
        if (virusName.contains(".")) {
            String[] split = virusName.split("\\.");
            if (split != null && split.length > 0) {
                return split[0];
            }
            return null;
        }
        return null;
    }

    /**
     * 通过type获取Suggest
     *
     * @param type
     * @return
     */
    public static String obtainSuggestByType(Context context,String type) {
        if (TextUtils.isEmpty(type))
            return "";
        String suggest = getSuggest(context,type);
        return suggest;
    }

    /**
     * TODO 通过type匹配suggest 暂时只显示固定的一条，逻辑待定  by 2016.7.7 Steve
     *
     * @param type
     * @return
     */
    public static String getSuggest(Context context,String type) {
        if ("Malware".equals(type)) {

        } else if ("Spam".equals(type)) {

        } else if ("Pub".equals(type)) {

        } else if ("Phishing".equals(type)) {

        } else if ("Virus".equals(type)) {

        } else if ("Trojan".equals(type)) {

        } else if ("Exploit".equals(type)) {

        } else if ("Suspicious".equals(type)) {

        } else if ("Ransomware".equals(type)) {

        } else if ("Pup_adware".equals(type)) {

        } else if ("Pup_spyware".equals(type)) {

        } else if ("Unclassified".equals(type)) {

        } else {

        }
        return context.getString(R.string.suggest_common);
    }

    /**
     * 通过type获取suggest
     *
     * @param type
     * @return
     */
    public static String obtainSuggestByType(Context context,int type) {
        String typeStr = threatTypeToString(type);
        return obtainSuggestByType(context,typeStr);
    }


}
