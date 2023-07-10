package com.mooc.commonbusiness.route;

public interface Paths {

    //HOME module
    String GROUP_HOME = "/home";
    String PAGE_HOME = GROUP_HOME + "/homeActivity";
    //    String PAGE_INNERCOMPONENT = GROUP_HOME + "/innerComponentActivity";
    String PAGE_ADJUST_TARGET = GROUP_HOME + "/adjustTargetActivity";    //调整目标
    String PAGE_SCHEME = GROUP_HOME + "/SchemeActivity";    //外部跳转


    //StudyRoom module
    String GROUP_STUDYROOM = "/studyroom";
    String PAGE_STUDYLIST_SORT = GROUP_STUDYROOM + "/studyListSortActivity";    //学习清单列表
    String PAGE_STUDYLIST_MOVE = GROUP_STUDYROOM + "/studyListMoveActivity";    //学习清单移动
    String PAGE_STUDYLIST_DETAIL = GROUP_STUDYROOM + "/studyListDetailActivity";  //学习清单详情
    String PAGE_DATA_BOARD = GROUP_STUDYROOM + "/dataBoardActivity";  //数据看板
    String PAGE_SCORE_RULE = GROUP_STUDYROOM + "/scoreRuleActivity";  //积分规则
    String PAGE_SCORE_DETAIL = GROUP_STUDYROOM + "/scoreDetailActivity";   //积分详情
    String PAGE_STUDY_RECORD = GROUP_STUDYROOM + "/studyRecordActivity";    //学习档案
    String PAGE_MY_DOWNLOAD = GROUP_STUDYROOM + "/myDownloadActivity";    //我的下载
    String SERVICE_STUDYROOM = GROUP_STUDYROOM + "/studyRoomService";
    String PAGE_MY_MSG = GROUP_STUDYROOM + "/MyMsgActivity";//我的消息页面
    String PAGE_INTEGRADL_EXCHANGE= GROUP_STUDYROOM + "/IntegralExchangeActivity";//积分兑换列表
    String PAGE_INTEGRADL_EXCHANGE_RECORD= GROUP_STUDYROOM + "/IntegralRecordActivity";//积分兑换记录
    String PAGE_EXCHANGE_POINT= GROUP_STUDYROOM + "/ExchangePointActivity";//积分兑换列表
    String PAGE_COURSE_MSG_DETAIL = GROUP_STUDYROOM + "/CourseMsgDetailActivity";//课程消息详情页面
    String PAGE_NODE = GROUP_STUDYROOM + "/NodeActivity";//笔记页面
    String PAGE_DOWNLOAD_COURSE_CHAPTER = GROUP_STUDYROOM + "/downloadCourseChaterActivity";//课程下载章节列表页面
//    String PAGE_OLD_DOWNLOAD_COURSE_CHAPTER = GROUP_STUDYROOM + "/oldDownloadCourseChaterActivity";//老版本课程下载章节列表页面
    String PAGE_DOWNLOAD_COURSE_PLAY = GROUP_STUDYROOM + "/downloadCoursePlayActivity";//离线课程播放页面
    String PAGE_DOWNLOAD_ALBUM_DETAIL = GROUP_STUDYROOM + "/downloadAlbumDetailActivity";//离线详情页面
    //    String Move_Note = GROUP_STUDYROOM + "/MoveNoteActivity";//移动笔记弹框页面
    String PAGE_INVITE_READ_BOOK = GROUP_STUDYROOM + "/inviteReadBookActivity";    //调整目标
    String PAGE_CONTRIBUTE_TASK = GROUP_STUDYROOM + "/contributeTaskActivity";    //贡献任务
    String PAGE_UNDERSTAND_CONTRIBUTION = GROUP_STUDYROOM + "/UnderstandContributionActivity";//了解贡献榜
    String PAGE_COLLECT_STUDY_LIST = GROUP_STUDYROOM + "/CollectStudyListActivity";//收藏的清单
    String PAGE_PUBLIC_STUDY_LIST = GROUP_STUDYROOM + "/PublickListActivity";//公开的学习清单(自己，他人，或收藏)
    String PAGE_FRIEND_SCORE_RANK = GROUP_STUDYROOM + "/ScoreRankActivity";//好友积分排行

    //SPLASH module
    String GROUP_SPLASH = "/splash";
    String PAGE_GUIDE = GROUP_SPLASH + "/guideActivity";

