package com.mooc.home.ui.todaystudy.must

import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.changeskin.SkinManager
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.setDrawLeft
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.ShowDialogUtils
import com.mooc.home.R
import com.mooc.home.model.todaystudy.CheckInStatus
import com.mooc.home.model.todaystudy.HotResource
import com.mooc.home.model.todaystudy.TaskStatus
import com.mooc.statistics.LogUtil

/**
 * 最近在学适配器
 */
class MustAdapter2(list: ArrayList<Pair<Int, List<Any>>>, var mustViewModel: MustViewModel) :
    BaseQuickAdapter<Pair<Int, List<Any>>, BaseViewHolder>(
        R.layout.home_item_todaystudy_must_resource,
        list
    ), DraggableModule {

    companion object {
        const val TYPE_CHECKIN_STR = 200   //签到状态
        const val TYPE_COURSE_STR = ResourceTypeConstans.TYPE_COURSE   //课程
        const val TYPE_ALBUM_STR = ResourceTypeConstans.TYPE_ALBUM   //音频
        const val TYPE_STUDYPROJECT_STR = ResourceTypeConstans.TYPE_STUDY_PLAN    //学习计划
        const val TYPE_EBOOK_STR = ResourceTypeConstans.TYPE_E_BOOK   //电子书
        const val TYPE_PUBLICATION_STR = ResourceTypeConstans.TYPE_PUBLICATION   //刊物
        const val TYPE_HOT_STR = 300    //最热资源
        const val TYPE_TASK_STR = 400    //我的任务

    }

    override fun convert(holder: BaseViewHolder, item: Pair<Int, List<Any>>) {
//        SkinManager.getInstance().injectSkin(holder.itemView)
        //绑定课程，音频，电子书数据
        when (item.first) {
            TYPE_CHECKIN_STR -> {   //签到
                holder.setText(R.id.tvResourceTitle, "签到打卡")
            }
            TYPE_TASK_STR -> {    //我的任务
                holder.setText(R.id.tvResourceTitle, "我的任务")
            }
            TYPE_COURSE_STR -> {    //课程
                holder.setText(R.id.tvResourceTitle, "课程")
            }
            TYPE_ALBUM_STR -> {     //音频
                holder.setText(R.id.tvResourceTitle, "音频课")
            }
            TYPE_STUDYPROJECT_STR -> { //学习项目
                holder.setText(R.id.tvResourceTitle, "学习项目")
            }
            TYPE_EBOOK_STR -> {     //电子书
                holder.setText(R.id.tvResourceTitle, "电子书")
            }
            TYPE_PUBLICATION_STR -> {     //刊物
                holder.setText(R.id.tvResourceTitle, "刊物")
            }
            TYPE_HOT_STR -> { //大家最新在学资源二级列表item
                holder.setText(R.id.tvResourceTitle, "大家最近在学的资源")
            }
        }

        //换肤
        val tvResourceTitle = holder.getView<TextView>(R.id.tvResourceTitle)
        val tvResourceTitleDrawableLefet =
            SkinManager.getInstance().resourceManager.getDrawableByName("shape_width4_height17_primary")
        tvResourceTitle.setDrawLeft(tvResourceTitleDrawableLefet, 10.dp2px())
        //设置对应资源的课程adaptar
        val rvResourceList = holder.getView<RecyclerView>(R.id.rvResourceList)
        rvResourceList.isNestedScrollingEnabled = false
        val second = item.second
        val mustResourceAdapter = MustResourceAdapter(second as ArrayList<Any>)
        mustResourceAdapter.addChildClickViewIds(R.id.tvTostudy)
        mustResourceAdapter.setOnItemClickListener { adapter, view, position ->
            onItemClick(adapter as MustResourceAdapter, second, position)
        }
        rvResourceList.layoutManager = LinearLayoutManager(context)
        rvResourceList.adapter = mustResourceAdapter

        //隐藏最后一条分割线
        val b = holder.layoutPosition - headerLayoutCount == data.size - 1
        holder.setGone(R.id.viewSpace, b)
    }

    /**
     * item点击事件
     */
    private fun onItemClick(adapter: MustResourceAdapter, it: ArrayList<Any>, position: Int) {
        val any = it[position]
        when (any) {
            is CheckInStatus -> {     //签到
                ARouter.getInstance().build(Paths.PAGE_CHECKIN).navigation()
            }

            is TaskStatus -> {     //任务
                ARouter.getInstance().build(Paths.PAGE_TASK_DETAILS)
                    .withString(IntentParamsConstants.PARAMS_TASK_ID, any.url).navigation();
            }
            is BaseResourceInterface -> {
                if (any is HotResource) { //如果是最热资源，需要调用资源已完成接口
                    mustViewModel.postTaskComplete(any)
                    adapter.remove(it[position])
                }

                LogUtil.addClickLogNew(
                    LogEventConstants2.P_TODAY, any._resourceId,
                    "${any._resourceType}",
                    any._other?.get(IntentParamsConstants.WEB_PARAMS_TITLE) ?: "",
                    "${LogEventConstants2.typeLogPointMap[any._resourceType]}#${any._resourceId}"
                )

                if (any._resourceType == ResourceTypeConstans.TYPE_COURSE
                    && ResourceTypeConstans.allCourseDialogId.contains(any._resourceId)
                ) {//工信部点击不进入课程详情页面，需要弹框的课程id
                    ShowDialogUtils.showDialog(context)
                } else {
                    ResourceTurnManager.turnToResourcePage(any)
                }

            }
        }
    }
}