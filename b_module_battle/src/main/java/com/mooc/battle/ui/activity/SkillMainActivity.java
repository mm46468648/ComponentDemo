package com.mooc.battle.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mooc.battle.R;
import com.mooc.battle.adapter.GameNoticeAdapter;
import com.mooc.battle.databinding.ActivitySkillMainBinding;
import com.mooc.battle.model.BattleExp;
import com.mooc.battle.model.GameNotice;
import com.mooc.battle.model.GameNoticeBean;
import com.mooc.battle.util.GlideCircleWithBorder;
import com.mooc.battle.util.RequestUtil;
import com.mooc.common.utils.rxjava.RxUtils;
import com.mooc.commonbusiness.base.BaseActivity;
import com.mooc.commonbusiness.global.GlobalsUserManager;
import com.mooc.commonbusiness.manager.BaseObserver;
import com.mooc.commonbusiness.model.HttpResponse;
import com.mooc.commonbusiness.model.UserInfo;
import com.mooc.commonbusiness.route.Paths;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import retrofit2.http.Path;

/**
 * 比武平台首页页面
 */
@Route(path = Paths.PAGE_SKILL_MAIN)
public class SkillMainActivity extends BaseActivity {

    ActivitySkillMainBinding activitySkillMainBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitySkillMainBinding = ActivitySkillMainBinding.inflate(getLayoutInflater());
        setContentView(activitySkillMainBinding.getRoot());

        initView();
        initData();
    }

    private void initData() {
        UserInfo userBean = GlobalsUserManager.INSTANCE.getUserInfo();
        Glide.with(this).load(userBean.getAvatar())
                .apply(new RequestOptions().error(R.mipmap.common_ic_user_head_default)
                        .placeholder(R.mipmap.common_ic_user_head_default).centerCrop()
                        .transform(new GlideCircleWithBorder(2, Color.parseColor("#FFFFFF")))).into(activitySkillMainBinding.imgUserHeader);
        activitySkillMainBinding.tvName.setText(userBean.getName());

        RequestUtil.getUserApi().getExperience().compose(RxUtils.applySchedulers())
                .subscribe(new BaseObserver<HttpResponse<BattleExp>>(this) {
                               @Override
                               public void onSuccess(HttpResponse<BattleExp> listHttpResponse) {
                                   BattleExp data = listHttpResponse.getData();
                                   if(data !=null){
                                        activitySkillMainBinding.tvLevel.setText("Lv." + data.level);
                                        activitySkillMainBinding.progress.setMax(data.current_level_max_exp);
                                        activitySkillMainBinding.progress.setProgress(data.current_level_exp);
                                    }
                               }
                           }
                );
    }

    private void initView() {
        activitySkillMainBinding.commonTitle.setOnLeftClickListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                finish();
                return null;
            }
        });
        activitySkillMainBinding.movMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(Paths.PAGE_SKILL_LIST).withInt(SkillListActivity.PARAMS_PAGE_INDEX,0).navigation();
            }
        });
        activitySkillMainBinding.movIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(Paths.PAGE_SKILL_LIST).withInt(SkillListActivity.PARAMS_PAGE_INDEX,1).navigation();
            }
        });
        activitySkillMainBinding.movNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(Paths.PAGE_CREATE_SKILL).navigation();
            }
        });
    }
}
