package com.mooc.studyproject.ui

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.studyproject.JoinStudyState
import com.mooc.studyproject.R
import com.mooc.studyproject.databinding.StudyprojectActivityResSimpleIntroBinding
import com.mooc.common.ktextends.extraDelegate
import com.mooc.commonbusiness.route.Paths
//import kotlinx.android.synthetic.main.studyproject_activity_comment_list.*

/**
 * 资源简介页面
 */
@Route(path = Paths.PAGE_RES_SIMPLE_INTRO)
class ResSimpleIntroActivity : BaseActivity(){


    val simpleIntro  by extraDelegate("simpleIntro", JoinStudyState())
    val resourceType by extraDelegate(IntentParamsConstants.PARAMS_RESOURCE_TYPE,-1)
    lateinit var contentView : StudyprojectActivityResSimpleIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contentView = DataBindingUtil.setContentView<StudyprojectActivityResSimpleIntroBinding>(this, R.layout.studyproject_activity_res_simple_intro)


        //绑定详情 (返回的是html格式，要做一下转化)
        contentView.simpleIntro = simpleIntro
        //绑定标题
        contentView.commonTitle.middle_text = ResourceTypeConstans.typeStringMap[resourceType]

        initListener()

    }

    private fun initListener() {
        contentView.commonTitle.setOnLeftClickListener { finish() }
    }
}