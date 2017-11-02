package com.clean.spaceplus.cleansdk.base.db.process_list;

/**
 * @author zengtao.kuang
 * @Description: 锁定垃圾选项 模型
 * @date 2016/5/11 19:37
 * @copyright TCL-MIG
 */
public class JunkLockedModel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 系统缓存类型
     */
    public final static int TYPE_ALL_SYS_CACHE=0;
    /**
     *
     */
    public final static int TYPE_DB_ID = 1;
    /**
     *
     */
    public final static int TYPE_FILEPATH=2;



    public final static int STATUS_UNLOCKED = 0 ;

    public final static int STATUS_LOCKED=1;

    /**
     * 系统缓存大条目类型统一为一条锁定信息  其ID 为-1024
     */
    public final static int ID_ALL_SYS_CACHE = -1024;

    //对应clearpath5.db中的 id   系统缓存 ，sd卡缓存，残留 ，广告 根据此类型
    private int mId;
    //类型
    private int mType;
    //路径  大文件，apk安装包，临时文件根据此类型
    private String mFilePath;

    //状态
    private int mStatus;


    public JunkLockedModel(int type) {
        // TODO Auto-generated constructor stub
        this.mType =type;
    }

    public int getId() {
        return mId;
    }
    public void setId(int id) {
        this.mId = id;
    }
    public int getType() {
        return mType;
    }
    public void setType(int type) {
        this.mType = type;
    }
    public String getFilePath() {
        return mFilePath;
    }
    public void setFilePath(String filePath) {
        this.mFilePath = filePath;
    }
    public int getStatus() {
        return mStatus;
    }
    public void setStatus(int status) {
        this.mStatus = status;
    }
}
