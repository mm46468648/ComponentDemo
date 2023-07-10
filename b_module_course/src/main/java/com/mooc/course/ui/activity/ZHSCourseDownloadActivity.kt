package com.mooc.course.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.ktextends.put
import com.mooc.common.ktextends.setDrawLeft
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.base.PermissionApplyActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.DownloadUtils
import com.mooc.course.R
import com.mooc.course.databinding.CourseActivityZhsDownloadBinding
import com.mooc.course.ui.fragment.ZHSDownloadFragment
import com.mooc.course.viewmodel.ZHSDownloadActivityViewModel
//import kotlinx.android.synthetic.main.course_activity_zhs_download.*

/**
 * 智慧树课程下载
 */
@Route(path = Paths.PAGE_COURSE_ZHS_DOWNLOAD)
class ZHSCourseDownloadActivity : BaseActivity() {


    val courseTitle by extraDelegate(IntentParamsConstants.COURSE_PARAMS_TITLE, "")
    val courseId by extraDelegate(IntentParamsConstants.COURSE_PARAMS_ID, "")
    val courseBean: CourseBean? by extraDelegate(IntentParamsConstants.COURSE_PARAMS_DATA, null)

    val mViewModel: ZHSDownloadActivityViewModel by lazy {
//        ViewModelProviders.of(this)[ZHSDownloadActivityViewModel::class.java]
//        ViewModelProviders.of(this, BaseViewModelFactory(courseId))[ZHSDownloadActivityViewModel::class.java]

        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ZHSDownloadActivityViewModel(courseId) as T
            }
        }).get(ZHSDownloadActivityViewModel::class.java)

    }

    private lateinit var inflater: CourseActivityZhsDownloadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = CourseActivityZhsDownloadBinding.inflate(layoutInflater)
        setContentView(inflater.root)


        initView()

        //设置fragment
        val instence = ZHSDownloadFragment.getInstence(Bundle()
                .put(IntentParamsConstants.COURSE_PARAMS_ID, courseId)
                .put(IntentParamsConstants.COURSE_PARAMS_TITLE, courseTitle)
                .put(IntentParamsConstants.COURSE_PARAMS_DATA, courseBean))
        supportFragmentManager.beginTransaction().replace(R.id.flZhsDownload, instence).commit()
    }

    /**
     * 初始化View
     */
    private fun initView() {
        inflater.commonTitle.setOnLeftClickListener { finish() }
        inflater.commonTitle.middle_text = courseTitle
        inflater.commonTitle.setOnRightTextClickListener(View.OnClickListener {
            clickEditMode()
        })

        //点击全选
        inflater.llSelectAll.setOnClickListener {
            mViewModel.isAllSelect.value = !(mViewModel.isAllSelect.value ?: false)
            inflater.cbSelectAll.isChecked = (mViewModel.isAllSelect.value ?: false)

        }
        //点击删除
        inflater.btDelete.setOnClickListener {
//            toast("删除选中布局")
            mViewModel.deleteSelectDownload.value = true
            clickEditMode()
        }

//        showAllStartStr()
        changeStateFromFragment(false)
        //点击全部开始
        inflater.tvStartAll.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PermissionApplyActivity.REQUEST_CODE_DEFAULT
                )
            }else{
                if (DownloadUtils.checkNetStateCanDownload(this)){
                    val text = inflater.tvStartAll.text
                    if(text == "全部开始"){
                        mViewModel.allStartDownload.value = true
                        changeStateFromFragment(true)
                    }else{
                        mViewModel.allStartDownload.value = false
                        changeStateFromFragment(false)
                    }
//                    mViewModel.allStartDownload.value = !(mViewModel.allStartDownload.value ?: false)
//                    changeStateFromFragment(mViewModel.allStartDownload.value?:false)
                }
            }

        }

        mViewModel.downloadStatsFromFragment.observe(this, Observer {
            changeStateFromFragment(it)
        })
    }


    private fun clickEditMode() {
        mViewModel.editMode.value = !(mViewModel.editMode.value ?: false)
        if (mViewModel.editMode.value == true) {
            //编辑模式
            inflater.commonTitle.right_text = "完成"
        } else {
            inflater.commonTitle.right_text = "编辑"
        }

        showBottomEditView(mViewModel.editMode.value ?: false)
    }

    /**
     * 展示底部编辑布局
     */
    fun showBottomEditView(editMode: Boolean) {
        if (editMode) {
            inflater.tvStartAll.visibility = View.GONE
            inflater.llSelectAll.visibility = View.VISIBLE
            inflater.btDelete.visibility = View.VISIBLE
        } else {
            inflater.tvStartAll.visibility = View.VISIBLE
            inflater.llSelectAll.visibility = View.GONE
            inflater.btDelete.visibility = View.GONE
        }
    }

    /**
     * @param downloading 正在下载中
     */
    fun changeStateFromFragment(downloading: Boolean){
        val str = if (downloading) "全部暂停" else "全部开始"
        val drawLeft = if (downloading) R.mipmap.course_ic_download_all_pause else R.mipmap.course_ic_all_download_start
        inflater.tvStartAll.text = str
        inflater.tvStartAll.setDrawLeft(drawLeft)


    }

}