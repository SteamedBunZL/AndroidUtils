package rx.functions;

/**
 * Created by Steve on 2018/4/12.
 */

public interface Func1<T,R> extends Function {
    R call(T t);
}
