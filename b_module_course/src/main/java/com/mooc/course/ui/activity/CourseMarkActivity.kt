package com.mooc.course.ui.activity

import android.app.Activity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatViewInflater
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.module.studyroom.course.CourseViewModel
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.course.R
import com.mooc.course.databinding.ActivityScoreBinding
import com.mooc.course.model.CourseScoreBean
import com.mooc.course.ui.adapter.CourseScoreAdapter
import com.mooc.course.viewmodel.CoursePlayViewModel
import com.mooc.course.viewmodel.CourseScoreViewModel
import com.mooc.course.viewmodel.NewXtCourseDownloadActivityViewModel
//import kotlinx.android.synthetic.main.activity_score.*
import org.json.JSONArray
import org.json.JSONObject


/**
 *课程评分页面
 * @author limeng
 * @date 2022/4/29
 */
@Route(path = Paths.PAGE_COURSE_MARK)
class CourseMarkActivity : BaseActivity() {
    private var mScoreAdapter: CourseScoreAdapter? = null
    private var resource_id: String? = null
    private var categoryData = ArrayList<CourseScoreBean>()
    var isAllChange: Boolean? = true// 检测是否选了所有的星星

//    val mViewModel: CourseScoreViewModel by lazy {
//        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
//            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//                return CourseScoreViewModel() as T
//            }
//        }).get(CourseScoreViewModel::class.java)
//    }
    val mViewModel: CourseScoreViewModel by viewModels()

    private lateinit var inflater: ActivityScoreBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflater = ActivityScoreBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        initView()
        initData()
        initListener()
        initDataListener()
    }


    private fun initView() {
        resource_id = intent.getStringExtra(IntentParamsConstants.COURSE_PARAMS_ID);
        mScoreAdapter = CourseScoreAdapter(categoryData)
        inflater.recyclerView.layoutManager = LinearLayoutManager(this)
        inflater.recyclerView.adapter = mScoreAdapter
        inflater.commonTitle.tv_right?.isEnabled = false
    }

    private fun initData() {
        mViewModel.getAppraise(ResourceTypeConstans.TYPE_COURSE.toString())
    }

    private fun initListener() {
        inflater.commonTitle.setOnLeftClickListener { finish() }
        inflater.commonTitle.setOnRightTextClickListener() {
            //提交数据
            if (resource_id == null) {
                return@setOnRightTextClickListener

            }
            val appraise = ArrayList<CourseScoreBean>()
            mScoreAdapter?.data?.forEach {
                val bean = CourseScoreBean()
                bean.category_id = it.id
                bean.score = it.score
                appraise.add(bean)
            }
            val hashMapOf = hashMapOf(
                    "appraise" to appraise,
                    "resource_id" to resource_id,
            )
            mViewModel.postAppraise(RequestBodyUtil.fromJsonStr(Gson().toJson(hashMapOf)))

        }
        inflater.recyclerView.itemAnimator = null
        mScoreAdapter?.onChangeCallBack = { i: Int,sF: Float, score: String? ->
            //进行局部刷新
            mScoreAdapter?.data?.get(i)?.score = score
            mScoreAdapter?.notifyItemChanged(i,score)




            isAllChange = true
            mScoreAdapter?.data?.forEach {
                if (it.score.isNullOrEmpty() || it.score.equals("0.0")) {
                    isAllChange = false
                    return@forEach
                }
            }
            if (isAllChange == true) {//可以点击完成
                inflater.commonTitle.tv_right?.isEnabled = true
                inflater.commonTitle.tv_right?.setTextColor(resources.getColor(R.color.color_007E47))
            } else {// 不能点击完成
                inflater.commonTitle.tv_right?.isEnabled = false
                inflater.commonTitle.tv_right?.setTextColor(resources.getColor(R.color.color_B6))

            }
        }

    }

    private fun initDataListener() {
        mViewModel.appraiseList.observe(this, Observer {
            mScoreAdapter?.setNewInstance(it)

        })
        mViewModel.postCourseScoreResult.observe(this, Observer {
            toast(it.msg)
            if (it.code == 200) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        })
    }
}