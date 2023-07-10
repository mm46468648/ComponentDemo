package com.mooc.battle.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mooc.battle.GameConstant;
import com.mooc.battle.R;
import com.mooc.battle.adapter.GameMainAdapter;
import com.mooc.battle.adapter.GameNoticeAdapter;
import com.mooc.battle.databinding.ActivityGameMainBinding;
import com.mooc.battle.model.BattleExp;
import com.mooc.battle.model.GameMain;
import com.mooc.battle.model.GameMainBean;
import com.mooc.battle.model.GameNotice;
import com.mooc.battle.model.GameNoticeBean;
import com.mooc.battle.presenter.GameMainPresenter;
import com.mooc.battle.util.GlideCircleWithBorder;
import com.mooc.battle.util.RequestUtil;
import com.mooc.battle.util.Utils;
import com.mooc.battle.view.GameLoadMoreView;
import com.mooc.common.utils.rxjava.RxUtils;
import com.mooc.common.utils.statusbar.StatusBarUtils;
import com.mooc.commonbusiness.base.BaseActivity;
import com.mooc.commonbusiness.global.GlobalsUserManager;
import com.mooc.commonbusiness.manager.BaseObserver;
import com.mooc.commonbusiness.model.HttpResponse;
import com.mooc.commonbusiness.model.UserInfo;
import com.mooc.commonbusiness.route.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * 军事大脑游戏入口页面
 */
@Route(path = Paths.PAGE_BATTLE_MAIN)
public class GameMainActivity extends BaseActivity {

    ActivityGameMainBinding mBinding;
    GameMainPresenter mainPresenter;
    GameMainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatusBarUtils.setTransparent(this);
        StatusBarUtils.setTextDark(this, false);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_game_main);
        mainPresenter = new GameMainPresenter(this);

        initView();
        initData();
        initListener();
    }

    public void initView() {
        mBinding.commonTitle.setOnLeftClickListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                finish();
                return null;
            }
        });
        mBinding.tvToSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(Paths.PAGE_SKILL_MAIN).navigation();
            }
        });
    }

    public void initData() {
        showUserInfo();
        adapter = new GameMainAdapter(null);
        adapter.getLoadMoreModule().setLoadMoreView(new GameLoadMoreView());
        adapter.setSeasonIntent(new GameMainAdapter.SeasonIntent() {
            @Override
            public void toSeason(GameMain gameMain) {
                if (gameMain != null) {
                    mainPresenter.enterSeason(gameMain);
                }
            }
        });
        adapter.setEmptyView(Utils.getGameEmptyView(this, R.mipmap.icon_game_empty, "当前没有正在进行的竞赛"));
        mBinding.rcyBrum.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rcyBrum.setAdapter(adapter);
        getCompetitions();
        getNotices();

    }

    void getExpLevel() {
        RequestUtil.getUserApi().getExperience().compose(RxUtils.applySchedulers())
                .subscribe(new BaseObserver<HttpResponse<BattleExp>>(this) {
                               @Override
                               public void onSuccess(HttpResponse<BattleExp> listHttpResponse) {
                                   BattleExp data = listHttpResponse.getData();
                                   if (data != null) {
                                       mBinding.tvLevel.setText("Lv." + data.level);
                                       mBinding.pbLevel.setMax(data.current_level_max_exp);
                                       mBinding.pbLevel.setProgress(data.current_level_exp);
                                   }
                               }
                           }
                );
    }

    @Override
    protected void onResume() {
        super.onResume();
        getExpLevel();
    }

    public void initListener() {

        mBinding.swipe.setOnRefreshListener(() -> {
            offset = 0;
            getCompetitions();
            getNotices();
        });
        adapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            offset = limit + offset;
            getCompetitions();
        });
        adapter.setOnItemClickListener((adapter, view, position) -> {
            GameMain gameMain = (GameMain) adapter.getData().get(position);
            if (gameMain != null) {
                mainPresenter.enterSeason(gameMain);
            }
        });
        //返回
//        mBinding.includeTitle.llBack.setOnClickListener(view -> finish());
        //规则
