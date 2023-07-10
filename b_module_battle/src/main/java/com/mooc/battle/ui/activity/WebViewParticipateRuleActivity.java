package com.mooc.battle.ui.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.mooc.battle.R;
import com.mooc.battle.databinding.ActivityWvParticipateRuleBinding;
import com.mooc.battle.model.CompetitionManageData;
import com.mooc.battle.util.ConstantUtils;
import com.mooc.battle.util.HtmlX5Util;
import com.mooc.battle.util.RequestUtil;
import com.mooc.common.utils.rxjava.RxUtils;
import com.mooc.commonbusiness.base.BaseActivity;
import com.mooc.commonbusiness.dialog.CustomProgressDialog;
import com.mooc.commonbusiness.manager.BaseObserver;
import com.mooc.commonbusiness.model.HttpResponse;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * 竞赛参与规则WebView详情
 */
public class WebViewParticipateRuleActivity extends BaseActivity {
    ActivityWvParticipateRuleBinding binding;
    private String url;
    private String title = "";
    private String id = "";
    String from = "";
    private View videoView;//webView中视频view
    private IX5WebChromeClient.CustomViewCallback viewCallback;//x5回调

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wv_participate_rule);
        getBundleData();
        initView();
        initData();
        initListener();
//        pageEventStatistics(LogEventConstants.EVENT_TYPE_PARTICIPATE_RULE_LOAD);

    }

    private void getBundleData() {
        if (getIntent() != null) {
            url = getIntent().getStringExtra(ConstantUtils.KEY_URL);
            title = getIntent().getStringExtra(ConstantUtils.KEY_TITLE);
            id = getIntent().getStringExtra(ConstantUtils.KEY_ID);
            from = getIntent().getStringExtra(ConstantUtils.KEY_FROM);
        }
    }

    public void initView() {
        HtmlX5Util.INSTANCE.setWebView(binding.wvDetail, WebViewParticipateRuleActivity.this);
    }


    public void initData() {
        if (TextUtils.isEmpty(title)) {
            title = getString(R.string.str_participate_rule);
        }
        binding.commonTitle.setMiddle_text(title);
        if (!TextUtils.isEmpty(id)) {
            getDataFromNet();
            return;
        }
        if (url != null) {
            binding.wvDetail.loadUrl(url);
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    public void initListener() {
        binding.commonTitle.setOnLeftClickListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                onBackPressed();
                return null;
            }
        });
        //刷新接口
        binding.swipeView.setOnRefreshListener(this::getDataFromNet);
        //解决webView和刷新控件的冲突
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.wvDetail.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY == 0) {
                    //开启下拉刷新
                    binding.swipeView.setEnabled(true);
                } else {
                    //关闭下拉刷新
                    binding.swipeView.setEnabled(false);
                }
            });
        }

        //全屏显示
        binding.wvDetail.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                if (i <= 100) {
                    binding.progressBarWv.setProgress(i);
                }
            }

            @Override
            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                videoView = view;
                binding.flVideo.addView(videoView);
                viewCallback = customViewCallback;
                binding.flVideo.setVisibility(View.VISIBLE);
//                binding.includeWvDetail.getRoot().setVisibility(View.GONE);
                binding.wvDetail.setVisibility(View.GONE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            @Override
            public void onHideCustomView() {
                if (viewCallback != null) {
                    viewCallback.onCustomViewHidden();
                    viewCallback = null;
                }
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                binding.flVideo.removeView(videoView);
                binding.flVideo.setVisibility(View.GONE);
//                binding.includeWvDetail.getRoot().setVisibility(View.VISIBLE);
                binding.wvDetail.setVisibility(View.VISIBLE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        });
        binding.wvDetail.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
                sslErrorHandler.proceed();// 接受所有网站的证书
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                return HtmlX5Util.INSTANCE.setUrlLoading(webView, s);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
                return HtmlX5Util.INSTANCE.setUrlLoading(webView, webResourceRequest.getUrl().toString());
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                binding.progressBarWv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                binding.progressBarWv.setVisibility(View.GONE);
            }
        });
    }

    public void getDataFromNet() {
        CustomProgressDialog dialog = CustomProgressDialog.Companion.createLoadingDialog(this, true);
        dialog.show();
        RequestUtil.getUserApi().getParticipateRuleDetail(id)
                .compose(RxUtils.applySchedulers())
                .subscribe(new BaseObserver<HttpResponse<CompetitionManageData>>(this, binding.swipeView) {
                    @Override
                    public void onSuccess(HttpResponse<CompetitionManageData> competitionManageDataHttpResponse) {
                        dialog.dismiss();
                        if (competitionManageDataHttpResponse != null) {
                            if (competitionManageDataHttpResponse.isSuccess()) {
                                if (competitionManageDataHttpResponse.getData() != null) {
                                    showContent(competitionManageDataHttpResponse.getData());
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        dialog.dismiss();
                    }
                });
    }

    //设置内容
    private void showContent(CompetitionManageData data) {
        String detail = data.getContent();
//        String title;
//        if (!TextUtils.isEmpty(data.getTitle())) {
//            title = HtmlX5Util.INSTANCE.getAddLabel(data.getTitle(), "#666666", "18px");
//            detail = title + detail;
//        }
        binding.wvDetail.loadDataWithBaseURL(null, HtmlX5Util.INSTANCE.getFormatHtml(detail), "text/html", "UTF-8", null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.wvDetail.canGoBack()) {
            binding.wvDetail.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
//        pageEventStatistics(LogEventConstants.EVENT_TYPE_PARTICIPATE_RULE_CLOSE);

        super.onDestroy();
    }

    /**
     * 页面事件打点
     *
     * @param type 二级事件类型，加载或者关闭
     */
    void pageEventStatistics(String type) {
//        StatisticsBean.EventBean<ArticalEventModel> eventBean = new StatisticsBean.EventBean<>();
//        ArticalEventModel eventData = new ArticalEventModel();
//        eventData.arID = id;
//        eventBean.eventData = eventData;
//        eventBean.eventType = LogEventConstants.EVENT_TYPE_PARTICIPATE_RULE;
//
//        StatisticsBean.EventBean<ArticalEventModel> eventBean2 = new StatisticsBean.EventBean<>();
//        eventBean2.eventType = type;
//
//
//        StatisticsUtil.getInstance().addLog(from, LogEventConstants.PAGE_RUL_PARTICIPATE_RULE + LogEventConstants.SPLIT_POUND + id
//                , eventBean, eventBean2);

    }

    @Override
    public void onBackPressed() {
        if (binding.wvDetail.canGoBack()) {
            binding.wvDetail.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
