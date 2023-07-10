package com.mooc.battle.ui.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.mooc.battle.GameConstant;
import com.mooc.battle.R;
import com.mooc.battle.databinding.ActivitySkillAnswerBinding;
import com.mooc.battle.dialog.DialogMatchTwoButton;
import com.mooc.battle.dialog.DialogOneButton;
import com.mooc.battle.model.GameFindResponse;
import com.mooc.battle.model.GameOptions;
import com.mooc.battle.model.GameQuestion;
import com.mooc.battle.model.GameResultResponse;
import com.mooc.battle.model.GameViewAnswer;
import com.mooc.battle.model.SkillQuestionInfo;
import com.mooc.battle.presenter.GameMatchPresenter;
import com.mooc.battle.presenter.SkillAnswerPresenter;
import com.mooc.battle.view.SkillAnswerView;
import com.mooc.common.ktextends.AnyExtentionKt;
import com.mooc.common.ktextends.IntExtentionKt;
import com.mooc.commonbusiness.base.BaseActivity;
import com.mooc.commonbusiness.constants.IntentParamsConstants;
import com.mooc.commonbusiness.global.GlobalsUserManager;
import com.mooc.commonbusiness.interfacewrapper.ARouterNavigationCallbackWrapper;
import com.mooc.commonbusiness.model.UserInfo;
import com.mooc.commonbusiness.route.Paths;
import com.mooc.commonbusiness.utils.format.TimeFormatUtil;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * 比武答题页面
 */
@Route(path = Paths.PAGE_SKILL_ANSWER)
public class SkillAnswerActivity extends BaseActivity implements SkillAnswerView {

    ActivitySkillAnswerBinding binding;
    String TAG = GameMatchActivity.class.getSimpleName();

    SkillAnswerPresenter mPresenter;
//    GameOptionsAdatper mAdatper = new GameOptionsAdatper(null);

    String tournament_id = "";   // 对局id
    boolean isSubmit = false;       //如果是自动提交,进入下一题不能再点击
    String currentSelectOrderId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tournament_id = getIntent().getStringExtra(IntentParamsConstants.PARAMS_RESOURCE_ID);

        binding = ActivitySkillAnswerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mPresenter = new SkillAnswerPresenter(this, tournament_id);


        initView();
        initListener();
        initData();
    }


    public void initView() {
        binding.tvBack.setOnClickListener(view -> {
            onBackPressed();
        });

        //点击下一题的时候再提交答案
        binding.tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(currentSelectOrderId)) {
                    AnyExtentionKt.toast(this, "请先完成当前题目");
                    return;
                }
                if (!isSubmit) {
                    isSubmit = true;
                    mPresenter.submitAnswer(currentSelectOrderId);
                }
            }
        });
    }


    public void initData() {
//        UserBean userBean = SPUserUtils.getInstance().getUserInfo();
        mPresenter.getQuestion();
    }

    Function1<Integer, Unit> mTimeCallBack;

    @Override
    protected void onPause() {
        super.onPause();

    }

    boolean firstLoad = true;

    @Override
    protected void onResume() {
        super.onResume();

        if (!firstLoad && mPresenter != null) {
            mPresenter.checkGameState();
        }
        firstLoad = false;
    }

    public void initListener() {
        if (mTimeCallBack == null) {
            mTimeCallBack = new Function1<Integer, Unit>() {
                @Override
                public Unit invoke(Integer aLong) {
                    AnyExtentionKt.logd(TAG, "开始倒计时" + aLong);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //秒值转换成10:22的形式
                            String s = TimeFormatUtil.formatAudioPlayTime(aLong * 1000);
                            binding.tvCutDown.setText(s);
                        }
                    });
                    if (aLong == 0) {       //倒计时结束
                        AnyExtentionKt.logd(TAG, "倒计时结束，自动到结算页面");
                        isSubmit = true;
                        showResultPage();
                    }
                    return null;
                }
            };
        }
