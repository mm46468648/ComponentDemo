package com.mooc.course.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.course.R
import com.mooc.course.viewmodel.NewXtCourseDownloadActivityViewModel
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.ktextends.put
import com.mooc.common.ktextends.setDrawLeft
import com.mooc.common.utils.FileMgr
import com.mooc.common.utils.SDUtils
import com.mooc.commonbusiness.base.PermissionApplyActivity
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.DownloadUtils
import com.mooc.course.databinding.CourseActivityXtDownloadBinding
import com.mooc.course.ui.fragment.XtCourseDownloadNewFragment
//import kotlinx.android.synthetic.main.course_activity_xt_download.*
import java.io.File

@Route(path = Paths.PAGE_COURSE_NEWXT_DOWNLOAD)
class NewXtCourseDownloadActivity : BaseActivity() {


    val classRoomId by extraDelegate(IntentParamsConstants.COURSE_PARAMS_CLASSROOM_ID, "")
    val courseBean by extraDelegate<CourseBean?>(IntentParamsConstants.COURSE_PARAMS_DATA, null)
//    val mViewModel by lazy {
//        ViewModelProviders.of(this)[NewXtCourseDownloadActivityViewModel::class.java]
//    }
    val mViewModel by viewModels<NewXtCourseDownloadActivityViewModel>()
    private lateinit var inflater: CourseActivityXtDownloadBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = CourseActivityXtDownloadBinding.inflate(layoutInflater)
//        mViewModel.classRoomId = classRoomId
        setContentView(inflater.root)

        initView()

        //添加fragment
//        val xtCourseDownloadFragment = XtCourseDownloadFragment()
        val xtCourseDownloadFragment = XtCourseDownloadNewFragment()
        xtCourseDownloadFragment.arguments = Bundle()
            .put(IntentParamsConstants.COURSE_PARAMS_CLASSROOM_ID, classRoomId)
            .put(IntentParamsConstants.COURSE_PARAMS_DATA, courseBean)

        supportFragmentManager.beginTransaction()
            .replace(R.id.flContainer, xtCourseDownloadFragment).commit()

    }

    private fun initView() {

        inflater.commonTitle.setOnLeftClickListener { finish() }

//        showAllStartStr()

        mViewModel.changeAllTvShowState.observe(this, Observer {
            val str = if (it) "全部暂停" else "全部开始"
            val drawLeft =
                if (it) R.mipmap.course_ic_download_all_pause else R.mipmap.course_ic_all_download_start
            inflater.tvAllStart.text = str
            inflater.tvAllStart.setDrawLeft(drawLeft)
        })

        //点击全部开始
        inflater.tvAllStart.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PermissionApplyActivity.REQUEST_CODE_DEFAULT
                )
            }else{
                if (DownloadUtils.checkNetStateCanDownload(this)){
                    mViewModel.allStartDownload.value = !(mViewModel.allStartDownload.value ?: false)
                    showAllStartStr(mViewModel.allStartDownload.value?:false)
                }
            }
        }
        getStorageMessage()

//        mViewModel.changeAllTvShowState.observe(this, Observer {
//            if(it){
//                showTvAllStart()
//            }else{
//                showTvAllPase()
//            }
//        })
    }


    /**
     * @param start 是否已经开始
     */
    fun showAllStartStr(start: Boolean) {

        val str = if (start) "全部暂停" else "全部开始"
        val drawLeft =
            if (start) R.mipmap.course_ic_download_all_pause else R.mipmap.course_ic_all_download_start
        inflater.tvAllStart.text = str
        inflater.tvAllStart.setDrawLeft(drawLeft)
    }

    /**
     * 获取存储信息，已占用，可用空间
     */
    private fun getStorageMessage() {

        //获取当前手机剩余空间
        val bytes: LongArray = SDUtils.getDirSize(getExternalFilesDir(null)?.absolutePath ?: "")
        val freeValue = bytes[0]
        //获取可用空间
        val avilableSize = FileMgr.FormetFileSize(freeValue)

        val fileSize = FileMgr.getFileSize(File(DownloadConfig.courseLocation))

        //获取下载的已用空间
        val allreadyUser = FileMgr.FormetFileSize(fileSize)
        inflater.tvStorageMessage.text = "已用$allreadyUser,可用空间$avilableSize"
    }
}