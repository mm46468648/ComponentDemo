package com.mooc.studyroom.ui.activity

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.bitmap.BitmapUtils
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.dialog.PublicDialog
import com.mooc.commonbusiness.glide.GlideTransform
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.model.eventbus.RefreshStudyRoomEvent
import com.mooc.commonbusiness.model.home.NoteBean
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.pop.IncreaseScorePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.route.routeservice.StudyRoomService
import com.mooc.commonbusiness.utils.IShare
import com.mooc.commonbusiness.utils.share.ShareSchoolUtil
import com.mooc.resource.widget.MoocImageView
import com.mooc.studyroom.R
import com.mooc.studyroom.databinding.StudyroomActivityNodeBinding
import com.mooc.studyroom.viewmodel.NodeViewModel
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

/**
 *笔记页面
 * @author limeng
 * @date 2021/3/17
 */
@Route(path = Paths.PAGE_NODE)
class NodeActivity : BaseActivity() {
    private var bean: NoteBean? = null
//    val mViewModel by lazy {
//        ViewModelProviders.of(this).get(NodeViewModel::class.java)
//    }
    val mViewModel by viewModels<NodeViewModel>()
    var nodeId: String? = null
    private lateinit var inflater: StudyroomActivityNodeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = StudyroomActivityNodeBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        initView()
        initListener()
        initData()
        initDataListener()
    }

    private fun initDataListener() {

        //分享学友圈结果
        mViewModel.shareSchoolCircleBean.observe(this, Observer {
            if (it?.code == 1) {
                if (it.score == 1) {//禁成长
                    toast(it.message)
//                    val pop = ShowShareScorePop(this, note_copy, it.score, 0)
//                    pop.show()
                    XPopup.Builder(this)
                            .asCustom(IncreaseScorePop(this, it.score, 0))
                            .show()
                } else if (it.score == 0) {
                    toast(it.message + "," + it.share_message)
                }
            } else {
                toast(it?.message)
            }
        })
        //获取data 数据
        mViewModel.nodeResultBean.observe(this, {
            setUI(it)
        })
        //删除笔记功能
        mViewModel.deleteResultBean.observe(this, {
            if (it.message.isNotEmpty()) {
                toast(it.message)
            }
            if (it.isSuccess) {
                EventBus.getDefault().post(RefreshStudyRoomEvent(ResourceTypeConstans.TYPE_NOTE))
                finish()
            }

        })


    }

    private fun setUI(noteBean: NoteBean) {
        bean = noteBean
        inflater.noteTitle.setText(noteBean.other_resource_title)
        inflater.noteContent.setText(noteBean.content)
        val belong = noteBean.recommend_title
        if (TextUtils.isEmpty(belong)) {
            inflater.noteBelong.text = ""
        } else {
            inflater.noteBelong.text = "|  $belong"
        }
        inflater.noteTime.text = noteBean.create_time

        if (noteBean.is_allowed_move != "1") { //如果这个笔记所在的学习清单在公开的学习清单中。
            inflater.noteDel.setTextColor(Color.parseColor("#B6B6B6"));
            inflater.noteMove.setTextColor(Color.parseColor("#B6B6B6"));
            inflater.noteDel.isEnabled = false;
            inflater.noteMove.isEnabled = false;
        }

        //如果不是自己的id，隐藏底部
        if (noteBean.user_id != GlobalsUserManager.uid) {
            inflater.llBottom.visibility = View.GONE
        }
        //点击标题进入文章
        inflater.noteTitle.setOnClickListener {
            //直接跳转文章页面
            ResourceTurnManager.turnToResourcePage(object : BaseResourceInterface {
                override val _resourceId: String
                    get() = noteBean.other_resource_id.toString()
                override val _resourceType: Int
                    get() = noteBean.other_resource_type
                override val _other: Map<String, String>
                    get() {
                        val hashMapOf = hashMapOf(
                                IntentParamsConstants.WEB_PARAMS_TITLE to noteBean.other_resource_title,
                                IntentParamsConstants.WEB_PARAMS_URL to noteBean.other_resource_url
                        )

                        if (_resourceType == ResourceTypeConstans.TYPE_PERIODICAL && noteBean.basic_url.isNotEmpty()) {
                            hashMapOf.put(IntentParamsConstants.PERIODICAL_PARAMS_BASICURL, noteBean.basic_url)
                        }
                        return hashMapOf
                    }
            })
        }

        //点击属于进入专栏，专题，或者全部
        inflater.noteBelong.setOnClickListener {
            when (noteBean.recommend_type) {
                ResourceTypeConstans.TYPE_COLUMN.toString(), ResourceTypeConstans.TYPE_SPECIAL.toString() -> {
                    ResourceTurnManager.turnToResourcePage(object : BaseResourceInterface {
                        override val _resourceId: String
                            get() = noteBean.recommend_id
                        override val _resourceType: Int
                            get() = noteBean.recommend_type.toInt()
                        override val _other: Map<String, String>?
                            get() = null
                    })
                }
                ResourceTypeConstans.TYPE_BLOCK.toString() -> {
                    ARouter.getInstance().build(Paths.PAGE_RECOMMEND_SPECIAL)
                            .withString(IntentParamsConstants.PARAMS_RESOURCE_ID, noteBean.recommend_id)
                            .navigation();
                }

            }
        }
    }

    private fun initView() {
        nodeId = intent.getStringExtra(IntentParamsConstants.INTENT_NODE_ID)
    }

    private fun initData() {
        mViewModel.getNodeData(nodeId)
    }

    private fun initListener() {
        inflater.commonTitleLayout.setOnLeftClickListener {
            finish()
        }

        //删除功能
        inflater.noteDel.setOnClickListener {
            showDelDialog()
        }
        //分享功能
        inflater.noteShare.setOnClickListener {

            downLoadHeader()

        }
        //复制功能
        inflater.noteCopy.setOnClickListener(View.OnClickListener {
            val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            cm.text = if (TextUtils.isEmpty(bean?.content)) "" else bean?.content
            toast(getString(R.string.copy_success))
        })
        //移动功能
        inflater.noteMove.setOnClickListener(View.OnClickListener {
            val jsonObject = JSONObject()

            jsonObject.put("url", bean?.other_resource_url)
            jsonObject.put("resource_type", ResourceTypeConstans.TYPE_NOTE)
            if (!TextUtils.isEmpty(bean?.other_resource_id.toString()) && "0" != bean?.other_resource_id.toString()) {
                jsonObject.put("other_resource_id", bean?.other_resource_id.toString())
            }
            jsonObject.put("title", bean?.other_resource_title)
            jsonObject.put("column_id", bean?.recommend_id)
            jsonObject.put("column_title", bean?.recommend_title)
            jsonObject.put("content", bean?.content)
            if (!TextUtils.isEmpty(bean?.id)) {
                jsonObject.put("note_id", bean?.id)
            }

            val studyRoomService: StudyRoomService? = ARouter.getInstance().navigation(StudyRoomService::class.java)
            studyRoomService?.showAddToStudyRoomPop(this, jsonObject) {
//                if (it) {
//                    //更新加入学习室状态
//                    commonTitleLayout.setRightSecondIconRes(R.mipmap.common_ic_title_right_added)
//                }
            }
        })
    }

    private fun showDelDialog() {
        val publicDialogBean = PublicDialogBean()
        publicDialogBean.strMsg = resources.getString(R.string.text_ser_del_resource)
        publicDialogBean.strLeft = resources?.getString(R.string.text_cancel)
        publicDialogBean.strRight = resources?.getString(R.string.text_ok)
        publicDialogBean.isLeftGreen = 0
        XPopup.Builder(this)
                .asCustom(PublicDialog(this, publicDialogBean) {
                    if (it == 1) {
                        delNode()
                    }
                })
                .show()

    }

    private fun delNode() {
        mViewModel.delNode(nodeId)
    }

    /**
     * 生成图片并分享
     */
    @SuppressLint("SetTextI18n", "InflateParams")
    private fun toShare(bitmap: Bitmap) {

        val shareView = LayoutInflater.from(this).inflate(R.layout.studyroom_view_note_share, null)
        val title = shareView.findViewById(R.id.note_title) as TextView
        val content = shareView.findViewById(R.id.note_content) as TextView
        val belong = shareView.findViewById(R.id.note_belong) as TextView
        val name = shareView.findViewById(R.id.name) as TextView

        val header = shareView.findViewById(R.id.head) as MoocImageView
        header.setImageBitmap(BitmapUtils.makeRoundCorner(bitmap))

        name.setText(GlobalsUserManager.userInfo?.name + "  的笔记")
        title.setText(bean?.other_resource_title)
        content.setText(bean?.content)
        belong.text = if (TextUtils.isEmpty(bean?.recommend_title)) "" else "|  引自" + bean?.recommend_title.toString()

        val shareBitmap = BitmapUtils.createUnShowBitmapFromLayout(shareView)


//        var commonBottomSharePop =
        val choose: (platform: Int) -> Unit = { platform ->
            if (platform == CommonBottomSharePop.SHARE_TYPE_SCHOOL) {
                // 分享到学友圈
                ShareSchoolUtil.postSchoolShare(this, ResourceTypeConstans.TYPE_NOTE.toString(), nodeId.toString(), bean?.other_resource_url)
            } else {
                val shareService = ARouter.getInstance().navigation(ShareSrevice::class.java)
                shareService.shareAddScore(ShareTypeConstants.TYPE_NOTE, this, IShare.Builder()
                        .setSite(platform)
                        .setTitle("")
                        .setMessage("")
                        .setImageBitmap(shareBitmap)
                        .build())

            }
        }
        XPopup.Builder(this)
                .asCustom(CommonBottomSharePop(this, choose, false))
                .show()


    }


    fun downLoadHeader() {
        Glide.with(this)
                .load(GlobalsUserManager.userInfo?.avatar)
                .transform(GlideTransform.centerCropAndRounder2)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                    ) {
                        toShare(resource.toBitmap())
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        val decodeResource = BitmapFactory.decodeResource(
                            getResources(),
                            R.mipmap.common_ic_user_head_default,
                            null
                        )

                        toShare(decodeResource)
                    }
                })
    }

}