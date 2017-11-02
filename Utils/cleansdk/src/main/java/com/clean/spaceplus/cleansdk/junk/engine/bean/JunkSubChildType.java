package com.clean.spaceplus.cleansdk.junk.engine.bean;

import com.clean.spaceplus.cleansdk.junk.view.LinkedCheckableChild;
import com.clean.spaceplus.cleansdk.junk.view.LinkedCheckableGroup;

import java.util.List;

/**
 * @author zeming_liu
 * @Description: 扩展列表子项展开的bean
 * @date 2016/7/18.
 * @copyright TCL-MIG
 */
public class JunkSubChildType extends LinkedCheckableChild implements Comparable{

    public String subChildTypeName;
    public String subJunkChildSize;
    //大小
    public long subJunkSize;
    //路径
    public String subRoute;
    //描述
    public String subDescritpion;
    //包名
    public String subPackageName;
    public boolean isWhiteList;
    public CacheInfo mCachInfo;
    public int junkModelType = -1;
    public JunkModel junkModel;

    public JunkSubChildType(LinkedCheckableGroup parent) {
        super(parent);
    }

    @Override
    public int compareTo(Object another) {
        //目前根据文件大小进行排序
        JunkSubChildType subChildType=(JunkSubChildType)another;
        if(this.subJunkSize>=subChildType.subJunkSize){
            return -1;
        }
        else{
            return 1;
        }
    }

    public void toggleCheck() {
        // 修改其即子的勾选状态
        this.isChildChecked = !this.isChildChecked;
        // 修改清理标识
        setJunkModelChecked(this.isChildChecked);
        // 更新父的勾选状态
        getParent().refreshCheckStatus();
    }

    /**
     * 设置三级明细数据CacheInfo的选中状态
     */
    public void setJunkModelChecked(boolean isCheck){
        mCachInfo.setCheck(isCheck);
    }
}
