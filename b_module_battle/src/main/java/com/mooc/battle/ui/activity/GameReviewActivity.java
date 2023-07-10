package com.mooc.battle.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.mooc.battle.GameConstant;
import com.mooc.battle.R;
import com.mooc.battle.databinding.ActivityGameReviewBinding;
import com.mooc.battle.dialog.DialogOneButton;
import com.mooc.battle.model.GameOptions;
import com.mooc.battle.model.GameReviewResponse;
import com.mooc.battle.model.GameUserInfo;
import com.mooc.battle.util.RequestUtil;
import com.mooc.battle.util.Utils;
import com.mooc.common.ktextends.AnyExtentionKt;
import com.mooc.common.ktextends.IntExtentionKt;
import com.mooc.common.utils.rxjava.RxUtils;
import com.mooc.common.utils.statusbar.StatusBarUtils;
import com.mooc.commonbusiness.base.BaseActivity;
import com.mooc.commonbusiness.global.GlobalsUserManager;
import com.mooc.commonbusiness.model.UserInfo;
import com.mooc.battle.ui.adapter.GameOptionsAdatper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;

/**
 * 游戏回顾页面
 */
public class GameReviewActivity extends BaseActivity {

    private ActivityGameReviewBinding viewDataBinding;
    private Disposable subscribe;
    GameOptionsAdatper gameOptionsAdatper = new GameOptionsAdatper(null);
    int currentIndex = 0;
    int maxIndex = 4;     //需求是5道题,实际还需要看接口返回
    ArrayList<GameReviewResponse> allQuestion;
    String matchUUid = "";
    String seasonId = "";

    int[] myRoundScore = new int[5];         //存放当前轮次,累加得分
    int[] otherRoundScore = new int[5];      //存放当前轮次,累加得分

