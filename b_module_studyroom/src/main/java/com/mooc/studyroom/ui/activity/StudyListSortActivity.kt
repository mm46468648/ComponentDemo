package com.mooc.studyroom.ui.activity

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.model.studyroom.FolderItem
import com.mooc.commonbusiness.model.eventbus.StudyRoomResourceChange
import com.mooc.studyroom.R
import com.mooc.studyroom.databinding.StudyroomActivityStudylistSortBinding
import com.mooc.studyroom.ui.adapter.StudyListSortAdapter
import com.mooc.studyroom.viewmodel.StudyListSortViewModel
//import kotlinx.android.synthetic.main.studyroom_activity_studylist_sort.*
import org.greenrobot.eventbus.EventBus

/**
 * 学习清单排序页面
 */
@Route(path = Paths.PAGE_STUDYLIST_SORT)
class StudyListSortActivity : BaseActivity() {

//    val mViewModel: StudyListSortViewModel by lazy {
//        ViewModelProviders.of(this)[StudyListSortViewModel::class.java]
//    }
    val mViewModel: StudyListSortViewModel by viewModels()

    val studyListSortAdapter by lazy {
        StudyListSortAdapter(null)
    }

    //获取系统震动服务
    val vib by lazy { getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    lateinit var inflater: StudyroomActivityStudylistSortBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflater = StudyroomActivityStudylistSortBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        initView()

        loadList()
    }

    private fun initView() {
        //左上角返回
        inflater.commonTitle.setOnLeftClickListener { finish() }
        //点击确定发送新的排序
        inflater.tvOk.setOnClickListener {
            mViewModel.postFolderSort().observe(this, Observer {b->
                if (b) {     //上传排序成功
//                    LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_MOVE_FOLDER_ID).postValue(true)
                    EventBus.getDefault().post(StudyRoomResourceChange(StudyRoomResourceChange.TYPE_FOLODER))
                    finish()
                }
            })
        }

        inflater.recyclerView.layoutManager = LinearLayoutManager(this)

        //设置拖拽帮助
        studyListSortAdapter.draggableModule.isDragEnabled = true
        studyListSortAdapter.draggableModule.setOnItemDragListener(object : OnItemDragListener {
            override fun onItemDragMoving(source: RecyclerView.ViewHolder?, from: Int, target: RecyclerView.ViewHolder?, to: Int) {
            }

            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                //改变背景颜色，并震动一下,震动70毫秒
                viewHolder?.itemView?.setBackgroundColor(ContextCompat.getColor(this@StudyListSortActivity, R.color.color_B2F))
                vib.vibrate(70)
            }

            override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                //还原背景颜色
                viewHolder?.itemView?.setBackgroundColor(0)

                mViewModel.getListLiveData().postValue(studyListSortAdapter.data as ArrayList<FolderItem>?)
            }
        })
        inflater.recyclerView.adapter = studyListSortAdapter

    }

    private fun loadList() {
        mViewModel.getListLiveData().observe(this, {
            //过滤掉学习室
            val filter = it.filter {folder->
                folder.id != "0"
            }

            studyListSortAdapter.setNewInstance(filter as ArrayList<FolderItem>)
        })

        mViewModel.getRootFolder()
    }

}