package com.mooc.studyroom.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.common.ktextends.extraDelegate
import com.mooc.studyroom.R
import com.mooc.studyroom.databinding.StudyroomActivityStudyrecordBinding
import com.mooc.studyroom.ui.adapter.StudyArchiveVpAdapter
import com.mooc.studyroom.viewmodel.StudyRecordActivityViewModel
//import kotlinx.android.synthetic.main.studyroom_activity_studyrecord.*

/**
 * 学习档案
 */
@Route(path = Paths.PAGE_STUDY_RECORD)
class StudyArchiveActivity : BaseActivity() {

    val titleArray = arrayOf("学习记录","我的荣誉")
    val mAdapter : StudyArchiveVpAdapter by lazy { StudyArchiveVpAdapter(this) }

//    val mViewModel by lazy {
//        ViewModelProviders.of(this)[StudyRecordActivityViewModel::class.java]
//    }
    val mViewModel by viewModels<StudyRecordActivityViewModel>()

    //贡献值任务intent 传递是否直接选中学习记录->参加的学习项目
    val selectStudyProject by extraDelegate("selectStudyProject",false)

    private lateinit var inflater: StudyroomActivityStudyrecordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = StudyroomActivityStudyrecordBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        initView()

        if(selectStudyProject){
            mViewModel.selectStudyProject.postValue(true)
        }

    }

    private fun initView() {
        inflater.commonTitle.setOnLeftClickListener { finish() }
        //设置viewpagder
        inflater.viewPager.adapter = mAdapter
        inflater.simpleTab.setUpWithViewPage2(inflater.viewPager, titleArray.toList() as ArrayList<String>)
        inflater.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val commonTitleRightVisibal = if(position == 0) View.VISIBLE else View.GONE
                inflater.commonTitle.tv_right?.visibility = commonTitleRightVisibal
            }
        })



    }

}