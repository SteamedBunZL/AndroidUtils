package tlog;

import java.util.Map;

/**
 * Created by hui.zhu on 2016/5/17.
 */
public interface HttpProvider {
    /**
     * 获取服务网络地址
     * @return 返回接口地址
     */
    String getURL();

    /**
     * 判断是否支持Post方法
     * @return 返回是否支持Post方法的判断结果
     */
    boolean supportPost();


    /**
     * 获取请求参数集
     * @return 返回参数值对
     */
    Map<String, String> getParams();

    /**
     * 获取上传实体集合
     * @return 返回需要上传的实体集合
     */
    Map<String, byte[]> getPostEntities();

    /**
     * 操作成功回调
     */
    void onSuccess();

    /**
     * 操作取消回调
     */
    void onCancel();

    /**
     * 发生错误时的回调
     * @param err 错误码
     */
    void onError(int err);
}