    //WEB module
    String GROUP_WEB = "/web";
    String PAGE_WEB_EXTERNAL_WEB = GROUP_WEB + "/ExternalLikeWebViewActivity";
    String PAGE_WEB = GROUP_WEB + "/webviewActivity";
    String PAGE_WEB_RECOMMEND_RESOURCE = GROUP_WEB + "/RecommendResourceWebviewActivity";    //有推荐的文章
    String PAGE_WEB_RESOURCE = GROUP_WEB + "/baseResourceWebviewActivity";    //h5类型学习资源
    //    String PAGE_WEB_TEXTVOLUM = GROUP_WEB + "/TextVolumeWebviewActivity";//测试卷
    String PAGE_WEB_MICROCOURSE = GROUP_WEB + "/microCourseWebActivity";   //微课
    String PAGE_WEB_MICRO_PROFESSIONAL = GROUP_WEB + "/microProfessionalWebactivity";   //微专业
    String PAGE_WEB_VERIFYCODE = GROUP_WEB + "/VerifyCodeWebActivity";   //验证码文章
    String PAGE_WEB_KNOWLEDGE = GROUP_WEB + "/knowledgeActivity";   //知识点
    String PAGE_WEB_PERIODICAL = GROUP_WEB + "/periodicalWebActivity";   //期刊
    String PAGE_WEB_ACTIVITY_TASK = GROUP_WEB + "/activityTaskWebActivity";   //活动任务
    String PAGE_WEB_STUDY = GROUP_WEB + "/WebViewStudyActivity";   //学习计划相关
    String PAGE_WEB_INITIORBRIEF = GROUP_WEB + "/InitiorBriefActivity";   //发起人简介
    //    String PAGE_WEB_TESTVOLUMN= GROUP_WEB + "/TestVolumnWebActivity";   //测试卷(可以统一跳转到basewebresource页面)
    String PAGE_COURSE_NEWXT_PLAY = GROUP_WEB + "/NewXtCoursePlayActivity"; //新学堂课程播放页面
    String SERVICE_SHARE_TEXT = GROUP_WEB + "/ShareSelectTextService";//
    String PAGE_ANNOUNCEMENT_DETAIL = GROUP_WEB + "/AnnouncementActivity";   //公告详情
    String PAGE_NOTICE_INFO = GROUP_WEB + "/NoticeInfoActivity";   //公告详情
    String SERVICE_WEB_INITX5 = GROUP_WEB + "/initX5Service";   //初始化x5内核服务
    String PAGE_WEB_PRIVACY = GROUP_WEB + "/PrivacyWebActivity";   //隐私策略页面
    String PAGE_WEB_MATCH_RULE = GROUP_WEB + "/MatchRuleWebActivity";   //比赛参与规则
    String PAGE_WEB_FEED_BACK = GROUP_WEB + "/FeedBackWebViewActivity";   //意见反馈


    //Login module
    String GROUP_LOGIN = "/login";
    String PAGE_LOGIN = GROUP_LOGIN + "/loginActivity";
    String SERVICE_USERINFO = GROUP_LOGIN + "/userInfoService";
    String SERVICE_SHARE = GROUP_LOGIN + "/shareService";
    String SERVICE_LOGIN = GROUP_LOGIN + "/loginservice"; //登录模块（微信相关）的服务

    //Search module
    String GROUP_SEARCH = "/search";
    String PAGE_SEARCH = GROUP_SEARCH + "/searchActivity";
    String PAGE_SEARCH_ADD_BOOK = GROUP_SEARCH + "/SearchBookActivity";


