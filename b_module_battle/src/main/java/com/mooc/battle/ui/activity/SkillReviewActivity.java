package com.mooc.battle.ui.activity;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.mooc.battle.BattleApi;
import com.mooc.battle.GameConstant;
import com.mooc.battle.R;
import com.mooc.battle.databinding.ActivitySkillReviewBinding;
import com.mooc.battle.dialog.DialogOneButton;
import com.mooc.battle.model.GameOptions;
import com.mooc.battle.model.GameReviewResponse;
import com.mooc.battle.ui.adapter.GameOptionsAdatper;
import com.mooc.battle.util.RequestUtil;
import com.mooc.battle.util.Utils;
import com.mooc.common.ktextends.AnyExtentionKt;
import com.mooc.common.ktextends.IntExtentionKt;
import com.mooc.common.utils.rxjava.RxUtils;
import com.mooc.common.utils.statusbar.StatusBarUtils;
import com.mooc.commonbusiness.base.BaseActivity;
import com.mooc.commonbusiness.constants.IntentParamsConstants;
import com.mooc.commonbusiness.net.ApiService;
import com.mooc.commonbusiness.route.Paths;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;

/**
 * 游戏回顾页面
 */
@Route(path = Paths.PAGE_SKILL_REVIEW)
public class SkillReviewActivity extends BaseActivity {

