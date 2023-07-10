package com.mooc.studyroom.ui.pop

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter
import com.contrarywind.view.WheelView
import com.lxj.xpopup.core.BottomPopupView
import com.mooc.studyroom.R
import com.mooc.studyroom.databinding.StudyroomDialogScoreTimePickBinding
//import kotlinx.android.synthetic.main.studyroom_dialog_score_time_pick.view.*
import java.util.*

/**
 * 积分明细年月选择弹窗
 */
@SuppressLint("ViewConstructor")
class ScoreTimePickDialog(
        mContext: Context,
        private var currentYears: Int = -1,
        private var currentMonth: Int = -1
) : BottomPopupView(mContext) {

    private lateinit var inflater: StudyroomDialogScoreTimePickBinding
    override fun getImplLayoutId(): Int {
        return R.layout.studyroom_dialog_score_time_pick
    }


    var onConfirm: ((year: Int, term: Int) -> Unit)? = null

    override fun onCreate() {
        super.onCreate()
        inflater = StudyroomDialogScoreTimePickBinding.bind(popupImplView)
        setWheelView(inflater.wvYear)
        setWheelView(inflater.wvMonth)


        //初始化年份数据
        initData()

        inflater.tvCancel.setOnClickListener { dismiss() }
        inflater.tvOk.setOnClickListener {
            onConfirm?.invoke(currentYears, currentMonth)
            dismiss()
        }
    }

    private fun initData() {
        // 设置"年"的显示数据
        val endDate = Date(System.currentTimeMillis())
        val calendar = Calendar.getInstance()
        calendar.time = endDate
        val endYear = calendar.get(Calendar.YEAR)
//        val endMonth = calendar.get(Calendar.MONTH) + 1

        val yearItems = (2016..endYear).map {
            it.toString()
        }.toMutableList()
//        yearItems.add("全部")

        inflater.wvYear.adapter = ArrayWheelAdapter(yearItems)
        //初始化时显示的数据，显示全部上一个，最新日期
//        if (currentYears == -1) {//年份为全部，初始化时则把年份定位到全部，月份定位到空
//            inflater.wvYear.currentItem = yearItems.indexOf("全部")
//            setMonthData("全部")
//        } else {
        inflater.wvYear.currentItem = yearItems.indexOf(currentYears.toString())
        setMonthData(currentYears.toString())
//        }

        inflater.wvYear.setOnItemSelectedListener {
            //terms联动，根据位置获取term
            //滑动年份的时候，从全部过度到其他年份，如果月份从1月份开始，则把currentMonth从-1改为1
//            if (currentYears == -1) {
//                currentMonth = 1
//            }
            setMonthData(yearItems[it])
        }

    }

    private fun setMonthData(selectYear: String) {

        val calendar = Calendar.getInstance()
        calendar.time = Date(System.currentTimeMillis())
        val endYear = calendar.get(Calendar.YEAR)
        val endMonth = calendar.get(Calendar.MONTH) + 1

//        if ("全部" == selectYear) {
//            //选择当前日期数据
//            currentYears = -1
//            currentMonth = -1
//            wv_month.adapter = ArrayWheelAdapter<String>(arrayListOf())
//            return
//        }
        currentYears = selectYear.toInt()

        // 设置"月"的显示数据
        val endMothNum = if (selectYear.toInt() == endYear) endMonth else 12
        val monthItems = (1..endMothNum).map {
            it.toString()
        }.toMutableList()
        monthItems.add("全部")

        inflater.wvMonth.adapter = ArrayWheelAdapter(monthItems)
//        if (currentMonth == -1) {//月份为全部，定位到全部
//            inflater.wvMonth.currentItem = monthItems.indexOf("全部")
//        } else {
        if (selectYear.toInt() == endYear) {//如果是当前年，则定位到当前年的最后月份
            if (currentMonth > endMonth) {//如果当前月份大于当年的最后月份，则当前月份定位到最后月份
                inflater.wvMonth.currentItem = monthItems.indexOf(endMonth.toString())
                currentMonth = endMonth
            } else {
                inflater.wvMonth.currentItem = monthItems.indexOf(currentMonth.toString())
            }
        } else {
            inflater.wvMonth.currentItem = monthItems.indexOf(currentMonth.toString())
        }
//        }

        inflater.wvMonth.setOnItemSelectedListener {
            val selectMonth = monthItems[it]

            if (!TextUtils.isEmpty(selectMonth)) {
                currentMonth = if ("全部" == selectMonth) {
                    -1
                } else {
                    selectMonth.toInt()
                }
            }
        }
    }


    private fun setWheelView(wv: WheelView) {
        wv.setCyclic(false)
        wv.setAlphaGradient(true)
        wv.setItemsVisibleCount(5)
    }
}