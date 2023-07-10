package com.mooc.ebook

//import kotlinx.android.synthetic.main.common_fragment_layout_recyclerlist.*
//import kotlinx.android.synthetic.main.activity_e_book_detail.*
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.base.BaseVmActivity
import com.mooc.commonbusiness.base.BaseVmViewModel
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.manager.ebook.ReadHistoryManager
import com.mooc.commonbusiness.model.eventbus.EbookProgressRefreshEvent
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.route.routeservice.StudyRoomService
import com.mooc.commonbusiness.utils.ClickFilterUtil
import com.mooc.commonbusiness.utils.IShare
import com.mooc.commonbusiness.utils.incpoints.AddPointManager
import com.mooc.commonbusiness.utils.incpoints.FirstAddPointManager
import com.mooc.ebook.databinding.ActivityEBookDetailBinding
import com.mooc.ebook.viewmodel.EbookShareViewModel
import com.mooc.ebook.viewmodel.EbookViewModel
import com.mooc.statistics.LogUtil
import com.zhangyue.plugin.ZYReaderSdkHelper
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

@Route(path = Paths.PAGE_EBOOK_DETAIL)
class EBookDetailActivity : BaseVmActivity<EBookBean, ActivityEBookDetailBinding>() {

    val bookId by extraDelegate(IntentParamsConstants.EBOOK_PARAMS_ID, "")
    var bookDetail: EBookBean? = null

    private val shareService: ShareSrevice by lazy {
        ARouter.getInstance().build(Paths.SERVICE_SHARE).navigation() as ShareSrevice
    }

    private val ebookViewModel: EbookShareViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置返回健
        bindingView?.commonTitle?.setOnLeftClickListener {
            finish()
        }

