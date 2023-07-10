package com.mooc.battle.presenter;


import com.mooc.battle.ui.activity.GameMainActivity;
import com.mooc.battle.model.GameMain;
import com.mooc.battle.model.SeasonInfo;
import com.mooc.battle.util.RequestUtil;
import com.mooc.battle.util.Utils;
import com.mooc.common.ktextends.AnyExtentionKt;
import com.mooc.common.utils.rxjava.RxUtils;
import com.mooc.commonbusiness.model.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class GameMainPresenter {
    private CompositeDisposable mCompositeDisposable;
    private WeakReference<GameMainActivity> gameMatchPresenterWeakReference;

    public GameMainPresenter(GameMainActivity gameMainActivity) {
        gameMatchPresenterWeakReference = new WeakReference(gameMainActivity);
    }

    @SuppressWarnings("rawtypes")
    public void enterSeason(GameMain gameMain) {
        if (!Utils.checkAllNet(gameMatchPresenterWeakReference.get())) {
//            ToastUtils.toast(gameMatchPresenterWeakReference.get(), "网络异常");
            AnyExtentionKt.toast(gameMatchPresenterWeakReference.get(), "网络异常");
            return;
        }

        if (gameMain != null) {
            SeasonInfo seasonInfo = gameMain.getSeasonInfo();
            if (seasonInfo == null || seasonInfo.getIsEnroll() == null) {
//                ToastUtils.toast(gameMatchPresenterWeakReference.get(), gameMatchPresenterWeakReference.get().getResources().getString(R.string.str_no_season_in_progress));
                gameMatchPresenterWeakReference.get().enterCompetition(gameMain.getId().toString());
                return;
            }

            if (seasonInfo.getIsEnroll() == 0) {
                JSONObject requestData = new JSONObject();
                try {
                    requestData.put("season_id", seasonInfo.getId().toString());
                } catch (
                        JSONException ignored) {
                }
                //未报名先报名
                Disposable subscribe = RequestUtil.getUserApi().postGameSingup(Utils.getRequestBody(requestData))
                        .compose(RxUtils.applySchedulers())
                        .subscribe((Consumer<HttpResponse>) httpResponse -> {
                            if (httpResponse.isSuccess()) {
                                gameMatchPresenterWeakReference.get().enterCompetition(gameMain.getId().toString());
                            } else {
                                AnyExtentionKt.toast(gameMatchPresenterWeakReference.get(), httpResponse.getMsg());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                
                            }
                        });

                if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
                    mCompositeDisposable.add(subscribe);
                }
            } else {
                //已报名直接进入
                gameMatchPresenterWeakReference.get().enterCompetition(gameMain.getId().toString());
            }
        }

    }


    public void release() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear(); // clear时网络请求会随即cancel
            mCompositeDisposable = null;
        }

        gameMatchPresenterWeakReference.clear();
        gameMatchPresenterWeakReference = null;
    }
}
