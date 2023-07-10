package com.mooc.battle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.mooc.battle.GameConstant;
import com.mooc.battle.R;
import com.mooc.battle.adapter.SeasonCompetitionAdapter;
import com.mooc.battle.databinding.ActivityCompetitionDetailsBinding;
import com.mooc.battle.model.CompetitionDetails;
import com.mooc.battle.util.RequestUtil;
import com.mooc.battle.util.Utils;
import com.mooc.common.ktextends.AnyExtentionKt;
import com.mooc.common.utils.DateStyle;
import com.mooc.common.utils.DateUtil;
import com.mooc.common.utils.rxjava.RxUtils;
import com.mooc.commonbusiness.base.BaseActivity;
import com.mooc.commonbusiness.constants.IntentParamsConstants;
import com.mooc.commonbusiness.manager.BaseObserver;
import com.mooc.commonbusiness.model.HttpResponse;
import com.mooc.commonbusiness.route.Paths;
import com.mooc.commonbusiness.utils.ClickFilterUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import okhttp3.RequestBody;

/***
 * 竞赛详情
 */

public class CompetitionDetailsActivity extends BaseActivity {

    ActivityCompetitionDetailsBinding mBinding;
    String competition_id;
    SeasonCompetitionAdapter adapter;
    CompetitionDetails.SeasonInfo seasonInfo;
    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_competition_details);
        initView();
        initData();
        initListener();
    }


    @SuppressWarnings("deprecation")
    public void initView() {
        mBinding.title.setText(getString(R.string.str_season));
//        mBinding.commState.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, QMUIDisplayHelper.getStatusBarHeight(this)));
        mBinding.commonTitle.setOnLeftClickListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                finish();
                return null;
            }
        });


    }

    int limit = 10, offset = 0;

    public void initData() {
        competition_id = getIntent().getStringExtra(GameConstant.PARAMS_COMPETITION_ID);
        mBinding.emptyView.tvRefresh.setText(getString(R.string.str_no_season_in_progress_refresh));

        adapter = new SeasonCompetitionAdapter(null);

        adapter.setEmptyView(Utils.getGameEmptyView(this, R.mipmap.empty_competition_details, "战场正在打扫中,请稍后再来"));
        mBinding.rcyBrum.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rcyBrum.setAdapter(adapter);
    }

    private void getDetailsAndSchedulers() {
        RequestUtil.getUserApi().getCompetitionDetail(competition_id, limit, offset).compose(RxUtils.applySchedulers())
                .subscribe(
                        new BaseObserver<HttpResponse<CompetitionDetails>>(this) {
                            @Override
                            public void onSuccess(HttpResponse<CompetitionDetails> competitionDetailsHttpResponse) {
                                setCompetitionDetails(competitionDetailsHttpResponse);
                            }

                            @Override
                            public void onFailure(int code, String message) {
                                super.onFailure(code, message);
                                mBinding.conDetails.setVisibility(View.GONE);
                                mBinding.emptyView.getRoot().setVisibility(View.VISIBLE);

                            }
                        }
                );
    }

    private void setCompetitionDetails(HttpResponse<CompetitionDetails> competitionDetailsHttpResponse) {
        mBinding.conDetails.setVisibility(View.VISIBLE);
        mBinding.emptyView.getRoot().setVisibility(View.GONE);
        mBinding.swipe.setRefreshing(false);
        CompetitionDetails details = null;
        if (competitionDetailsHttpResponse != null) {
            details = competitionDetailsHttpResponse.getData();
        }
        if (details != null) {
            seasonInfo = details.getSeasonInfo();
            mBinding.commonTitle.setMiddle_text(details.getTitle());
            if (seasonInfo != null) {
                mBinding.title.setText(seasonInfo.getTitle());
                if (seasonInfo.getStartTimeStamp() != null) {
                    String start = DateUtil.timeToString(seasonInfo.getStartTimeStamp() * 1000, DateStyle.YYYY_MM_DD_CN.getValue());
                    String end = DateUtil.timeToString(seasonInfo.getEndTimeStamp() * 1000, DateStyle.YYYY_MM_DD_CN.getValue());
                    mBinding.duration.setText(String.format("%s~%s", start, end));
                }


                if (!TextUtils.isEmpty(seasonInfo.getConclusion())) {
                    adapter.setEmptyView(Utils.getGameEmptyView(this, R.mipmap.empty_competition_details, seasonInfo.getConclusion()));
                }
                mBinding.userLeveName.setText(seasonInfo.getLevelTitle());

                mBinding.tvName.setText(seasonInfo.getNickname());
                Glide.with(CompetitionDetailsActivity.this).
                        load(seasonInfo.getCover()).
                        placeholder(R.mipmap.common_ic_user_head_default).into(mBinding.imgUserHeader);

                Glide.with(CompetitionDetailsActivity.this).
                        load(seasonInfo.getLevelImageSmallIcon()).
                        placeholder(R.mipmap.ic_game_rank_level_holder).into(mBinding.imgLevel);

                if (!TextUtils.isEmpty(seasonInfo.getSelfRankNum())) {
                    mBinding.userRank.setText(String.format("当前排名 %s", seasonInfo.getSelfRankNum()));
                }

                setData(seasonInfo.getSchedule(), limit, offset);
            }

        }


    }

    public void setData(List<CompetitionDetails.SeasonInfo.Schedule> data, int limit, int offset) {

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

    public void initListener() {

        adapter.setOnItemClickListener((adapter, view, position) -> {
            CompetitionDetails.SeasonInfo.Schedule schedule = (CompetitionDetails.SeasonInfo.Schedule) adapter.getData().get(position);
            if (schedule.getIsComplete() == 1) {//已完成当前等级，不可进入
                AnyExtentionKt.toast(CompetitionDetailsActivity.this, getString(R.string.str_level_completed_prompt));
            } else if (schedule.getIsComplete() == 0) {//未完成
                if (schedule.getIsEnter() == 1) {//可以进入
                    enterBattlePage();
                } else {//不可进入，加锁状态
                    AnyExtentionKt.toast(CompetitionDetailsActivity.this, getString(R.string.str_no_unlock_level));
                }

            }
        });
//        adapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                offset = limit + offset;
//                getDetailsAndSchedulers();
//            }
//        });

        mBinding.swipe.setOnRefreshListener(() -> {
            offset = 0;
            getDetailsAndSchedulers();
        });

        mBinding.tvRankContainer.setOnClickListener(view -> enterRankPage());
        mBinding.userRank.setOnClickListener(v -> enterRankPage());
        mBinding.commonTitle.setOnRightTextClickListener(view -> {
//            Intent intent = new Intent(CompetitionDetailsActivity.this, WebViewParticipateRuleActivity.class);
//            intent.putExtra(ConstantUtils.KEY_ID, competition_id);
////            intent.putExtra(ConstantUtils.KEY_FROM, LogEventConstants.PAGE_RUL_COMPETITION_DETAIL);
//            startActivity(intent);
            ARouter.getInstance().build(Paths.PAGE_WEB_MATCH_RULE)
                    .withString(IntentParamsConstants.PARAMS_RESOURCE_ID, competition_id)
                    .withString(IntentParamsConstants.WEB_PARAMS_TITLE, "参与规则")
                    .navigation();

        });
        mBinding.emptyView.getRoot().setOnClickListener(view -> {
            offset = 0;
            getDetailsAndSchedulers();
        });
    }

    /**
     * 进入排行榜页面
     */
    void enterRankPage() {
        if (seasonInfo != null) {
            Intent intent = new Intent(this, GameRankActivity.class);
            intent.putExtra(GameConstant.PARAMS_SEASON_ID, String.valueOf(seasonInfo.getId()));
            intent.putExtra(GameConstant.PARAMS_COMPETITION_ID, competition_id);
            startActivity(intent);
        }

    }

    /**
     * 进入对战页面
     */
    void enterBattlePage() {
        if (seasonInfo == null) return;
        //控制连点
        if(!ClickFilterUtil.canClick()) return;

        if (!Utils.checkAllNet(this)) {
            AnyExtentionKt.toast(this, "网络异常");
            return;
        }

        //进入对战之前调接口查看是否能进入
        JSONObject requestData = new JSONObject();
        try {
            requestData.put(GameConstant.GAME_PARAMS_SEASON_ID, seasonInfo.getId());
            requestData.put(GameConstant.GAME_PARAMS_ACTION, GameConstant.GAME_ACTION_START);
        } catch (JSONException ignored) {
        }
        RequestBody requestBody = Utils.getRequestBody(requestData);


        RequestUtil.getUserApi().postMatchCommonRequest(requestBody)
                .compose(RxUtils.applySchedulers())
                .subscribe(new BaseObserver<HttpResponse<Object>>(CompetitionDetailsActivity.this, true) {
                    @Override
                    public void onSuccess(HttpResponse<Object> gameBattleResponseHttpResponse) {
                        if (gameBattleResponseHttpResponse == null) return;

                        if (gameBattleResponseHttpResponse.getCode() == GameConstant.STATUS_CODE_SUCCESS) {
                            Intent intent = new Intent(CompetitionDetailsActivity.this, GameMatchActivity.class);
                            intent.putExtra(GameConstant.GAME_PARAMS_SEASON_ID, String.valueOf(seasonInfo.getId()));
                            startActivity(intent);
                        } else {
                            AnyExtentionKt.toast(CompetitionDetailsActivity.this, gameBattleResponseHttpResponse.getMsg());
                        }
                    }
                });


    }

    public void getDataFromNet() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.checkAllNet(CompetitionDetailsActivity.this)) {
            offset = 0;
            getDetailsAndSchedulers();
        }

    }
}