    //My module
    String GROUP_MY = "/my";
    String PAGE_EVERYDAY_READ = GROUP_MY + "/EverydayReadActivity";    //每日一读
    String PAGE_CHECKIN = GROUP_MY + "/CheckInActivity";    //签到打卡
    String PAGE_MY_MEDAL = GROUP_MY + "/MyMedalActivity";    //我的荣耀（已经迁移到学习档案）
    //    String PAGE_MY_CERTIFICATION= GROUP_MY + "/MyMedalActivity";    //我的荣耀
    String PAGE_APPLYCER = GROUP_MY + "/ApplyCerActivity";    //申请证书
    String PAGE_APPLYCER_INPUT = GROUP_MY + "/ApplyCerInputActivity";    //申请证书详情
    String PAGE_QUESTION_LIST = GROUP_MY + "/QuestionListActivity";    //常见问题列表页面
    String PAGE_QUESTION_MORE = GROUP_MY + "/QuestionMoreActivity";    //热门问题更多列表页面
    String PAGE_QUESTION_INFO = GROUP_MY + "/QuestionInfoActivity";    //问题详情
    String PAGE_FEED_LIST = GROUP_MY + "/FeedListActivity";    //意见反馈列表
    String PAGE_FEED_BACK = GROUP_MY + "/FeedBackActivity";    //意见反馈内容编辑页面
    String PAGE_FEED_BACK_LIST = GROUP_MY + "/FeedBackListActivity";    //意见反馈提交页面
    String PAGE_USER_INFO = GROUP_MY + "/UserInfoActivity";    //个人中心或者他人 的分享页面
    String PAGE_USERINFO_EDIT = GROUP_MY + "/UserInfoEditActivity";    //用户资料设置
    String PAGE_USERINFO_NAME = GROUP_MY + "/UserInfoNameActivity";    //用户昵称设置页面

    String PAGE_USERFOLLOW = GROUP_MY + "/UserFollowActivity";    //粉丝和关注人的页面
    String PAGE_CUSTOMER_SERVICE = GROUP_MY + "/CustomerServiceActivity";    //小梦客服页面
    String PAGE_SCHOOL_CIRCLE = GROUP_MY + "/SchoolCircleActivity";    //学友圈
    String PAGE_CHECKIN_REPAIRS = GROUP_MY + "/CheckinRepairsactivity";    //补签

    //Column module
    String GROUP_COLUMN = "/column";
    //    String PAGE_COLUMN = GROUP_COLUMN + "/ColumnActivity";   //专栏列表 （由首页推荐中专栏列表每一个item右上角或底部点击全部进入）
    String PAGE_COLUMN_DETAIL = GROUP_COLUMN + "/ColumnDetailActivity";     //专栏详情 （推荐中专栏列表中专栏类型item点击进入）
    String PAGE_COLUMN_ALL = GROUP_COLUMN + "/ColumnAllActivity";     //全部类型专栏
    String PAGE_COLUMN_QUICK = GROUP_COLUMN + "/ColumnQuickActivity";     //快捷入口组合栏目页面
    String PAGE_COLUMN_SUBSCRIBE_ALL = GROUP_COLUMN + "/AllColumnSubscribeActivity"; //全部专栏订阅
    String PAGE_COLUMN_LIST = GROUP_COLUMN + "/ColumnListActivity";   //专栏列表页面（由首页推荐中专栏列表每一个item右上角或底部点击全部进入）

    //Setting module
    String GROUP_SETTING = "/set";
    String PAGE_SETTING = GROUP_SETTING + "/SettingActivity";    //设置
    String PAGE_ABOUT = GROUP_SETTING + "/AboutActivity";    //关于我们
    String PAGE_DOWNLOAD_PATH_CHOOSE = GROUP_SETTING + "/DownloadPathChooseActivity";    //下载路径选择
    String PAGE_DOWNLOAD_QR = GROUP_SETTING + "/QrCodeDownloadActivity";    //下载二维码
    String PAGE_APK_UPDATE = GROUP_SETTING + "/ApkUpdateActivity";    //apk升级
    String SERVICE_UPDATE_APK = GROUP_SETTING + "/ApkUpdate";  //用户打点服务
    String SERVICE_SETTING_MSG = GROUP_SETTING + "/SettingMsgActivity";  //消息提醒
    String SERVICE_SETTING_PRIVACY = GROUP_SETTING + "/PrivacyActivity";  //隐私设置
    String SERVICE_SETTING_UPDATE_LOG = GROUP_SETTING + "/UpdateLogActivity";  //更新日志
    //privacy
    String PAGE_CONTROLLER = GROUP_SETTING + "/ControllerActivity";    //控制台页面
    String PAGE_CONTROLLER_TEST_ACCOUNT = GROUP_SETTING + "/ControllerTestAccountActivity";    //控制台测试账号页面
    String PAGE_CONTROLLER_TEST_RESOURCE = GROUP_SETTING + "/ControllerTestResourceActivity";    //控制台测试资源跳转
    String PAGE_LIVE_PUSH = GROUP_SETTING + "/LivePushActivity";    //直播推流页面
    String PAGE_LIVE_PLAY = GROUP_SETTING + "/LivePlayActivity";    //直播播放页面

