package com.mooc.battle.presenter;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mooc.battle.BattleApi;
import com.mooc.battle.GameConstant;
import com.mooc.battle.model.GameQuestion;
import com.mooc.battle.model.GameResultResponse;
import com.mooc.battle.model.GameViewAnswer;
import com.mooc.battle.model.SkillQuestionInfo;
import com.mooc.battle.util.RequestUtil;
import com.mooc.battle.util.Utils;
import com.mooc.battle.view.SkillAnswerView;
import com.mooc.common.ktextends.AnyExtentionKt;
import com.mooc.common.utils.rxjava.RxUtils;
import com.mooc.commonbusiness.net.ApiService;
import com.moocnd.in.militarycerebrum.AnswerTimeManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;

public class SkillAnswerPresenter {
    String TAG = SkillAnswerPresenter.class.getSimpleName();

    public static final int GAME_STATUS_UNSTART = 0;   //未开始
    public static final int GAME_STATUS_DOIND = 1;//进行中
    public static final int GAME_STATUS_END = 2;//已结束

    private SkillAnswerView mView;
    private MHandler mHandler = new MHandler(this);
    int currentIndex = 0;       //默认第一题
    public boolean opponentStatus = false;      //对手是否答完
    public int gameStatus = GAME_STATUS_UNSTART;          //游戏状态0，未开始，1，进行中，2，已结束
    public ArrayList<GameQuestion> questions = new ArrayList<>();

    private CompositeDisposable mCompositeDisposable;
    private String tournament_id = "";

    private final int netWorkRetryTimes = 2;     //接口异常重试次数

    public AnswerTimeManager timeManager;