//        AnswerTimeManager.INSTANCE.setCurrentTimeCallBack(mTimeCallBack);
        if (mPresenter != null && mPresenter.timeManager != null) {
            mPresenter.timeManager.setCurrentTimeCallBack(mTimeCallBack);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.release();

    }

    @Override
    public void onBackPressed() {
        if (mPresenter.getGameState() != GameMatchPresenter.GAME_STATUS_END) {
            showLeaveTip();
            return;
        }
        super.onBackPressed();
    }

    /**
     * 展示退出提示
     */
    void showLeaveTip() {
        AnyExtentionKt.logd(TAG, "提示正在进行中");

        DialogMatchTwoButton alertDialog = new DialogMatchTwoButton(this, R.style.DefaultDialogStyle, new DialogMatchTwoButton.InfoCallback() {
            @Override
            public void onRight() {
                mPresenter.postLeaveGame();
                finish();
            }

            @Override
            public void onLeft() {

            }

            @Override
            public void show() {

            }
        });
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setStrLeft("取消");
        alertDialog.setStrRight("确定");
        alertDialog.setTvDescColor(Color.parseColor("#FFFFFF"));
        alertDialog.setBackGroundResource(R.mipmap.bg_match_dialog);
        alertDialog.setTvLeftColor(Color.parseColor("#FFFFFF"));
        alertDialog.setTvLeftResource(R.mipmap.bg_match_button_left);
        alertDialog.setTvRightBgResource(R.mipmap.bg_match_button_right);
        alertDialog.setButtonMargin(IntExtentionKt.dp2px(15));
        alertDialog.setDialogTitle(GameConstant.QUIT_SKILL_TIP);
        alertDialog.show();
    }


    /**
     * 展示比我信息
     * 标题等
     * @param
     */
    @Override
    public void showPageDetail(SkillQuestionInfo gameFindResponse) {
        binding.tvPublicTitle.setText(gameFindResponse.title);
    }


    /**
     * 展示对战结果页面
     */
    @Override
    public void showResultPage() {
//        if (data == null || data.self_summary == null || data.pk_summary == null) {
//            //提示服务端异常并退出
//            showGameStatueError(GameConstant.STATUS_SERVER_ERROR);
//            return;
//        }
        ARouter.getInstance().build(Paths.PAGE_SKILL_RESULT)
                .withString(IntentParamsConstants.PARAMS_RESOURCE_ID, tournament_id)
                .navigation(SkillAnswerActivity.this, new ARouterNavigationCallbackWrapper() {
                    @Override
                    public void onArrival(@Nullable Postcard postcard) {
                        finish();
                    }
                });
    }


    /**
     * 展示问题
     *
     * @param question
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void showQuestion(int index, GameQuestion question) {
        //重置状态
        isSubmit = false;
        currentSelectOrderId = "";
        binding.tvQuestionTitle.setContent(question.title);
        binding.tvNum.setText((index + 1) + "");
        binding.tvTotalNum.setText("/" + mPresenter.getQuestionsSize());

        //设置题目类型
        String typeStr = "";
        if (question.type == 1) {
            typeStr = "单选";
        } else if (question.type == 2) {
            typeStr = "判断";
        }
        if (TextUtils.isEmpty(typeStr)) {
            binding.tvQuestionType.setVisibility(View.GONE);
        } else {
            binding.tvQuestionType.setVisibility(View.VISIBLE);
            binding.tvQuestionType.setText(typeStr);
        }
        initOptions(question.options, GameConstant.GAME_DEFAULT_ORDER_VALUE, GameConstant.GAME_DEFAULT_ORDER_VALUE, GameConstant.GAME_DEFAULT_ORDER_VALUE);
    }


    /**
     * 动态添加选项,使用RecyclerView有滑动冲突和显示问题
     *
     * @param options
     * @param right_answer
     * @param my_answer
     * @param other_answer
     */
    void initOptions(ArrayList<GameOptions> options, String right_answer, String my_answer, String other_answer) {
        binding.llOptionContainer.removeAllViews();
        for (int i = 0; i < options.size(); i++) {
            GameOptions item = options.get(i);
            View itemView = View.inflate(this, R.layout.item_skill_option, null);
            TextView tvOption = itemView.findViewById(R.id.tvOption);
            tvOption.setText(Html.fromHtml(item.title));
            itemView.setTag(item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentSelectOrderId = item.order;
                    resetOptionBackground();
                    itemView.setBackgroundResource(R.drawable.bg_game_options_myselect);
                }
            });

            tvOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentSelectOrderId = item.order;
                    resetOptionBackground();
                    itemView.setBackgroundResource(R.drawable.bg_game_options_myselect);
                }
            });

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, IntExtentionKt.dp2px(55));
            layoutParams.setMargins(0, 0, 0, IntExtentionKt.dp2px(13));
            binding.llOptionContainer.addView(itemView, layoutParams);
        }
    }

    void resetOptionBackground() {
        int count = binding.llOptionContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            binding.llOptionContainer.getChildAt(i).setBackgroundResource(R.drawable.bg_game_options_default);
        }
    }

    /**
     * 更新选项的显示
     *
     * @param right_answer 正确选项
     * @param my_answer    我得选项
     * @param other_answer 对手的选项
     */
    void updateOptions(String right_answer, String my_answer, String other_answer) {
        for (int i = 0; i < binding.llOptionContainer.getChildCount(); i++) {
            View itemView = binding.llOptionContainer.getChildAt(i);
            GameOptions item = (GameOptions) itemView.getTag();

            ImageView ivMySelect = itemView.findViewById(R.id.ivMySelect);
            ImageView ivOtherSelect = itemView.findViewById(R.id.ivOtherSelect);
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
        }
    }

