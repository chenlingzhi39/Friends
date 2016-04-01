package com.cyan.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.util.RxBus;

import rx.Observable;

/**
 * Created by Administrator on 2016/3/28.
 */
public abstract class BaseFragment extends Fragment {
    protected View fragmentRootView;
    protected int mContentViewId;
    protected Observable<String> mReSearchObservable;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mReSearchObservable= RxBus.get().register("reSearch",String.class);
        if (null == fragmentRootView) {
            if (getClass().isAnnotationPresent(ActivityFragmentInject.class)) {
                ActivityFragmentInject annotation = getClass()
                        .getAnnotation(ActivityFragmentInject.class);
                mContentViewId = annotation.contentViewId();
            } else {
                throw new RuntimeException(
                        "Class must add annotations of ActivityFragmentInitParams.class");
            }
            fragmentRootView = inflater.inflate(mContentViewId, container, false);
            initView(fragmentRootView);
        }

        return fragmentRootView;
    }
    protected abstract void initView(View fragmentRootView);

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister("reSearch",mReSearchObservable);
    }
}
