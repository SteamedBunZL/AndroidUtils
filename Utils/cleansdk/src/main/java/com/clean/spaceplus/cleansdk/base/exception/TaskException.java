package com.clean.spaceplus.cleansdk.base.exception;

import android.content.res.Resources;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.R;
import com.clean.spaceplus.cleansdk.app.SpaceApplication;

/**
 * @author Jerry
 * @Description:
 * @date 2016/4/28 14:19
 * @copyright TCL-MIG
 */
public class TaskException extends Exception{
    public String code;
    public String msg;

    private static ExceptionDeclare exceptionDeclare;

    public enum TaskError {
        // 无网络链接
        noneNetwork,
        // 连接超时
        timeout,
        // 响应超时
        socketTimeout,
        // 返回数据不合法
        resultIllegal
    }

   public TaskException(String code){
       this.code = code;
   }

    public TaskException(String code, String msg){
        this(code);
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        if (!TextUtils.isEmpty(msg)){
            return msg;
        }
        if (!TextUtils.isEmpty(code) && exceptionDeclare != null){
            String msg = exceptionDeclare.getExceptionMsg(code);
            if (!TextUtils.isEmpty(msg)){
                return msg;
            }
        }
        try {
            Resources res = SpaceApplication.getInstance().getContext().getResources();

            TaskError error = TaskError.valueOf(code);
            if (error == TaskError.noneNetwork)
                msg = res.getString(R.string.comm_error_noneNetwork);
            else if (error == TaskError.socketTimeout || error == TaskError.timeout)
                msg = res.getString(R.string.comm_error_timeout);
            else if (error == TaskError.resultIllegal)
                msg = res.getString(R.string.comm_error_resultIllegal);
            if (!TextUtils.isEmpty(msg))
                return msg;
        } catch (Exception e) {
        }
        return super.getMessage();
    }

    public static void configExceptionDeclare(ExceptionDeclare exceptionDeclare) {
        TaskException.exceptionDeclare = exceptionDeclare;
    }

    public static TaskException getnerataException(String code){
        TaskException taskException = new TaskException(code);
        taskException.setMsg(taskException.getMessage());
        return taskException;
    }


}