//        mBinding.includeTitle.tvRight.setOnClickListener(view -> enterGameRule());
    }

    int limit = 10, offset = 0;

    private void getCompetitions() {
        RequestUtil.getUserApi().getCompetitionDataList(limit, offset).compose(RxUtils.applySchedulers())
                .subscribe(new BaseObserver<HttpResponse<GameMainBean>>(this, mBinding.swipe) {
                    @Override
                    public void onSuccess(HttpResponse<GameMainBean> gameMainBeanHttpResponse) {

                        setData(gameMainBeanHttpResponse.getData().getResults(), limit, offset);
                    }
                });
    }

    public void setData(List<GameMain> data, int limit, int offset) {

        int size = data == null ? 0 : data.size();
        if (offset == 0) {
            adapter.setNewInstance(data);
        } else {
            if (size > 0) {
                adapter.addData(data);
            }
        }
        if (size < limit) {
            //第一页如果不够一页就不显示没有更多数据布局
            adapter.getLoadMoreModule().loadMoreEnd(false);
        } else {
            adapter.getLoadMoreModule().loadMoreComplete();
        }
    }


//    @SuppressWarnings("deprecation")
//    @SuppressLint("HandlerLeak")
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == 1) {
//
//
//            }
//        }
//    };

    public void getDataFromNet() {

    }


    GameNoticeAdapter noticeAdapter;

    private void getNotices() {

        RequestUtil.getUserApi().getGameNotice().compose(RxUtils.applySchedulers())
                .subscribe(new BaseObserver<HttpResponse<GameNoticeBean>>(this) {
                               @Override
                               public void onSuccess(HttpResponse<GameNoticeBean> listHttpResponse) {
                                   GameNoticeBean bean = listHttpResponse.getData();
                                   if (bean != null && bean.getResults() != null && bean.getResults().size() > 0) {
                                       mBinding.conNotify.setVisibility(View.VISIBLE);
                                       noticeAdapter = new GameNoticeAdapter(GameMainActivity.this);
                                       noticeAdapter.setCurrentDataList((ArrayList<GameNotice>) listHttpResponse.getData().getResults());
                                       mBinding.rcyNotice.setLayoutManager(new LinearLayoutManager(GameMainActivity.this, LinearLayoutManager.VERTICAL, false));
                                       mBinding.rcyNotice.setAdapter(noticeAdapter);
                                       mBinding.rcyNotice.setNestedScrollingEnabled(false);

                                       if (bean.getResults().size() > 1) {
                                           startTimer();
                                       }else {
                                           stopTimer();
                                       }
                                   } else {
                                       mBinding.conNotify.setVisibility(View.GONE);
                                   }
                               }
                           }

                );
    }


    Timer timer;

    public void startTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int offset = mBinding.rcyNotice.computeVerticalScrollOffset();
                        if (offset % noticeAdapter.getItemHeight() == 0) {
                            mBinding.rcyNotice.smoothScrollBy(0, noticeAdapter.getItemHeight());
                        } else {
                            int count = offset / noticeAdapter.getItemHeight();
                            int verOffset = noticeAdapter.getItemHeight() - (offset - noticeAdapter.getItemHeight() * count);
                            mBinding.rcyNotice.smoothScrollBy(0, noticeAdapter.getItemHeight() + verOffset);
                        }
                    }
                });
            }
        }, 0, 4000);

    }

    /**
     * 展示用户信息
     */
    public void showUserInfo() {
//        UserBean userBean = SPUserUtils.getInstance().getUserInfo();
        UserInfo userBean = GlobalsUserManager.INSTANCE.getUserInfo();
        if (userBean != null) {
            mBinding.tvName.setText(userBean.getName());
//        Glide.with(this).load(userBean.getCover())
//                .error(R.mipmap.common_ic_user_head_default)
//                .transform(new CircleCrop()).into(mBinding.imgUserHeader);
            Glide.with(this).load(userBean.getAvatar())
                    .apply(new RequestOptions().error(R.mipmap.common_ic_user_head_default)
                            .placeholder(R.mipmap.common_ic_user_head_default).centerCrop()
                            .transform(new GlideCircleWithBorder(2, Color.parseColor("#FFFFFF")))).into(mBinding.imgUserHeader);
        }

    }

    /**
     * 等级选择页面
     */
    public void enterCompetition(String competition_id) {
        Intent intent = new Intent(this, CompetitionDetailsActivity.class);
        intent.putExtra(GameConstant.PARAMS_COMPETITION_ID, competition_id);
        startActivity(intent);
    }


    /**
     * 进入参与规则页面
     */
    void enterGameRule() {

    }


    void stopTimer(){
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        mainPresenter.release();
    }
}
