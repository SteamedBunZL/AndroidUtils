package rx.functions;

/**
 * Created by Steve on 2018/4/12.
 */

public interface Action1<T> extends Action {
    void call(T t);
}
