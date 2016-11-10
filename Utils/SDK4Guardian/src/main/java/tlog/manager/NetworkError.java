package tlog.manager;

/**
 * description:
 * author hui.zhu
 * date 2016/10/24
 * copyright TCL-MIG
 */
public interface  NetworkError {
    int SUCCESS = 0;
    int FAIL_UNKNOWN = -1;
    int FAIL_CONNECT_TIMEOUT = -2;
    int FAIL_NOT_FOUND = -3;
    int FAIL_IO_ERROR = -4;
    int CANCEL = -5;
    int NO_AVALIABLE_NETWORK = -6;

}
