package tlog.manager;

/**
 * description:
 * author hui.zhu
 * date 2016/10/24
 * copyright TCL-MIG
 */


public abstract class Singleton<T> {
    private T mInstance;
    protected abstract T create();

    public final T get() {
        synchronized (this) {
            if (mInstance == null) {
                mInstance = create();
            }

            return mInstance;
        }
    }
}
