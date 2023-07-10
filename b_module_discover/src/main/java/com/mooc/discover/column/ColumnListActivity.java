package com.mooc.discover.column;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.mooc.common.ktextends.AnyExtentionKt;
import com.mooc.common.utils.rxjava.RxUtils;
import com.mooc.commonbusiness.base.BaseActivity;
import com.mooc.commonbusiness.constants.IntentParamsConstants;
import com.mooc.commonbusiness.constants.LogEventConstants2;
import com.mooc.commonbusiness.constants.ResourceTypeConstans;
import com.mooc.commonbusiness.constants.ShareTypeConstants;
import com.mooc.commonbusiness.constants.UrlConstants;
import com.mooc.commonbusiness.manager.BaseObserver;
import com.mooc.commonbusiness.manager.ResourceTurnManager;
import com.mooc.commonbusiness.model.HttpResponse;
import com.mooc.commonbusiness.model.sharedetail.ShareDetailModel;
import com.mooc.commonbusiness.pop.CommonBottomSharePop;
import com.mooc.commonbusiness.route.Paths;
import com.mooc.commonbusiness.route.routeservice.AudioFloatService;
import com.mooc.commonbusiness.route.routeservice.ShareSrevice;
import com.mooc.commonbusiness.utils.IShare;
import com.mooc.discover.R;
import com.mooc.discover.adapter.ColumnListAdapter;
import com.mooc.discover.httpserver.HttpService;
import com.mooc.discover.model.RecommendContentBean;
import com.mooc.discover.window.ColumnSubscribePop;
import com.mooc.resource.utils.AppBarStateChangeListener;
import com.mooc.resource.widget.MoocSwipeRefreshLayout;
import com.mooc.statistics.LogUtil;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

import static com.mooc.resource.utils.AppBarStateChangeListener.State.COLLAPSED;
import static com.mooc.resource.utils.AppBarStateChangeListener.State.EXPANDED;

@Route(path = Paths.PAGE_COLUMN_LIST)
public class ColumnListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private TextView tvTitle;
    private ImageButton tvLeft, ivModel, ivShare;
    private View layoutTop;
    private RecyclerView recyclerView;
    private ColumnListAdapter columnListAdapter;
    private Toolbar mToolbar;
    private AppBarLayout appBarLayout;
    private MoocSwipeRefreshLayout swipeRefreshLayout;
    private String columnId;    //专栏id
    private int page = 1;
    public RecommendContentBean mContentBean;
    private TextView tvHeadDetail, tvHeadTitle, tvHeadSubscribe, tvUpDateTime;
    private TextView tvHeadCount;
    private ImageView tvHeadImageView;
    private LinearLayout layoutSubscribe;
    private ImageView ivSubscribeAdd;
    private int columnType = 0;
    private boolean mAppBarStatus;//判断现在是折叠还是展开，true 折叠，false 展开
    private final ArrayList<RecommendContentBean.DataBean> currentDataList = new ArrayList<>();
//    public String pageID = LogPageConstants.PID_COLUMN_LIST;
//    public final static String FROM_LAUNCH = "from_launch";
//    public static final String INTENT_COLUMN_IMAGEURL = "intent_column_imageurl";
    private ColumnListViewModel mViewModel = new ColumnListViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.column_activity_list);
