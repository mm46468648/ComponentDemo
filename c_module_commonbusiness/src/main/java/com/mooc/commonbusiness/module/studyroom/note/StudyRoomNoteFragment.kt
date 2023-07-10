package com.mooc.commonbusiness.module.studyroom.note

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.base.BaseUserLogListenFragment
import com.mooc.commonbusiness.base.BaseViewModelFactory
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.home.NoteBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.module.studyroom.StudyListDetailViewModel
import com.mooc.resource.widget.Space120LoadMoreView


/**
 * 学习室笔记
 */
class StudyRoomNoteFragment : BaseUserLogListenFragment<NoteBean, NoteViewModel>() {


    companion object {
        fun getInstance(bundle: Bundle? = null): StudyRoomNoteFragment {
            val fragment = StudyRoomNoteFragment()
            bundle?.apply {
                fragment.arguments = this
            }
            return fragment
        }
    }
    var folderId:String = ""
    var parentViewModel: StudyListDetailViewModel? = null

    override fun genericViewModel() {
        //文件夹id，如果不传代表是根目录，""
        folderId = arguments?.getString(IntentParamsConstants.STUDYROOM_FOLDER_ID) ?: ""
        if (folderId.isNotEmpty()) {
            parentViewModel =
                ViewModelProviders.of(requireActivity())[StudyListDetailViewModel::class.java]
        }
        mViewModel = ViewModelProviders.of(this, BaseViewModelFactory(folderId))
            .get(NoteViewModel::class.java)
    }


    override fun initAdapter(): BaseQuickAdapter<NoteBean, BaseViewHolder>? {
        val let = (mViewModel as NoteViewModel).getPageData().value?.let {
            val discoverMoocAdapter = NoteAdapter(it, true)
            discoverMoocAdapter.setOnItemClickListener { adapter, view, position ->
                val noteBean = adapter.data[position] as NoteBean
                ARouter.getInstance()
                    .build(Paths.PAGE_NODE)
                    .withString(IntentParamsConstants.INTENT_NODE_ID, noteBean.id)
                    .navigation()
            }
            discoverMoocAdapter.addChildClickViewIds(R.id.tvNoteTitle, R.id.tvNoteBelong)
            discoverMoocAdapter.setOnItemChildClickListener { adapter, view, position ->
                val noteBean = discoverMoocAdapter.data.get(position)
                when (view.id) {
                    R.id.tvNoteTitle -> {//点击标题进去笔记详情页面
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
                                        hashMapOf.put(
                                            IntentParamsConstants.PERIODICAL_PARAMS_BASICURL,
                                            noteBean.basic_url
                                        )
                                    }
                                    return hashMapOf
                                }
                        })
                    }
                    R.id.tvNoteBelong -> { //点击进入属于的专栏或者专题，或者查看全部
                        when (noteBean.recommend_type) {
                            ResourceTypeConstans.TYPE_COLUMN.toString(), ResourceTypeConstans.TYPE_SPECIAL.toString() -> {
                                ResourceTurnManager.turnToResourcePage(object :
                                    BaseResourceInterface {
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
                                    .withString(
                                        IntentParamsConstants.PARAMS_RESOURCE_ID,
                                        noteBean.recommend_id
                                    )
                                    .withString(
                                        IntentParamsConstants.WEB_PARAMS_TITLE,
                                        noteBean.recommend_title
                                    )
                                    .navigation();
                            }

                        }
                    }
                    else -> {
                    }
                }
            }
            discoverMoocAdapter
        }

//        //获取公开状态，确定是否可以编辑
        //        //获取公开状态，确定是否可以编辑
        parentViewModel?.publicStatus?.observe(this,
            Observer<Boolean> {
                let?.needEdit = !it
                let?.toast = resources.getString(R.string.text_no_publicate_tip)
            })
        return let
    }

    override fun initEmptyView() {
        val tipStr = if (mViewModel?.folderId?.isEmpty() == true) "你还没有添加资源"
        else "抱歉，该学习清单里没有任何资源文件"
        //在学习室底部
        emptyView.setTitle(tipStr)
        //在学习室底部
        emptyView.setTitle("你还没有添加资源")
        emptyView.setEmptyIcon(R.drawable.common_gif_folder_empty)
        emptyView.setGravityTop(20.dp2px())
        emptyView.setIconOverride(150.dp2px(), 150.dp2px())
        emptyView.setButton("+添加学习资源", View.OnClickListener {
            //跳转专题页面可以通过拦截器判断登录状态
            if (!GlobalsUserManager.isLogin()) {
                ResourceTurnManager.turnToLogin()
                return@OnClickListener
            }
            //已登录，跳转发现页
            ARouter.getInstance().build(Paths.PAGE_HOME)
                .withInt(IntentParamsConstants.HOME_SELECT_POSITION, 0)
                .withInt(IntentParamsConstants.HOME_SELECT_CHILD_POSITION, 0)
                .navigation()
        })
    }


    override fun getLoadMoreView(): BaseLoadMoreView {
        return Space120LoadMoreView()
    }
}