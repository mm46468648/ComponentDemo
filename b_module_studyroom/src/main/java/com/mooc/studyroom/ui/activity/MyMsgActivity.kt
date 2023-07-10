package com.mooc.studyroom.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.ktextends.toast
import com.mooc.common.ktextends.visiable
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.dialog.CustomProgressDialog
import com.mooc.commonbusiness.route.Paths
import com.mooc.resource.widget.SimpleTabLayout
import com.mooc.studyroom.databinding.StudyroomActivityMyMsgBinding
import com.mooc.studyroom.model.MyMsgBean
import com.mooc.studyroom.ui.adapter.MyMsgPageAdapter
import com.mooc.studyroom.viewmodel.MyMsgViewModel

/**
 *我的消息页面
 * @author limeng
 * @date 2021/3/4
 */
@Route(path = Paths.PAGE_MY_MSG)
class MyMsgActivity : BaseActivity() {

    var createLoadingDialog: CustomProgressDialog? = null
    val viewModel: MyMsgViewModel by viewModels()
    var titleList = ArrayList<String?>()

    private lateinit var inflater: StudyroomActivityMyMsgBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = StudyroomActivityMyMsgBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        initView()
        initData()
        initListener()
    }

    private fun initListener() {
        inflater.titleLayout.tv_right?.setOnClickListener {
            ARouter.getInstance().build(Paths.SERVICE_SETTING_MSG).navigation()

        }
        inflater.titleLayout.setOnLeftClickListener { finish() }
        viewModel.myMsgBean.observe(this, Observer {
            if (createLoadingDialog != null) {
                createLoadingDialog?.dismiss()
            }
            setupViewPager(inflater.viewPager, it)
        })
        viewModel.error.observe(this, Observer {
            if (createLoadingDialog != null) {
                createLoadingDialog?.dismiss()
            }
            toast(it)
        })
    }

    private fun initView() {
    }

    private fun initData() {
        createLoadingDialog = CustomProgressDialog.createLoadingDialog(this, true)
        createLoadingDialog?.show()
        viewModel.getMyMsgData()

    }

    private fun setupViewPager(viewPager: ViewPager2, list: ArrayList<MyMsgBean>) {

        if (list.isEmpty()) {
//            inflater.emptyView.root.visiable(true)
            inflater.emptyView.visiable(true)
            inflater.emptyView.setButton("",null)
            inflater.tabLayout.visiable(false)
            viewPager.visiable(false)

            return
        }

        titleList.clear()
        list.forEach {
            titleList.add(it.title)
        }
//        tabLayout.setMsgList(list)
        inflater.tabLayout.setRedPointVisiablity(object : com.mooc.resource.widget.SimpleTabLayout.RedPointVisiablity {
            override fun show(position: Int): Boolean {
                return position != 0 && list.get(position).unread_num > 0
            }

            override fun title(position: Int): String {
                return titleList.get(position) ?: ""
            }


        })
//        tabLayout.setReadNotice(true)
        inflater.tabLayout.setTabStrs(titleList.toTypedArray())
        viewPager.adapter = MyMsgPageAdapter(list, this)
        inflater.tabLayout.setViewPager2(viewPager)

    }
}