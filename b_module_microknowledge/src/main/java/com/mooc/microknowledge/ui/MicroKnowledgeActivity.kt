package com.mooc.microknowledge.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.webkit.WebView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.*
import com.mooc.common.utils.bitmap.BitmapUtils
import com.mooc.commonbusiness.adapter.CommonMicroKnowAdapter
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.LogPageConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.decoration.GrideItemDecoration
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.TestVolume
import com.mooc.commonbusiness.model.microknowledge.MicroKnowBean
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.pop.studyroom.CertificationPop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.utils.ClickFilterUtil
import com.mooc.commonbusiness.utils.HtmlUtils
import com.mooc.commonbusiness.utils.IShare
import com.mooc.microknowledge.KnowLedgeViewModel
import com.mooc.microknowledge.R
import com.mooc.microknowledge.databinding.KnowledgeActivityMainBinding
import com.mooc.microknowledge.model.KnowledgeResource
import com.mooc.microknowledge.model.MicroKnowledge
import com.mooc.statistics.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn

@Route(path = Paths.PAGE_KNOWLEDGE_MAIN)
class MicroKnowledgeActivity : BaseActivity() {

    lateinit var inflater: KnowledgeActivityMainBinding
    val mViewModel by viewModels<KnowLedgeViewModel>()
    val resourceId by extraDelegate(IntentParamsConstants.PARAMS_RESOURCE_ID, "")
    var firstLoad = true
    var listTag = 0

    //分享服务
    val shareService: ShareSrevice by lazy {
        ARouter.getInstance().navigation(ShareSrevice::class.java)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = KnowledgeActivityMainBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        inflater.webSouth.setOnTouchListener { v, event ->
            (v as WebView).requestDisallowInterceptTouchEvent(true)
            false
        }
//        inflater.llRoot.post {   //沉浸式状态栏高度
//            inflater.llRoot.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0)
//        }
        inflater.commonTitle.setOnLeftClickListener { finish() }
        dataObserver()
        getData()
    }

    fun getData() {
        mViewModel.getKnowLedgeDetail(resourceId) {}
        mViewModel.getKnowLedgeStatics(resourceId) {}
        mViewModel.getKnowledgeTest(resourceId) {}
        mViewModel.getKnowledgeMicroRecommend(resourceId) {}
    }

    override fun onResume() {
        super.onResume()
        //返回页面后刷新一下测试资源
        if (!firstLoad)
            mViewModel.getKnowledgeTest(resourceId) {}
        firstLoad = false
    }

    fun dataObserver() {
        mViewModel.knowledgeDetailData.observe(this, Observer {
            bindDetail(it)
            //详情接口请求成功之后请求资源，因为需要用到list_tag
            mViewModel.getKnowledgeResource(resourceId) {}
        })

        mViewModel.knowledgeStatisticsData.observe(this, Observer {
            bindStatistics(it)
        })
        mViewModel.knowledgeResourceData.observe(this, Observer {
            bindResource(it)
        })
        mViewModel.knowledgeTestData.observe(this, Observer {
            bindTest(it)
        })
        mViewModel.knowledgeMicroRecommendData.observe(this, Observer {
            bindMircoKnowRecommend(it)
        })
    }

