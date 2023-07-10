package com.mooc.battle;

/**
 * 游戏中使用常量
 */
public class GameConstant {

    public static final String GAME_PARAMS_SEASON_ID = "season_id";
    public static final String GAME_PARAMS_ACTION = "action";
    public static final String GAME_PARAMS_UUID = "match_uuid";
    public static final String GAME_PARAMS_ORDER = "order";      //废弃,之前的题目序号,后续改为题目id
    public static final String GAME_PARAMS_ANSWER = "answer";
    public static final String GAME_PARAMS_RESULT = "gameResult"; //对战结果
    public static final String GAME_PARAMS_PK_USER = "pkUserInfo"; //对战对手信息
    public static final String GAME_PK_USER_ID = "pk_user_id";      //对手id
    public static final String GAME_QUESTION_ID = "question_id";      //题目id
    public static final String GAME_NEXT_QUESTION_ID = "next_question_id";      //下一题目id

    public static final String GAME_ACTION_FIND_MATCH = "find_match";        //寻找对局
    public static final String GAME_ACTION_GET_QUESTION = "get_question";      //获取题目
    public static final String GAME_ACTION_SUBMIT_ANSWER = "submit_answer";      //提交答案
    public static final String GAME_ACTION_VIEW_ANSWER = "view_answer";      //轮询结果
    public static final String GAME_ACTION_CHECK_STATE = "check";      //检测游戏状态
    public static final String GAME_ACTION_START = "start";
    public static final String GAME_ACTION_END = "end";
    public static final String GAME_ACTION_REVIEW = "review";     //回顾
    public static final String GAME_ACTION_QUIT = "quit";

    public static final String GAME_DEFAULT_ORDER_VALUE = "-1";         //默认的选项序号,(未选)

    public static final String PARAMS_COMPETITION_ID = "params_competition_id";   //竞赛id
    public static final String PARAMS_SEASON_ID = "params_season_id";   //赛季id
    public static final String PARAMS_SEASON_FROM_HISTORY = "params_season_from_history";   //是否是从我得历史入


    public static final int CUT_DOWN_UNIT = 10;   //答题倒计时单位间隔
    public static final String QUIT_GAME_TIP = "对战正在进行中，退出将导致对局可能直接被判失败，是否确认退出？"; //退出答题的文案
    public static final String QUIT_SKILL_TIP = "答题正在进行中，是否确认退出？"; //比武退出答题的文案

    //接口中使用的状态码
    public static final int STATUS_CODE_SUCCESS = 0;   //正常情况
    public static final int STATUS_CODE_RUN = 10000;   //对手逃跑


    //游戏中的提示消息
    public static final String STATUS_MATCH_FAIL = "申请加入对战失败";   //匹配失败
    public static final String STATUS_MATCH_RUN = "10000_";   //对手逃跑状态码
    public static final String STATUS_NETWORK_ERROR = "网络异常";   //匹配失败
    public static final String STATUS_SERVER_ERROR = "服务端异常";   //匹配失败


    //比武中使用的常量

    public static final String SKILL_TITLE = "title";   //比武名称
    public static final String SKILL_ACTION_CREATE = "start";   //创建比武
    public static final String SKILL_CREATE_DESC = "get_create_desc";   //创建比武描述信息
    public static final String SKILL_JOIN_DESC = "get_join_desc";   //创建比武描述信息
    public static final String SKILL_GET_QUESTION = "get_questions";   //获取比武问题
    public static final String SKILL_END = "end";   //比武结算

    public static final String SKILL_TOURNAMENT_ID = "tournament_id";   //比武id
    public static final String SKILL_QUESTION_ID = "question_id";   //问题id
    public static final String SKILL_NEXT_QUESTION_ID = "next_question_id";   //比武下一个问题id

    /**
     * 格式化一下名字,由于名字太长,展示不下需要...
     *
     * @return
     */
    public static String getFormatName(String name, int needLength) {
        String newName = name;
        if (name.length() > needLength) {
            String s = name.substring(0, needLength);
            newName = s + "...";
        }
        return newName;
    }

    /**
     * 格式化一下名字,由于名字太长,展示不下中间...
     * needLength 大于的这个数字时
     * beforeLength  显示前几个
     * afterLength  显示后几个
     *
     * @return
     */
    public static String getSpecialFormatName(String name, int needLength, int beforeLength, int afterLength) {
        String newName = name;
        if (name.length() > needLength) {
            newName = name.substring(0, beforeLength) + "***" + name.substring(name.length() - afterLength);
        }
        return newName;
    }

    /**
     * 格式化一下名字,由于名字太长,第一行替换成...后换行
     */
    public static String getSpecialFormatName(String name) {

        if (name.length() > 10) {
            return name.substring(0, 5) +
                    "...\r\n" +
                    name.substring(name.length() - 5);
        } else if (name.length() > 5) {
            return name.substring(0, 5) +
                    "\r\n" +
                    name.substring(5);
        }


        return name;
    }
}
