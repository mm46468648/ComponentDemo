package com.mooc.studyroom.ui.fragment.record;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.lxj.xpopup.XPopup;
import com.mooc.studyroom.R;
import com.mooc.studyroom.ui.adapter.StudyRecordVpAdapter;
import com.mooc.studyroom.ui.fragment.myhonor.MyHonorFragment;
import com.mooc.studyroom.ui.pop.ClearRecordPop;
import com.mooc.studyroom.viewmodel.StudyRecordActivityViewModel;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * 学习记录
 */
public class StudyRecordFragment extends Fragment implements View.OnClickListener {

    private ViewPager2 viewPager;
    int currentIndex = 0;  //当前显示的位置，默认0
    private TextView tvStudyHistory;
    private TextView tvComplete;
    private TextView tvJoin;
    private FrameLayout flClear;
    private View viewShadow;
    private ArrayList<TextView> tabTextViewList = new ArrayList<TextView>();


    StudyRecordActivityViewModel parentViewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        parentViewModel = ViewModelProviders.of(requireActivity()).get(StudyRecordActivityViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.studyroom_fragment_study_record, null);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewPager);
        tvStudyHistory = view.findViewById(R.id.tvStudyHistory);
        tvComplete = view.findViewById(R.id.tvComplete);
        tvJoin = view.findViewById(R.id.tvJoin);
        flClear = view.findViewById(R.id.flClear);
        viewShadow = view.findViewById(R.id.viewShadow);
        initViewPager(viewPager);
        initTab();
    }

    /**
     * 初始化tab与viewPager关联
     */
    private void initTab() {
        tabTextViewList.clear();
        tabTextViewList.add(tvStudyHistory);
        tabTextViewList.add(tvComplete);
        tabTextViewList.add(tvJoin);

        tvStudyHistory.setOnClickListener(this);
        tvComplete.setOnClickListener(this);
        tvJoin.setOnClickListener(this);
        viewShadow.setOnClickListener(this);
        flClear.setOnClickListener(this);

        //默认选中0
        seleteTab(0);

    }

    private void initViewPager(ViewPager2 viewPager) {
        StudyRecordVpAdapter studyArchiveVpAdapter = new StudyRecordVpAdapter(getActivity());
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
                seleteTab(position);
            }
        });
        viewPager.setAdapter(studyArchiveVpAdapter);

        if(parentViewModel!=null){
            //直接打开参加的学习项目
            parentViewModel.getSelectStudyProject().observe(this,aBoolean -> {
                viewPager.setCurrentItem(2);
            });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tvStudyHistory) {
            if (currentIndex != 0) {    //如果不是0，则切换到0
                changePosition(0);
                return;
            }
            //如果是0，则显示或清空历史布局
            setClearHistoryLayout(flClear.getVisibility() == View.VISIBLE);
        } else if (id == R.id.tvComplete) {
            changePosition(1);
        } else if (id == R.id.tvJoin) {
            changePosition(2);
        } else if (id == R.id.viewShadow) {
            setClearHistoryLayout(true);
        } else if (id == R.id.flClear) {//eventbus通知fragment，清除学习记录
            setClearHistoryLayout(true);
            //通知学习记录页面清除数据
            showClearPop();
//                EventBus.getDefault().post(new ClearStudyRecordEvent());
        }
    }

    /**
     * 显示清空弹窗
     */
    private void showClearPop() {
        ClearRecordPop clearRecordPop = new ClearRecordPop(requireContext());
        clearRecordPop.setOnClickRight(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                //点击清空
                StudyRecordActivityViewModel studyRecordActivityViewModel = ViewModelProviders.of(requireActivity()).get(StudyRecordActivityViewModel.class);
                studyRecordActivityViewModel.deleteStudyRecord();
                return null;
            }
        });

        new XPopup.Builder(requireContext())
                .asCustom(clearRecordPop)
                .show();
    }

    /**
     * 设置显示或隐藏晴空历史布局
     * @param hide 是否隐藏
     */
    private void setClearHistoryLayout(boolean hide) {
        if(hide){
            flClear.setVisibility(View.GONE);
            viewShadow.setVisibility(View.GONE);
            setTabOneDrawable(R.mipmap.studyroom_ic_history_down);
            return;
        }


        flClear.setVisibility(View.VISIBLE);
        viewShadow.setVisibility(View.VISIBLE);
        setTabOneDrawable(R.mipmap.studyroom_ic_history_up);


    }

    /**
     * 切换位置
     *
     * @param i
     */
    private void changePosition(int i) {

        if (viewPager.getAdapter()!=null && i >= viewPager.getAdapter().getItemCount()) return;
        viewPager.setCurrentItem(i);
    }

    /**
     * 设置Tab的选中和未选中状态
     *
     * @param position
     */
    private void seleteTab(int position) {
        for (int i = 0; i < tabTextViewList.size(); i++) {
            TextView textView = tabTextViewList.get(i);
            if (position == i) {
                //设置选中状态

                textView.setTextSize(17);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.color_2));
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                if (position == 0) {    //如果选中的不是第一个学习历史还要设置drawright隐藏
                    setTabOneDrawable(R.mipmap.studyroom_ic_history_down);
                } else {
                    setClearHistoryLayout(true);
                    setTabOneDrawable(-1);
                }

            } else { //设置未选中状态
                textView.setTextSize(13);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.color_6));
                textView.setTypeface(Typeface.DEFAULT);

            }
        }
    }

    /**
     * 设置第一个tab的drawableRight
     *
     * @param drawableRes -1代表空
     */
    private void setTabOneDrawable(int drawableRes) {
        Drawable drawable = drawableRes == -1 ? null : ContextCompat.getDrawable(getContext(), drawableRes);
        tvStudyHistory.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
    }


}
