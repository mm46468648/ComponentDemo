package com.mooc.battle.ui.activity;

import static com.mooc.common.ktextends.AnyExtentionKt.loge;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.appbar.AppBarLayout;
import com.mooc.battle.GameConstant;
import com.mooc.battle.R;
import com.mooc.battle.databinding.ActivityGameRankBinding;
import com.mooc.battle.model.GameRankResponse;
import com.mooc.battle.model.GameSeason;
import com.mooc.battle.ui.adapter.GameRankAdapter;
import com.mooc.battle.util.RequestUtil;
import com.mooc.battle.view.GameLoadMoreView;
import com.mooc.common.ktextends.AnyExtentionKt;
import com.mooc.common.utils.DateStyle;
import com.mooc.common.utils.DateUtil;
import com.mooc.common.utils.rxjava.RxUtils;
import com.mooc.common.utils.sharepreference.SpUtils;
import com.mooc.common.utils.statusbar.StatusBarUtils;
import com.mooc.commonbusiness.base.BaseActivity;
import com.mooc.commonbusiness.constants.SpConstants;
import com.mooc.commonbusiness.manager.BaseObserver;
import com.mooc.commonbusiness.model.HttpResponse;
import com.mooc.resource.utils.AppBarStateChangeListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/**
 * 游戏排行榜页面
 */
public class GameRankActivity extends BaseActivity {

    private ActivityGameRankBinding dataBinding;
    private final int limit = 10;
    private int offset = 0;
    private boolean isLoadMore = false;

    GameRankAdapter gameRankAdapter = new GameRankAdapter(null);
    private String seasonId = "";
    private String competitionId;