    var isLike: Boolean = false
    var curLikeNum = 0
    fun bindDetail(detail: MicroKnowledge?) {

        detail?.apply {
            listTag = this.list_tag

            Glide.with(this@MicroKnowledgeActivity).load(this.head_pic).into(inflater.ivHead)
            Glide.with(this@MicroKnowledgeActivity).load(this.page_pic).into(inflater.ivBottom)

            try {
                inflater.clRoot.setBackgroundColor(Color.parseColor(this.color))
            } catch (e: Exception) {

            }
            //设置模块标题
            var learnGuide = "学习指南"
            var learnContent = "学习内容"
            var microTest = "微测试"
            if (this.config != null) {
                val configBean = this.config
                if (!TextUtils.isEmpty(configBean?.des_title)) {
                    learnGuide = configBean?.des_title.toString()
                }
                if (!TextUtils.isEmpty(configBean?.detail_title)) {
                    learnContent = configBean?.detail_title.toString()
                }
                if (!TextUtils.isEmpty(configBean?.exam_title)) {
                    microTest = configBean?.exam_title.toString()
                }
            }
            inflater.tvLearnGuide.text = learnGuide
            inflater.tvLearnContent.text = learnContent
            inflater.tvMicroTest.text = microTest


            inflater.webSouth.setMaxHeight(300.dp2px())
            val des = HtmlUtils.getHtmlWithMargin(detail.des, 25)
            HtmlUtils.setWebView(inflater.webSouth, this@MicroKnowledgeActivity)
            inflater.webSouth.loadDataWithBaseURL(null, des, "text/html", "utf-8", null)

            //设置分享
            inflater.tvShare.setOnClickListener {
                share(this)
            }


            //设置点赞
            isLike = detail.like_status ?: false
            if (detail.like_status == true) {
                inflater.tvPriseNum.setDrawLeft(R.mipmap.knowledge_ic_prised, 3)
            } else {
                inflater.tvPriseNum.setDrawLeft(R.mipmap.knowledge_ic_unprise, 3)
            }

            inflater.tvPriseNum.setOnClickListener {
                if (!ClickFilterUtil.canClick()) return@setOnClickListener
                if (isLike) {
                    inflater.tvPriseNum.setDrawLeft(R.mipmap.knowledge_ic_unprise, 3)
                    inflater.tvPriseNum.text = "点赞${--curLikeNum}人"
                } else {
                    inflater.tvPriseNum.setDrawLeft(R.mipmap.knowledge_ic_prised, 3)
                    inflater.tvPriseNum.text = "点赞${++curLikeNum}人"
                }
                isLike = !isLike
                mViewModel.clickPrise(resourceId, if (isLike) 1 else 0)
            }
        }
    }

    fun bindStatistics(detail: MicroKnowledge?) {
        detail?.apply {
            inflater.tvStudyNum.text = "${detail.click_num}人学习"
            inflater.tvJoinNum.text = "${detail.exam_num}人参加微测试"
            inflater.tvGetMedalNum.text = "${detail.medal_num}人获得勋章"
            inflater.tvGetCerNum.text = "${detail.certificate_num}人获得学习证明"


            curLikeNum = detail.like_num
            inflater.tvPriseNum.text = "点赞${detail.like_num}人"
            inflater.tvShare.text = "分享${detail.share_num}人"

            //如果不为空,弹出勋章弹窗
            if (!TextUtils.isEmpty(medal_link)) {
                showMedal(medal_link)
            }

        }
    }

    private fun bindResource(resource: List<KnowledgeResource>?) {
        if (resource == null) return
        when (listTag) {
            4 -> {
                val mGridLayoutManager =
                    GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
                inflater.rvResource.layoutManager = mGridLayoutManager
                val space = 15f.dp2px()
                inflater.rvResource.setPadding(space, 10.dp2px(), 0, 0)
                while (inflater.rvResource.itemDecorationCount > 0) {
                    inflater.rvResource.removeItemDecorationAt(0);
                }
                inflater.rvResource.addItemDecoration(GrideItemDecoration(space))
            }
            7 -> {
                val mGridLayoutManager =
                    GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
                inflater.rvResource.layoutManager = mGridLayoutManager
                val space = 15f.dp2px()
                inflater.rvResource.setPadding(space, 10.dp2px(), 0, 0)
                while (inflater.rvResource.itemDecorationCount > 0) {
                    inflater.rvResource.removeItemDecorationAt(0);
                }
                inflater.rvResource.addItemDecoration(GrideItemDecoration(space))
            }
            6 -> {
                val mGridLayoutManager =
                    GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
                inflater.rvResource.layoutManager = mGridLayoutManager
                val space = 15f.dp2px()
                inflater.rvResource.setPadding(space, 10.dp2px(), 0, 0)

                while (inflater.rvResource.itemDecorationCount > 0) {
                    inflater.rvResource.removeItemDecorationAt(0);
                }
                inflater.rvResource.addItemDecoration(GrideItemDecoration(space))
            }
            else -> {
                val layoutManager = LinearLayoutManager(this)
                inflater.rvResource.layoutManager = layoutManager
            }
        }
        if (resource.isNotEmpty()) {
            resource.forEach {
                it.classType = listTag
            }
        }

        val microResourceAdapter =
            MicroKnowLedgeResourceAdapter(resource as ArrayList<KnowledgeResource>)
        microResourceAdapter.setOnItemClickListener { adapter, view, position ->
            //资源跳转
            val resourceItem = resource[position]
            ResourceTurnManager.turnToResourcePage(resourceItem)
            LogUtil.addClick(
                LogPageConstants.PID_MICRO_KNOWLEDGE,
                resourceItem.title,
                resourceItem._resourceType,
                resourceItem._resourceId,
                resourceId
            )
        }
        inflater.rvResource.adapter = microResourceAdapter
    }

