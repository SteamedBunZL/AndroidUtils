package com.clean.spaceplus.cleansdk.main.bean.residual_cache;

/**
 * @author Jerry
 * @Description:
 * @date 2016/5/14 14:40
 * @copyright TCL-MIG
 */
public class ResiducalLangQuery {
    public int _id;
    public int dirid;
    public String lang;
    public String name;
    public String alert;
    public String desc;


    @Override
    public String toString() {
        return "ResiducalLangQuery{" +
                "_id=" + _id +
                ", dirid=" + dirid +
                ", lang='" + lang + '\'' +
                ", name='" + name + '\'' +
                ", alert='" + alert + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