    //Course moduel
    String GROUP_COURSE = "/course";
    String PAGE_COURSE_DETAIL = GROUP_COURSE + "/CourseDetailActivity"; //课程详情页面
    String PAGE_COURSE_PLAY = GROUP_COURSE + "/CoursePlayActivity"; //学堂课程播放页面
    String PAGE_COURSE_ZHS_PLAY = GROUP_COURSE + "/ZHSCoursePlayActivity"; //智慧树课程播放页面
    String PAGE_COURSE_ZHS_DOWNLOAD = GROUP_COURSE + "/ZHSCourseDownloadActivity"; //智慧树课程下载
    String PAGE_COURSE_NEWXT_DOWNLOAD = GROUP_COURSE + "/NewXtCourseDownloadActivity"; //新学堂课程下载
    String PAGE_COURSE_APPLY_VERIFY = GROUP_COURSE + "/CourseApplyVerifyActivity"; //课程申请证书
    String PAGE_COURSE_MARK = GROUP_COURSE + "/CourseMarkActivity"; //课程评分
    String PAGE_COURSE_SUB_USER = GROUP_COURSE + "/CourseChooseUserActivity"; //一起在学习的人
    String SERVICE_COURSE_DOWNLOAD = GROUP_COURSE + "/CourseDownloadService"; //课程下载服务
    String SERVICE_COURSE_VIDEO_ACTION = GROUP_COURSE + "/CourseVideoActionService"; //课程视频行为服务
    String SERVICE_COURSE_SCORE = GROUP_COURSE + "/ScoreActivity"; //课程评分

    //Statistics module //统计模块
    String GROUP_STATISTICS = "/statistics";
    String SERVICE_LOG = GROUP_STATISTICS + "/log";  //用户打点服务

    //Audio module 音频模块
    String GROUP_AUDIO = "/audio";
    String PAGE_ALBUM = GROUP_AUDIO + "/AlbumActivity"; //音频课页面
    String PAGE_AUDIO_PLAY = GROUP_AUDIO + "/AudioPlayActivity"; //音频播放页面
    String PAGE_AUDIO_OWN_BUILD_PLAY = GROUP_AUDIO + "/OwnBuildAudioPlayActivity"; //音频播放页面
    String SERVICE_AUDIO_FLOAT = GROUP_AUDIO + "/audioFloatService";   //音频浮窗服务
    String SERVICE_AUDIO_DOWNLOAD = GROUP_AUDIO + "/audioDownloadService";   //音频下载相关服务
    String SERVICE_AUDIO_PLAY = GROUP_AUDIO + "/audioPlayService";   //音频播放相关服务

    //Ebook module 电子书模块
    String GROUP_EBOOK = "/ebook";
    String PAGE_EBOOK_DETAIL = GROUP_EBOOK + "/ebookDetailActivity";
    String SERVICE_EBOOK = GROUP_EBOOK + "/ebookService";

    //Periodical module 期刊模块
    String GROUP_PERIODICAL = "/periodical";
    String PAGE_PUBLICATION_DETAIL = GROUP_PERIODICAL + "/publicationDetailActivity";

    //discover 发现
    String GROUP_DISCOVER = "/discover";
    String PAGE_HOT_RESOURCE = GROUP_DISCOVER + "/HotResourceActivity";   //热门课程和文章

    String PAGE_DISCOVER_TASK = GROUP_DISCOVER + "/DiscoverTestActivity";    //任务列表页
    String PAGE_DISCOVER_ALREADY_GET_TASK = GROUP_DISCOVER + "/DiscoverAlreadyGetTaskActivity";    //已领取的任务列表页
    String PAGE_DISCOVER_HISTORY_GET_TASK = GROUP_DISCOVER + "/DiscoverHistoryTaskActivity";    //已领取的任务列表页

    String PAGE_TASK_DETAILS = GROUP_DISCOVER + "/TaskDetailsActivity";//任务详情页面
    String PAGE_TASK_SIGN_DETAIL = GROUP_DISCOVER + "/TaskSignDetailActivity";//任务签到页面
    String PAGE_TASK_MUTUAL_DETAIL = GROUP_DISCOVER + "/TaskMutualDetailActivity";//互斥任务详情页面
    String PAGE_SELECT_STUDY_LIST_ACTIVITY = GROUP_DISCOVER + "/SelectStudyListActivity"; //绑定学习清单


