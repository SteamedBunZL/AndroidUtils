package tlog;

/**
 * Created by hui.zhu on 2016/5/24.
 */
public interface IReportCallback {

    void onSuccess(String result);

    void onFailed(int code, String msg, Object obj);

    void onCancel();
}
