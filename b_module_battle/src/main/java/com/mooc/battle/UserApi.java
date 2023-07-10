package com.mooc.battle;

import com.mooc.battle.model.AnnouncementManageData;
import com.mooc.battle.model.BattleExp;
import com.mooc.battle.model.CompetitionDetails;
import com.mooc.battle.model.CompetitionHistoryRank;
import com.mooc.battle.model.CompetitionManageData;
import com.mooc.battle.model.GameFindResponse;
import com.mooc.battle.model.GameLevelResponse;
import com.mooc.battle.model.GameMainBean;
import com.mooc.battle.model.GameNoticeBean;
import com.mooc.battle.model.GameQuestion;
import com.mooc.battle.model.GameRankResponse;
import com.mooc.battle.model.GameResultResponse;
import com.mooc.battle.model.GameReviewQuestions;
import com.mooc.battle.model.GameStatusBean;
import com.mooc.battle.model.GameViewAnswer;
import com.mooc.commonbusiness.model.HttpResponse;
import com.mooc.commonbusiness.model.studyroom.HonorDataBean;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApi {
    //竞赛中公告详情
    @GET("api/mobile/bs/battle/notice/{id}/")
    Observable<HttpResponse<AnnouncementManageData>> getCompetitionAnnouncementDetail(@Path("id") String id);

    //竞赛规则
    @GET("api/mobile/bs/battle/competition/{id}/")
    Observable<HttpResponse<CompetitionManageData>> getParticipateRuleDetail(@Path("id") String id);

    //获取赛季通知
    @GET("api/mobile/bs/battle/notice/")
    Observable<HttpResponse<GameNoticeBean>> getGameNotice();

    //获取对战首页列表数据
    @GET("api/mobile/bs/battle/competition/")
    Observable<HttpResponse<GameMainBean>> getCompetitionDataList(@Query("limit") int limit, @Query("offset") int offset);

    //赛季报名
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/mobile/bs/battle/season/enroll/")
    Observable<HttpResponse> postGameSingup(@Body RequestBody body);

    //本人对战级别解锁页面
    @GET("api/mobile/bs/battle/schedule/")
    Observable<HttpResponse<GameLevelResponse>> gameLevel();

    //游戏排行榜
    @GET("api/mobile/bs/battle/rank/")
    Observable<HttpResponse<GameRankResponse>> gameRank(@Query("offset") int offset, @Query("limit") int limit);

    //多竞赛版本,赛季排行
    @GET("api/mobile/bs/battle/season/rank/detail/")
    Observable<HttpResponse<GameRankResponse>> gameSeasonRank(@Query("season_id") String season_id, @Query("offset") int offset, @Query("limit") int limit);


    /**
     * 游戏对战接口根据action区分游戏动作
     * {"action":"start"} //# 开始
     * {"action":"find_match"} # 轮询获得比赛信息
     * {"action":"get_question"} # 获得题目
     * {"action":"submit_answer"} # 提交答案
     * {"action":"view_answer"} # 轮训结果
     * {"action":"end"} # 总结合计
     * {"action":"review"} # 回顾
     * {"action":"quit"} # 退出
     *
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/mobile/bs/battle/play/")
    Observable<HttpResponse<GameFindResponse>> postGameFind(@Body RequestBody body);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/mobile/bs/battle/play/")
    Observable<HttpResponse<GameStatusBean>> postCheckStatus(@Body RequestBody body);

    /**
     * CODE_MAPPING = {
     * # code msg toast （app弹框用的）
     * 10000: {"msg": "对手逃跑", "toast": "对手已逃跑"},
     * # 10001-10020 # 无需处理
     * 10001: {"msg": "找不到赛季", "toast": "当前没有进行中的赛季"},
     * 10002: {"msg": "回顾异常", "toast": "查看回顾异常"},
     * # 10021-10040 # 清理队列及redis
     * 10021: {"msg": "已经有比赛正在处理", "toast": "已经有比赛正在处理中，请稍后再试"},
     * 10022: {"msg": "没有题目", "toast": "申请加入对战失败"},
     * 10023: {"msg": "没有可用机器人", "toast": "申请加入对战失败"},
     * 10024: {"msg": "发卷异常", "toast": "申请加入对战失败"},
     * # 10041-10060 # 异常结算 判输
     * 10041: {"msg": "提交答案异常", "toast": "服务器异常"},
     * 10042: {"msg": "轮训答案异常", "toast": "服务器异常"},
     * 10043: {"msg": "结算异常", "toast": "服务器异常"},
     * 10099: {"msg": "其它异常", "toast": "其它服务器异常"},
     * }
     * # 以上情况只有在对手逃跑的情况下 需要跳到结算页进行展示
     * # 只有在code =0 的时候代表流程正常
     * app 前端注意的是
     * 1. 匹配超时的时候需要调用退出接口
     * 2.前端在等本题目回顾的时候 如果超时 需要调用退出接口
     * 3. 如果匹配过程中 及答题过程中 程序切换到后台（待议论）
     * 4.回顾接口 返回数据多套了一层 需要app调整下（具体看回顾接口
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/mobile/bs/battle/play/")
    Observable<HttpResponse<Object>> postMatchCommonRequest(@Body RequestBody body);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/mobile/bs/battle/play/")
    Observable<HttpResponse<GameViewAnswer>> postSubmitAnswer(@Body RequestBody body);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/mobile/bs/battle/play/")
    Observable<HttpResponse<GameViewAnswer>> postViewAnswer(@Body RequestBody body);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/mobile/bs/battle/play/")
    Observable<HttpResponse<GameResultResponse>> getBattleResult(@Body RequestBody body);

    /**
     * 本局回顾接口
     *
     * @param body
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/mobile/bs/battle/play/")
    Observable<HttpResponse<GameReviewQuestions>> getBattleReview(@Body RequestBody body);

    //获取历史数据排名
    @GET("/api/mobile/bs/battle/season/rank/history/")
    Observable<HttpResponse<CompetitionHistoryRank>> getCompetitionHistory(@Query("competition_id") String competition_id, @Query("limit") int limit, @Query("offset") int offset);

    //获取竞赛详情
    @GET("/api/mobile/bs/battle/competition/detail/")
    Observable<HttpResponse<CompetitionDetails>> getCompetitionDetail(@Query("competition_id") String competition_id, @Query("limit") int limit, @Query("offset") int offset);

    //获取经验值接口
    @GET("api/mobile/bs/battle/user/exp/")
    Observable<HttpResponse<BattleExp>> getExperience();


}
