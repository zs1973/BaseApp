package com.ofu.base.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ofu.base.mvp.BasePresenter;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by coder on 2017/8/4.
 */

public abstract class BaseFragment<T extends BasePresenter> extends SupportFragment {

    protected Toolbar _toolbar;
    //protected LoadingDialog _loadDialog;

    private Unbinder mUnbinder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(provideLayoutId(), container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        if (!needLazyLoad() && !needLoadDataAfterEnterAnimationEnd()) {
            init();
        }
        return rootView;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (needLazyLoad()) {
            init();
        }
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        if (needLoadDataAfterEnterAnimationEnd()) {
            init();
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (asContainerFragment()) {
            initChildFragment();
        }
    }

    /**
     *提供布局Id
     *
     * @return 布局Id
     */
    protected abstract int provideLayoutId();

    /**
     * 提供ToolBar
     *
     * @return Toolbar
     */
    protected abstract Toolbar provideToolbar();

    /**
     * 处理一些逻辑
     */
    protected abstract void toDo();

    /**
     * 提供子类Fragment的Presenter 用于解除订阅
     *
     * @return BasePresenter的子类
     */
    protected abstract T providePresenter();

    /**
     * 网络错误时调用此方法
     */
    protected abstract void onNetWorkError();

    /**
     * 此Fragment是否作为容器Fragment
     *
     * @return 返回true表示作为容器，false不作为容器
     */
    protected abstract boolean asContainerFragment();

    /**
     * 是否需要懒加载
     *
     * @return true表示需要，false表示不需要
     */
    protected abstract boolean needLazyLoad();

    /**
     * 是否在Fragment进入动画结束之后进行其他操作
     *
     * @return true表示需要，false表示不需要
     */
    protected abstract boolean needLoadDataAfterEnterAnimationEnd();

    /**
     * 是否需要
     * @return
     */
    protected abstract boolean needLoadingDialog();

    protected void setToolBarTitle(String title) {
//        _toolbar = provideToolbar();
//        if (_toolbar != null) {
//            TextView tv = (TextView) _toolbar.findViewById(R.id.toolBar_title);
//            tv.setText(title);
//        }
    }

    protected void initChildFragment() {
        //// TODO: 2017/8/12  重写此类需要asContainerFragment返回true
    }

    private void init() {
        if (needLoadingDialog()) {
            initLoadingDialog();
        }
        if (!netWorkAvailable()) {
            onNetWorkError();
        }
        initToolBar();
        toDo();
    }

    private void initToolBar() {
        _toolbar = provideToolbar();
        if (_toolbar != null) {
            //_toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        }
    }

    private void initLoadingDialog() {
       // if (_loadDialog == null) {
            //_loadDialog = new LoadingDialog(_mActivity, "努力加载中");
        //}
        //_loadDialog.show();
    }

    private boolean netWorkAvailable() {
        ConnectivityManager mgr = (ConnectivityManager) _mActivity.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (NetworkInfo anInfo : info) {
                if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        if (providePresenter() != null) {
            providePresenter().unSubscribe();
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
