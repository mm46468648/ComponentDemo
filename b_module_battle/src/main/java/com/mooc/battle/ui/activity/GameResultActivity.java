package com.mooc.battle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.mooc.battle.GameConstant;
import com.mooc.battle.GameInterf;
import com.mooc.battle.R;
import com.mooc.battle.databinding.ActivityGameResultContainerBinding;
import com.mooc.battle.fragment.GameDrawFragment;
import com.mooc.battle.fragment.GameFailFragment;
import com.mooc.battle.fragment.GameSuccessFragment;
import com.mooc.battle.model.GameResultResponse;
import com.mooc.battle.model.GameUserInfo;
import com.mooc.battle.util.RequestUtil;
import com.mooc.battle.util.Utils;
import com.mooc.battle.viewModel.GameResuleViewModel;
import com.mooc.common.ktextends.AnyExtentionKt;
import com.mooc.common.utils.rxjava.RxUtils;
import com.mooc.common.utils.statusbar.StatusBarUtils;
import com.mooc.commonbusiness.base.BaseActivity;
import com.mooc.commonbusiness.manager.BaseObserver;
import com.mooc.commonbusiness.model.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;

/**
 * 对战结果展示页面
 */
public class GameResultActivity extends BaseActivity implements GameInterf {

    private ActivityGameResultContainerBinding viewDataBinding;
    private CompositeDisposable mCompositeDisposable;
    String matchUUid = "";
    String pkUserId = "";
    String seasonId = "";
    GameUserInfo pkUserInfo;
    private static final int DRAW = 2; //平局   1 赢了 -1 输了  2平局  -2 逃跑
    private static final int FAIL = -1;
    private static final int SUC = 1;
    GameResuleViewModel gameResuleViewModel;


    GameSuccessFragment gameSuccessFragment;
    GameFailFragment gameFailFragment;
    GameDrawFragment gameDrawFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setTransparent(this);
        StatusBarUtils.setTextDark(this, false);
        mCompositeDisposable = new CompositeDisposable();
        gameResuleViewModel = new ViewModelProvider(this).get(GameResuleViewModel.class);
        viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_game_result_container);

        initView();
        initData();
        initListener();
//        getDataFromNet();
    }

    public void initView() {


    }

    private void showResultAnimal(int state, GameResultResponse response) {
        if (state == FAIL) {
            gameFailFragment = GameFailFragment.newInstance(response);
            gameFailFragment.setGameInterf(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, gameFailFragment).commit();
        } else if (state == SUC) {
            gameSuccessFragment = GameSuccessFragment.newInstance(response);
            gameSuccessFragment.setGameInterf(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, gameSuccessFragment).commit();
        } else {
            gameDrawFragment = GameDrawFragment.newInstance(response);
            gameDrawFragment.setGameInterf(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, gameDrawFragment).commit();
        }
    }

    public void initData() {
        matchUUid = getIntent().getStringExtra(GameConstant.GAME_PARAMS_UUID);
        pkUserId = getIntent().getStringExtra(GameConstant.GAME_PK_USER_ID);
        seasonId = getIntent().getStringExtra(GameConstant.GAME_PARAMS_SEASON_ID);

        if (getIntent().hasExtra(GameConstant.GAME_PARAMS_RESULT)) {
            GameResultResponse gameResultResponse = (GameResultResponse) getIntent().getSerializableExtra(GameConstant.GAME_PARAMS_RESULT);
            Log.e("GameResult", "initData: " + gameResultResponse.toString());
//            gameResultResponse.self_summary.result = DRAW;
            showResultAnimal(gameResultResponse.self_summary.result, gameResultResponse);
            pkUserInfo = gameResultResponse.pk_user_info;
        }


    }


    public void initListener() {

    }

    public void getDataFromNet() {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put(GameConstant.GAME_PARAMS_ACTION, GameConstant.GAME_ACTION_END);
            requestData.put(GameConstant.GAME_PARAMS_UUID, matchUUid);
            requestData.put(GameConstant.GAME_PK_USER_ID, pkUserId);
            requestData.put(GameConstant.GAME_PARAMS_SEASON_ID, seasonId);

        } catch (JSONException e) {
        }
        RequestBody requestBody = Utils.getRequestBody(requestData);
        Disposable subscribe = RequestUtil.getUserApi().getBattleResult(requestBody)
                .compose(RxUtils.applySchedulers())
                .subscribe(gameBattleResponseHttpResponse -> {
                    onResponse(gameBattleResponseHttpResponse);
                }, throwable -> {

                });

        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.add(subscribe);
        }
    }

    private void onResponse(HttpResponse<GameResultResponse> gameBattleResponseHttpResponse) {
        GameResultResponse data = gameBattleResponseHttpResponse.getData();

        AnyExtentionKt.loge("GameResultActivity", data.self_summary.result + "");
    }

    void continueBattle() {
        //进入对战之前调接口查看是否能进入
        JSONObject requestData = new JSONObject();
        try {
            requestData.put(GameConstant.GAME_PARAMS_ACTION, GameConstant.GAME_ACTION_START);
            requestData.put(GameConstant.GAME_PARAMS_SEASON_ID, seasonId);
        } catch (JSONException e) {
        }
        RequestBody requestBody = Utils.getRequestBody(requestData);

        RequestUtil.getUserApi().postMatchCommonRequest(requestBody)
                .compose(RxUtils.applySchedulers())
                .subscribe(new BaseObserver<HttpResponse<Object>>(this, true) {

                    @Override
                    public void onSubscribe(Disposable subscribe) {
                        super.onSubscribe(subscribe);
                        
                        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
                            mCompositeDisposable.add(subscribe);
                        }
                        
                    }

                    @Override
                    public void onSuccess(HttpResponse<Object> gameBattleResponseHttpResponse) {
                        gameResuleViewModel.continueAble.postValue(true);
                        if (gameBattleResponseHttpResponse == null) return;

                        if (gameBattleResponseHttpResponse.getCode() == GameConstant.STATUS_CODE_SUCCESS) {
                            Intent intent = new Intent(GameResultActivity.this, GameMatchActivity.class);
                            intent.putExtra(GameConstant.GAME_PARAMS_SEASON_ID, seasonId);
                            startActivity(intent);
                            finish();
                        } else {
                            AnyExtentionKt.toast(this, gameBattleResponseHttpResponse.getMsg());
                        }
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);

                        gameResuleViewModel.continueAble.postValue(true);
                        AnyExtentionKt.toast(this, GameConstant.STATUS_NETWORK_ERROR);
                    }
                });

       
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
    public void bottomLeftClick() {
        Intent intent = new Intent(this, GameReviewActivity.class);
        intent.putExtra(GameConstant.GAME_PARAMS_UUID, matchUUid);
        intent.putExtra(GameConstant.GAME_PARAMS_PK_USER, pkUserInfo);
        intent.putExtra(GameConstant.GAME_PARAMS_SEASON_ID, seasonId);
        startActivity(intent);
    }

    @Override
    public void bottomRightClick() {
        continueBattle();
    }
}