    public SkillAnswerPresenter(SkillAnswerView gameMatchView, String tournament_id) {
        mView = gameMatchView;
        this.tournament_id = tournament_id;
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
            requestData.put(GameConstant.GAME_PARAMS_UUID, tournament_id);
        } catch (JSONException e) {
        }
        RequestBody requestBody = Utils.getRequestBody(requestData);
        Disposable subscribe = RequestUtil.getUserApi().postGameFind(requestBody)
                .compose(RxUtils.applySchedulers())
                .subscribe(gameBattleResponseHttpResponse -> {
                    //异常情况处理异常信息
                    if (gameBattleResponseHttpResponse != null && gameBattleResponseHttpResponse.getCode() != GameConstant.STATUS_CODE_SUCCESS) {
                        mView.showGameStatueError(gameBattleResponseHttpResponse.getMsg());
                        return;
                    }
                }, throwable -> {

                });

        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.add(subscribe);
        }
    }



    /**
     * 新版从本地数据集中获取题目
     */
    void getQuestionNew() {
        if (currentIndex >= questions.size()) return;
        GameQuestion gameQuestion = questions.get(currentIndex);
        mView.showQuestion(currentIndex,gameQuestion);
        AnyExtentionKt.logd(TAG, "开始答第" + (currentIndex + 1) + "题");
//        AnswerTimeManager.INSTANCE.startTime(gameQuestion.question_time_limit);

    }

    /**
     * 提交答案
     * @param question 是option中的order字段
     */
    public void submitAnswer(String question) {

        JSONObject requestData = new JSONObject();
        try {
            requestData.put(GameConstant.GAME_PARAMS_ACTION, GameConstant.GAME_ACTION_SUBMIT_ANSWER);
            requestData.put(GameConstant.GAME_PARAMS_ANSWER, question);
            requestData.put(GameConstant.SKILL_TOURNAMENT_ID, tournament_id);

            GameQuestion gameQuestion = questions.get(currentIndex);
            requestData.put(GameConstant.SKILL_QUESTION_ID, gameQuestion.id);

            if(currentIndex+1 == questions.size()){
                requestData.put(GameConstant.SKILL_NEXT_QUESTION_ID, 0);
            }else {
                GameQuestion nextQuestion1 = questions.get(currentIndex+1);
                requestData.put(GameConstant.SKILL_NEXT_QUESTION_ID, nextQuestion1.id);
            }
//            addCommonParams(requestData);
//            requestData.put(GameConstant.GAME_PARAMS_ORDER, currentIndex);
        } catch (JSONException e) {
        }
        RequestBody requestBody = Utils.getRequestBody(requestData);
        Disposable subscribe = ApiService.getRetrofit().create(BattleApi.class).submitAnswer(requestBody)
                .compose(RxUtils.applySchedulers())
                .retry(netWorkRetryTimes)
                .subscribe(gameBattleResponseHttpResponse -> {
                    Log.e(TAG, "submitAnswer subscribe: 一次");
                    if (gameBattleResponseHttpResponse != null && gameBattleResponseHttpResponse.getCode() != GameConstant.STATUS_CODE_SUCCESS) {
                        mView.showGameStatueError(gameBattleResponseHttpResponse.getMsg());
                        return;
                    }
                    if (gameBattleResponseHttpResponse != null && gameBattleResponseHttpResponse.getCode() == GameConstant.STATUS_CODE_SUCCESS) {
                        onSubmitSuccess();
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
     * 提交答案成功,进入下一题或者结束
     */
    void onSubmitSuccess(){
        currentIndex++;
        if (currentIndex < questions.size()) {
            getQuestionNew();
        } else {
            getBattleResult();
        }
    }

    /**
     * 获取对战结果
     */
    void getBattleResult() {
        showGameResult();
    }

    /**
     * 显示对战结果
     * 重置对战中的状态
     *
     * @param
     */
    void showGameResult() {
        currentIndex = 0;
        gameStatus = GAME_STATUS_END;
        questions.clear();
        AnyExtentionKt.logd(TAG, "全部答题完毕，展示对战结果");

        if (mView != null) {
            mView.showResultPage();
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
            requestData.put(GameConstant.GAME_PARAMS_UUID, tournament_id);
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
            requestData.put(GameConstant.SKILL_TOURNAMENT_ID, tournament_id);
        } catch (JSONException e) {
        }
        RequestBody requestBody = Utils.getRequestBody(requestData);
        Disposable subscribe = ApiService.getRetrofit().create(BattleApi.class).postLeave(requestBody)
                .compose(RxUtils.applySchedulers())
                .subscribe(gameBattleResponseHttpResponse -> {
                }, throwable -> {
                });


//        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
//            mCompositeDisposable.add(subscribe);
//        }


    }

    /**
     * 获取题目
     */
    public void getQuestion() {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put(GameConstant.GAME_PARAMS_ACTION, GameConstant.SKILL_GET_QUESTION);
            requestData.put(GameConstant.SKILL_TOURNAMENT_ID, tournament_id);
        } catch (JSONException ignored) {
        }
        RequestBody requestBody = Utils.getRequestBody(requestData);
        Disposable subscribe = ApiService.getRetrofit().create(BattleApi.class).getQuestions(requestBody)
                .compose(RxUtils.applySchedulers())
                .retry(netWorkRetryTimes)
                .subscribe(skillQuestionInfoHttpResponse -> {
                    SkillQuestionInfo data = skillQuestionInfoHttpResponse.getData();
                    if(data !=null){
                        if (timeManager != null) {
                            timeManager.startTime(data.total_answer_limit_time,1000);
                        }
                        mView.showPageDetail(data);

                    }
                    if (data.questions != null) {
                        questions.clear();
                        questions.addAll(data.questions);
                        getQuestionNew();
                    }
                }, throwable -> {
                    Log.e(TAG, "submitAnswer Error: " + throwable);
                    mView.showNetWorkError();
                });

        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.add(subscribe);
        }
    }


    static class MHandler extends Handler {
        private WeakReference<SkillAnswerPresenter> mActivity;
        public static final int MSG_LOOP_OPPONENT_STATE = 0; //查询对手状态
        public static final int MSG_MATCH_OPPONENT = 1; //匹配对手
        public static final int timePeriod = 3 * 1000;
        public static final int match_timePeriod = 2 * 1000;     //匹配对手延迟时间,因为后端做了延迟,本地只延迟1s

        public MHandler(SkillAnswerPresenter activity) {
            this.mActivity = new WeakReference(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            SkillAnswerPresenter gameMatchPresenter = mActivity.get();
            if (msg.what == MSG_LOOP_OPPONENT_STATE) {
                if (gameMatchPresenter != null) {
//                    gameMatchPresenter.opponentStatus = true;
//                    gameMatchPresenter.getOpponentStatus();
                }
            } else if (msg.what == MSG_MATCH_OPPONENT) {
//                if (gameMatchPresenter != null) {
//                    gameMatchPresenter.matchOpponent();
//                }
            }
            super.handleMessage(msg);
        }
    }

    public int getQuestionsSize(){
        return questions.size();
    }
}
