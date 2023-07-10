package com.mooc.battle.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.mooc.battle.GameInterf;
import com.mooc.battle.R;
import com.mooc.battle.anim.GameAnimationHolder;
import com.mooc.battle.databinding.FrgGameResultFailBinding;
import com.mooc.battle.model.GameResultResponse;
import com.mooc.battle.util.ConstantUtils;
import com.mooc.battle.viewModel.GameResuleViewModel;
import com.mooc.battle.viewanimator.ViewAnimator;
import com.mooc.common.ktextends.IntExtentionKt;
import com.mooc.common.utils.ScreenUtil;
import com.mooc.common.utils.rxjava.RxUtils;
import com.mooc.commonbusiness.base.BaseFragment;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class GameFailFragment extends BaseFragment {

    FrgGameResultFailBinding viewDataBinding;


    GameInterf gameInterf;
    GameResuleViewModel gameResuleViewModel;
    public GameInterf getGameInterf() {
        return gameInterf;
    }

    public void setGameInterf(GameInterf gameInterf) {
        this.gameInterf = gameInterf;
    }

    public static GameFailFragment newInstance(GameResultResponse gameResultResponse) {
        GameFailFragment gameFailFragment = new GameFailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantUtils.KEY_BUNDLE, gameResultResponse);
        gameFailFragment.setArguments(bundle);
        return gameFailFragment;
    }

    GameResultResponse gameResultResponse;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameResuleViewModel = new ViewModelProvider(requireActivity()).get(GameResuleViewModel.class);
        gameResultResponse = (GameResultResponse) getArguments().getSerializable(ConstantUtils.KEY_BUNDLE);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewDataBinding = DataBindingUtil.inflate(inflater, R.layout.frg_game_result_fail, container, false);

        initView();
        initData();
        initListener();

        return viewDataBinding.getRoot();
    }

    public void initView() {
        viewDataBinding.commonTitle.setMiddle_text("对战");
        viewDataBinding.commonTitle.setOnLeftClickListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                getActivity().finish();
                return null;
            }
        });
    }

    public void initData() {
        viewDataBinding.scoreLeftUser.setData(gameResultResponse.self_summary);
        viewDataBinding.scoreRightUser.setData(gameResultResponse.pk_summary, gameResultResponse.pk_user_info);
        viewDataBinding.tvExp.setText("+" +  gameResultResponse.self_summary.exp + "经验值");
        statFailAnimal();
    }

    Disposable moveDisposable;

    private void statFailAnimal() {

        viewDataBinding.scoreLeftUser.post(new Runnable() {
            @Override
            public void run() {
                int width = viewDataBinding.scoreLeftUser.getWidth();
                Log.e("TEst", "left widh: " + width);

                ViewAnimator.animate(viewDataBinding.scoreLeftUser)
                        .slideLeftIn(-width)
                        .start();


                //合并之后的上下偏移,10dp * tan30度
                double degrees = 30;
                double radians = Math.toRadians(degrees);
                double tan = Math.tan(radians);
                double cot = Math.atan(radians);

                int offsetX = (int) (tan * IntExtentionKt.dp2px(10));
                int offsetY = (int) (cot * IntExtentionKt.dp2px(10));

                ViewAnimator.animate(viewDataBinding.scoreLeftUser)
                        .translationY(offsetY)
                        .translationX(-offsetX)
                        .duration(1000)
                        .startDelay(500)
                        .start();
            }
        });


        viewDataBinding.scoreRightUser.post(new Runnable() {
            @Override
            public void run() {
                int width = viewDataBinding.scoreRightUser.getWidth();
                int screenWidth = ScreenUtil.getScreenWidth(requireContext());

                Log.e("TEst", "offset width: " + width + screenWidth);

                ViewAnimator.animate(viewDataBinding.scoreRightUser)
                        .slideRightIn(width + screenWidth)
                        .start();


                //合并之后的上下偏移,10dp * tan30度
                double degrees = 30;
                double radians = Math.toRadians(degrees);
                double tan = Math.tan(radians);
                double cot = Math.atan(radians);
                int offsetX = (int) (tan * IntExtentionKt.dp2px(10));
                int offsetY = (int) (cot * IntExtentionKt.dp2px(10));



                ViewAnimator.animate(viewDataBinding.scoreRightUser)
                        .translationY(-offsetY)
                        .translationX(offsetX)
                        .duration(1000)
                        .startDelay(500)
                        .start();
            }
        });

        ViewAnimator.animate(viewDataBinding.imgTrophy)
                .rubber()
                .duration(1000)
                .start();

        ViewAnimator.animate(viewDataBinding.imgSucTip)
                .slideLeftIn()
                .start();

        ViewAnimator.animate(viewDataBinding.topHightLine)
                .alpha(0, 1)
                .duration(1000)
                .start();

        viewDataBinding.imgSunLight.startAnimation(GameAnimationHolder.createRotationAnimator());


        ViewAnimator.animate(viewDataBinding.imgFailText)
                .slideRightIn()
                .start();

        moveDisposable = Observable.timer(500, TimeUnit.MILLISECONDS).compose(RxUtils.applySchedulers())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {

                        ViewAnimator.animate(viewDataBinding.imgFailText)
                                .revertShake()
                                .repeatMode(ViewAnimator.REVERSE)
                                .repeatCount(1)
                                .interpolator(new LinearInterpolator())
                                .start();

                        ViewAnimator.animate(viewDataBinding.imgSucTip)
                                .shake()
                                .repeatMode(ViewAnimator.REVERSE)
                                .repeatCount(1)
                                .interpolator(new LinearInterpolator())
                                .start();




                        ViewAnimator.animate(viewDataBinding.imgStarLeft,
                                        viewDataBinding.imgStarRight)
                                .alpha(0, 1)
                                .duration(1000)
                                .start();


                        moveDisposable.dispose();
                    }
                });

    }


    public void initListener() {
        //对战回顾
        viewDataBinding.btnLeft.setOnClickListener(view -> {
            if (gameInterf != null) {
                gameInterf.bottomLeftClick();
            }
        });

        //继续对战
        viewDataBinding.btnRight.setOnClickListener(view -> {
            if (gameInterf != null) {
                gameInterf.bottomRightClick();
                viewDataBinding.btnRight.setEnabled(false);
            }
        });

        gameResuleViewModel.continueAble.observe(this,aBoolean -> {
            if(aBoolean){
                viewDataBinding.btnRight.setEnabled(true);
            }
        });
    }
}