    private ActivitySkillReviewBinding viewDataBinding;
    private Disposable subscribe;
    GameOptionsAdatper gameOptionsAdatper = new GameOptionsAdatper(null);
    int currentIndex = 0;
    int maxIndex = 4;     //需求是5道题,实际还需要看接口返回
    ArrayList<GameReviewResponse> allQuestion;
    String tournament_id = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_skill_review);

        initView();
        initData();
        initListener();
        getDataFromNet();
    }

    public void initView() {

        viewDataBinding.tvBack.setOnClickListener(view -> {
            onBackPressed();
        });
        LinearLayoutManager layout = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

//        viewDataBinding.rvOptions.setLayoutManager(layout);
//        viewDataBinding.rvOptions.setAdapter(gameOptionsAdatper);
    }

    public void initData() {
        tournament_id = getIntent().getStringExtra(IntentParamsConstants.PARAMS_RESOURCE_ID);

    }

    public void initListener() {
        viewDataBinding.ivLeft.setOnClickListener(view -> {
                    if (currentIndex > 0) {
                        changeIndex(--currentIndex);
                    }
                }
        );

        viewDataBinding.ivRight.setOnClickListener(view -> {
            if (currentIndex < maxIndex) {
                changeIndex(++currentIndex);
            }
        });
    }

    public void getDataFromNet() {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put(GameConstant.GAME_PARAMS_ACTION, GameConstant.GAME_ACTION_REVIEW);
            requestData.put(GameConstant.SKILL_TOURNAMENT_ID, tournament_id);
        } catch (JSONException ignored) {
        }
        RequestBody requestBody = Utils.getRequestBody(requestData);
        subscribe = ApiService.getRetrofit().create(BattleApi.class).getSkillReview(requestBody)
                .compose(RxUtils.applySchedulers())
                .subscribe(gameReviewQuestionsHttpResponse -> {
//                    if (gameReviewQuestionsHttpResponse != null && gameReviewQuestionsHttpResponse.getCode() != GameConstant.STATUS_CODE_SUCCESS) {
//                        showErrorDialog(gameReviewQuestionsHttpResponse.getMsg());
//                        return;
//                    }
                    if (gameReviewQuestionsHttpResponse != null && gameReviewQuestionsHttpResponse.getData() != null) {
                        viewDataBinding.tvPublicTitle.setText(gameReviewQuestionsHttpResponse.getData().title);
                        onResponse(gameReviewQuestionsHttpResponse.getData().questions);
                    }
                }, throwable -> {
                    showErrorDialog("网络异常");
                    AnyExtentionKt.loge("GameReview", throwable.toString());
                });
    }

    /**
     * 展示错误弹窗
     * 点击确定退出页面
     */
    void showErrorDialog(String msg) {
        DialogOneButton alertDialog = new DialogOneButton(this, R.style.DefaultDialogStyle, new DialogOneButton.InfoCallback() {
            @Override
            public void onRight() {
                finish();
            }

            @Override
            public void show() {

            }
        });
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setCancelable(false);
        alertDialog.setBackGroundResource(R.mipmap.bg_match_dialog);
        alertDialog.setButtonBgResource(R.mipmap.bg_match_button_left);
        alertDialog.setDialogTitle(msg);
        alertDialog.show();
    }


    private void onResponse(ArrayList<GameReviewResponse> response) {
        if (response == null || response.isEmpty()) return;

        //初始化一些数据
        allQuestion = response;
        maxIndex = allQuestion.size() - 1;

        changeIndex(currentIndex);
    }


    void changeIndex(int index) {

        if (allQuestion == null || index < 0 || index >= allQuestion.size()) return;

//        viewDataBinding.tvNum.setText((currentIndex + 1) + "/" + (maxIndex + 1));
        GameReviewResponse data = allQuestion.get(index);


        viewDataBinding.tvQuestionTitle.setContent(data.title);

        viewDataBinding.tvNum.setText((index + 1) + "");
        viewDataBinding.tvTotalNum.setText("/" + allQuestion.size());

        //设置题目类型
        String typeStr = "";
        if(data.type == 1){
            typeStr = "单选";
        }else if(data.type == 2){
            typeStr = "判断";
        }
        if(TextUtils.isEmpty(typeStr)){
            viewDataBinding.tvQuestionType.setVisibility(View.GONE);
        }else {
            viewDataBinding.tvQuestionType.setVisibility(View.VISIBLE);
            viewDataBinding.tvQuestionType.setText(typeStr);
        }
        addOptions(data.options, data.right_answer, data.answer);

    }

    /**
     * 动态添加选项,使用RecyclerView有滑动冲突和显示问题
     *
     * @param options
     * @param right_answer
     * @param my_answer
     */
    void addOptions(ArrayList<GameOptions> options, String right_answer, String my_answer) {
        viewDataBinding.llOptionContainer.removeAllViews();
        for (int i = 0; i < options.size(); i++) {
            GameOptions item = options.get(i);
            View itemView = View.inflate(this, R.layout.item_skill_option, null);
            TextView tvOption = itemView.findViewById(R.id.tvOption);
            ImageView ivMySelect = itemView.findViewById(R.id.ivMySelect);
            ImageView ivOtherSelect = itemView.findViewById(R.id.ivOtherSelect);

            tvOption.setText(Html.fromHtml(item.title));
            if (item.order.equals(right_answer)) {  //正确变绿
                itemView.setBackgroundResource(R.drawable.bg_game_options_right);
                if (item.order.equals(my_answer)) {         //我如果选了正确的设置左边对号
                    ivMySelect.setImageResource(R.mipmap.ic_game_option_select_right);
                } else {
                    ivMySelect.setImageResource(R.drawable.bg_transparent_oval);

                }

//                if (item.order.equals(other_answer)) { //如果对面是正确选择,设置右边对号
//                    ivOtherSelect.setImageResource(R.mipmap.ic_game_option_select_right);
//                } else {
//                    ivOtherSelect.setImageResource(R.drawable.bg_transparent_oval);
//                }

            } else {
                //如果该选项是我或对手选的错误答案变红
                if (item.order.equals(my_answer)) {
                    itemView.setBackgroundResource(R.drawable.bg_game_options_error);
                } else {
                    itemView.setBackgroundResource(R.drawable.bg_game_options_default);
                }

                if (item.order.equals(my_answer)) {
                    ivMySelect.setImageResource(R.mipmap.ic_game_option_select_error);
                } else {
                    ivMySelect.setImageResource(R.drawable.bg_transparent_oval);
                }

//                if (item.order.equals(other_answer)) { //如果对面是正确选择,设置右边对号
//                    ivOtherSelect.setImageResource(R.mipmap.ic_game_option_select_error);
//                } else {
//                    ivOtherSelect.setImageResource(R.drawable.bg_transparent_oval);
//                }
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, IntExtentionKt.dp2px(55));
            layoutParams.setMargins(0, 0, 0, IntExtentionKt.dp2px(13));
            viewDataBinding.llOptionContainer.addView(itemView, layoutParams);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (subscribe != null && !subscribe.isDisposed()) {
            subscribe.dispose();
            subscribe = null;
        }
    }
}
