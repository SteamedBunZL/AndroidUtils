package com.clean.spaceplus.cleansdk.junk.engine.bean;

import com.clean.spaceplus.cleansdk.junk.view.LinkedCheckableGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * @author li.li
 * @Description:可扩展控件标题bean
 * @date 2016/4/20 13:15
 * @copyright TCL-MIG
 */
public class JunkGroupTitle implements LinkedCheckableGroup<JunkChildType> {
    public static final int TYPE_SCANNING = 0;
    public static final int TYPE_FINISH = 1;
    public static final int TYPE_SHOW = 2;
    public static final int TYPE_CLEANING = 3;


    // 这几个值的大小不影响列表的顺序，要改变列表的顺序，要在initJunkData()方法中修改flags数组的顺序
    public static final int ITEM_APPCACHE_FLAG = 0;
    public static final int ITEM_LEFTCACHE_FLAG = 1;
    public static final int ITEM_ADCACHE_FLAG = 2;
    public static final int ITEM_APKCACHE_FLAG = 3;
    public  static final int ITEM_SYSCACHE_FLAG = 4;
    public static final int ITEM_MEMCACHE_FLAG = 5;

    public int groupIcon;
    public String groupName;
    public String groupSize;
    public long groupSizeBytes;
    public boolean isGroupScanFinish;  //该item已经扫描结束
    public int stateType = 0; //0 scanning, 1 finish, 2 cleaning, 3 showResult
    public boolean isGroupChecked;
    public boolean halfCheck;
    public int groupFlag;//标识该item的tag，如：系统缓存 ITEM_SYSCACHE_FLAG
//    public boolean hasChild = true;

    List<JunkChildType> mChildren=new ArrayList<>();
    @Override
    public void refreshCheckStatus() {
        int childrenCount=mChildren.size();
        boolean childrenAllIsChecked = true;
        boolean isHalfCheck=false;
        int num=childrenCount;
        for(int i=0;i<childrenCount;i++){
            JunkChildType childType=mChildren.get(i);
            //完全没选
            if (childType.isNoneChecked()) {
                childrenAllIsChecked = false;
                num--;
            }
            //如果是半选状态
            if(childType.isPartChecked()){
                isHalfCheck=true;
            }
        }
        if(isHalfCheck){
            this.halfCheck=isHalfCheck;
        }
        else{
            if (num>0&&num<childrenCount){
                this.halfCheck=true;
            }else{
                this.halfCheck=false;
            }
        }
        this.isGroupChecked=childrenAllIsChecked;
    }

    @Override
    public boolean isAllChecked() {
        return isGroupChecked;
    }

    @Override
    public boolean isPartChecked() {
        return halfCheck;
    }

    @Override
    public boolean isNoneChecked() {
        return !isGroupChecked&&!halfCheck;
    }

    @Override
    public void toggleCheck() {
        // 修改其即子的勾选状态
        this.isGroupChecked = !this.isGroupChecked;
        this.halfCheck = false;
        int childrenCount = mChildren.size();
        for (int i = 0; i < childrenCount; i++) {
            //修改自选项的选中状态跟父选项一样
            JunkChildType childType = mChildren.get(i);
            childType.isChildChecked = this.isGroupChecked;
            childType.halfCheck = false;
            if(childType.getChildren() != null){
                for(JunkSubChildType item : childType.getChildren()){
                    item.isChildChecked = this.isGroupChecked;
                }
            }
        }
        // 修改清理标识
        setJunkModelChecked(this.isGroupChecked);
    }

    public void setJunkModelChecked(boolean isChecked) {
        for (JunkChildType junkChildType : mChildren) {
            junkChildType.setJunkModelChecked(isChecked);
        }
    }

    @Override
    public List<JunkChildType> getChildren() {
        return mChildren;
    }


    @Override
    public void addChild(JunkChildType child) {
        this.mChildren.add(child);
    }

    @Override
    public boolean hasChild() {
        return !mChildren.isEmpty();
    }


    public enum enumGroup{
        TRUE,FALSE,HAVECHECK;
    }


    @Override
    public String toString() {
        return "JunkGroupTitle";
    }

}
