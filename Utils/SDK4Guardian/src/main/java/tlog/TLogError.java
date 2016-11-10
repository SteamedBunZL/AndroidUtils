package tlog;

/**
 * Created by hui.zhu on 2016/6/2.
 */
public interface TLogError {
    int SUCCESS = 0;
    int FAIL_UNKNOWN = -1;
    int FAIL_CONNECT_TIMEOUT = -2;
    int FAIL_NOT_FOUND = -3;
    int FAIL_IO_ERROR = -4;
    int CANCEL = -5;
}
