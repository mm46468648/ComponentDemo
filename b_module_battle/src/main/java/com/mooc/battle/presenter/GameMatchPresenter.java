package com.mooc.battle.presenter;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mooc.battle.GameConstant;
import com.mooc.battle.model.GameFindResponse;
import com.mooc.battle.model.GameQuestion;
import com.mooc.battle.model.GameResultResponse;
import com.mooc.battle.model.GameStatusBean;
import com.mooc.battle.model.GameUserInfo;
import com.mooc.battle.model.GameViewAnswer;
import com.mooc.battle.util.RequestUtil;
import com.mooc.battle.util.Utils;
import com.mooc.battle.view.GameMatchView;
import com.mooc.common.ktextends.AnyExtentionKt;
import com.mooc.common.utils.rxjava.RxUtils;
import com.mooc.commonbusiness.model.HttpResponse;
import com.moocnd.in.militarycerebrum.AnswerTimeManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;

public class GameMatchPresenter {
    String TAG = GameMatchPresenter.class.getSimpleName();

    public static final int GAME_STATUS_UNSTART = 0;   //未开始
    public static final int GAME_STATUS_DOIND = 1;//进行中
    public static final int GAME_STATUS_END = 2;//已结束

    private GameMatchView mView;
    private MHandler mHandler = new MHandler(this);
    int currentIndex = 0;       //默认第一题
    public boolean opponentStatus = false;      //对手是否答完
    public int gameStatus = GAME_STATUS_UNSTART;          //游戏状态0，未开始，1，进行中，2，已结束
    public ArrayList<GameQuestion> questions = new ArrayList<>();

    private CompositeDisposable mCompositeDisposable;
    private GameUserInfo pkUserInfo;
    private String matchUUID = "";
    private String seasonId;
    private GameFindResponse gameFindResponse;         //匹配成功后的数据,包括对局id对手信息,题目等
    private int answerPeriod = 1000; //获取答题结果频率(默认1000)
    private int currentMatchCount = 0; //当前尝试匹配次数

    private final int netWorkRetryTimes = 2;     //接口异常重试次数

    public AnswerTimeManager timeManager;

    public GameUserInfo getPkUserInfo() {
        return pkUserInfo;
    }

    public void setAnswerPeriod(int answerPeriod) {
        this.answerPeriod = answerPeriod;
    }

