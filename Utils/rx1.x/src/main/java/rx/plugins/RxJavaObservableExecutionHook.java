package rx.plugins;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscription;
import rx.Observable.Operator;

/**
 * Created by Steve on 2018/4/12.
 */

public abstract class RxJavaObservableExecutionHook {

    public <T> OnSubscribe<T> onCreate(OnSubscribe<T> f){
        return f;
    }

    public <T> OnSubscribe<T> onSubscribeStart(Observable<? extends T> observableInstance,final OnSubscribe<T> onSubscribe){
        return onSubscribe;
    }

    public <T> Subscription onSubscribeReturn(Subscription subscription){
        return subscription;
    }

    public <T> Throwable onSubscribeError(Throwable e){
        return e;
    }

    public <T,R> Operator<? extends R,? super T> onLift(final Operator<? extends R,? super T> lift){
        return lift;
    }
}
