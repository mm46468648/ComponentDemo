package com.mooc.commonbusiness.base;

import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;


abstract class CacheFragment extends Fragment {

    protected View mRootView = null;
    protected boolean mViewCreated = false;

    private SparseArray<View> mCacheViews = null;

    ViewDataBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            final int layoutId = getLayoutRes();
            if (layoutId > 0) {
                binding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false);
                if (binding == null) {
                    mRootView = inflater.inflate(getLayoutRes(), container, false);
                } else {
                    mRootView = binding.getRoot();
                }

            }
        }
        mViewCreated = true;
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRootView = null;
        if (mCacheViews != null) {
            mCacheViews.clear();
            mCacheViews = null;
        }
        mViewCreated = false;
    }

    @Nullable
    public View getRootView() {
        return mRootView;
    }

    public ViewDataBinding getBinding() {
        return binding;
    }

    public final <V extends View> V getView(@IdRes int id) {
        if (mCacheViews == null) {
            mCacheViews = new SparseArray<>();
        }
        View view = mCacheViews.get(id);
        if (view == null) {
            view = findViewById(id);
            if (view != null) {
                mCacheViews.put(id, view);
            }
        }
        return (V) view;
    }

    public final <V extends View> V findViewById(@IdRes int id) {
        if (mRootView == null) {
            return null;
        }
        return mRootView.findViewById(id);
    }

    protected abstract int getLayoutRes();

    public void initStatusBar(int color) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
