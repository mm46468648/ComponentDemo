package com.mooc.discover.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.common.ktextends.visiable
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
import com.mooc.discover.adapter.TaskStudyListAdapter
import com.mooc.discover.databinding.ActivitySelectStudyListBinding
import com.mooc.discover.model.TaskBindStudyListBean
import com.mooc.resource.utils.StatusBarUtil
//import kotlinx.android.synthetic.main.activity_select_study_list.*

/**
 *任务绑定学习清单
 * @author limeng
 * @date 2/24/22
 */
@Route(path = Paths.PAGE_SELECT_STUDY_LIST_ACTIVITY)
class SelectStudyListActivity : BaseActivity() {
    var studyListData: ArrayList<TaskBindStudyListBean>? = null
    var searchData = ArrayList<TaskBindStudyListBean>()
    var studyListAdapter: TaskStudyListAdapter? = null
    private lateinit var inflater: ActivitySelectStudyListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = ActivitySelectStudyListBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        initView()
        initData()
        initListener()
    }

    private fun initView() {
        inflater.clRoot.post {   //沉浸式状态栏高度
            inflater.clRoot.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0)
        }
        studyListData = intent.getParcelableArrayListExtra<TaskBindStudyListBean>(TaskDetailsActivity.STUDYLIST)
        val name = intent.getStringExtra(TaskDetailsActivity.STUDYLISTNAME)
        if (!name.isNullOrEmpty()) {
            inflater.etSearch.setText(name)
            studyListData?.forEach {
                if (it.source_name.contains(name)) {
                    searchData.add(it)
                }

            }
        }

    }

    private fun initData() {
        inflater.recyclerView.layoutManager = LinearLayoutManager(this)
        if (searchData.size > 0) {
            studyListAdapter = TaskStudyListAdapter(searchData)

        } else {
            studyListAdapter = TaskStudyListAdapter(studyListData)

        }
        inflater.recyclerView.adapter = studyListAdapter
        inflater.recyclerView.visiable(true)

    }

    private fun initListener() {
        inflater.etSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (!inflater.etSearch.text.toString().trim().isEmpty()) {
                    intent.putExtra(TaskDetailsActivity.STUDYLISTNAME, inflater.etSearch.text.toString().trim())
                    setResult(RESULT_OK, intent)
                }
                finish()
                return false
            }

        })
        studyListAdapter?.setOnItemClickListener { adapter, view, position ->
            val bean: TaskBindStudyListBean? = studyListAdapter?.data?.get(position)
            intent.putExtra(TaskDetailsActivity.STUDYLISTID, bean?.source_id)
            intent.putExtra(TaskDetailsActivity.STUDYLISTNAME, bean?.source_name)
            setResult(RESULT_OK, intent)
            finish()
        }
        inflater.ivEtDelete.setOnClickListener {
            inflater.etSearch.setText("")
        }
        inflater.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (s.toString().isEmpty()) {
                    inflater.ivEtDelete.visiable(false)
                } else {
                    inflater.ivEtDelete.visiable(true)
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    inflater.ivEtDelete.visiable(false)
                } else {
                    inflater.ivEtDelete.visiable(true)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    inflater.ivEtDelete.visiable(false)
                    studyListAdapter?.setNewInstance(studyListData)
                    studyListAdapter?.notifyDataSetChanged()
                } else {
                    searchData.clear()
                    studyListData?.forEach {
                        if (it.source_name.contains(s.toString())) {
                            searchData.add(it)
                        }

                    }
                    studyListAdapter?.setNewInstance(searchData)
//                    studyListAdapter?.notifyDataSetChanged()
                    inflater.ivEtDelete.visiable(true)
                }


            }
        })
        inflater.ivBack.setOnClickListener {
            finish()
        }
        //携带数据返回
        inflater.tvOk.setOnClickListener {
            //  数据
            if (!inflater.etSearch.text.toString().trim().isEmpty()) {
                var souId: String? = null
                studyListData?.forEach {
                    if (inflater.etSearch.text.toString().trim().equals(it.source_name)) {//存在这个学习清单
                        souId = it.source_id
                    }
                }
                if (souId != null) {
                    intent.putExtra(TaskDetailsActivity.STUDYLISTID, souId)
                }
                intent.putExtra(TaskDetailsActivity.STUDYLISTNAME, inflater.etSearch.text.toString().trim())
                setResult(RESULT_OK, intent)
            }
            finish()

        }
    }
}