//        LogBeanUtil.getInstance().addOnLoadLog(pageID, null, null, null, null, true);
//        LogUtil.INSTANCE.addLoadLog(pageID);
        initView();
        initData();
        initListener();
    }

    public void initView() {
        ivShare = findViewById(R.id.iv_column_share);
        ivModel = findViewById(R.id.iv_column_model);
        tvTitle = findViewById(R.id.tv_title_column);
        tvLeft = findViewById(R.id.tv_left_column);
        recyclerView = findViewById(R.id.rcv_column_list);
        layoutTop = findViewById(R.id.layout_back_top);
        mToolbar = findViewById(R.id.tb_column);
        appBarLayout = findViewById(R.id.al_column);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        tvHeadTitle = findViewById(R.id.tv_title);
        tvHeadCount = findViewById(R.id.tv_subscribe_count);
        tvHeadImageView = findViewById(R.id.imageView);
        tvHeadDetail = findViewById(R.id.tv_detail);
        tvHeadSubscribe = findViewById(R.id.tv_subscribe_column);
        layoutSubscribe = findViewById(R.id.layout_subscribe);
        ivSubscribeAdd = findViewById(R.id.iv_subscribe_add);
        tvUpDateTime = findViewById(R.id.tv_update_time);
    }

    public void initData() {
        ivShare.setImageResource(R.mipmap.common_ic_right_share_white);
        ivModel.setImageResource(R.mipmap.column_ic_model_big);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        columnListAdapter = new ColumnListAdapter(currentDataList);
        recyclerView.setAdapter(columnListAdapter);
        columnListAdapter.setListener(new ColumnListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, @Nullable Object data) {
                ColumnListActivity.this.onItemClick(currentDataList.get(position),position);
            }

            @Override
            public void onWebClick(int position, @Nullable Object data, @Nullable TextView textView) {
                uploadReadState(currentDataList.get(position),position);
            }
        });

        //设置滚动时隐藏或者显示悬浮音频播放布局,因为，有一个返回顶部浮窗在底部
        setScrollFolating(recyclerView);

        setActionBar();
        Intent intent = getIntent();
        if (intent != null) {
            columnId = intent.getStringExtra(IntentParamsConstants.COLUMN_PARAMS_ID);
        }
        getDataFromNet();
    }


    private static final int MinDy = 50;

    private void setScrollFolating(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isSignificantDelta = Math.abs(dy) > MinDy;
                if (isSignificantDelta) {

                    AudioFloatService audioFloatService = ARouter.getInstance().navigation(AudioFloatService.class);

                    if (dy > 0) {
                        audioFloatService.hide(ColumnListActivity.this, true);
                        layoutTop.setVisibility(View.VISIBLE);
                    } else {
                        audioFloatService.hide(ColumnListActivity.this, false);
                        layoutTop.setVisibility(View.GONE);
                    }

                }
            }
        });
    }

    private void setActionBar() {
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }


    public void getDataFromNet() {

        int pageSize = 10;
        HttpService.Companion.getDiscoverApi().getColumnList(columnId, page, pageSize).compose(RxUtils.applySchedulers())
                .subscribe(new BaseObserver<RecommendContentBean>(this) {
                    @Override
                    public void onSuccess(RecommendContentBean contentBean) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (contentBean != null) {
                            if (contentBean.getShare_data() != null && !TextUtils.isEmpty(contentBean.getShare_data().getShare_title())) {
                                ivShare.setVisibility(View.VISIBLE);
                            } else {
                                ivShare.setVisibility(View.GONE);
                            }

                            mContentBean = contentBean;
                            updateHeadUI();
                            if (page == 1) {
                                currentDataList.clear();
                            }


                            if (contentBean.getData() == null || contentBean.getData().isEmpty()) {
                                columnListAdapter.getLoadMoreModule().loadMoreEnd();
                                return;
                            }
                            if (contentBean.getData() != null && contentBean.getData().size() > 0) {

                                currentDataList.addAll(contentBean.getData());
                                columnListAdapter.getLoadMoreModule().loadMoreComplete();
                                columnListAdapter.notifyDataSetChanged();
                                page++;
                            }
                        }
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);

                        swipeRefreshLayout.setRefreshing(false);
                        columnListAdapter.getLoadMoreModule().loadMoreFail();

                    }
                });

    }

    private void updateHeadUI() {
        tvHeadTitle.setText(mContentBean.getTitle());
        layoutSubscribe.setVisibility(View.VISIBLE);
        if (mContentBean.is_subscribe()) {
            tvHeadSubscribe.setText(R.string.column_str_subscribed);
            ivSubscribeAdd.setVisibility(View.GONE);
            layoutSubscribe.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_radius20_stroke1_gray9));
        } else {
            tvHeadSubscribe.setText(R.string.column_str_subscribe);
            ivSubscribeAdd.setVisibility(View.VISIBLE);
            layoutSubscribe.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_radius20_color_primary));
        }
        String strTemp = mContentBean.getDetail();
        if (!TextUtils.isEmpty(strTemp)) {
            strTemp = strTemp.replaceAll("<p>", "").replaceAll("</p>", "");
        }
        tvUpDateTime.setText(mContentBean.getAbout());
        tvHeadCount.setText(String.format(getString(R.string.column_subscribe_count), mContentBean.getStudent_num()));
        tvHeadDetail.setText(strTemp);
        Glide.with(this).load(mContentBean.getBig_image()).into(tvHeadImageView);
    }


    public void initListener() {
        columnListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ColumnListActivity.this.onItemClick(currentDataList.get(position),position);
            }
        });
        columnListAdapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getDataFromNet();
            }
        });

        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        layoutTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mContentBean) {
                    showShareDialog();
                }
            }
        });
        layoutSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContentBean != null) {
                    subscribeDialog();
                }
            }
        });

        //appbarlayout 的折叠监听
        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, int state) {
                switch (state) {
                    case EXPANDED:
                        //展开状态
                        mAppBarStatus = false;
                        tvTitle.setText("");
                        tvLeft.setImageResource(R.mipmap.common_iv_back_white);
                        ivShare.setImageResource(R.mipmap.common_ic_right_share_white);
                        if (columnType == 0) {
                            ivModel.setImageResource(R.mipmap.column_ic_model_big);
                        } else if (columnType == 1) {
                            ivModel.setImageResource(R.mipmap.column_ic_model_small);
                        }
                        layoutTop.setVisibility(View.GONE);
                        mToolbar.setBackgroundColor(ContextCompat.getColor(ColumnListActivity.this, R.color.transparent));
                        break;
                    case COLLAPSED:
                        //折叠状态
                        mAppBarStatus = true;
                        if (mContentBean != null && !TextUtils.isEmpty(mContentBean.getTitle())) {
                            tvTitle.setText(mContentBean.getTitle());
                        }
                        tvLeft.setImageResource(R.mipmap.common_ic_back_black);
                        ivShare.setImageResource(R.mipmap.common_ic_right_share_gray);
                        if (columnType == 0) {
                            ivModel.setImageResource(R.mipmap.column_ic_model_big_gray);
                        } else if (columnType == 1) {
                            ivModel.setImageResource(R.mipmap.column_ic_model_small_gray);
                        }
                        layoutTop.setVisibility(View.VISIBLE);
                        mToolbar.setBackgroundColor(ContextCompat.getColor(ColumnListActivity.this, R.color.white));

                        break;
                    default:
                        //中间状态
                        tvTitle.setText("");
                        layoutTop.setVisibility(View.GONE);
                        mToolbar.setBackgroundColor(ContextCompat.getColor(ColumnListActivity.this, R.color.transparent));

                        break;
                }
            }
        });
        ivModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (columnType == 0) {
                    columnType = 1;
                    if (mAppBarStatus) {
                        ivModel.setImageResource(R.mipmap.column_ic_model_small_gray);
                    } else {
                        ivModel.setImageResource(R.mipmap.column_ic_model_small);
                    }

                } else if (columnType == 1) {
                    columnType = 0;
                    if (mAppBarStatus) {
                        ivModel.setImageResource(R.mipmap.column_ic_model_big_gray);
                    } else {
                        ivModel.setImageResource(R.mipmap.column_ic_model_big);
                    }
                }
                columnListAdapter.setSimpleMode(columnType == 1);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void showShareDialog() {
        //请求分享接口之后再显示
        mViewModel.getShareData(columnId).observe(this, new Observer<ShareDetailModel>() {
            @Override
            public void onChanged(ShareDetailModel shareDetailModel) {
                CommonBottomSharePop commonBottomSharePop = new CommonBottomSharePop(ColumnListActivity.this, new Function1<Integer, Unit>() {
                    @Override
                    public Unit invoke(Integer integer) {
                        shareWebPage(integer,shareDetailModel);
                        return null;
                    }
                }, true, false);

                new XPopup.Builder(ColumnListActivity.this).asCustom(commonBottomSharePop).show();
            }
        });

    }

    private void shareWebPage(Integer integer,ShareDetailModel shareDataBean) {
        ShareSrevice shareAddScore = ARouter.getInstance().navigation(ShareSrevice.class);

//        ShareDataBean shareDataBean = shareMode;
        String shareResource = "&resource_type=" + ResourceTypeConstans.TYPE_COLUMN + "&resource_id=" + mContentBean.getId();
        String targetUrl = TextUtils.isEmpty(shareDataBean.getWeixin_url()) ? UrlConstants.COLUMN_SHARE_URL_HEADER + mContentBean.getId() + UrlConstants.SHARE_FOOT + UrlConstants.SHARE_FOOT_MASTER + shareResource : shareDataBean.getWeixin_url();
        String content = TextUtils.isEmpty(shareDataBean.getShare_desc()) ? mContentBean.getAbout() : shareDataBean.getShare_desc();
        String imageUrl = TextUtils.isEmpty(shareDataBean.getShare_picture()) ? UrlConstants.SHARE_LOGO_URL : shareDataBean.getShare_picture();
        String strTitle = TextUtils.isEmpty(shareDataBean.getShare_title()) ? mContentBean.getTitle() : shareDataBean.getShare_title();

        IShare.Builder build = new IShare.Builder()
                .setSite(integer)
                .setTitle(strTitle)
                .setMessage(content)
                .setWebUrl(targetUrl)
                .setImageUrl(imageUrl)
                .build();
        //分享后获取积分
        shareAddScore.shareAddScore(ShareTypeConstants.TYPE_COLUMN, this, build, null);
    }


    /**
     * 刷新
     */
    @Override
    public void onRefresh() {
        page = 1;
        getDataFromNet();
    }

    @Override
    protected void onPause() {
        //todo 如果有视频播放，需要暂停
        super.onPause();
//        StatService.onPageEnd(this, ConstantUtils.COLUMN_LIST_ACTIVITY);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        StatService.onPageStart(this, ConstantUtils.COLUMN_LIST_ACTIVITY);
    }

    /**
     * 订阅弹窗
     */
    private void subscribeDialog() {
        ColumnSubscribePop courseChoosePop = new ColumnSubscribePop(this);

        String message = String.format(getResources().getString(mContentBean.is_subscribe() ? R.string.my_str_subscribe_dialog_title : R.string.my_str_subscribe_add), mContentBean.getTitle());
        courseChoosePop.setMessage(message);
        courseChoosePop.setOnConfrim(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                if (mContentBean.is_subscribe()) {
//                    LogUtil.INSTANCE.addClickLog(LogPageConstants.PID_COLUMN_LIST + "#" + columnId, LogEventConstants.TO_UNSUBSCRIBE + "#" + LogEventConstants.EID_TO_CONFIRM,LogEventConstants.BID_ALERT);
                    delSubscribe();
                    return null;
                }
//                LogUtil.INSTANCE.addClickLog(LogPageConstants.PID_COLUMN_LIST + "#" + columnId, LogEventConstants.TO_UNSUBSCRIBE + "#" + LogEventConstants.EID_TO_CONFIRM,LogEventConstants.BID_ALERT);
                addSubscribe();
                return null;
            }
        });
        new XPopup.Builder(this)
                .asCustom(courseChoosePop)
                .show();

    }

    /**
     * 取消订阅
     */
    private void delSubscribe() {

        HttpService.Companion.getDiscoverApi().deleteSubscribeReq(String.valueOf(mContentBean.getId())).compose(RxUtils.<HttpResponse>applySchedulers())
                .subscribe(new BaseObserver<HttpResponse>(this, true) {
                    @Override
                    public void onSuccess(HttpResponse httpResponse) {
                        if (httpResponse.isSuccess()) {
                            mContentBean.setIs_subscribe(false);
                            tvHeadSubscribe.setText(R.string.column_str_subscribe);
                            ivSubscribeAdd.setVisibility(View.VISIBLE);
                            layoutSubscribe.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_radius20_color_primary));
                            int studentNum = mContentBean.getStudent_num() - 1;
                            mContentBean.setStudent_num(studentNum);
                            tvHeadCount.setText(String.format(getString(R.string.column_subscribe_count), studentNum));
                        }
                    }
                });

    }


    /**
     * 请求订阅
     */
    private void addSubscribe() {
        HttpService.Companion.getDiscoverApi().postSubscribeReq(String.valueOf(mContentBean.getId())).compose(RxUtils.<HttpResponse>applySchedulers())
                .subscribe(new BaseObserver<HttpResponse>(this) {
                    @Override
                    public void onSuccess(HttpResponse httpResponse) {
                        if (httpResponse.isSuccess()) {
                            AnyExtentionKt.toast(ColumnListActivity.this, getString(R.string.subscribe_str_success));
                            mContentBean.setIs_subscribe(true);
                            tvHeadSubscribe.setText(R.string.column_str_subscribed);
                            ivSubscribeAdd.setVisibility(View.GONE);
                            layoutSubscribe.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_radius20_storke1white_solid_gray));
                            int studentNum = mContentBean.getStudent_num() + 1;
                            mContentBean.setStudent_num(studentNum);
                            tvHeadCount.setText(String.format(getString(R.string.column_subscribe_count), studentNum));
                        }
                    }
                });

    }

    /**
     * item点击
     *
     * @param dataBean
     */
    public void onItemClick(RecommendContentBean.DataBean dataBean,int position) {

        if (columnListAdapter.getSimpleMode()) {   //简洁模式，跳转到专栏详情页
//            ARouter.getInstance().build(Paths.PAGE_COLUMN_DETAIL).withParcelable(IntentParamsConstants.INTENT_COLUMN_DATA,dataBean).navigation();
            Gson gson = new Gson();
            ARouter.getInstance().build(Paths.PAGE_COLUMN_DETAIL).withString(IntentParamsConstants.INTENT_COLUMN_DATA, gson.toJson(dataBean)).navigation();
        } else { //跳转资源详情
            if (!"0".equals(dataBean.getResource_id()) && !TextUtils.isEmpty(dataBean.get_resourceId())) {
                ResourceTurnManager.Companion.turnToResourcePage(dataBean);
            }

            LogUtil.INSTANCE.addClickLogNew(
                    LogEventConstants2.P_COLUME + "#" + columnId,
                    dataBean.getResource_id(), String.valueOf(dataBean.get_resourceType()),
                    mContentBean.getTitle());
        }
        uploadReadState(dataBean,position);

    }

    private void uploadReadState(RecommendContentBean.DataBean dataBean,int position) {
        //接口请求已阅读
        HttpService.Companion.getDiscoverApi().updateRecommendRead(String.valueOf(dataBean.getId())).compose(RxUtils.<HttpResponse>applySchedulers())
                .subscribe(new BaseObserver<HttpResponse>(this) {
                    @Override
                    public void onSuccess(HttpResponse httpResponse) {
                        if (httpResponse.isSuccess()) {
                            dataBean.setIs_read(true);
                            columnListAdapter.notifyItemChanged(position);
                        }
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                    }
                });
    }
}
