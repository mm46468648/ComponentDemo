package com.mooc.battle.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.mooc.battle.BattleApi;
import com.mooc.battle.GameConstant;
import com.mooc.battle.databinding.ActivityCreateSkillBinding;
import com.mooc.battle.model.SkillDesc;
import com.mooc.battle.model.SkillInfo;
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

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import okhttp3.RequestBody;

@Route(path = Paths.PAGE_CREATE_SKILL)
public class SkillCreateActivity extends BaseActivity {

    ActivityCreateSkillBinding createSkillBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createSkillBinding = ActivityCreateSkillBinding.inflate(getLayoutInflater());
        setContentView(createSkillBinding.getRoot());

        initView();
        getTipInfo();
    }

    private void initView() {
        createSkillBinding.commonTitle.setOnLeftClickListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                finish();
                return null;
            }
        });
        createSkillBinding.tvCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = createSkillBinding.etName.getText().toString();
                if (!TextUtils.isEmpty(title)) {
                    postCreateSkill(title);
                }
            }
        });
//        createSkillBinding.etName.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus) {//有焦点
//                if (!TextUtils.isEmpty(createSkillBinding.etName.getHint().toString())) {
//                    createSkillBinding.etName.setHint("");
//                }
//            }
//        });
    }

    /**
     * 获取提示信息
     */
    void getTipInfo() {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put(GameConstant.GAME_PARAMS_ACTION, GameConstant.SKILL_CREATE_DESC);
        } catch (JSONException ignored) {
        }
        RequestBody requestBody = Utils.getRequestBody(requestData);
        ApiService.getRetrofit().create(BattleApi.class).getCreateTipInfo(requestBody).compose(RxUtils.applySchedulers())
                .subscribe(new BaseObserver<HttpResponse<SkillDesc>>(this) {
                               @Override
                               public void onSuccess(HttpResponse<SkillDesc> response) {
                                   SkillDesc data = response.getData();
                                   if (data != null) {
                                       createSkillBinding.tvDesc.setText(data.getCreate_desc());
                                   }
                               }
                           }
                );
    }

    /**
     * 创建比武
     *
     * @param name
     */
    void postCreateSkill(String name) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put(GameConstant.GAME_PARAMS_ACTION, GameConstant.SKILL_ACTION_CREATE);
            requestData.put(GameConstant.SKILL_TITLE, name);
        } catch (JSONException ignored) {
        }
        RequestBody requestBody = Utils.getRequestBody(requestData);
        ApiService.getRetrofit().create(BattleApi.class).createSkill(requestBody).compose(RxUtils.applySchedulers())
                .subscribe(new BaseObserver<HttpResponse<SkillInfo>>(this) {
                               @Override
                               public void onSuccess(HttpResponse<SkillInfo> response) {
                                   SkillInfo data = response.getData();
                                   if (response.isSuccess()) {
                                       //进入到开始答题页面
                                       ARouter.getInstance().build(Paths.PAGE_BEGIN_SKILL)
                                               .withString(IntentParamsConstants.PARAMS_RESOURCE_ID, data.getTournament_id())
                                               .withString(IntentParamsConstants.PARAMS_RESOURCE_TITLE, data.getTitle())
                                               .navigation(SkillCreateActivity.this, new ARouterNavigationCallbackWrapper() {
                                                   @Override
                                                   public void onArrival(@Nullable Postcard postcard) {
                                                       finish();
                                                   }
                                               });
                                   }
                               }
                           }
                );
    }
}
