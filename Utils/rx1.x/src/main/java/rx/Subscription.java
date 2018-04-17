package rx;

/**
 * Created by Steve on 2018/4/11.
 */

public interface Subscription {

    void unsubscribe();

    boolean isUnsubscribed();
}
