package com.mooc.studyroom.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gavin.com.library.PowerfulStickyDecoration
import com.gavin.com.library.listener.PowerGroupListener
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.setDrawRight
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.studyroom.R
import com.mooc.studyroom.model.ScoreDetail
import com.mooc.studyroom.ui.adapter.StudyScoreDetailAdapter
import com.mooc.studyroom.ui.pop.ScoreTimePickDialog
import com.mooc.studyroom.viewmodel.ScoreDetailViewModel
import com.mooc.studyroom.window.ScoreEmptyView
import java.text.SimpleDateFormat
import java.util.*


/**
 *积分明细页面
 * @author
 * @date 2021/4/2
 */
class ScoreDetailFragment : BaseListFragment<ScoreDetail, ScoreDetailViewModel>() {
    var scoreEmptyView: ScoreEmptyView? = null
    var emptyDate: String? = null

    companion object {
        const val SEARCH_TYPE_ALL = 1 //搜索全部
        const val SEARCH_TYPE_YEAR = 2   //搜索年份
        const val SEARCH_TYPE_MONTH = 3    //搜索月份
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scoreEmptyView = activity?.let { ScoreEmptyView(it) }
        refresh_layout?.setBackgroundColor(resources.getColor(R.color.color_white))
        moocPullRefresh?.setBackgroundColor(resources.getColor(R.color.color_white))
        mViewModel?.getPageData()?.observe(viewLifecycleOwner, {
            if (recycler_view.itemDecorationCount != 0) {
                recycler_view.invalidateItemDecorations()
            }

            if (it.isNotEmpty() && recycler_view.itemDecorationCount == 0) {
                recycler_view.addItemDecoration(getDecoration(it))
            }

            loge("beginDate", mViewModel?.beginDate ?: "")
            //如果没有当前选择的月份积，提示一下
//            if (mViewModel?.beginDate?.isNotEmpty() == true && it.isEmpty()) {
//                toast("该月无积分明细记录")
//            }

            if (it.isNotEmpty()) {
                loge("monthDate", it[0].month_date)
            }
//            if(mViewModel?.beginDate?.isNotEmpty()==true && it.isNotEmpty() && it[0].month_date != mViewModel?.beginDate){
//                toast("该月无积分明细记录")
//            }
        })
    }

    override fun initAdapter(): BaseQuickAdapter<ScoreDetail, BaseViewHolder>? {
        return mViewModel?.getPageData()?.value?.let {
            val adapter = StudyScoreDetailAdapter(it)
            adapter
        }
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATION")
    @SuppressLint("SimpleDateFormat")
    fun getDecoration(list: ArrayList<ScoreDetail>): RecyclerView.ItemDecoration {
        val listener: PowerGroupListener = object : PowerGroupListener {
            override fun getGroupName(position: Int): String? {
                if (position in list.indices) {
                    val get = list[position]

                    val beginDataType = getBeginDataType()
                    return if (beginDataType == SEARCH_TYPE_YEAR) {
                        get.year_date
                    } else {
                        get.month_date
                    }
                }
                return null
            }

            @SuppressLint("SetTextI18n", "InflateParams")
            override fun getGroupView(position: Int): View {
                //获取自定定义的组View
                val view: View = layoutInflater.inflate(
                        R.layout.studyroom_item_score_detail_decor,
                        null,
                        false
                )


                //如果筛选的日期没有数据，在顶部覆盖一个View，展示日期
                val textView = view.findViewById<View>(R.id.tvMonth) as TextView
                if (list.isEmpty()) {
                    //如果是空，就是当前月份，如果不为空就是yyyy-dd的格式
                    var formatStr =
                            TimeFormatUtil.formatDate(System.currentTimeMillis(), "yyyy年MM月")

                    val beginDataType = getBeginDataType()
                    if (beginDataType == SEARCH_TYPE_YEAR) {
                        (view.findViewById<View>(R.id.llMonth) as LinearLayout).setBackgroundColor(resources.getColor(R.color.color_e7))
                        formatStr = "${mViewModel!!.beginDate}年"
                        (view.findViewById<View>(R.id.tvScore) as TextView).text = "年积分"
                    } else {
                        (view.findViewById<View>(R.id.llMonth) as LinearLayout).setBackgroundColor(resources.getColor(R.color.color_F2F2F2))
                        if (mViewModel?.beginDate?.contains("-") == true) {
                            formatStr = "${mViewModel!!.beginDate.split("-")[0]}年${
                                mViewModel!!.beginDate.split("-")[1]
                            }月"
                        }
                        (view.findViewById<View>(R.id.tvScore) as TextView).text = "月积分"
                    }

                    textView.text = formatStr
                }
                if (position in list.indices) {
                    val get = list[position]
                    val beginDataType = getBeginDataType()
//                    if (get.studyplan_name == Constans.SCORE_TOTAL_STR) {
////                        val formatDate = TimeFormatUtil.formatDate(get.add_time * 1000, "yyyy年")
//                        (view.findViewById<View>(R.id.llMonth) as LinearLayout).setBackgroundColor(resources.getColor(R.color.color_e7))
//                        textView.text = "${get.year_date}年"
//                        textView.setDrawRight(null)
//                        (view.findViewById<View>(R.id.tvScore) as TextView).text = "年积分  ${get.year_score}"
//                    } else
                        if (beginDataType == SEARCH_TYPE_YEAR) {
                            val formatDate = TimeFormatUtil.formatDate(get.add_time * 1000, "yyyy年")
                            (view.findViewById<View>(R.id.llMonth) as LinearLayout).setBackgroundColor(resources.getColor(R.color.color_e7))
                            textView.text = formatDate
                            textView.setDrawRight(R.mipmap.studyroom_ic_score_detail_arrow_down)
                            (view.findViewById<View>(R.id.tvScore) as TextView).text = "年积分  ${get.year_score}"
                        } else {
                            val formatDate = TimeFormatUtil.formatDate(get.add_time * 1000, "yyyy年MM月")
                            (view.findViewById<View>(R.id.llMonth) as LinearLayout).setBackgroundColor(resources.getColor(R.color.color_F2F2F2))
                            textView.text = formatDate
                            textView.setDrawRight(R.mipmap.studyroom_ic_score_detail_arrow_down)
                            (view.findViewById<View>(R.id.tvScore) as TextView).text = "月积分  ${get.month_score}"
                        }

                }
                return view
            }
        }

        //当begin data只有年的时候,需要改变颜色
        val bgColor = if (getBeginDataType() == SEARCH_TYPE_YEAR) {
            Color.parseColor("#E7E7E7")
        } else {
            Color.parseColor("#F1F1F1")
        }

        @Suppress("UnnecessaryVariable")
        val decoration = PowerfulStickyDecoration.Builder
                .init(listener)
                .setGroupHeight(50.dp2px())
                .setGroupBackground(bgColor)
                .setOnClickListener { position, _ ->//点击弹出来时间选择弹框
//                pvTime?.show()
                    if (position in list.indices) {
//                        if(list[position].studyplan_name == Constans.SCORE_TOTAL_STR) {     //全部积分不弹
//                            return@setOnClickListener
//                        }
                        initTimePicker(list[position].add_time * 1000)
                    } else {
                        //如果是空数据的情况下选择
                        val beginDataType = getBeginDataType()
                        if (beginDataType == SEARCH_TYPE_YEAR) {
                            if (mViewModel?.beginDate?.isNotEmpty() == true) {
                                val simpleDateFormat = SimpleDateFormat("yyyy")
                                initTimePicker(simpleDateFormat.parse(mViewModel?.beginDate).time)
                            }
                        } else if (beginDataType == SEARCH_TYPE_MONTH) {
                            if (mViewModel?.beginDate?.isNotEmpty() == true) {
                                val simpleDateFormat = SimpleDateFormat("yyyy-MM")
                                initTimePicker(simpleDateFormat.parse(mViewModel?.beginDate).time)
                            }
                        } else {
                            initTimePicker(System.currentTimeMillis())
                        }
                    }


                } //重置span（使用GridLayoutManager时必须调用）
                //.resetSpan(mRecyclerView, (GridLayoutManager) manager)
                .build()

        return decoration
    }

