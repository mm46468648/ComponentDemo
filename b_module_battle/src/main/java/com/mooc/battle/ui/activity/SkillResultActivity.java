package com.mooc.battle.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mooc.battle.BattleApi;
import com.mooc.battle.GameConstant;
import com.mooc.battle.R;
import com.mooc.battle.databinding.ActivitySkillResultBinding;
import com.mooc.battle.model.SkillResult;
import com.mooc.battle.util.GlideCircleWithBorder;
import com.mooc.battle.util.Utils;
import com.mooc.common.utils.rxjava.RxUtils;
import com.mooc.commonbusiness.base.BaseActivity;
import com.mooc.commonbusiness.constants.IntentParamsConstants;
import com.mooc.commonbusiness.global.GlobalsUserManager;
import com.mooc.commonbusiness.manager.BaseObserver;
import com.mooc.commonbusiness.model.HttpResponse;
import com.mooc.commonbusiness.model.UserInfo;
import com.mooc.commonbusiness.net.ApiService;
import com.mooc.commonbusiness.route.Paths;
import com.mooc.commonbusiness.utils.format.TimeFormatUtil;

import org.json.JSONException;
import org.json.JSONObject;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import okhttp3.RequestBody;

@Route(path = Paths.PAGE_SKILL_RESULT)
public class SkillResultActivity extends BaseActivity {

    ActivitySkillResultBinding activitySkillResultBinding;
    String tournament_id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitySkillResultBinding = ActivitySkillResultBinding.inflate(getLayoutInflater());
        setContentView(activitySkillResultBinding.getRoot());
        tournament_id = getIntent().getStringExtra(IntentParamsConstants.PARAMS_RESOURCE_ID);


        initView();
        getResult();
    }

    private void initView() {
        UserInfo userBean = GlobalsUserManager.INSTANCE.getUserInfo();
        Glide.with(this).load(userBean.getAvatar())
                .apply(new RequestOptions().error(R.mipmap.common_ic_user_head_default)
                        .placeholder(R.mipmap.common_ic_user_head_default).centerCrop()
                        .transform(new GlideCircleWithBorder(2, Color.parseColor("#FFFFFF"))))
                .into(activitySkillResultBinding.imgUserHeader);
        activitySkillResultBinding.tvName.setText(userBean.getName());

        activitySkillResultBinding.tvLookRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看排行
                ARouter.getInstance().build(Paths.PAGE_SKILL_RANK)
                        .withString(IntentParamsConstants.TOURNAMENT_ID,tournament_id)
                        .navigation();

            }
        });

        activitySkillResultBinding.tvToReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(Paths.PAGE_SKILL_REVIEW)
                        .withString(IntentParamsConstants.PARAMS_RESOURCE_ID,tournament_id)
                        .navigation();
            }
        });

        activitySkillResultBinding.commonTitle.setOnLeftClickListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                finish();
                return null;
            }
        });
    }

    private void getResult() {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put(GameConstant.GAME_PARAMS_ACTION, GameConstant.SKILL_END);
            requestData.put(GameConstant.SKILL_TOURNAMENT_ID, tournament_id);
        } catch (JSONException ignored) {
        }
        RequestBody requestBody = Utils.getRequestBody(requestData);
        ApiService.getRetrofit().create(BattleApi.class)
                .getSkillResult(requestBody)
                .compose(RxUtils.applySchedulers())
                .subscribe(new BaseObserver<HttpResponse<SkillResult>>(this) {
                               @Override
                               public void onSuccess(HttpResponse<SkillResult> response) {
                                   SkillResult data = response.getData();
                                   if (data != null) {
                                       bindInfo(data);
                                   }
                               }
                           }
                );
    }

    private void bindInfo(SkillResult data) {
        activitySkillResultBinding.tvTotalRight.setText(data.total_right_num);
        String total_time_cost = TimeFormatUtil.formatAudioPlayTime(data.total_time_cost * 1000);
        activitySkillResultBinding.tvTotalTime.setText(total_time_cost);
        activitySkillResultBinding.tvScoreDetail.setText(data.total_score);
        activitySkillResultBinding.tvExp.setText(data.exp);
    }
}