    public int getAnswerPeriod(){
        return answerPeriod;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public GameMatchPresenter(GameMatchView gameMatchView) {
        mView = gameMatchView;
        mCompositeDisposable = new CompositeDisposable();
        timeManager = new AnswerTimeManager();
    }


    /**
     * 检测游戏状态
     * 如果退出后台超出对局时间,或其他异常进行提示
     */
    public void checkGameState() {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put(GameConstant.GAME_PARAMS_ACTION, GameConstant.GAME_ACTION_CHECK_STATE);
            requestData.put(GameConstant.GAME_PARAMS_UUID, matchUUID);
            requestData.put(GameConstant.GAME_PARAMS_SEASON_ID, seasonId);
        } catch (JSONException e) {
        }
        RequestBody requestBody = Utils.getRequestBody(requestData);
        Disposable subscribe = RequestUtil.getUserApi().postCheckStatus(requestBody)
                .compose(RxUtils.applySchedulers())
                .subscribe(gameBattleResponseHttpResponse -> {
//                    if (gameBattleResponseHttpResponse != null && gameBattleResponseHttpResponse.getCode() == GameConstant.STATUS_CODE_RUN) {
////                        mView.showGameStatueError(GameConstant.STATUS_CODE_RUN +"_"+gameBattleResponseHttpResponse.getMsg());
//                        showGameStatueError(GameConstant.STATUS_CODE_RUN +"_"+gameBattleResponseHttpResponse.getMsg());
//                        return;
//                    }
                    //异常情况处理异常信息
                    if (gameBattleResponseHttpResponse != null && gameBattleResponseHttpResponse.getCode() != GameConstant.STATUS_CODE_SUCCESS) {
                        showGameStatueError(gameBattleResponseHttpResponse.getMsg(),gameBattleResponseHttpResponse.getCode());
                        return;
                    }

                    //如果是正常状态,设置该题剩余作答时间
                    GameStatusBean data = gameBattleResponseHttpResponse.getData();
                    if (data!=null && data.question_remain_time!=-1 && timeManager != null) {
                        timeManager.startTime(data.question_remain_time);
                    }
                }, throwable -> {

                });

        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.add(subscribe);
        }
    }

    /**
     * 游戏状态异常,将定时器置空,方式计时器重启
     */
    void showGameStatueError(String msg){
        if (timeManager != null) {
            timeManager.stopTime();
        }
        timeManager = null;
        if(mView!=null){
            mView.showGameStatueError(msg,GameConstant.STATUS_CODE_SUCCESS);
        }
    }

    /**
     * 游戏状态异常,将定时器置空,方式计时器重启
     */
    void showGameStatueError(String msg,int errorCode){
        if (timeManager != null) {
            timeManager.stopTime();
        }
        timeManager = null;
        if(mView!=null){
            mView.showGameStatueError(msg,errorCode);
        }
    }
    /**
     * 匹配对手
     */
    public void matchOpponent() {
        //匹配超过10次就退出匹配
        int maxMatchCount = 10;
        if (currentMatchCount >= maxMatchCount) {
            showGameStatueError(GameConstant.STATUS_MATCH_FAIL);
            return;
        }
//        AnyExtentionKt.logd(TAG, "开始匹配对手");
        AnyExtentionKt.logd(TAG, "开始匹配对手");
        gameStatus = GAME_STATUS_UNSTART;
        JSONObject requestData = new JSONObject();
        try {
            requestData.put(GameConstant.GAME_PARAMS_ACTION, GameConstant.GAME_ACTION_FIND_MATCH);
            requestData.put(GameConstant.GAME_PARAMS_SEASON_ID, seasonId);
        } catch (JSONException e) {
        }
        RequestBody requestBody = Utils.getRequestBody(requestData);
        Disposable subscribe = RequestUtil.getUserApi().postGameFind(requestBody)
                .compose(RxUtils.applySchedulers())
                .subscribe(gameBattleResponseHttpResponse -> {
                    onMatchResponse(gameBattleResponseHttpResponse);
                }, throwable -> {
                    mHandler.sendEmptyMessageDelayed(MHandler.MSG_MATCH_OPPONENT, MHandler.match_timePeriod);
                });

        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.add(subscribe);
        }

        currentMatchCount++;

    }

    void onMatchResponse(HttpResponse<GameFindResponse> gameBattleResponseHttpResponse) {
        //异常情况处理异常信息
        if (gameBattleResponseHttpResponse != null && gameBattleResponseHttpResponse.getCode() != GameConstant.STATUS_CODE_SUCCESS) {
            showGameStatueError(gameBattleResponseHttpResponse.getMsg());
            return;
        }
        if (gameBattleResponseHttpResponse == null || gameBattleResponseHttpResponse.getCode() != GameConstant.STATUS_CODE_SUCCESS) {
            mHandler.sendEmptyMessageDelayed(MHandler.MSG_MATCH_OPPONENT, MHandler.match_timePeriod);
            return;
        }

        if (gameBattleResponseHttpResponse.getData() == null || gameBattleResponseHttpResponse.getData().pk_user_info == null) {
            mHandler.sendEmptyMessageDelayed(MHandler.MSG_MATCH_OPPONENT, MHandler.match_timePeriod);
            return;
        }

        gameStatus = GAME_STATUS_DOIND;
        pkUserInfo = gameBattleResponseHttpResponse.getData().pk_user_info;
        matchUUID = gameBattleResponseHttpResponse.getData().match_uuid;
        mHandler.removeMessages(MHandler.MSG_MATCH_OPPONENT);
        AnyExtentionKt.logd(TAG, "匹配成功，获取全部题目");
        if (gameBattleResponseHttpResponse.getData().questions != null) {
            questions.clear();
            questions.addAll(gameBattleResponseHttpResponse.getData().questions);
        }

        //设置查询答案的频率
        if(gameBattleResponseHttpResponse.getData().view_answer_request_intervals!=0){
            setAnswerPeriod(gameBattleResponseHttpResponse.getData().view_answer_request_intervals);
        }
        mView.showReadyPage(gameBattleResponseHttpResponse.getData());

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                getQuestion();
                getQuestionNew();
            }
        }, 2000);
    }

    /**
     * 新版从本地数据集中获取题目
     */
    void getQuestionNew() {
        if (currentIndex >= questions.size()) return;
        GameQuestion gameQuestion = questions.get(currentIndex);
        mView.showQuestion(gameQuestion);
        AnyExtentionKt.logd(TAG, "开始答第" + (currentIndex + 1) + "题");
//        AnswerTimeManager.INSTANCE.startTime(gameQuestion.question_time_limit);
        if (timeManager != null) {
            timeManager.startTime(gameQuestion.question_time_limit);
        }
    }

    /**
     * 提交答案
     */
    public void submitAnswer(String question) {

        JSONObject requestData = new JSONObject();
        try {
            requestData.put(GameConstant.GAME_PARAMS_ACTION, GameConstant.GAME_ACTION_SUBMIT_ANSWER);
            requestData.put(GameConstant.GAME_PARAMS_ANSWER, question);
            requestData.put(GameConstant.GAME_PARAMS_SEASON_ID, seasonId);
            addCommonParams(requestData);
//            requestData.put(GameConstant.GAME_PARAMS_ORDER, currentIndex);
        } catch (JSONException e) {
        }
        RequestBody requestBody = Utils.getRequestBody(requestData);
        Disposable subscribe = RequestUtil.getUserApi().postSubmitAnswer(requestBody)
                .compose(RxUtils.applySchedulers())
                .retry(netWorkRetryTimes)
                .subscribe(gameBattleResponseHttpResponse -> {
                    Log.e(TAG, "submitAnswer subscribe: 一次");

//                    if (gameBattleResponseHttpResponse != null && gameBattleResponseHttpResponse.getCode() == GameConstant.STATUS_CODE_RUN) {
//                        mView.showGameStatueError(GameConstant.STATUS_CODE_RUN +"_"+gameBattleResponseHttpResponse.getMsg());
//                        return;
//                    }

                    if (gameBattleResponseHttpResponse != null && gameBattleResponseHttpResponse.getCode() != GameConstant.STATUS_CODE_SUCCESS) {
                        showGameStatueError(gameBattleResponseHttpResponse.getMsg(),gameBattleResponseHttpResponse.getCode());
                        return;
                    }
                    if (gameBattleResponseHttpResponse != null && gameBattleResponseHttpResponse.getCode() == GameConstant.STATUS_CODE_SUCCESS) {
                        //如果对手已经有了答案,直接进行显示,不再查询
                        if (gameBattleResponseHttpResponse.getData() != null && gameBattleResponseHttpResponse.getData().pk_answer != null) {
                            AnyExtentionKt.logd(TAG, "对手答完了，展示双方答案");
                            showCorrectAnswer(gameBattleResponseHttpResponse.getData());
                        } else {
                            getOpponentStatus();
                        }
                    }
                }, throwable -> {
                    Log.e(TAG, "submitAnswer Error: " + throwable);
                    mView.showNetWorkError();
                });

        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.add(subscribe);
        }
    }

    /**
     * 显示正确答案
     * 2s后进入下一题
     *
     * @param data
     */
    void showCorrectAnswer(GameViewAnswer data) {
//        AnswerTimeManager.INSTANCE.stopTime();
        if (timeManager != null) {
            timeManager.stopTime();
        }
        if (mView != null) {
            mView.showCorrectAnswer(data);
        }
        currentIndex++;
        //停止倒计时
        //延迟2s进入下一题
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentIndex < questions.size()) {
//                    getQuestion();
                    getQuestionNew();
                } else {
                    getBattleResult();
//                    showGameResult();
                }
            }
        }, 2000);
    }

    /**
     * 获取对战结果
     */
    public void getBattleResult() {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put(GameConstant.GAME_PARAMS_ACTION, GameConstant.GAME_ACTION_END);
            requestData.put(GameConstant.GAME_PK_USER_ID, pkUserInfo.id);
            requestData.put(GameConstant.GAME_PARAMS_UUID, matchUUID);
            requestData.put(GameConstant.GAME_PARAMS_SEASON_ID, seasonId);
        } catch (JSONException e) {
        }
        RequestBody requestBody = Utils.getRequestBody(requestData);
        Disposable subscribe = RequestUtil.getUserApi().getBattleResult(requestBody)
                .compose(RxUtils.applySchedulers())
                .retry(netWorkRetryTimes)
                .subscribe(gameBattleResponseHttpResponse -> {
                    Log.e(TAG, "getBattleResult subscribe: 一次");
                    if (gameBattleResponseHttpResponse != null && gameBattleResponseHttpResponse.getCode() != GameConstant.STATUS_CODE_SUCCESS) {
                        showGameStatueError(gameBattleResponseHttpResponse.getMsg());
                        return;
                    }
                    if (gameBattleResponseHttpResponse != null && gameBattleResponseHttpResponse.getCode() == GameConstant.STATUS_CODE_SUCCESS) {
                        showGameResult(gameBattleResponseHttpResponse.getData());
                    }
                }, throwable -> {
                    Log.e(TAG, "getBattleResult: error" + throwable);
                    if (mView != null) {
                        mView.showNetWorkError();
                    }
                });

        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.add(subscribe);
        }
    }

    /**
     * 显示对战结果
     * 重置对战中的状态
     *
     * @param
     */
    void showGameResult(GameResultResponse data) {
        currentIndex = 0;
        currentMatchCount = 0;
        gameStatus = GAME_STATUS_END;
        questions.clear();
        AnyExtentionKt.logd(TAG, "全部答题完毕，展示对战结果");

        data.pk_user_info = pkUserInfo;
        if (mView != null) {
            mView.showResultPage(data);
        }
    }


    /**
     * 获取对手状态
     */
    void getOpponentStatus() {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put(GameConstant.GAME_PARAMS_ACTION, GameConstant.GAME_ACTION_VIEW_ANSWER);
//            requestData.put(GameConstant.GAME_PARAMS_ORDER, currentIndex);
            requestData.put(GameConstant.GAME_PARAMS_SEASON_ID, seasonId);
            addCommonParams(requestData);
        } catch (JSONException e) {
        }
        RequestBody requestBody = Utils.getRequestBody(requestData);
        Disposable subscribe = RequestUtil.getUserApi().postViewAnswer(requestBody)
                .compose(RxUtils.applySchedulers())
                .subscribe(gameBattleResponseHttpResponse -> {
//                    if (gameBattleResponseHttpResponse != null && gameBattleResponseHttpResponse.getCode() == GameConstant.STATUS_CODE_RUN) {
//                        mView.showGameStatueError(GameConstant.STATUS_CODE_RUN +"_"+gameBattleResponseHttpResponse.getMsg());
//                        return;
//                    }

                    if (gameBattleResponseHttpResponse != null && gameBattleResponseHttpResponse.getCode() != GameConstant.STATUS_CODE_SUCCESS) {
                        showGameStatueError(gameBattleResponseHttpResponse.getMsg(),gameBattleResponseHttpResponse.getCode());
                        return;
                    }

//                    if (gameBattleResponseHttpResponse == null || gameBattleResponseHttpResponse.getCode() != GameConstant.STATUS_CODE_SUCCESS) {
//                        AnyExtentionKt.logd(TAG, "开始轮训，等待对手答完");
//                        mHandler.sendEmptyMessageDelayed(MHandler.MSG_LOOP_OPPONENT_STATE, getAnswerPeriod());
//                        return;
//                    }

                    if (gameBattleResponseHttpResponse == null || gameBattleResponseHttpResponse.getData() == null || gameBattleResponseHttpResponse.getData().pk_answer == null) {
                        AnyExtentionKt.logd(TAG, "开始轮训，等待对手答完");
                        mHandler.sendEmptyMessageDelayed(MHandler.MSG_LOOP_OPPONENT_STATE, getAnswerPeriod());
                        return;
                    }
                    GameViewAnswer data = gameBattleResponseHttpResponse.getData();

                    AnyExtentionKt.logd(TAG, "对手答完了，展示双方答案");
                    showCorrectAnswer(data);

                }, throwable -> {
                    //如果超出答题时间限制，就不再调了，直接提示异常，退出对战
                    if (timeManager != null && timeManager.getTotalTime() <= 0) {
                        if (mView != null) {
                            mView.showNetWorkError();
                        }
                    } else {
                        AnyExtentionKt.logd(TAG, "查询对方答案失败，重试一次");
                        mHandler.sendEmptyMessageDelayed(MHandler.MSG_LOOP_OPPONENT_STATE, getAnswerPeriod());
                    }
                });

        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.add(subscribe);
        }

    }

    /**
     * 添加公共参数
     *
     * @param requestData
     */
    void addCommonParams(JSONObject requestData) {
        if (currentIndex >= this.questions.size()) return;

        GameQuestion currentGameQuestion = this.questions.get(currentIndex);
        String nextQuestionId = "";
        if (currentIndex < questions.size() - 1) {
            nextQuestionId = questions.get(currentIndex + 1).id;
        }
        try {
            requestData.put(GameConstant.GAME_QUESTION_ID, currentGameQuestion.id);
            requestData.put(GameConstant.GAME_PK_USER_ID, pkUserInfo.id);
            requestData.put(GameConstant.GAME_PARAMS_UUID, matchUUID);
            requestData.put(GameConstant.GAME_NEXT_QUESTION_ID, nextQuestionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public int getGameState() {
        return gameStatus;
    }

    public void release() {
//        AnswerTimeManager.INSTANCE.stopTime();

        if (timeManager != null) {
            timeManager.setCurrentTimeCallBack(null);
            timeManager.stopTime();
            timeManager = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        mView = null;
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear(); // clear时网络请求会随即cancel
            mCompositeDisposable = null;
        }
    }

    /**
     * 尝试告诉后端退出游戏
     */
    public void postLeaveGame() {
        release();
        JSONObject requestData = new JSONObject();
        try {
            requestData.put(GameConstant.GAME_PARAMS_ACTION, GameConstant.GAME_ACTION_QUIT);
            requestData.put(GameConstant.GAME_PARAMS_SEASON_ID, seasonId);
            requestData.put(GameConstant.GAME_PARAMS_UUID, matchUUID);
        } catch (JSONException e) {
        }
        RequestBody requestBody = Utils.getRequestBody(requestData);
        Disposable subscribe = RequestUtil.getUserApi().postMatchCommonRequest(requestBody)
                .compose(RxUtils.applySchedulers())
                .subscribe(gameBattleResponseHttpResponse -> {
                }, throwable -> {
                });


//        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
//            mCompositeDisposable.add(subscribe);
//        }


    }


    static class MHandler extends Handler {
        private WeakReference<GameMatchPresenter> mActivity;
        public static final int MSG_LOOP_OPPONENT_STATE = 0; //查询对手状态
        public static final int MSG_MATCH_OPPONENT = 1; //匹配对手
//        public static final int timePeriod = 1 * 100;
        public static final int match_timePeriod = 2 * 1000;     //匹配对手延迟时间,因为后端做了延迟,本地只延迟1s

        public MHandler(GameMatchPresenter activity) {
            this.mActivity = new WeakReference(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            GameMatchPresenter gameMatchPresenter = mActivity.get();
            if (msg.what == MSG_LOOP_OPPONENT_STATE) {
                if (gameMatchPresenter != null) {
                    gameMatchPresenter.opponentStatus = true;
                    gameMatchPresenter.getOpponentStatus();
                }
            } else if (msg.what == MSG_MATCH_OPPONENT) {
                if (gameMatchPresenter != null) {
                    gameMatchPresenter.matchOpponent();
                }
            }
            super.handleMessage(msg);
        }
    }

}
