package tlog;


import tlog.manager.TLogReport;

/**
 * Created by hui.zhu on 2016/5/24.
 */
public abstract class PostTaskProvider extends TaskProvider {
    public static final String JSON_HEAD_FMT = "{appkey:\"%1$s\",";
    public final String JSON_HEAD;
    public static final String JSON_END = "]}";
    protected String errorMsg;
    protected IReportCallback callback;

    public PostTaskProvider(IReportCallback callback, String appKey,String defaultStr) {
        super();
        this.callback = callback;
        JSON_HEAD = String.format(JSON_HEAD_FMT, appKey);
    }

    private String getCommonStr(String appkey,String defaultStr){
        StringBuffer sb = new StringBuffer();
        sb.append("{\"appkey\":\"").append(appkey).append("\"").append(",");
        sb.append("\"default\":").append(defaultStr).append(",");
        sb.append("\"events\":[");
        return sb.toString();
    }



    @Override
    public void onSuccess() {
        super.onSuccess();
        if (callback != null) {
            callback.onSuccess("0");
        }
    }

    @Override
    public void onCancel() {
        super.onCancel();
        if (callback != null) {
            callback.onCancel();
        }
    }

    @Override
    public void onError(final int err) {
        super.onError(err);
        if (callback != null) {
            callback.onFailed(err, errorMsg, null);
        }
    }

    public void execute() {
        TLogReport tLogReport = new TLogReport(this);
        tLogReport.send();
    }


}
