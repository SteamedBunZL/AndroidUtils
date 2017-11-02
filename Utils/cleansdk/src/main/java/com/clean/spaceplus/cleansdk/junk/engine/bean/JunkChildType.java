package com.clean.spaceplus.cleansdk.junk.engine.bean;

import com.clean.spaceplus.cleansdk.boost.dao.ProcessWhiteListDAOHelper;
import com.clean.spaceplus.cleansdk.boost.engine.BoostEngine;
import com.clean.spaceplus.cleansdk.boost.engine.data.BoostDataManager;
import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.clean.spaceplus.cleansdk.boost.engine.process.ProcessResult;
import com.clean.spaceplus.cleansdk.junk.engine.DataTypeInterface;
import com.clean.spaceplus.cleansdk.junk.view.LinkedCheckableChild;
import com.clean.spaceplus.cleansdk.junk.view.LinkedCheckableGroup;
import com.clean.spaceplus.cleansdk.util.BackgroundThread;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author li.li
 * @Description:可扩展控件子列表bean
 * @date 2016/4/20 14:00
 * @copyright TCL-MIG
 */
public class JunkChildType extends LinkedCheckableChild implements LinkedCheckableGroup<JunkSubChildType> {
    public int childIcon;
    public String childTypeName;
    public String junkSuggestion;
    public String junkChildSize;
    public long junkSize;
    public String junkpkgname;
    public JunkModel junkModel;
    public CacheInfo cacheInfo; // TYPE_SYSTEM_CACHE 和 TYPE_SYS_FIXED_CACHE
    public int junkModelType = -1;
    public boolean hasExpand;
    public boolean halfCheck;
    public ProcessModel mProcessModel;
    public List<JunkSubChildType> mSubChildList;
    public int mWhiteListIndex=-1;

    public JunkChildType(LinkedCheckableGroup parent) {
        super(parent);
    }

    @Override
    public String toString() {
        return "JunkChildType";
    }

