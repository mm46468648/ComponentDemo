package com.mooc.studyroom.ui.pop

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lxj.xpopup.XPopup
import com.mooc.commonbusiness.pop.studyroom.CreateStudyListPop
import com.mooc.common.ktextends.setDrawLeft
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.model.eventbus.RefreshStudyRoomEvent
import com.mooc.commonbusiness.model.studyroom.FolderItem
import com.mooc.resource.ktextention.dp2px
import com.mooc.studyroom.R
import com.mooc.studyroom.databinding.StudyroomDialogMovetoStudyzoneBinding
import com.mooc.studyroom.ui.adapter.MoveToStudyZonePopAdapter
import com.mooc.studyroom.viewmodel.StudyListMoveViewModel
//import kotlinx.android.synthetic.main.studyroom_dialog_moveto_studyzone.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

/**
 * 添加到学习室
 */
class AddToStudyZoneBottomDialog : BottomSheetDialogFragment() {
    //学习清单添加移动公共ViewModel
//    val mViewModel: StudyListMoveViewModel by lazy {
//        ViewModelProviders.of(this)[StudyListMoveViewModel::class.java]
//    }
    val mViewModel: StudyListMoveViewModel by viewModels()
    var currentFolderId = ""    //当前文件夹id
    var mAdapter = MoveToStudyZonePopAdapter(null)
    private val creatStudyListStr = "新建学习清单"

    var callback: ((success: Boolean) -> Unit)? = null
    var resourceJson: JSONObject? = null

    lateinit var binding: StudyroomDialogMovetoStudyzoneBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = StudyroomDialogMovetoStudyzoneBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mViewModel.getListLiveData().observe(this, androidx.lifecycle.Observer {

            //将公开的学习清单隐藏
            val filter = it.filterNot { folderItem ->
                folderItem.is_show
            }
            mViewModel.recordPageData(currentFolderId, filter as ArrayList<FolderItem>)
            changeHeadTextView(currentFolderId)
            mAdapter.setList(filter)

//            mViewModel.recordPageData(currentFolderId, it)
//            changeHeadTextView(currentFolderId)
//            mAdapter.setList(it)
        })

        initAdapter()

        changeHeadTextView(currentFolderId)
        //请求学习室列表并展示
        mViewModel.getRootFolder()
    }

    val mHeadView: TextView by lazy {
        createHeadView()
    }

    /**
     * 创建头部布局
     */
    fun createHeadView(): TextView {
        val textView = TextView(requireContext())
        textView.text = creatStudyListStr
        textView.gravity = Gravity.CENTER_VERTICAL
        textView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 55.dp2px())
        textView.setPadding(15.dp2px(), 0, 15.dp2px(), 0)
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_3))
        textView.compoundDrawablePadding = 10.dp2px()
        textView.setDrawLeft(R.mipmap.common_ic_title_right_add)
        textView.setBackgroundResource(R.color.color_F1F1F1)
        return textView
    }

    /**
     * 初始化Adapter
     */
    private fun initAdapter() {
        //给adapter添加头部布局
        mAdapter.addHeaderView(mHeadView)
        mAdapter.setOnItemClickListener { adapter, _, position ->
//            val get = mViewModel.getListLiveData().value?.get(position)
            val get = (adapter.data as ArrayList<FolderItem>).get(position)
            //点击加入到学习室
            resourceJson?.let { json ->
                mViewModel.addToFolder(get.id, json).observe(this, Observer {

                    //成功失败，都弹出提示
                    toast(it.msg)
                    //添加成功，通知学习室刷新
                    val resourceType = json.getInt("resource_type")
                    EventBus.getDefault().post(RefreshStudyRoomEvent(resourceType))
                    //关闭页面
                    dismiss()
                    //回调成功失败
                    callback?.invoke(it.success)

                })
            }
        }

        //点击下一级
        mAdapter.addChildClickViewIds(R.id.tvNextFolder)
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.tvNextFolder) {
//                val get = mViewModel.getListLiveData().value?.get(position)
                val get = (adapter.data as ArrayList<FolderItem>).get(position)
                //点击获取子文件夹数据
                get.id.let {
                    currentFolderId = it
                    mViewModel.getChildFolder(it)
                }
            }
        }
        //设置到rv
        binding.rvStudyZoneList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudyZoneList.adapter = mAdapter
    }


    /**
     * 改变headTextView的文案与点击事件
     */
    private fun changeHeadTextView(folderId: String) {
        if (folderId.isNotEmpty()) {  //不为空代表子文件夹
            mHeadView.text = "返回"
            mHeadView.setDrawLeft(null)
            mHeadView.setOnClickListener {
                try {
                    currentFolderId = mViewModel.backToPrePage(currentFolderId)
                } catch (e: Exception) {
                    dismiss()
                }
            }
        } else {
            mHeadView.text = creatStudyListStr
            mHeadView.setDrawLeft(R.mipmap.common_ic_title_right_add)
            mHeadView.setOnClickListener {
                showCreateFolderPop()
            }
        }

    }

    /**
     * 创建根目录中的学习清单弹窗
     */
    private fun showCreateFolderPop() {
        //点击添加学习清单文件夹
        val createStudyListPop = CreateStudyListPop(requireContext())
        createStudyListPop.onConfirmCallBack = {
            //发送创建文件夹请求
            mViewModel.createNewStudyFolder(it, currentFolderId,pop = createStudyListPop)
        }
        XPopup.Builder(requireContext())
                .autoOpenSoftInput(retainInstance)
                .asCustom(createStudyListPop)
                .show()
    }

}