    /**
     * 获取搜索数据的类型
     * @return 1代表全部类型,2代表只有年,3代表只有月份
     *
     */
    fun getBeginDataType(): Int {
        return when {
            TextUtils.isEmpty(mViewModel?.beginDate ?: "") -> {
                SEARCH_TYPE_ALL
            }
            mViewModel?.beginDate?.contains("-") == true -> {
                SEARCH_TYPE_MONTH
            }
            else -> {
                SEARCH_TYPE_YEAR
            }
        }
    }

    private fun initTimePicker(currentTime: Long) { //Dialog 模式下，在底部弹出
        val c = Calendar.getInstance()
        c.time = Date(currentTime)
        val year = c[Calendar.YEAR]
        val month = c[Calendar.MONTH] + 1
//        when (getBeginDataType()) {
//            SEARCH_TYPE_YEAR -> {
//                year = c[Calendar.YEAR]
//                month = -1
//            }
//            SEARCH_TYPE_MONTH -> {
//                year = c[Calendar.YEAR]
//                month = c[Calendar.MONTH] + 1
//            }
//            SEARCH_TYPE_ALL -> {
//                year = -1
//                month = -1
//            }
//        }

        val termPickDialog = ScoreTimePickDialog(requireContext(), year, month)
        //根据年,月的选择,请求该年月下的接口,当为全部的时候,y,t = -1
        termPickDialog.onConfirm = { y, t ->
//            toast("${y}年${t}月")
//            mViewModel?.beginDate = "$y-$t"
            if (y == -1 && t == -1) {//都为全部
                mViewModel?.beginDate = ""
            } else if (y != -1 && t == -1) {//年不为全部,月为全部
                mViewModel?.beginDate = "$y"
            } else {
                mViewModel?.beginDate = "$y-$t"
            }

            if (recycler_view.itemDecorationCount != 0) {
                val itemDecorationAt =
                        recycler_view.getItemDecorationAt(0) as PowerfulStickyDecoration
                itemDecorationAt.clearCache()
            }
//            refresh_layout?.setBackgroundColor(resources.getColor(R.color.color_white))
//            moocPullRefresh?.setBackgroundColor(resources.getColor(R.color.color_white))
            loadDataWithRrefresh()
        }
        XPopup.Builder(requireContext())
                .offsetY((-10).dp2px())
                .asCustom(termPickDialog)
                .show()
    }

}