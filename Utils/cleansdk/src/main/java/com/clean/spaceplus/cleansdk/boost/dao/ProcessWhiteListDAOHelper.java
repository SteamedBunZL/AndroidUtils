package com.clean.spaceplus.cleansdk.boost.dao;

import com.clean.spaceplus.cleansdk.base.db.process_list.ProcessWhiteList;
import com.clean.spaceplus.cleansdk.boost.engine.BoostEngine;
import com.clean.spaceplus.cleansdk.boost.engine.data.BoostDataManager;
import com.clean.spaceplus.cleansdk.boost.engine.data.BoostResult;
import com.clean.spaceplus.cleansdk.boost.engine.data.ProcessModel;
import com.clean.spaceplus.cleansdk.boost.util.ProcessWhiteListMarkHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengtao.kuang
 * @Description: 进程白名单DAO辅助类
 * @date 2016/4/19 19:36
 * @copyright TCL-MIG
 */
public class ProcessWhiteListDAOHelper {

    private Object mTaskWhiteListLock = new Object();

    private ProcessWhiteListDAO mProcessWhiteListDAO;
    public interface ProcessWhiteListDAOHelperHolder{
        ProcessWhiteListDAOHelper sInstance = new ProcessWhiteListDAOHelper();
    }

    public static final ProcessWhiteListDAOHelper getInstance(){
        return ProcessWhiteListDAOHelperHolder.sInstance;
    }

    public ProcessWhiteListDAO getProcessWhiteListDAO(){
        if(mProcessWhiteListDAO==null){
            mProcessWhiteListDAO = new ProcessWhiteListDAO();
        }
        return mProcessWhiteListDAO;
    }

    public int getProcessWhiteListCount() {
        synchronized (mTaskWhiteListLock) {
            return getProcessWhiteListDAO().getProcessWhiteListCount();
        }
    }

    /**
     * 仅仅在白名单设置界面中使用
     * @return
     */
    public List<ProcessModel> getProcessWhiteList() {
        synchronized (mTaskWhiteListLock) {
            ArrayList<ProcessModel> models = new ArrayList<ProcessModel>();
            List<ProcessWhiteList> datas = getProcessWhiteListDAO().getProcessWhiteList();
            if (datas != null && !datas.isEmpty()) {
                models.ensureCapacity(datas.size());
                for (ProcessWhiteList data : datas) {
                    ProcessModel m = new ProcessModel();
                    m.setPkgName(data.getPkgname());
                    m.setTitle(data.getTitle());
                    m.setIgnoreMark(data.getMark());
                    models.add(m);
                }
            }
            return models;
        }
    }



    /**
     * why 区分to save 和to return
     *
     * 从数据库中查出来的mark和实际用于界面的不一定一样，因为数据库只代表静态结果，用于界面的数据综合考虑了静态和动态的东西。
     * 但动态的东西不能存入数据库。
     * 所以有修改状态的话，只能记录静态的东西，动态的只用于返回。
     *
     * */
    public int setChecked(ProcessModel item, boolean checked) {
        synchronized (mTaskWhiteListLock) {
            // to save
            ProcessWhiteListDAO dao = getProcessWhiteListDAO();
            int mark = dao.queryMarkByName(item.getPkgName());
            mark = ProcessWhiteListMarkHelper.setUserChecked(mark, checked);
            dao.insertOrUpdate(item.getPkgName(), item.getTitle(), mark);

            // to return
            int markReturn = ProcessWhiteListMarkHelper.setUserChecked(item.getIgnoreMark(), checked);
            return markReturn;
        }
    }

    /**
     * 用户手动加入白名单
     * @param item
     * @return
     */
    public void addProcessWhiteListItem(ProcessModel item) {
        item.mIsHide = true;
        addProcessWhiteListItem(item.getPkgName(), item.getTitle());
    }

    public void addProcessWhiteListItem(String pkgName, String title) {
        synchronized (mTaskWhiteListLock) {
            // update process data
            BoostResult result = BoostDataManager.getInstance()
                    .getResult(BoostEngine.BOOST_TASK_MEM);
            if (result != null) {
                result.removeData(pkgName);
            }

            int oldMark = getProcessWhiteListDAO().queryMarkByName(pkgName);
            int newMark = ProcessWhiteListMarkHelper.addToWhiteListByUser(oldMark);
            if (newMark == 0) {
                getProcessWhiteListDAO().deleteProcessWhiteListItem(pkgName);
            } else {
                getProcessWhiteListDAO().insertOrUpdate(pkgName, title, newMark);
            }
        }
    }

    public void loadAllProcessWhiteList() {

        synchronized (mTaskWhiteListLock) {
            getProcessWhiteListDAO().loadAllProcessWhiteList();
        }
    }

    /**
     * 预期达到效果：
     * 1. 如果是原始就是白名单的，直接去掉FLAG_WHITELIST属性，加FLAG_USER_UNCHECKED
     * 2. 如果没FLAG_WHITELIST属性的，属性置0即可，让重新计算
     *
     * 属性是属性，包括自然属性（库以及我们自动规则生成的属性）和用户设置属性（包括用户要显示，不勾选，加白处理）
     * 对于最终显示而言，优先考虑用户属性， --- 设想而已，没时间这样去重构
     *
     * */
    public void removeProcessWhiteListItem(ProcessModel item) {
        synchronized (mTaskWhiteListLock) {
            int newMark = ProcessWhiteListMarkHelper.removeFromWhiteList(item.getIgnoreMark());
            getProcessWhiteListDAO().insertOrUpdate(item.getPkgName(), item.getTitle(), newMark);
        }
    }

    public int getProcessWhiteListIgnoreLevel(String pkgName) {
        synchronized (mTaskWhiteListLock) {
            return getProcessWhiteListDAO().queryMarkByName(pkgName);
        }
    }
}
