package com.mooc.battle.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mooc.battle.GameConstant;
import com.mooc.battle.R;
import com.mooc.battle.adapter.CompetitionHistoryAdapter;
import com.mooc.battle.databinding.ActivityGameHistoryRankBinding;
import com.mooc.battle.model.CompetitionHistoryRank;
import com.mooc.battle.model.Rank;
import com.mooc.battle.util.RequestUtil;
import com.mooc.battle.util.Utils;
import com.mooc.battle.view.GameLoadMoreView;
import com.mooc.common.utils.rxjava.RxUtils;
import com.mooc.common.utils.statusbar.StatusBarUtils;
import com.mooc.commonbusiness.base.BaseActivity;
import com.mooc.commonbusiness.manager.BaseObserver;
import com.mooc.commonbusiness.model.HttpResponse;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;


/***
 * 竞赛历史排行榜
 */
@SuppressWarnings("deprecation")
public class GameHistoryRankActivity extends BaseActivity {

    ActivityGameHistoryRankBinding mBinding;
    CompetitionHistoryAdapter adapter;
    private String competitionId;
    int limit = 10, offset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setTransparent(this);
        StatusBarUtils.setTextDark(this, false);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_game_history_rank);
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
    }

    void enterRankPage(Rank historyRank) {
        //跳转到排名
        Intent intent = new Intent(this, GameRankActivity.class);
        intent.putExtra(GameConstant.PARAMS_SEASON_FROM_HISTORY, true);
        intent.putExtra(GameConstant.PARAMS_SEASON_ID, String.valueOf(historyRank.getId()));
        intent.putExtra(GameConstant.PARAMS_COMPETITION_ID, competitionId);
        startActivity(intent);
    }

    public void initData() {
        competitionId = getIntent().getStringExtra(GameConstant.PARAMS_COMPETITION_ID);
        adapter = new CompetitionHistoryAdapter(null);

        adapter.setEmptyView(Utils.getGameEmptyView(this, R.mipmap.icon_game_empty, "暂无历史赛季"));
        mBinding.rcyBrum.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rcyBrum.setAdapter(adapter);
        getDataFromNet();
    }

    public void initListener() {
        mBinding.swipe.setOnRefreshListener(() -> {
            offset = 0;
            getDataFromNet();
        });

        adapter.getLoadMoreModule().setLoadMoreView(new GameLoadMoreView());
        adapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            offset = offset + limit;
            getDataFromNet();
        });
        adapter.setOnItemClickListener((adapter, view, position) -> enterRankPage((Rank) adapter.getData().get(position)));


    }

    public void setData(List<Rank> data, int limit, int offset) {

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

    public void getDataFromNet() {
        RequestUtil.getUserApi().getCompetitionHistory(competitionId, limit, offset).compose(RxUtils.applySchedulers())
                .subscribe(new BaseObserver<HttpResponse<CompetitionHistoryRank>>(this, mBinding.swipe) {
                    @Override
                    public void onSuccess(HttpResponse<CompetitionHistoryRank> competitionHistoryRankHttpResponse) {
                        CompetitionHistoryRank ranks = competitionHistoryRankHttpResponse.getData();
                        setData(ranks.getResults(), limit, offset);
                    }
                });
    }
}
