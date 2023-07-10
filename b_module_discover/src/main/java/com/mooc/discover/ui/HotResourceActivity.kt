package com.mooc.discover.ui

import android.os.Bundle
import android.view.LayoutInflater
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.ktextends.put
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
import com.mooc.discover.databinding.ActivityHotResourceBinding
import com.mooc.discover.fragment.HotResourceFragment
//import kotlinx.android.synthetic.main.activity_hot_resource.*

/**
 * 每日推荐的最热课程或者文章栏目列表页面
 * @author limeng
 * @date 2020/11/12
 */
@Route(path = Paths.PAGE_HOT_RESOURCE)
class HotResourceActivity : BaseActivity() {


    companion object {
        //发现
        const val HOME_HOT_TYPE = "hot_type"
    }

    val type: String by extraDelegate(HOME_HOT_TYPE,"")

    private lateinit var inflater: ActivityHotResourceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = ActivityHotResourceBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        inintView()

        //添加fragment
        val resourceType = if ("1".equals(type)) ResourceTypeConstans.TYPE_COURSE else ResourceTypeConstans.TYPE_ARTICLE
        val xtCourseDownloadFragment = HotResourceFragment()
        xtCourseDownloadFragment.arguments = Bundle()
            .put(IntentParamsConstants.PARAMS_RESOURCE_TYPE, resourceType)
        supportFragmentManager.beginTransaction()
            .replace(R.id.flContainer, xtCourseDownloadFragment).commit()

    }

    private fun inintView() {

        //type去分是课程还是文章
        val title = if ("1".equals(type)) "热门课程" else "热门文章"
        inflater.commonTitle.middle_text = title
        inflater.commonTitle.setOnLeftClickListener { finish() }



    }


}