package com.mooc.battle.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.mooc.battle.adapter.SkillListAdapter;
import com.mooc.battle.adapter.SkillMyCreateAdapter;
import com.mooc.battle.databinding.ActivitySkillListBinding;
import com.mooc.commonbusiness.base.BaseActivity;
import com.mooc.commonbusiness.route.Paths;

import java.util.ArrayList;
import java.util.List;

/**
 * 比武列表页面
 */
@Route(path = Paths.PAGE_SKILL_LIST)
public class SkillListActivity extends BaseActivity {

    ActivitySkillListBinding activitySkillListBinding;
    public static final String PARAMS_PAGE_INDEX =  "PARAMS_PAGE_INDEX";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int pageIndex = getIntent().getIntExtra(PARAMS_PAGE_INDEX,0);
        activitySkillListBinding = ActivitySkillListBinding.inflate(getLayoutInflater());
        setContentView(activitySkillListBinding.getRoot());
        activitySkillListBinding.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ArrayList<String> tabTitle = new ArrayList<>();
        tabTitle.add("我发起的比武");
        tabTitle.add("正在进行的比武");

        SkillListAdapter skillMyCreateAdapter = new SkillListAdapter(this);
        activitySkillListBinding.viewPage2.setAdapter(skillMyCreateAdapter);
        activitySkillListBinding.mctSkillTab.setUpWithViewPage2(activitySkillListBinding.viewPage2,tabTitle);
        activitySkillListBinding.viewPage2.setCurrentItem(pageIndex);
    }
}
