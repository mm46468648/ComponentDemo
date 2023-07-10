package com.mooc.studyroom.ui.fragment.myhonor;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.android.arouter.launcher.ARouter;
import com.mooc.commonbusiness.route.Paths;
import com.mooc.studyroom.R;
import com.mooc.studyroom.ui.adapter.MyHonorVpAdapter;

/**
 * 我的荣誉
 */
public class MyHonorFragment extends Fragment {

    private TextView tvApply, tvMedal, tvCertificate;
    private LinearLayout llApply;
    private ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.studyroom_fragment_my_honor, null);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewPager);
        tvApply = view.findViewById(R.id.tvApply);
        tvMedal = view.findViewById(R.id.tvMedal);
        tvCertificate = view.findViewById(R.id.tvCertificate);
        llApply = view.findViewById(R.id.llApply);
        setupViewPager(viewPager);


        initView();

    }

    private void initView() {
        //默认选中0
        changeTabStyle(0);
        //点击跳转证书申请
        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(Paths.PAGE_APPLYCER).navigation();
            }
        });
        //点击切换勋章
        tvMedal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        //点击切换证书
        tvCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        //viewpager切换监听
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                changeTabStyle(position);
            }
        });

    }

    /**
     * 改变tab的样式
     *
     * @param i 0证书，1勋章
     */
    private void changeTabStyle(int i) {
        changeTextViewBold(i == 0, tvCertificate);
        changeTextViewBold(i != 0, tvMedal);

        //显示或隐藏申请证书按钮
        int applyVisibilty = i ==0 ? View.VISIBLE : View.GONE;
        llApply.setVisibility(applyVisibilty);

    }

    /**
     * 改变文字是否加粗
     *
     * @param b
     * @param textView
     */
    private void changeTextViewBold(boolean b, TextView textView) {
        if (b) {
            textView.setTextSize(17);
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.color_2));
            textView.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            textView.setTextSize(13);
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.color_6));
            textView.setTypeface(Typeface.DEFAULT);
        }
    }


    private void setupViewPager(ViewPager2 viewPager) {


        MyHonorVpAdapter tabAdapter = new MyHonorVpAdapter(requireActivity());
        viewPager.setAdapter(tabAdapter);

    }

}
