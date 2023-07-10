package com.mooc.battle.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.mooc.battle.BattleApi;
import com.mooc.battle.GameConstant;
import com.mooc.battle.databinding.ActivityBeginSkillBinding;
import com.mooc.battle.model.SkillDesc;
import com.mooc.battle.util.Utils;
import com.mooc.common.utils.rxjava.RxUtils;
import com.mooc.commonbusiness.base.BaseActivity;
import com.mooc.commonbusiness.constants.IntentParamsConstants;
import com.mooc.commonbusiness.interfacewrapper.ARouterNavigationCallbackWrapper;
import com.mooc.commonbusiness.manager.BaseObserver;
import com.mooc.commonbusiness.model.HttpResponse;
import com.mooc.commonbusiness.net.ApiService;
import com.mooc.commonbusiness.route.Paths;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;

@Route(path = Paths.PAGE_BEGIN_SKILL)
public class BeginSkillActivity extends BaseActivity {

    ActivityBeginSkillBinding activityBeginSkillBinding;
    String title;
    String tournament_id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityBeginSkillBinding = ActivityBeginSkillBinding.inflate(getLayoutInflater());
        setContentView(activityBeginSkillBinding.getRoot());

        title = getIntent().getStringExtra(IntentParamsConstants.PARAMS_RESOURCE_TITLE);
        tournament_id = getIntent().getStringExtra(IntentParamsConstants.PARAMS_RESOURCE_ID);

        activityBeginSkillBinding.tvTitle.setText(title);
        activityBeginSkillBinding.tvBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(Paths.PAGE_SKILL_ANSWER)
                        .withString(IntentParamsConstants.PARAMS_RESOURCE_ID,tournament_id)
                        .navigation(BeginSkillActivity.this, new ARouterNavigationCallbackWrapper() {
                    @Override
                    public void onArrival(@Nullable Postcard postcard) {
                        finish();
                    }
                });
            }
        });

        getTipInfo();
    }

    /**
     * 获取提示信息
     */
    void getTipInfo() {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put(GameConstant.GAME_PARAMS_ACTION, GameConstant.SKILL_JOIN_DESC);
            requestData.put(GameConstant.SKILL_TOURNAMENT_ID, tournament_id);
            requestData.put(GameConstant.SKILL_TITLE, title);
        } catch (JSONException ignored) {
        }
        RequestBody requestBody = Utils.getRequestBody(requestData);
        ApiService.getRetrofit().create(BattleApi.class).getJoinTipInfo(requestBody).compose(RxUtils.applySchedulers())
                .subscribe(new BaseObserver<HttpResponse<SkillDesc>>(this) {
                               @Override
                               public void onSuccess(HttpResponse<SkillDesc> response) {
                                   SkillDesc data = response.getData();
                                   if (data != null) {
                                       activityBeginSkillBinding.tvTitle.setText(data.getTitle());
                                       activityBeginSkillBinding.tvDesc1.setText(data.getJoin_desc_1());
                                       activityBeginSkillBinding.tvDesc2.setText(data.getJoin_desc_2());
                                   }
                               }
                           }
                );
    }
}