    fun bindTest(testList: List<TestVolume>?) {
        if (testList == null) return
        inflater.rvTest.layoutManager = LinearLayoutManager(this)
        val microTestAdapter = MicroTestAdapter(testList as ArrayList<TestVolume>)
        microTestAdapter.addChildClickViewIds(R.id.tvGoTest)
        microTestAdapter.addChildClickViewIds(R.id.tvToApply)
        microTestAdapter.setOnItemChildClickListener { adapter, view, position ->
            val testVolume = testList.get(position)
            if (view.id == R.id.tvGoTest) {
                //去测试
                ResourceTurnManager.turnToResourcePage(object : BaseResourceInterface {
                    override val _resourceId: String
                        get() = testVolume.test_paper_id
                    override val _resourceType: Int
                        get() = ResourceTypeConstans.TYPE_TEST_VOLUME
                    override val _other: Map<String, String>
                        get() = hashMapOf(IntentParamsConstants.WEB_PARAMS_URL to testVolume.test_paper_url)
                })
            }

            if (view.id == R.id.tvToApply) {
                if (testVolume.certificate_status == "1") {
                    applyCertificate(testVolume)
                }
                if (testVolume.certificate_status == "3" && !TextUtils.isEmpty(testVolume.cert_link)) {
                    showCertificate(testVolume.cert_link)
                }
            }
        }
        inflater.rvTest.adapter = microTestAdapter
    }

    private fun bindMircoKnowRecommend(resource: List<MicroKnowBean>?) {
        if (resource == null) return
        inflater.rvMicroKnowList.layoutManager = LinearLayoutManager(this)
        val microResourceAdapter =
            CommonMicroKnowAdapter(resource as ArrayList<MicroKnowBean>)
        microResourceAdapter.setOnItemClickListener { adapter, view, position ->
            //资源跳转
            val resourceItem = resource[position]
            ResourceTurnManager.turnToResourcePage(resourceItem)
            LogUtil.addClick(
                LogPageConstants.PID_MICRO_KNOWLEDGE,
                resourceItem.title,
                resourceItem._resourceType,
                resourceItem._resourceId,
                resourceId
            )
        }
        inflater.rvMicroKnowList.adapter = microResourceAdapter
    }

    /**
     * 申请证书
     */
    fun applyCertificate(volume: TestVolume) {
        ARouter.getInstance().build(Paths.PAGE_APPLYCER_INPUT)
            .withString(IntentParamsConstants.INTENT_CERTIFICATE_ID, volume.certificate_id)
            .navigation()
    }

    /**
     * 展示证书
     */
    fun showCertificate(src: String) {
        val window = CertificationPop(this, src)
        window.showAtLocation(inflater.root, Gravity.CENTER_VERTICAL, 0, 0)
    }

    /**
     * 展示勋章
     */
    fun showMedal(img: String) {
        XPopup.Builder(this).asCustom(ShowMedalPop(this, img)).show()
    }

    fun share(detail: MicroKnowledge) {
        LogUtil.addClick(
            LogPageConstants.PID_MICRO_KNOWLEDGE,
            LogPageConstants.EID_SHARE,
            resourceId
        )
        lifecycleScope.launchWhenCreated {
            mViewModel.createShareBitmap(this@MicroKnowledgeActivity, detail)
                .flowOn(Dispatchers.IO)
                .catch { e ->
                    loge(e.toString())
                    toast("图片状态异常")
                }
                .collect { view ->
                    loge(Thread.currentThread())
                    share(BitmapUtils.createUnShowBitmapFromLayout(view))
                }
        }
    }

    /**
     * 调用分享
     */
    fun share(bitmap: Bitmap) {
        XPopup.Builder(this@MicroKnowledgeActivity)
            .asCustom(
                CommonBottomSharePop(this@MicroKnowledgeActivity, { platform ->
                    shareService.shareAddScore(
                        ShareTypeConstants.TYPE_READ, this@MicroKnowledgeActivity, IShare.Builder()
                            .setSite(platform)
                            .setTitle("")
                            .setMessage("")
                            .setImageBitmap(bitmap)
                            .build(), {
                            //分享成功后,刷新数据接口
                            mViewModel.getKnowLedgeStatics(resourceId) {}
                        })
//                    actionLog(date, platform)
                })
            )
            .show()
    }
}