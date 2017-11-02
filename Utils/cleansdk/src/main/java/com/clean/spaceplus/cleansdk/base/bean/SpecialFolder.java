package com.clean.spaceplus.cleansdk.base.bean;

import com.clean.spaceplus.cleansdk.junk.engine.PathScanCallback;

import java.util.List;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/3 14:09
 * @copyright TCL-MIG
 */
public class SpecialFolder {
    public SpecialFolder(){

    }
    /**
     * 路径。在遍历此路径的时候，使用下面的策略去查找对应的文件。找到后调用mCallback处理
     */
    public String mStrPath;
    /**
     * 时间线。单位(秒)。非零起效。起效时，正则表达式、全名匹配和特征匹配都被忽略
     */
    public long mTimeLine; // 暂时无效果，本地尽量不要添加该字段
    /**
     * 是否计算稀疏文件的真实size,默认值是false
     */
    public boolean mCalSparseSize = false;
    /**
     * 正则表达式匹配。非null并且mTimeLine=0起效。起效时，全名匹配和特征匹配都被忽略
     */
    public String mStrRegex;
    /**
     * 全名匹配列表和特征匹配列表。 非null 平且mTimeLine=0 and mStrRegex==null时起效。
     * 匹配规则：
     * 先匹配mFullsMatchArr，如果未匹配成功再匹配特征。（如果特征都为null则认为匹配失败）
     */
    /**
     * 全名匹配列表.只有在正则表达式为null的情况下起效.
     */
    public List<String> mFullsMatchArr;
    /**
     * 特征匹配之 起始字节特征列表
     */
    public List<String> mStartsWithArr;
    /**
     * 特征匹配之 后缀字节特征列表
     */
    public List<String> mEndsWithArr;
    /**
     * 特征匹配之 内容特征列表
     */
    public List<String> mContainsArr;
    /**
     * 特征匹配之 不能出现的内容特征列表
     */
    public List<String> mNotContainsArr;
    /**
     * 回调
     */
    public PathScanCallback mCallback;
}
