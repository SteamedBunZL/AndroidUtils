package rx;

import rx.functions.Action1;
import rx.functions.Func1;
import rx.plugins.RxJavaObservableExecutionHook;

/**
 * Created by Steve on 2018/4/11.
 */

public class Observable<T> {

    public interface OnSubscribe<T> extends Action1<Subscriber<? super T>>{

    }

    public interface Operator<R,T> extends Func1<Subscriber<? super R>,Subscriber<? super T>>{

    }

    //private static final RxJavaObservableExecutionHook hook =

    final OnSubscribe<T> onSubscribe;

    protected Observable(OnSubscribe<T> f){
        this.onSubscribe = f;
    }

    public final static <T> Observable<T> create(OnSubscribe<T> f){
        return null;
    }
}
