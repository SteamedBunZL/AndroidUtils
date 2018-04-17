package rx;

/**
 * Created by Steve on 2018/4/11.
 */

public interface Observer<T> {

    void onCompleted();

    void onError(Throwable e);

    void onNext(T t);
}