//
//    int myScore = 0;
//    int otherScore = 0;

    @Override
    public void showCorrectAnswer(GameViewAnswer data) {
        String myAnswer = data.self_answer.answer;
        String otherAnswer = data.pk_answer.answer;
//        mAdatper.setResult(data.right_answer, myAnswer, otherAnswer);
        updateOptions(data.right_answer, myAnswer, otherAnswer);

//        myScore += data.self_answer.score;
//        otherScore += data.pk_answer.score;

//        binding.vpMyScore.setProgress(myScore / 10);         //总分1000
//        binding.vpbOtherScore.setProgress(otherScore / 10);
    }

    /**
     * 提示接口服务异常，停止计时，点击退出游戏
     *
     * @param msg
     */
    @Override
    public void showGameStatueError(String msg) {
//        AnswerTimeManager.INSTANCE.stopTime();
        if (mPresenter != null && mPresenter.timeManager != null) {
            mPresenter.timeManager.stopTime();
        }
        if (isFinishing()) return;
        DialogOneButton alertDialog = new DialogOneButton(this, R.style.DefaultDialogStyle, new DialogOneButton.InfoCallback() {
            @Override
            public void onRight() {
                if (GameConstant.STATUS_MATCH_FAIL.equals(msg) && mPresenter != null) {
                    mPresenter.postLeaveGame();
                }
                finish();
            }

            @Override
            public void show() {

            }
        });
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setBackGroundResource(R.mipmap.bg_match_dialog);
        alertDialog.setButtonBgResource(R.mipmap.bg_match_button_left);
        alertDialog.setDialogTitle(msg);
        alertDialog.show();
    }

    /**
     * 弹出网络异常，停止计时
     * 点击退出游戏
     */
    @Override
    public void showNetWorkError() {
//        AnswerTimeManager.INSTANCE.stopTime();
        if (mPresenter != null && mPresenter.timeManager != null) {
            mPresenter.timeManager.stopTime();
        }
        if (isFinishing()) return;
//        DialogOneButton alertDialog = new DialogOneButton(this, R.style.CustomDialogTheme, new DialogOneButton.InfoCallback() {
        DialogOneButton alertDialog = new DialogOneButton(this, R.style.DefaultDialogStyle, new DialogOneButton.InfoCallback() {
            @Override
            public void onRight() {
                mPresenter.postLeaveGame();
                finish();
            }

            @Override
            public void show() {

            }
        });
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setBackGroundResource(R.mipmap.bg_match_dialog);
        alertDialog.setButtonBgResource(R.mipmap.bg_match_button_left);
        alertDialog.setDialogTitle(GameConstant.STATUS_NETWORK_ERROR);
        alertDialog.show();
    }


}