    private boolean fromMyHistory = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setTransparent(this);
        StatusBarUtils.setTextDark(this, false);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_game_rank);
        seasonId = getIntent().getStringExtra(GameConstant.PARAMS_SEASON_ID);
        competitionId = getIntent().getStringExtra(GameConstant.PARAMS_COMPETITION_ID);
        fromMyHistory = getIntent().getBooleanExtra(GameConstant.PARAMS_SEASON_FROM_HISTORY, false);
        initView();
        initData();
        initListener();
        getDataFromNet();
    }

    public void initView() {
        dataBinding.rvRank.setLayoutManager(new LinearLayoutManager(this));
        gameRankAdapter.getLoadMoreModule().setLoadMoreView(new GameLoadMoreView());
        dataBinding.rvRank.setAdapter(gameRankAdapter);
        if (fromMyHistory) {
            dataBinding.tvHistory.setVisibility(View.GONE);
        } else {
            dataBinding.tvHistory.setOnClickListener(view -> {
                //跳转到历史赛季
                toHistoryRank();
            });
        }
    }


    private void toHistoryRank() {
        Intent intent = new Intent(this, GameHistoryRankActivity.class);
        intent.putExtra(GameConstant.PARAMS_COMPETITION_ID, competitionId);
        startActivity(intent);
    }

    public void initData() {
        setActionBar();
    }

    public void initListener() {

        dataBinding.tvBack.setOnClickListener(view -> onBackPressed());
        //刷新
        dataBinding.swipeGameRank.setOnRefreshListener(() -> {
            offset = 0;
            isLoadMore = false;
            getDataFromNet();
        });
        //加载更多
        gameRankAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            isLoadMore = true;
            getDataFromNet();
        });
        dataBinding.appBar.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, int state) {
                switch (state) {
                    case State.EXPANDED://展开状态
//                        dataBinding.tvSeasonTime.setVisibility(View.VISIBLE);
//                        dataBinding.tvSeasonName.setVisibility(View.VISIBLE);
                        dataBinding.clRankOne.setVisibility(View.VISIBLE);
                        dataBinding.clRankTwo.setVisibility(View.VISIBLE);
                        dataBinding.clRankThree.setVisibility(View.VISIBLE);
//                        dataBinding.ivRank3bottombg.setVisibility(View.VISIBLE);
//                        dataBinding.llSeasonFold.setVisibility(View.GONE);
                        dataBinding.swipeGameRank.setEnabled(true);
                        dataBinding.conShadowBottom.setVisibility(View.VISIBLE);
//                        removeTopThree();
                        break;
                    case State.COLLAPSED://折叠状态
//                        dataBinding.tvSeasonTime.setVisibility(View.GONE);
//                        dataBinding.tvSeasonName.setVisibility(View.GONE);
                        dataBinding.clRankOne.setVisibility(View.GONE);
                        dataBinding.clRankTwo.setVisibility(View.GONE);
                        dataBinding.clRankThree.setVisibility(View.GONE);
//                        dataBinding.ivRank3bottombg.setVisibility(View.GONE);
                        dataBinding.llSeasonFold.setVisibility(View.VISIBLE);
                        dataBinding.swipeGameRank.setEnabled(false);
                        dataBinding.conShadowBottom.setVisibility(View.GONE);
//                        addTopThree();
                        break;
                    default://中间状态
//                        dataBinding.tvSeasonTime.setVisibility(View.VISIBLE);
//                        dataBinding.tvSeasonName.setVisibility(View.VISIBLE);
                        dataBinding.clRankOne.setVisibility(View.VISIBLE);
                        dataBinding.clRankTwo.setVisibility(View.VISIBLE);
                        dataBinding.clRankThree.setVisibility(View.VISIBLE);
//                        dataBinding.ivRank3bottombg.setVisibility(View.VISIBLE);
//                        dataBinding.llSeasonFold.setVisibility(View.GONE);
                        dataBinding.swipeGameRank.setEnabled(false);
                        dataBinding.conShadowBottom.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        dataBinding.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float v = Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange();
                AnyExtentionKt.loge(this,v + "");
                //滑动toolbar改变透明度
                dataBinding.clRankOne.setAlpha(1-v);
                dataBinding.clRankTwo.setAlpha(1-v);
                dataBinding.clRankThree.setAlpha(1-v);

            }
        });




        //如果需要显示,提示排行榜10分钟一刷新
        if(SpUtils.get().getValue(SpConstants.SP_RANK_REFRESH_TIP,true)){
            dataBinding.llRefreshContainerFolder.setVisibility(View.VISIBLE);
            dataBinding.ivCloseFolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataBinding.llRefreshContainerFolder.setVisibility(View.GONE);
                    SpUtils.get().putValue(SpConstants.SP_RANK_REFRESH_TIP,false);
                }
            });
        }


    }

    /**
     * 根据百分比改变颜色透明度
     */
    public int changeAlpha(int color, float fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, red, green, blue);
    }

    /*
     * 将前三名的数据添加到下面的列表
     */
//    void addTopThree() {
//        if (topThree.size() > 0)
//            gameRankAdapter.addData(0, topThree);
//    }

    /**
     * 下面的列表中移除前三名的数据
     */