    public boolean setJunkModelChecked(final boolean isJunkChecked) {
        switch (junkModelType) {
            case DataTypeInterface.TYPE_SYSTEM_CACHE:
            case DataTypeInterface.TYPE_SYS_FIXED_CACHE:
                if (cacheInfo.isCheck() == isJunkChecked) {
                    return false;
                }
                cacheInfo.setCheck(isJunkChecked);
                checkJunkModelCheckStatus();
                return true;
            case DataTypeInterface.TYPE_APP_CACHE:
                CacheInfo cacheInfo = junkModel.getCacheInfo();
                if (cacheInfo.isCheck() == isJunkChecked) {
                    return false;
                }
                List<CacheInfo> cacheInfos = junkModel.getChildList();
                if (cacheInfos != null && !cacheInfos.isEmpty()) {
                    for (CacheInfo temp : cacheInfos) {
                        temp.setCheck(isJunkChecked);
                    }
                }
                cacheInfo.setCheck(isJunkChecked);
                junkModel.setChecked(isJunkChecked);
                return true;
            case DataTypeInterface.TYPE_APP_LEFT:
            case DataTypeInterface.TYPE_TEMP_FILE:
            case DataTypeInterface.TYPE_AD_FILE:
                SDcardRubbishResult sDcardRubbishResult = junkModel.getSdcardRubbishResult();
                if (sDcardRubbishResult.isCheck() == isJunkChecked) {
                    return false;
                }
                sDcardRubbishResult.setCheck(isJunkChecked);
                junkModel.setChecked(isJunkChecked);
                return true;
            case DataTypeInterface.TYPE_APK_FILE:
                APKModel apkModel = junkModel.getApkModel();
                if (apkModel.isCheck() == isJunkChecked) {
                    return false;
                }
                apkModel.setCheck(isJunkChecked);
                junkModel.setChecked(isJunkChecked);
                return true;
            case DataTypeInterface.TYPE_PROCESS:  // TYPE_PROCESS 需根据 isProcessChecked去判断是否勾选
                BoostDataManager boostDataManager = BoostDataManager.getInstance();
                ProcessResult processResult = (ProcessResult) boostDataManager.getResult(BoostEngine.BOOST_TASK_MEM);
                if (processResult != null) {
                    List<ProcessModel> results = processResult.getData();
                    if (results != null) {
                        for (ProcessModel processModel : results) {
                            if (processModel.getPkgName().equals(junkModel.getProcessModel().getPkgName())) {
                                processModel.setChecked(isJunkChecked);
                                break;
                            }
                        }
                        processResult.updateData(results);
                    }
                }
                if (junkModel.isProcessChecked() == isJunkChecked) {
                    return false;
                }
                junkModel.setProcessChecked(isJunkChecked);
                junkModel.setChecked(isJunkChecked);
                final ProcessModel processModel = junkModel.getProcessModel();
                if (processModel != null) {
                    processModel.setChecked(isJunkChecked);
                    BackgroundThread.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            int mark = ProcessWhiteListDAOHelper.getInstance().setChecked(processModel, isJunkChecked);
                            //添加用户不勾选和不勾选的状态
                            processModel.setIgnoreMark(mark);
                        }
                    });

                }

                return true;
            default:
                return false;
        }
    }

    /**
     * 只对 TYPE_SYSTEM_CACHE 和 TYPE_SYS_FIXED_CACHE 有效
     */
    private void checkJunkModelCheckStatus() {
        for (CacheInfo cacheInfo : junkModel.getChildList()) {
            if (!cacheInfo.isCheck()) {  // 如果有一个子项没有勾选，整个model就相当于没勾选
                junkModel.setChecked(false);
                break;
            }
        }
        junkModel.setChecked(true);
    }

    public static class JunkChildTypeComparator implements Comparator<JunkChildType> {
        @Override
        public int compare(JunkChildType lhs, JunkChildType rhs) {
            return rhs.junkSize < lhs.junkSize ? -1 : (lhs.junkSize == rhs.junkSize ? 0 : 1);
        }
    }

    @Override
    public void addChild(JunkSubChildType child) {
        if(mSubChildList == null){
            mSubChildList=new ArrayList<JunkSubChildType>();
        }
        mSubChildList.add(child);
    }

    @Override
    public void refreshCheckStatus() {
        if(mSubChildList != null){
            int childrenCount=mSubChildList.size();
            boolean childrenAllIsChecked = true;
            int num=childrenCount;
            int j=0;
            for(int i=0;i<childrenCount;i++){
                JunkSubChildType subChildType=mSubChildList.get(i);
                if(subChildType.isWhiteList){
                    num--;
                    j++;
                    continue;
                }
                if (!subChildType.isChildChecked) {
                    childrenAllIsChecked = false;
                    num--;
                }
            }
            if (num>0&&num+j<childrenCount){
                this.halfCheck=true;
            }else{
                this.halfCheck=false;
            }
            this.isChildChecked=childrenAllIsChecked;
            getParent().refreshCheckStatus();
        }
    }

    @Override
    public boolean isAllChecked() {
        return isChildChecked;
    }

    @Override
    public boolean isPartChecked() {
        return halfCheck;
    }

    @Override
    public boolean isNoneChecked() {
        return !isChildChecked;
    }

    @Override
    public void toggleCheck() {
        // 修改其即子的勾选状态
        this.isChildChecked = !this.isChildChecked;
        this.halfCheck = false;
        if (mSubChildList != null){
            for(JunkSubChildType item : mSubChildList){
                item.isChildChecked = this.isChildChecked;
            }
        }
        // 修改清理标识
        setJunkModelChecked(this.isChildChecked);
        // 更新父的勾选状态
        getParent().refreshCheckStatus();
    }

    @Override
    public List<JunkSubChildType> getChildren() {
        return this.mSubChildList;
    }

    @Override
    public boolean hasChild() {
        if(mSubChildList!=null && mSubChildList.size()>0){
            return true;
        }
        return false;
    }

    //获取子类的选中状态的数值
    public long getChildIsCheckSize(boolean isCheck){
        long size=0;
        if(hasChild()){
            for(JunkSubChildType type:mSubChildList){
                if(type.isChildChecked==isCheck){
                    size+=type.subJunkSize;
                }
            }
        }
        return size;
    }

    public int getChildIsCheckNum(boolean isCheck){
        int num=0;
        if(hasChild()){
            for(JunkSubChildType type:mSubChildList){
                if(type.isChildChecked==isCheck){
                    num++;
                }
            }
        }
        return num;
    }
}

