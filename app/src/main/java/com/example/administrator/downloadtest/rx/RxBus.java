package com.example.administrator.downloadtest.rx;

import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.components.ActivityLifecycleProvider;
import com.trello.rxlifecycle.components.FragmentLifecycleProvider;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by tlh on 2016/11/26 0026.
 */

public class RxBus {

    private static RxBus instance;
    private static CompositeSubscription mCompositeSubscription;
    //PublishSubject:源码中你会发现PublishSubjet继承抽象类Subject，Subject而又继承了Observable，实现了Observer
    //所以可以说PublishSubject既是Observable，又是Observer。意思就是说既可以发送信息又可以接收信息
    private final Subject<Object, Object> _bus = new SerializedSubject<>(PublishSubject.<Object>create());

    private RxBus() {

    }

    public static RxBus getInstance() {
        if (instance == null)
            synchronized (RxBus.class) {
                if (instance == null)
                    instance = new RxBus();
                mCompositeSubscription = new CompositeSubscription();
            }
        return instance;
    }

    public void send(Object o) {
        _bus.onNext(o);
    }


    public Observable<Object> toObservable() {
        return _bus;
    }

    public boolean hasObservers() {
        return _bus.hasObservers();
    }


    public void receive(Func1<Object, Boolean> func1, Action1<Object> onNext, Action1<Throwable> onError, Action0 onCompleted) {
        mCompositeSubscription.add(RxBus.getInstance().toObservable().filter(func1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError == null ? new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                } : onError, onCompleted));

    }

    public void unsubscribe() {
        if (instance.hasObservers() && mCompositeSubscription.isUnsubscribed())
            mCompositeSubscription.unsubscribe();
    }

    //---------------强大的分割线，下面是Rxjava和Activity以及Fragment生命周期邦定有关的。这里可以先不管-------------------------
    public static SubscriberBuilder with(FragmentLifecycleProvider fragmentLifecycleProvider) {
        return new SubscriberBuilder(fragmentLifecycleProvider);
    }

    public static SubscriberBuilder with(ActivityLifecycleProvider activityLifecycleProvider) {
        return new SubscriberBuilder(activityLifecycleProvider);
    }

    public static class SubscriberBuilder {

        private FragmentLifecycleProvider mFragLifecycleProvider;
        private ActivityLifecycleProvider mActLifecycleProvider;
        private FragmentEvent mFragmentEndEvent;
        private ActivityEvent mActivityEndEvent;
        private int what;

        public SubscriberBuilder(FragmentLifecycleProvider provider) {
            this.mFragLifecycleProvider = provider;
        }

        public SubscriberBuilder(ActivityLifecycleProvider provider) {
            this.mActLifecycleProvider = provider;
        }

        public SubscriberBuilder what(int what) {
            this.what = what;
            return this;
        }

        public SubscriberBuilder stopEvent(FragmentEvent event) {
            this.mFragmentEndEvent = event;
            return this;
        }

        public SubscriberBuilder stopEvent(ActivityEvent event) {
            this.mActivityEndEvent = event;
            return this;
        }


        public Subscription receive(Action1<? super Object> onNext, Action1<Throwable> onError) {
            if (mFragLifecycleProvider != null) {
                return RxBus.getInstance().toObservable()
                        .compose(mFragmentEndEvent == null ? mFragLifecycleProvider.<Object>bindToLifecycle() : mFragLifecycleProvider.<Object>bindUntilEvent(mFragmentEndEvent)) // 绑定生命周期
                        //过滤 根据code判断返回事件
                        .subscribe(onNext, onError == null ? new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        } : onError);
            }
            if (mActLifecycleProvider != null) {
                return RxBus.getInstance().toObservable()
                        .compose(mActivityEndEvent == null ? mActLifecycleProvider.<Object>bindToLifecycle() : mActLifecycleProvider.<Object>bindUntilEvent(mActivityEndEvent))

                        .subscribe(onNext, onError == null ? new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        } : onError);
            }

            return null;
        }
    }
}