    GameUserInfo pkUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setTransparent(this);
        StatusBarUtils.setTextDark(this, false);
        viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_game_review);

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

        viewDataBinding.rvOptions.setLayoutManager(layout);
        viewDataBinding.rvOptions.setAdapter(gameOptionsAdatper);
    }

    public void initData() {
        matchUUid = getIntent().getStringExtra(GameConstant.GAME_PARAMS_UUID);
        seasonId = getIntent().getStringExtra(GameConstant.GAME_PARAMS_SEASON_ID);
        if (getIntent().hasExtra(GameConstant.GAME_PARAMS_PK_USER)) {
            pkUserInfo = (GameUserInfo) getIntent().getSerializableExtra(GameConstant.GAME_PARAMS_PK_USER);
            Glide.with(this).load(pkUserInfo.cover)
                    .placeholder(R.mipmap.common_ic_user_head_default)
                    .error(R.mipmap.common_ic_user_head_default)
                    .transform(new CircleCrop()).into(viewDataBinding.ivOtherHead);
            viewDataBinding.tvOtherName.setText(GameConstant.getSpecialFormatName(pkUserInfo.nickname));
        }

//        UserBean userBean = SPUserUtils.getInstance().getUserInfo();
        UserInfo userBean = GlobalsUserManager.INSTANCE.getUserInfo();
        Glide.with(this).load(userBean.getAvatar())
                .placeholder(R.mipmap.common_ic_user_head_default)
                .error(R.mipmap.common_ic_user_head_default)
                .transform(new CircleCrop()).into(viewDataBinding.ivMyHead);

        viewDataBinding.tvMyName.setText(GameConstant.getSpecialFormatName(userBean.getName()));


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
            requestData.put(GameConstant.GAME_PARAMS_UUID, matchUUid);
            requestData.put(GameConstant.GAME_PARAMS_SEASON_ID, seasonId);

        } catch (JSONException ignored) {
        }
        RequestBody requestBody = Utils.getRequestBody(requestData);
        subscribe = RequestUtil.getUserApi().getBattleReview(requestBody)
                .compose(RxUtils.applySchedulers())
                .subscribe(gameReviewQuestionsHttpResponse -> {
                    if (gameReviewQuestionsHttpResponse != null && gameReviewQuestionsHttpResponse.getCode() != GameConstant.STATUS_CODE_SUCCESS) {
                        showErrorDialog(gameReviewQuestionsHttpResponse.getMsg());
                        return;
                    }
                    if (gameReviewQuestionsHttpResponse != null && gameReviewQuestionsHttpResponse.getData() != null) {
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

        myRoundScore = new int[allQuestion.size()];
        otherRoundScore = new int[allQuestion.size()];

        for (int i = 0; i < allQuestion.size(); i++) {
            GameReviewResponse gameReviewResponse = allQuestion.get(i);
            int currentMyAllScore;
            int currentOtherAllScore;
            if (i == 0) {
                currentMyAllScore = gameReviewResponse.self_answer.score;
                currentOtherAllScore = gameReviewResponse.pk_answer.score;
            } else {
                currentMyAllScore = myRoundScore[i - 1] + gameReviewResponse.self_answer.score;
                currentOtherAllScore = otherRoundScore[i - 1] + gameReviewResponse.pk_answer.score;
            }
            myRoundScore[i] = currentMyAllScore;
            otherRoundScore[i] = currentOtherAllScore;
        }

        changeIndex(currentIndex);


//        viewDataBinding.tvMyTotal.setText(myRoundScore[maxIndex] + "");
//        viewDataBinding.tvMyScore.setText(myRoundScore[maxIndex] + "");
//        viewDataBinding.tvOtherTotal.setText(otherRoundScore[maxIndex] + "");
//        viewDataBinding.tvOtherScore.setText(otherRoundScore[maxIndex] + "");
    }


    void changeIndex(int index) {
        if (allQuestion == null || index < 0 || index >= allQuestion.size()) return;

        setSelectorVisibale(index);
        viewDataBinding.tvNum.setText((currentIndex + 1) + "/" + (maxIndex + 1));
        GameReviewResponse data = allQuestion.get(index);


        viewDataBinding.tvQuestionTitle.setContent(data.title);
//        gameOptionsAdatper.setNewInstance(data.options);

        addOptions(data.options, data.right_answer, data.self_answer.answer, data.pk_answer.answer);
//        gameOptionsAdatper.setResult(data.right_answer, data.self_answer.answer, data.pk_answer.answer);


        viewDataBinding.tvMyScore.setText(myRoundScore[index] + "");
        viewDataBinding.tvOtherScore.setText(otherRoundScore[index] + "");

    }

    /**
     * 设置选择器是否可见
     * @param index
     */
    private void setSelectorVisibale(int index) {
        if(index == 0){ //向左不可见
            viewDataBinding.ivLeft.setVisibility(View.INVISIBLE);
            viewDataBinding.ivRight.setVisibility(View.VISIBLE);
        }else if(index == maxIndex){ //向右不可见
            viewDataBinding.ivRight.setVisibility(View.INVISIBLE);
            viewDataBinding.ivLeft.setVisibility(View.VISIBLE);
        }else { //都可见
            viewDataBinding.ivLeft.setVisibility(View.VISIBLE);
            viewDataBinding.ivRight.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 动态添加选项,使用RecyclerView有滑动冲突和显示问题
     *
     * @param options
     * @param right_answer
     * @param my_answer
     * @param other_answer
     */
    void addOptions(ArrayList<GameOptions> options, String right_answer, String my_answer, String other_answer) {
        viewDataBinding.llOptionContainer.removeAllViews();
        for (int i = 0; i < options.size(); i++) {
            GameOptions item = options.get(i);
            View itemView = View.inflate(this, R.layout.item_game_option, null);
            TextView tvOption = itemView.findViewById(R.id.tvOption);
            ImageView ivMySelect = itemView.findViewById(R.id.ivMySelect);
            ImageView ivOtherSelect = itemView.findViewById(R.id.ivOtherSelect);

            tvOption.setText(item.title);
            if (item.order.equals(right_answer)) {  //正确变绿
                itemView.setBackgroundResource(R.drawable.bg_game_options_right);
                if (item.order.equals(my_answer)) {         //我如果选了正确的设置左边对号
                    ivMySelect.setImageResource(R.mipmap.ic_game_option_select_right);
                } else {
                    ivMySelect.setImageResource(R.drawable.bg_transparent_oval);

                }

                if (item.order.equals(other_answer)) { //如果对面是正确选择,设置右边对号
                    ivOtherSelect.setImageResource(R.mipmap.ic_game_option_select_right);
                } else {
                    ivOtherSelect.setImageResource(R.drawable.bg_transparent_oval);
                }

            } else {
                //如果该选项是我或对手选的错误答案变红
                if (item.order.equals(my_answer) || item.order.equals(other_answer)) {
                    itemView.setBackgroundResource(R.drawable.bg_game_options_error);
                } else {
                    itemView.setBackgroundResource(R.drawable.bg_game_options_default);
                }

                if (item.order.equals(my_answer)) {
                    ivMySelect.setImageResource(R.mipmap.ic_game_option_select_error);
                } else {
                    ivMySelect.setImageResource(R.drawable.bg_transparent_oval);
                }

                if (item.order.equals(other_answer)) { //如果对面是正确选择,设置右边对号
                    ivOtherSelect.setImageResource(R.mipmap.ic_game_option_select_error);
                } else {
                    ivOtherSelect.setImageResource(R.drawable.bg_transparent_oval);
                }
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, IntExtentionKt.dp2px(93));
            layoutParams.setMargins(0, 0, 0, IntExtentionKt.dp2px(13));
            viewDataBinding.llOptionContainer.addView(itemView, layoutParams);
        }
    }

//    /**
//     * @param index
//     * @param direction 方向 0,上一条,1下一条
//     */
//    void changeIndex(int index, int direction) {
//
//        if (allQuestion == null || index < 0 || index >= allQuestion.size()) return;
//
//        viewDataBinding.tvNum.setText((currentIndex + 1) + "/" + (maxIndex + 1));
//        GameReviewResponse data = allQuestion.get(index);
//
//
//        viewDataBinding.tvQuestionTitle.setText(data.title);
//        gameOptionsAdatper.setNewInstance(data.options);
//        gameOptionsAdatper.setResult(data.right_answer, data.self_answer.answer, data.pk_answer.answer);
//
//        if (direction == 0) {
//            myRoundScore[index + 1] = 0;
//            otherRoundScore[index + 1] = 0;
//        } else {
//            myRoundScore[index] = allQuestion.get(index).self_answer.score;
//            otherRoundScore[index] = allQuestion.get(index).pk_answer.score;
//        }
//
//        viewDataBinding.tvMyScore.setText(accountScore(myRoundScore) + "");
//        viewDataBinding.tvOtherScore.setText(accountScore(otherRoundScore) + "");
//    }

    /**
     * 计算所有得分
     *
     * @return
     */
    int accountScore(int[] array) {
        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum;
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
