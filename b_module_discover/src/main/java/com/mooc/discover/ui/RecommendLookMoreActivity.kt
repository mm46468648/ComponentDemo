package com.mooc.discover.ui

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.common.ktextends.ExtraDelegate
import com.mooc.common.ktextends.put
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
import com.mooc.discover.fragment.RecommendLookMoreFragment
import com.mooc.resource.widget.CommonTitleLayout

/**
 * 老项目
 * 推荐查看更多页面
 * 暂时还不清楚具体的入口在哪里
 */
@Route(path = Paths.PAGE_RECOMMEND_LOOK_MORE)
class RecommendLookMoreActivity : BaseActivity(){

    val resourceId by ExtraDelegate(IntentParamsConstants.PARAMS_RESOURCE_ID,"")
    val resourceTitle by ExtraDelegate(IntentParamsConstants.WEB_PARAMS_TITLE,"")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.discover_activity_recommend_lookmore)

        val commonTitleLayout = findViewById<CommonTitleLayout>(R.id.commonTitleLayout)

        commonTitleLayout.setOnLeftClickListener { finish() }
        commonTitleLayout.middle_text = resourceTitle

        val recommendLookMoreFragment = RecommendLookMoreFragment()
        recommendLookMoreFragment.arguments = Bundle().put(IntentParamsConstants.PARAMS_RESOURCE_ID,resourceId)
        supportFragmentManager.beginTransaction().replace(R.id.flContainer,recommendLookMoreFragment).commit()

    }
}