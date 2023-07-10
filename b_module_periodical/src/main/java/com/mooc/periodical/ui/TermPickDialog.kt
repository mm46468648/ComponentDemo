package com.mooc.periodical.ui

import android.content.Context
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter
import com.contrarywind.view.WheelView
import com.lxj.xpopup.core.BottomPopupView
import com.mooc.periodical.R
import com.mooc.periodical.databinding.PeriodicalPopTermPickBinding
import com.mooc.periodical.model.Term
//import kotlinx.android.synthetic.main.periodical_pop_term_pick.view.*

/**
 * 期刊选择弹窗
 */
class TermPickDialog(mContext: Context, var terms: List<Term>) : BottomPopupView(mContext) {
    override fun getImplLayoutId(): Int {
        return R.layout.periodical_pop_term_pick
    }

    //    val years = arrayListOf<String>("2021","2020","2019")
//    val termsArray = arrayListOf<String>("1","2","3","4","5","6")
    var currentYears: Int = -1
    var currentTerm : String = ""
    var onConfirm : ((year:Int,term:String)->Unit)? = null
    private lateinit var inflater : PeriodicalPopTermPickBinding
    override fun onCreate() {
        super.onCreate()

        inflater = PeriodicalPopTermPickBinding.bind(popupImplView)
        setWheelView(inflater.wvYear)
        setWheelView(inflater.wvTerm)

        //年份数组
        val years = terms.map {
            it.year
        }
        inflater.wvYear.adapter = ArrayWheelAdapter(years)

        //初始化期
        setTermsData(0)

        inflater.wvYear.setOnItemSelectedListener {
            //terms联动，根据位置获取term
            setTermsData(it)
        }

        inflater.wvTerm.setOnItemSelectedListener {
            currentTerm = inflater.wvTerm.adapter.getItem(it) as String
        }

        inflater.tvCancel.setOnClickListener { dismiss() }
        inflater.tvOk.setOnClickListener {
            onConfirm?.invoke(currentYears,currentTerm)
            dismiss()
        }
    }

    fun setTermsData(yearPosition:Int){
        if(yearPosition !in terms.indices) return

        val get = terms.get(yearPosition)
        currentYears = get.year
        currentTerm = get.value.get(0)

        val value = get.value
        inflater.wvTerm.adapter = ArrayWheelAdapter(value)
    }
    fun setWheelView(wv: WheelView) {
        wv.setCyclic(false)
        wv.setAlphaGradient(true)
        wv.setItemsVisibleCount(3)
    }
}