package com.mooc.setting.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.dialog.CustomProgressDialog
import com.mooc.commonbusiness.route.Paths
import com.mooc.resource.widget.EmptyView
import com.mooc.setting.R
import com.mooc.setting.adapter.SettingMsgAdapter
import com.mooc.setting.databinding.ActivitySettingMsgBinding
import com.mooc.setting.model.SettingMsgBean
import com.mooc.setting.viewmodel.MsgViewModel
//import kotlinx.android.synthetic.main.activity_setting_msg.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

/**
 *消息提醒页面
 * @author limeng
 * @date 2021/2/18
 */
@Route(path = Paths.SERVICE_SETTING_MSG)
class SettingMsgActivity : BaseActivity() {
    var settingMsgAdapter: SettingMsgAdapter? = null
    var corseData : SettingMsgBean?=null;
//    val mViewModel: MsgViewModel by lazy {
//        ViewModelProviders.of(this)[MsgViewModel::class.java]
//    }
    val mViewModel: MsgViewModel by viewModels()
    private lateinit var inflater : ActivitySettingMsgBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflater = ActivitySettingMsgBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        initView()
        initListerner()
        initDataListener()
        getData()
    }

    private fun initView() {

        settingMsgAdapter=SettingMsgAdapter(null)
        inflater.recyclerView.layoutManager=LinearLayoutManager(this)
        inflater.recyclerView.adapter=settingMsgAdapter


    }

    private fun getData() {
        mViewModel.getSettingData()
    }
    var changeBean : SettingMsgBean? =null
    private fun initListerner() {
        inflater.swipeSettingMsg.setOnRefreshListener {
            getData()
        }
        inflater.middleTitle.setOnLeftClickListener { finish() }
        settingMsgAdapter?.addChildClickViewIds(R.id.sw_setting_msg)
        settingMsgAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val bean=settingMsgAdapter?.data?.get(position)
            if (bean != null) {
                when (bean.statusItem) {
                    "1" -> {//点了公告的按钮
                        postSettingInteraction(bean)
                    }
                    "2" -> {//点击了课程的按钮
                        postSettingCourse(bean)
                    }
                }
            }

        }

    }

    /**
     * 设置课程开关
     */
    private fun postSettingCourse(data: SettingMsgBean) {
         corseData = data;
        if (data.courseId != null) {
            val postMap = hashMapOf<String, String>(
                    "course_id" to data.courseId!!)
            val toJson = Gson().toJson(postMap)
            val stringBody = toJson.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
            mViewModel.postSettingCourseMsg(stringBody)

        }


    }

    private fun initDataListener() {
        val createLoadingDialog = CustomProgressDialog.createLoadingDialog(this, true)
        createLoadingDialog.show()
        mViewModel.resultList.observe(this, Observer {
            inflater.swipeSettingMsg.isRefreshing=false
            if(it.isEmpty()){
                settingMsgAdapter?.setEmptyView(com.mooc.resource.widget.EmptyView(this))
            }
            createLoadingDialog.dismiss()
            settingMsgAdapter?.setNewInstance(it)

        })
        mViewModel.noticeResult.observe(this, Observer {
            if (it.status == 202) {
                if (changeBean?.status != null) {
                    changeBean?.status = !changeBean?.status!!
                }
                settingMsgAdapter?.notifyDataSetChanged()
            }
        })
        // 课程公告的 开关结果
        mViewModel.courseResult.observe(this, Observer {
            corseData?.notice_status=it.notice_status
            settingMsgAdapter?.notifyDataSetChanged()
        })
    }

    /**
     * 设置开关通知
     */
    private fun postSettingInteraction(data: SettingMsgBean) {
        changeBean=data

        var isAllow=false
        if (data.status != null) {
             isAllow = !data.status!!

        }
        val postMap = hashMapOf<String, String>(
                "allow_interaction" to isAllow.toString())
        val toJson = Gson().toJson(postMap)
        val stringBody = toJson.toString().toRequestBody("application/json;charset=utf-8".toMediaType())

        mViewModel.postSettingInteractionMsg(stringBody)

    }

}