    String PAGE_NEW_ONLINE = GROUP_DISCOVER + "/NewOnLineActivity";   //新鲜上线
    String PAGE_RECOMMEND_SPECIAL = GROUP_DISCOVER + "/RecommendSpecialListActivity";   //专题栏目
    String PAGE_RECOMMEND_LOOK_MORE = GROUP_DISCOVER + "/RecommendLookMoreActivity";   //推荐查看更多，当recoursetyp，为block也就是0的时候，落地页

    // studyProject 学习计划
    String GROUP_STUDYPROJECT = "/studyProject";
    String PAGE_STUDYPROJECT = GROUP_STUDYPROJECT + "/studyProjectActivity"; //学习计划页面
    String PAGE_RES_SIMPLE_INTRO = GROUP_STUDYPROJECT + "/resSimpleIntroActivity"; //资源简介页面
    String SERVICE_STUDYPROJECT_SHARE = GROUP_STUDYPROJECT + "/shareService";
    String PAGE_PUBLISHINGDYNAMICSCOMMEN= GROUP_STUDYPROJECT + "/PublishingDynamicsCommentActivity";//发布动态或者评论页面
    String PAGE_COMMENTLIST= GROUP_STUDYPROJECT + "/CommentListActivity";//评论列表页面
//    String PAGE_SHOWWEB_IMAGE= GROUP_STUDYPROJECT + "/ShowWebImageActivity";//显示图片
    String PAGE_BET_POINT= GROUP_STUDYPROJECT + "/BetPointActivity";//挑战设置页面
    String PAGE_SHOW_MEDAL= GROUP_STUDYPROJECT + "/ShowMedalActivity";//展示勋章弹框的页面
    String PAGE_NOTICE_LIST= GROUP_STUDYPROJECT + "/NoticeListMoreActivity";//公告列表
    String PAGE_NOMINATION= GROUP_STUDYPROJECT + "/NominationActivity";//自荐页面
    String PAGE_FOLLOWUP= GROUP_STUDYPROJECT + "/FollowUpActivity";//跟读页面
    String PAGE_FOLLOWUP_NEW= GROUP_STUDYPROJECT + "/FollowUpNewActivity";//跟读页面,不和学习项目耦合的跟读资源

    //common_business 公共业务模块
    String GROUP_COMMON_BUSINESS = "/commonBusiness";
    String PAGE_REPORT_DIALOG = GROUP_COMMON_BUSINESS + "/ReportDialogActivity";   //举报弹窗页面
    String PAGE_SHAKE_DIALOG = GROUP_COMMON_BUSINESS + "/ShakeDialogActivity";    //摇一摇反馈弹窗
    String PAGE_TO_WX_PROGRAM_DIALOG = GROUP_COMMON_BUSINESS + "/ToWxProgramActivity";    //去微信小程序弹窗
    String PAGE_BIG_IMAGE_PREVIEW = GROUP_COMMON_BUSINESS + "/BigImagePreviewActivity";    //大图预览
    String SERVICE_ADDRESOURCEPOINTS = GROUP_COMMON_BUSINESS + "/AddResourcePoints";    //增加资源积分

    //对战模块
    String GROUP_BATTLE = "/battle";
    String PAGE_BATTLE_MAIN = GROUP_BATTLE + "/battleMainActivity"; //答题对战主页页面
    String PAGE_SKILL_MAIN = GROUP_BATTLE + "/skillMainActivity"; //比武主页页面
    String PAGE_CREATE_SKILL = GROUP_BATTLE + "/createSkillActivity"; //创建比武页面
    String PAGE_BEGIN_SKILL = GROUP_BATTLE + "/beginSkillActivity"; //比武开始答题页面
    String PAGE_SKILL_ANSWER = GROUP_BATTLE + "/skillAnswerActivity"; //比武答题页面
    String PAGE_SKILL_REVIEW = GROUP_BATTLE + "/SkillReviewActivity"; //比武回顾页面
    String PAGE_SKILL_RESULT = GROUP_BATTLE + "/skillResultActivity"; //比武结果页面
    String PAGE_SKILL_LIST = GROUP_BATTLE + "/skillListActivity"; //比武列表页面
    String PAGE_SKILL_RANK = GROUP_BATTLE + "/SkillRankActivity"; //比武排行列表页面

    //微知识模块
    String GROUP_KNOWLEDGE = "/knowledge";
    String PAGE_KNOWLEDGE_MAIN = GROUP_KNOWLEDGE + "/knowledgeMainActivity";
}