//    void removeTopThree() {
//        for (int i = 0; i < topThree.size(); i++) {
//            gameRankAdapter.remove(topThree.get(i));
//        }
//    }
    private void setActionBar() {
        setSupportActionBar(dataBinding.tbGameRank);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void getDataFromNet() {
        if (isLoadMore) {
            offset += limit;
        } else {
            offset = 0;
        }
        if (TextUtils.isEmpty(seasonId) || seasonId.equals("null")) {
            seasonId = "";
        }
        RequestUtil.getUserApi().gameSeasonRank(seasonId, offset, limit)
                .compose(RxUtils.applySchedulers())
                .subscribe(new BaseObserver<HttpResponse<GameRankResponse>>(this, dataBinding.swipeGameRank) {
                    @Override
                    public void onSuccess(HttpResponse<GameRankResponse> gameRankResponseHttpResponse) {
                        onResponse(gameRankResponseHttpResponse);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                    }
                });
    }

    private void onResponse(HttpResponse<GameRankResponse> gameBattleResponseHttpResponse) {

        if (gameBattleResponseHttpResponse != null) {
            GameRankResponse data = gameBattleResponseHttpResponse.getData();
            if (data != null) {
                String selfRankNum = "";
                if (!TextUtils.isEmpty(data.self_rank_num)) {
                    selfRankNum = data.self_rank_num;
                }
                dataBinding.tvMyRank.setText(String.format("当前我的排名%s", selfRankNum));
                if (data.season_info != null) {
                    setSeasonInfo(data.season_info);
                }
                setRankList(!isLoadMore, data.season_rank_list);
            }
        }

    }

    void setSeasonInfo(GameSeason seasonInfo) {
        String start = DateUtil.timeToString(seasonInfo.start_time_stamp * 1000, DateStyle.YYYY_MM_DD_CN.getValue());
        String end = DateUtil.timeToString(seasonInfo.end_time_stamp * 1000, DateStyle.YYYY_MM_DD_CN.getValue());
//        dataBinding.tvSeasonTime.setText(String.format("%s ~ %s", start, end));
//        dataBinding.tvSeasonName.setText(seasonInfo.title);
        dataBinding.tvSeasonTimeFold.setText(String.format("%s ~ %s", start, end));
        dataBinding.tvSeasonNameFold.setText(seasonInfo.title);
    }

    /**
     * 前三名
     */
    ArrayList<GameRankResponse.RankInfo> topThree = new ArrayList<>();

    private void setRankList(boolean isRefresh, ArrayList<GameRankResponse.RankInfo> data) {
        if (data == null) return;


        int size = data.size();
        if (isRefresh) {
            topThree.clear();
            setTopThree(data);
            //截取前三名
            Iterator<GameRankResponse.RankInfo> iterator = data.iterator();
            while (iterator.hasNext()) {
                if (topThree.size() >= 3) {
                    break;
                }
                GameRankResponse.RankInfo next = iterator.next();
                topThree.add(next);
                iterator.remove();
            }
            gameRankAdapter.setNewInstance(data);
        } else {
            gameRankAdapter.addData(data);
        }
        if (size < limit) { //如果不够一页就显示没有更多数据布局,第一页不显示
            gameRankAdapter.getLoadMoreModule().loadMoreEnd(!isRefresh);
        } else {
            gameRankAdapter.getLoadMoreModule().loadMoreComplete();
        }
    }

    /**
     * 设置前三名的数据
     */
    void setTopThree(ArrayList<GameRankResponse.RankInfo> data) {
        if (data.size() > 0) {
            GameRankResponse.RankInfo rankInfo = data.get(0);
            dataBinding.tvRankName1.setText(GameConstant.getSpecialFormatName(rankInfo.nickname));
            dataBinding.tvScore1.setText(rankInfo.stars);
            dataBinding.tvLevel1.setText(rankInfo.level_title);
            Glide.with(this).load(rankInfo.cover)
                    .placeholder(R.mipmap.common_ic_user_head_default)
                    .error(R.mipmap.common_ic_user_head_default).transform(new CircleCrop()).into(dataBinding.ivRankHead1);
        }
        if (data.size() > 1) {
            GameRankResponse.RankInfo rankInfo = data.get(1);
            dataBinding.tvRankName2.setText(GameConstant.getSpecialFormatName(rankInfo.nickname));
            dataBinding.tvLevel2.setText(rankInfo.level_title);
            dataBinding.tvScore2.setText(rankInfo.stars);
            Glide.with(this).load(rankInfo.cover)
                    .placeholder(R.mipmap.common_ic_user_head_default)
                    .error(R.mipmap.common_ic_user_head_default).transform(new CircleCrop()).into(dataBinding.ivRankHead2);
        }
        if (data.size() > 2) {
            GameRankResponse.RankInfo rankInfo = data.get(2);
            dataBinding.tvRankName3.setText(GameConstant.getSpecialFormatName(rankInfo.nickname));
            dataBinding.tvLevel3.setText(rankInfo.level_title);
            dataBinding.tvScore3.setText(rankInfo.stars);
            Glide.with(this).load(rankInfo.cover)
                    .placeholder(R.mipmap.common_ic_user_head_default)
                    .error(R.mipmap.common_ic_user_head_default).transform(new CircleCrop()).into(dataBinding.ivRankHead3);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        topThree.clear();
    }
}
