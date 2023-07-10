package com.mooc.studyroom.ui.activity

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.ktextends.extraDelegate
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyroom.Constans
import com.mooc.studyroom.R
import com.mooc.studyroom.databinding.ActivityIntegralExchangeBinding
import com.mooc.studyroom.ui.fragment.integral.IntegralExchangeFragment

/**
 *积分兑换列表
 * @author limeng
 * @date 2020/12/24
 */
@Route(path = Paths.PAGE_INTEGRADL_EXCHANGE)
class IntegralExchangeActivity:  BaseActivity(){


    val totalScore by extraDelegate(Constans.INTENT_TOTAL_SCORE, 0)

    private lateinit var inflater: ActivityIntegralExchangeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        inflater = ActivityIntegralExchangeBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        inintView()

        var bundle=Bundle();
        bundle.putInt(Constans.INTENT_TOTAL_SCORE,totalScore);
        val xtCourseDownloadFragment = IntegralExchangeFragment.getInstance(bundle)

        supportFragmentManager.beginTransaction()
            .replace(R.id.flContainer, xtCourseDownloadFragment).commit()

    }


    private fun inintView() {


        inflater.commonTitle.middle_text = "积分兑换"
        inflater.commonTitle.right_text="兑换记录"

        inflater.commonTitle.setOnLeftClickListener { finish() }

        inflater.commonTitle.setOnRightTextClickListener{
            ARouter.getInstance().build(Paths.PAGE_INTEGRADL_EXCHANGE_RECORD).withInt(Constans.INTENT_TOTAL_SCORE,totalScore).navigation();
        }


    }

}