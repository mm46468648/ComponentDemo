package com.mooc.battle.ui.activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.mooc.battle.GameConstant;
import com.mooc.battle.R;
import com.mooc.battle.anim.GameAnimationHolder;
import com.mooc.battle.databinding.ActivityGameMatchBinding;
import com.mooc.battle.dialog.DialogMatchTwoButton;
import com.mooc.battle.dialog.DialogOneButton;
import com.mooc.battle.model.GameFindResponse;
import com.mooc.battle.model.GameOptions;
import com.mooc.battle.model.GameQuestion;
import com.mooc.battle.model.GameResultResponse;
import com.mooc.battle.model.GameViewAnswer;
import com.mooc.battle.presenter.GameMatchPresenter;
import com.mooc.battle.view.GameMatchView;
import com.mooc.battle.viewanimator.ViewAnimator;
import com.mooc.common.ktextends.AnyExtentionKt;
import com.mooc.common.ktextends.IntExtentionKt;
import com.mooc.common.utils.statusbar.StatusBarUtils;
import com.mooc.commonbusiness.base.BaseActivity;
import com.mooc.commonbusiness.global.GlobalsUserManager;
import com.mooc.commonbusiness.model.UserInfo;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * 军职大脑游戏
 * 对战页面
 */
public class GameMatchActivity extends BaseActivity implements GameMatchView {


    ActivityGameMatchBinding binding;
    String TAG = GameMatchActivity.class.getSimpleName();

    GameMatchPresenter mPresenter;
//    GameOptionsAdatper mAdatper = new GameOptionsAdatper(null);

    String matchUUid = "";   // 对局唯一unqie值
    String seasonId = "";   // 赛季id
    boolean isSubmit = false;       //如果是自动提交,进入下一题不能再点击


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setTransparent(this);
        StatusBarUtils.setTextDark(this, false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game_match);
        mPresenter = new GameMatchPresenter(this);

        seasonId = getIntent().getStringExtra(GameConstant.GAME_PARAMS_SEASON_ID);
        mPresenter.setSeasonId(seasonId);

        initView();
        initListener();
        initData();
    }


    public void initView() {
        binding.tvBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }


    public void initData() {
//        UserBean userBean = SPUserUtils.getInstance().getUserInfo();
        UserInfo userBean = GlobalsUserManager.INSTANCE.getUserInfo();
        Glide.with(this).load(userBean.getAvatar())
                .placeholder(R.mipmap.common_ic_user_head_default)
                .error(R.mipmap.common_ic_user_head_default)
                .transform(new CircleCrop()).into(binding.ivHeadMiddle);
        showMatchAnimation();
        mPresenter.matchOpponent();
    }

    /**
     * 展示匹配动画
     */
    private void showMatchAnimation() {
        binding.ivCircle1.startAnimation(GameAnimationHolder.createRotationAnimator());
        binding.ivCircle2.startAnimation(GameAnimationHolder.createRotationAnimator2());
        binding.ivCircle3.startAnimation(GameAnimationHolder.createRotationAnimator());
        binding.ivCircle4.startAnimation(GameAnimationHolder.createRotationAnimator2());
        binding.ivCircle5.startAnimation(GameAnimationHolder.createRotationAnimator());
        binding.ivCircle6.startAnimation(GameAnimationHolder.createRotationAnimator2());
    }

    //匹配成功图形移动的距离
    int[] shapeMoveWidth = {-129, 75, 97, -74, -24};
    int[] shapeMoveHeight = {-187, 256, 72, -132, -40};
    int[] shapeDuration = {300, 200, 200, 300, 150};

    /**
     * 展示匹配成功样式
     */
    void showMatchSuccess(GameFindResponse gameFindResponse) {
        View[] views = {binding.ivShape1, binding.ivShape2, binding.ivShape3, binding.ivShape4, binding.ivShape5};

        clearCircleAnimation();

        ViewAnimator.animate(binding.clLeftContainer)
                .dp()
                .matchSuccessLeft()
                .duration(GameAnimationHolder.matchSuccessDuration)
                .start();

        ViewAnimator.animate(binding.clRightContainer)
                .dp()
                .matchSuccessRight()
                .duration(GameAnimationHolder.matchSuccessDuration)
                .start();

        ViewAnimator
                .animate(binding.flKnifeContainer)
                .duration(100)
                .alpha(0, 1)
                .andAnimate(binding.flKnifeContainer)
                .scale(3, 1)
                .interpolator(new BounceInterpolator())
                .duration(1000)
                .start();

        ViewAnimator
                .animate(binding.ivKnifeBlast)
                .duration(100)
                .alpha(0, 1)
                .interpolator(new BounceInterpolator())
                .duration(1000)
                .start();

        for (int i = 0; i < views.length; i++) {
            int f = shapeMoveWidth[i];
            int y = shapeMoveHeight[i];
            int d = shapeDuration[i];
            View shapeView = views[i];

            ViewAnimator.animate(shapeView)
                    .dp()
                    .matchSuccessLine(f, 0, y, 0)
                    .duration(d)
                    .start();
        }

        //动画
        binding.flMatchSuccess.setVisibility(View.VISIBLE);
        Glide.with(this).load(gameFindResponse.self_user_info.cover)
                .placeholder(R.mipmap.common_ic_user_head_default)
                .error(R.mipmap.common_ic_user_head_default)
                .transform(new CircleCrop()).into(binding.ivHeadLeft);

//        binding.tvNameLeft.setText(GameConstant.getFormatName(gameFindResponse.self_user_info.nickname, 6));
        binding.tvNameLeft.setText(GameConstant.getSpecialFormatName(gameFindResponse.self_user_info.nickname));

        Glide.with(this).load(gameFindResponse.pk_user_info.cover)
                .placeholder(R.mipmap.common_ic_user_head_default)
                .error(R.mipmap.common_ic_user_head_default)
                .transform(new CircleCrop()).into(binding.ivHeadRight);
//        binding.tvNameRight.setText(GameConstant.getFormatName(gameFindResponse.pk_user_info.nickname, 6));
        binding.tvNameRight.setText(GameConstant.getSpecialFormatName(gameFindResponse.pk_user_info.nickname));

//        binding.flMatchSuccess.startAnimation(GameAnimationHolder.createMatchSuccessAnima());
//        binding.flKnifeContainer.startAnimation(GameAnimationHolder.createMatchScccessAnima2());


        binding.llMatchingContainer.setVisibility(View.GONE);
    }


    void clearCircleAnimation() {
        binding.ivCircle1.clearAnimation();
        binding.ivCircle2.clearAnimation();
        binding.ivCircle3.clearAnimation();
        binding.ivCircle4.clearAnimation();
        binding.ivCircle5.clearAnimation();
        binding.ivCircle6.clearAnimation();
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
                            binding.rpbCutDown.setProgress(aLong);
//                        binding.tvTime.setText(aLong + "");
                        }
                    });
                    if (aLong == 0 && !isSubmit) {       //倒计时结束,并且没有做出选择
                        AnyExtentionKt.logd(TAG, "倒计时结束，自动提交空答案");
                        isSubmit = true;
                        mPresenter.submitAnswer("");
                    }
                    return null;
                }
            };
        }