        ebookViewModel.getRecommendEBooks(bookId).observe(this, {
            if (it.isNullOrEmpty()) {
                bindingView?.viewSpace?.visibility = View.GONE
                bindingView?.tvRecommendEBook?.visibility = View.GONE
                bindingView?.rcyRecommendEBook?.visibility = View.GONE
                return@observe
            }
            val ebookAdapter = RecommendEbookAdapter(it)
            bindingView?.rcyRecommendEBook?.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            bindingView?.rcyRecommendEBook?.adapter = ebookAdapter
            ebookAdapter.setOnItemClickListener { adapter, view, position ->
                val resource = ebookAdapter.data.get(position)
                ResourceTurnManager.turnToResourcePage(resource)
                LogUtil.addClickLogNew(
                    "${LogEventConstants2.T_EBOOK}#$bookId#${LogEventConstants2.P_SIM}",
                    resource._resourceId,
                    resource._resourceType.toString(),
                    resource.title,
                )

            }
        })
    }

    override fun onCreateLayout(bindingView: ActivityEBookDetailBinding?) {
    }

    override fun genericViewModel(): BaseVmViewModel<EBookBean> {
//        return ViewModelProviders.of(this, BaseViewModelFactory(bookId)).get(EbookViewModel::class.java)

        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return EbookViewModel(bookId) as T
            }
        }).get(EbookViewModel::class.java)
    }

    override fun getLayoutId(): Int = R.layout.activity_e_book_detail

    override fun onDataChange(it: EBookBean) {
        this.bookDetail = it
        bindingView?.data = it

        (mViewModel as EbookViewModel).postStudyLog(it)


        //电子书,老代码在initdata里直接加积分
        //学习资源增加积分
        AddPointManager.startAddPointsTask(
            this,
            it.title,
            ResourceTypeConstans.TYPE_E_BOOK.toString(),
            bookId
        )

        //每日首次增加积分
        FirstAddPointManager.startAddFirstPointsTask(
            this, ResourceTypeConstans.typeAliasName[ResourceTypeConstans.TYPE_E_BOOK]
                ?: "", bookId
        )

        setAddStudyRoomStata()
        //点击加入学习室
        bindingView?.commonTitle?.setOnSecondRightIconClickListener {
            if (bookDetail?.is_enroll != true) {
                //
                addToStudyRoom()
            }
        }


        //点击开始阅读
        bindingView?.btStartRead?.setOnClickListener {
            if (!GlobalsUserManager.isLogin()) {
                ResourceTurnManager.turnToLogin()
                return@setOnClickListener
            }


            //记录阅读数据，在我的下载中展示
            bookDetail?.let { book ->
                if (book.status != 0) {
                    toast("资源已下线")
                    return@let
                }

                if (!ClickFilterUtil.canClick()) {
                    return@let
                }

                try {
                    ZYReaderSdkHelper.enterBookReading(this, book.ebook_id)
                    LogUtil.addClickLogNew(
                        LogEventConstants2.P_EBOOK,
                        book._resourceId,
                        book._resourceType.toString(),
                        book.title
                    )
                    ReadHistoryManager.save(book)
                    ebookViewModel.uploadZYReaderProgress(bookId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }
        }
        initListener()
    }


    private fun initListener() {

        bindingView?.toBookCategory?.setOnClickListener {
            bookDetail?.let { book ->
                if (book.status != 0) {
                    toast("资源已下线")
                    return@let
                }
                ReadHistoryManager.save(book)
                ebookViewModel.uploadZYReaderProgress(bookId)
                if (!ClickFilterUtil.canClick()) {
                    return@let
                }
                if (!TextUtils.isEmpty(book.ebook_id)) {
                    ZYReaderSdkHelper.enterBookCatalog(this, book.ebook_id.toInt())
                }
            }
        }

        bindingView?.commonTitle?.setOnRightIconClickListener(View.OnClickListener {
            showChoseDialogNew(
                bookDetail?.picture ?: "",
                bookDetail?.title ?: "",
                bookDetail?.writer ?: ""
            )
        })
    }

    /**
     * 弹出来选择弹框
     */
    private fun showChoseDialog(cover: String, tit: String, msg: String) {
        val commonBottomSharePop = CommonBottomSharePop(this)
        ebookViewModel.getShareDetailData(
            this,
            ResourceTypeConstans.TYPE_E_BOOK.toString(),
            bookId,
            cover,
            tit,
            msg
        ).observe(this, Observer {
//                commonBottomSharePop.middleImage = it
            commonBottomSharePop.onItemClick = { shareSit ->
                val build = IShare.Builder()
                    .setSite(shareSit)
                    .setImageBitmap(it)
                    .build()
                shareService.shareAddScore(ShareTypeConstants.TYPE_EBOOK, this, build)
            }
        })
        XPopup.Builder(this).asCustom(commonBottomSharePop).show()

    }

    /**
     * 弹出来选择弹框
     */
    private fun showChoseDialogNew(cover: String, tit: String, msg: String) {
        XPopup.Builder(this)
            .asCustom(CommonBottomSharePop(this, { shareSit ->
                ebookViewModel.makeShareDetailAndShare(
                    this,
                    ResourceTypeConstans.TYPE_E_BOOK.toString(),
                    bookId,
                    cover,
                    tit,
                    msg,
                    shareSit
                )
            }))
            .show()

    }

    /**
     * 设置加入学习室状态
     */
    private fun setAddStudyRoomStata() {
        val rightIcon =
            if (bookDetail?.is_enroll == true) R.mipmap.common_ic_title_right_added else R.mipmap.common_ic_title_right_add
        bindingView?.commonTitle?.setRightSecondIconRes(rightIcon)
    }

    /**
     * 电子书加入学习室
     */
    private fun addToStudyRoom() {
        val jsonObject = JSONObject()
        jsonObject.put("url", bookId)
        jsonObject.put("resource_type", ResourceTypeConstans.TYPE_E_BOOK)
        jsonObject.put("other_resource_id", bookId)

        //显示加入学习室弹窗
        val studyRoomService: StudyRoomService? =
            ARouter.getInstance().navigation(StudyRoomService::class.java)
        studyRoomService?.showAddToStudyRoomPop(this, jsonObject) {
            if (it) {
                //更新加入学习室状态
                bookDetail?.is_enroll = true
                setAddStudyRoomStata()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()

        EventBus.getDefault().post(EbookProgressRefreshEvent(bookId))
    }
}
