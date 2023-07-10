package com.mooc.column.ui.allColumnSubscribe

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.discover.R
import com.mooc.discover.allColumnSubscribe.AllColumnSubscribeVM
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.allColumnSubscribe.AllColumnSubscribeAdapter
import com.mooc.discover.databinding.ActivityAllcolumnSubscribeBinding
//import kotlinx.android.synthetic.main.activity_allcolumn_subscribe.*


/**
 * 全部栏目订阅页面
 * 这里面订阅的栏目，可在今日学习中的订阅，专题查看
 */
@Route(path = Paths.PAGE_COLUMN_SUBSCRIBE_ALL)
class AllColumnSubscribeActivity : BaseActivity() {


//    val mViewModel : AllColumnSubscribeVM by lazy {
//        ViewModelProviders.of(this)[AllColumnSubscribeVM::class.java]
//    }
    val mViewModel : AllColumnSubscribeVM by viewModels()
    private lateinit var inflater: ActivityAllcolumnSubscribeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflater = ActivityAllcolumnSubscribeBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        initView()
    }

    private fun initView() {
        //设置头部点击x关闭页面
        inflater.commonTitle.setOnRightIconClickListener(View.OnClickListener {
            finish()
        })
        inflater.commonTitle.setIbBackVisibal(View.INVISIBLE)
        //初始化RecyclerView
        inflater.recyclerView.layoutManager = LinearLayoutManager(this)
        val mAdapter = mViewModel.assembleData.value?.let { AllColumnSubscribeAdapter(it) }
        mAdapter?.addChildClickViewIds(R.id.tvEdit)

        //设置点击事件
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.tvEdit) { //设置编辑点击事件
                mViewModel.setDataEditMode()
                val tvEdit = view.findViewById<TextView>(R.id.tvEdit)
                tvEdit.text = if(mViewModel.isEditState) "完成" else "编辑"
                val bgRes = if(mViewModel.isEditState) R.drawable.shape_radius20_color_primary else R.drawable.shape_radius20_stoke1primary_solidwhite
                tvEdit.setBackgroundResource(bgRes)
                tvEdit.setTextColor(if(mViewModel.isEditState) Color.WHITE else ContextCompat.getColor(this,R.color.colorPrimary))
            }
        }
        mAdapter?.onFlowItemClick = { position, subscribeBean -> //设置coulumn点击事件
            mViewModel.moveSubscribeColumn(subscribeBean)
        }
        inflater.recyclerView.adapter = mAdapter

        //监听数据改变，同步更新适配器
        mViewModel.assembleData.observe(this, Observer { mAdapter?.notifyDataSetChanged() })
    }
}