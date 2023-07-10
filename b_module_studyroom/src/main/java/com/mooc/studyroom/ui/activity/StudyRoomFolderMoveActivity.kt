package com.mooc.studyroom.ui.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.pop.studyroom.CreateStudyListPop
import com.lxj.xpopup.XPopup
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfacewrapper.ARouterNavigationCallbackWrapper
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.route.Paths
import com.mooc.common.ktextends.put
import com.mooc.commonbusiness.model.eventbus.RefreshStudyRoomEvent
import com.mooc.commonbusiness.model.eventbus.StudyRoomResourceChange
import com.mooc.commonbusiness.model.studyroom.FolderItem
import com.mooc.studyroom.R
import com.mooc.studyroom.databinding.StudyroomActivityFolderMoveBinding
import com.mooc.studyroom.ui.adapter.StudyListMoveAdatper
import com.mooc.studyroom.viewmodel.StudyListMoveViewModel
//import kotlinx.android.synthetic.main.studyroom_activity_folder_move.*
import org.greenrobot.eventbus.EventBus
import java.lang.Exception

/**
 * 学习室文件夹移动页面
 */
@Route(path = Paths.PAGE_STUDYLIST_MOVE)
class StudyRoomFolderMoveActivity : BaseActivity() {

    //viewmodel 暂时复用了排序页面的viewmodel，后续如果有特殊需求，再抽出来
    //因为有公共的请求学习清单列表逻辑
//    val mViewModel : StudyListMoveViewModel by lazy {
//        ViewModelProviders.of(this)[StudyListMoveViewModel::class.java]
//    }
    val mViewModel : StudyListMoveViewModel by viewModels()

    val studyListMoveAdatper by lazy {
        StudyListMoveAdatper(mViewModel.getListLiveData().value)
    }

    //当前展示的文件夹id,默认根目录为""
    var currentFolderId = ""

    //需要移动的资源id
    val needMoveResourceId by extraDelegate(IntentParamsConstants.PARAMS_RESOURCE_ID,"")
    //需要移动的资源类型
    val needMoveResourceType by extraDelegate(IntentParamsConstants.PARAMS_RESOURCE_TYPE,"")

    private lateinit var inflater: StudyroomActivityFolderMoveBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = StudyroomActivityFolderMoveBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        //点击左上角返回
        inflater.commonTitle.setOnLeftClickListener { onPressBack() }
        //点击右上角新建文件夹
        inflater.commonTitle.setOnRightTextClickListener(View.OnClickListener {
            showCreateFolderPop()
        })
        mViewModel.getListLiveData().observe(this, Observer {

            //将包含自身的文件夹移除,或者是公开的学习清单隐藏
            val filter = it.filterNot{ folderItem->
                folderItem.id == needMoveResourceId || folderItem.is_show
            }
//            mViewModel.recordPageData(currentFolderId,it)
            mViewModel.recordPageData(currentFolderId, filter as ArrayList<FolderItem>)
            studyListMoveAdatper.setList(filter)
        })

        //学习清单子文件夹
        mViewModel.getStudyListDetail().observe(this, Observer {
            val folderitems = it.folder.folder.items ?: return@Observer
            //将包含自身的文件夹移除,或者是公开的学习清单隐藏
            val filter = folderitems.filterNot{ folderItem->
                folderItem.id == needMoveResourceId || folderItem.is_show
            }
//            mViewModel.recordPageData(currentFolderId,it)
            mViewModel.recordPageData(currentFolderId, filter as ArrayList<FolderItem>)
            studyListMoveAdatper.setList(filter)
        })

        initAdapter()

        mViewModel.getRootFolder()

    }

    private fun showCreateFolderPop() {
        //点击添加学习清单文件夹
        val createStudyListPop = CreateStudyListPop(this)
        createStudyListPop.onConfirmCallBack = {
            //发送创建文件夹请求
            mViewModel.createNewStudyFolder(it,currentFolderId,pop = createStudyListPop)
        }
        XPopup.Builder(this)
                .autoOpenSoftInput(true)
                .asCustom(createStudyListPop)
                .show()
    }

    /**
     * 初始化适配器
     */
    private fun initAdapter() {
        inflater.recyclerView.layoutManager = LinearLayoutManager(this)
        //点击进行资源的移动
        studyListMoveAdatper.setOnItemClickListener { adapter, view, position ->
//            val get = mViewModel.getListLiveData().value?.get(position)
            val get = (adapter.data as ArrayList<FolderItem>).get(position)
            mViewModel.moveToFolder(get.id,needMoveResourceId,needMoveResourceType).observe(this, Observer {
                if(!it.isSuccess){ //失败，弹出失败提示
                    toast(it.msg)
                    return@Observer
                }
                //进行相应的刷新
                if(needMoveResourceType == ResourceTypeConstans.TYPE_STUDY_FOLDER){
                    EventBus.getDefault().post(StudyRoomResourceChange(StudyRoomResourceChange.TYPE_FOLODER))
                }else{
//                    EventBus.getDefault().post(StudyRoomResourceChange(StudyRoomResourceChange.TYPE_RESOURCE,needMoveResourceId))
                    EventBus.getDefault().post(RefreshStudyRoomEvent(needMoveResourceType.toInt()))
                }

                //测试说移动完毕，直接关闭，回到原来的页面
                finish()
                //要直接进入移动到的页面 ,并关闭当前页面
//                ARouter.getInstance().build(Paths.PAGE_STUDYLIST_DETAIL)
//                        .with(Bundle().put(IntentParamsConstants.STUDYROOM_FOLDER_ID, get?.id).put(IntentParamsConstants.STUDYROOM_FOLDER_NAME, get?.name))
//                        .navigation(this, object : ARouterNavigationCallbackWrapper(){
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
            })
        }

        //点击进入下一级子页面
        studyListMoveAdatper.addChildClickViewIds(R.id.tvNextFolder)
        studyListMoveAdatper.setOnItemChildClickListener { adapter, view, position ->
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
        inflater.recyclerView.adapter = studyListMoveAdatper
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            onPressBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 点击返回事件
     */
    private fun onPressBack() {
        try {
            currentFolderId = mViewModel.backToPrePage(currentFolderId)
        }catch (e:Exception){
            finish()
        }
    }

}