//        AnswerTimeManager.INSTANCE.setCurrentTimeCallBack(mTimeCallBack);
        if (mPresenter != null && mPresenter.timeManager != null) {
            mPresenter.timeManager.setCurrentTimeCallBack(mTimeCallBack);
        }

//        //测试匹配成功动效
//        binding.flMatchSuccess.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                GameFindResponse gameFindResponse = new GameFindResponse();
//                GameUserInfo pk_user_info = new GameUserInfo();
//                pk_user_info.cover = "";
//                pk_user_info.nickname = "";
//                gameFindResponse.pk_user_info = pk_user_info;
//                gameFindResponse.self_user_info = pk_user_info;
//                showMatchSuccess(gameFindResponse);
//            }
//        });
    }

    public void getDataFromNet() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearCircleAnimation();
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
        alertDialog.setDialogTitle(GameConstant.QUIT_GAME_TIP);
        alertDialog.show();
    }


    /**
     * 展示对战动画
     *
     * @param gameFindResponse
     */
    @Override
    public void showReadyPage(GameFindResponse gameFindResponse) {
        Glide.with(this).load(gameFindResponse.self_user_info.cover)
                .placeholder(R.mipmap.common_ic_user_head_default)
                .error(R.mipmap.common_ic_user_head_default)
                .transform(new CircleCrop()).into(binding.ivMyHead);

//        binding.tvMyName.setText(gameFindResponse.self_user_info.nickname);
        binding.tvMyName.setText(GameConstant.getSpecialFormatName(gameFindResponse.self_user_info.nickname));

        Glide.with(this).load(gameFindResponse.pk_user_info.cover)
                .placeholder(R.mipmap.common_ic_user_head_default)
                .error(R.mipmap.common_ic_user_head_default)
                .transform(new CircleCrop()).into(binding.ivOtherHead);
//        binding.tvOtherName.setText(gameFindResponse.pk_user_info.nickname);
        binding.tvOtherName.setText(GameConstant.getSpecialFormatName(gameFindResponse.pk_user_info.nickname));

        matchUUid = gameFindResponse.match_uuid;

        showMatchSuccess(gameFindResponse);
    }


    /**
     * 展示对战结果页面
     *
     * @param data
     */
    @Override
    public void showResultPage(GameResultResponse data) {
        if (data == null || data.self_summary == null || data.pk_summary == null) {
            //提示服务端异常并退出
            showGameStatueError(GameConstant.STATUS_SERVER_ERROR,GameConstant.STATUS_CODE_SUCCESS);
            return;
        }
        Intent intent = new Intent(this, GameResultActivity.class);
        intent.putExtra(GameConstant.GAME_PARAMS_UUID, matchUUid);
        intent.putExtra(GameConstant.GAME_PARAMS_SEASON_ID, seasonId);
        intent.putExtra(GameConstant.GAME_PARAMS_RESULT, data);
        startActivity(intent);
        finish();
    }


    /**
     * 展示问题
     *
     * @param question
     */
    @Override
    public void showQuestion(GameQuestion question) {
        binding.rpbCutDown.setMax(question.question_time_limit * GameConstant.CUT_DOWN_UNIT);
        binding.flMatchSuccess.clearAnimation();
        binding.flMatchSuccess.setVisibility(View.GONE);
        isSubmit = false;
        binding.tvQuestionTitle.setContent(question.title);
//        mAdatper.setNewInstance(question.options);
//        mAdatper.setResult(GameConstant.GAME_DEFAULT_ORDER_VALUE, GameConstant.GAME_DEFAULT_ORDER_VALUE, GameConstant.GAME_DEFAULT_ORDER_VALUE);
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
            View itemView = View.inflate(this, R.layout.item_game_option, null);
            TextView tvOption = itemView.findViewById(R.id.tvOption);
            tvOption.setText(item.title);
            itemView.setTag(item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isSubmit) {
                        isSubmit = true;
//                    GameOptions options = (GameOptions) adapter.getData().get(position);
//                        mAdatper.setMySelect(options.order);
                        itemView.setBackgroundResource(R.drawable.bg_game_options_myselect);
                        mPresenter.submitAnswer(item.order);
                    }
                }
            });

            tvOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isSubmit) {
                        isSubmit = true;
//                    GameOptions options = (GameOptions) adapter.getData().get(position);
//                        mAdatper.setMySelect(options.order);
                        itemView.setBackgroundResource(R.drawable.bg_game_options_myselect);
                        mPresenter.submitAnswer(item.order);
                    }
                }
            });

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, IntExtentionKt.dp2px(93));
            layoutParams.setMargins(0, 0, 0, IntExtentionKt.dp2px(13));
            binding.llOptionContainer.addView(itemView, layoutParams);
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


    int myScore = 0;
    int otherScore = 0;

    @Override
    public void showCorrectAnswer(GameViewAnswer data) {
        String myAnswer = data.self_answer.answer;
        String otherAnswer = data.pk_answer.answer;
//        mAdatper.setResult(data.right_answer, myAnswer, otherAnswer);
        updateOptions(data.right_answer, myAnswer, otherAnswer);

        myScore += data.self_answer.score;
        otherScore += data.pk_answer.score;

        binding.tvMyScore.setText(myScore + "");         //总分1000
        binding.tvOtherScore.setText(otherScore + "");

//        binding.vpMyScore.setProgress(myScore / 10);         //总分1000
//        binding.vpbOtherScore.setProgress(otherScore / 10);
    }

    /**
     * 提示接口服务异常，停止计时，点击退出游戏
     *
     * @param msg
     */
    @Override
    public void showGameStatueError(String msg,int errorCode) {
//        AnswerTimeManager.INSTANCE.stopTime();
        if (isFinishing()) return;

        String tempMsg = msg;
        DialogOneButton alertDialog = new DialogOneButton(this, R.style.DefaultDialogStyle, new DialogOneButton.InfoCallback() {
            @Override
            public void onRight() {
                if (errorCode == GameConstant.STATUS_CODE_RUN && mPresenter != null) {
                    //对手逃跑,直接获取结果
                    mPresenter.getBattleResult();
                    return;
                }
                if (GameConstant.STATUS_MATCH_FAIL.equals(msg) && mPresenter != null) {
                    mPresenter.postLeaveGame();
                }
                finish();
            }

            @Override
            public void show() {

            }
        });

        //截取message中
        if (msg.startsWith(GameConstant.STATUS_MATCH_RUN)) {
            tempMsg = msg.replace(GameConstant.STATUS_MATCH_RUN,"");
        }
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setBackGroundResource(R.mipmap.bg_match_dialog);
        alertDialog.setButtonBgResource(R.mipmap.bg_match_button_left);
        alertDialog.setDialogTitle(tempMsg);
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
