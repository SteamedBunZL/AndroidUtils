package rx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Steve on 2018/4/11.
 */

public class SubscriptionList implements Subscription{

    private LinkedList<Subscription> subscriptions;
    private volatile boolean unsubscribed;


    public SubscriptionList() {
    }

    public SubscriptionList(final Subscription... subscriptions) {
        this.subscriptions = new LinkedList<Subscription>(Arrays.asList(subscriptions));
    }

    public SubscriptionList(Subscription s){
        this.subscriptions = new LinkedList<Subscription>();
        this.subscriptions.add(s);
    }

    public void add(final Subscription s){
        if (s.isUnsubscribed())
            return;

        if (!unsubscribed){
            synchronized (this){
                if (!unsubscribed){
                    LinkedList<Subscription> subs = subscriptions;
                    if (subs == null){
                        subs = new LinkedList<>();
                        subscriptions = subs;
                    }
                    subs.add(s);
                    return;
                }
            }
        }

        s.unsubscribe();;
    }

    public void remove(final Subscription s){
        if (!unsubscribed){
            boolean unsubscribed = false;
            synchronized (this){
                LinkedList<Subscription> subs = subscriptions;
                if (unsubscribed || subs == null)
                    return;
                unsubscribed = subs.remove(s);
            }

            if (unsubscribed)
                s.unsubscribe();
        }
    }

    @Override
    public void unsubscribe() {
        if (!unsubscribed){
            List<Subscription> list;
            synchronized (this){
                if (unsubscribed)
                    return;
                unsubscribed = true;
                list= subscriptions;
                subscriptions = null;
            }

            unsubscribeFromAll(list);
        }
    }

    private static void unsubscribeFromAll(Collection<Subscription> subscriptions){
        if (subscriptions == null)
            return;

        for(Subscription s:subscriptions){
            try {
                s.unsubscribe();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void clear(){
        if (!unsubscribed){
            List<Subscription> list;
            synchronized (this){
                list= subscriptions;
                subscriptions = null;
            }
            unsubscribeFromAll(list);
        }
    }

    @Override
    public boolean isUnsubscribed() {
        return unsubscribed;
    }

    public boolean hasSubscriptions(){
        if (!unsubscribed){
            synchronized (this){
                return !unsubscribed && subscriptions != null && !subscriptions.isEmpty();
            }
        }
        return false;
    }
}
