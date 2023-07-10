package com.mooc.studyroom.ui.activity.download

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.AudioDownloadService
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.utils.FileUtils
import com.mooc.commonbusiness.model.search.TrackBean
import com.mooc.download.db.DownloadDatabase
import com.mooc.studyroom.R
import com.mooc.studyroom.databinding.DownloadActivityCourseListBinding
import com.mooc.studyroom.ui.adapter.mydownload.DownloadAudioListAdapter
import com.mooc.studyroom.viewmodel.AlbumDownladDetailViewModel
//import kotlinx.android.synthetic.main.download_activity_course_list.*
import java.io.File

@Route(path = Paths.PAGE_DOWNLOAD_ALBUM_DETAIL)
class DownloadAlbumDetailActivity : BaseActivity() {

    companion object {
        const val PARAMS_ALBUM_TITLE = "album_params_title"
    }

    val mViewModel : AlbumDownladDetailViewModel by viewModels()

    val albumId by extraDelegate(IntentParamsConstants.ALBUM_PARAMS_ID, "")
    val albumTitle by extraDelegate(PARAMS_ALBUM_TITLE, "")
    var mAdapter = DownloadAudioListAdapter(arrayListOf())

    private lateinit var inflater : DownloadActivityCourseListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = DownloadActivityCourseListBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        //titie
        initView()
        //adapter
        initAdapter()
    }

    override fun onResume() {
        super.onResume()

        //loaddata
        loadData()
    }

    private fun loadData() {
        mViewModel.getAudioList(albumId)
    }

    private fun initAdapter() {
        inflater.rvCourseChapter.layoutManager = LinearLayoutManager(this)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            setOnItemClick(adapter as DownloadAudioListAdapter, position)
        }
        inflater.rvCourseChapter.adapter = mAdapter
        mViewModel.audioListData.observe(this, Observer {
            mAdapter.setList(it)
        })
    }

    private fun initView() {
        inflater.commonTitle.middle_text = albumTitle
        inflater.commonTitle.setOnLeftClickListener { finish() }
        inflater.commonTitle.setOnRightTextClickListener(View.OnClickListener {
            mAdapter.editMode = !mAdapter.editMode
            val editStr = if(mAdapter.editMode) "完成" else "编辑"
            inflater.commonTitle.right_text = editStr
            //底部显示隐藏
            val bottomVisibal = if(mAdapter.editMode)View.VISIBLE else View.GONE
            inflater.rlBottom.visibility = bottomVisibal
        })

        inflater.cbSelectAll.setOnCheckedChangeListener { compoundButton, b ->
            if(b){ //全部选中
                mAdapter.selectTracks.clear()
                mAdapter.selectTracks.addAll(mAdapter.data)
                mAdapter.notifyDataSetChanged()
            }else{ //全部移除
                mAdapter.selectTracks.clear()
                mAdapter.notifyDataSetChanged()
            }
        }

        inflater.btDelete.setOnClickListener {
            //先删除缓存的文件
            deleteAllSelectFile(mAdapter.selectTracks)
            //删除全部选中的数据，并刷新
            val navigation = ARouter.getInstance().navigation(AudioDownloadService::class.java)
            navigation.deleteTracks(mAdapter.selectTracks.map { it.generateTrackDB() })
            loadData()

        }

        //添加更多章节
        inflater.tvAddMore.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_ALBUM)
                .withString(IntentParamsConstants.ALBUM_PARAMS_ID,albumId).navigation()
        }


    }

    /**
     * 删除缓存的文件，并删除数据库中国的数据
     */
    private fun deleteAllSelectFile(list:List<TrackBean>) {
        list.forEach {
            DownloadDatabase.database?.getDownloadDao()
                ?.getDownloadModle(it.generateDownloadDBId())?.apply {
                    val file = File(this.downloadPath,this.fileName)
                    FileUtils.deleteByParentPath(file)
                    DownloadDatabase.database?.getDownloadDao()?.delete(this)
                }
        }
    }

    private fun setOnItemClick(adapter: DownloadAudioListAdapter, position: Int) {
        val trackBean = adapter.data.get(position)
        if(adapter.editMode){ //如果是编辑模式
            val selectTracks = adapter.selectTracks
            if(selectTracks.contains(trackBean)){
                selectTracks.remove(trackBean)
            }else{
                selectTracks.add(trackBean)
            }
            adapter.notifyItemChanged(position)

            return
        }

        //进入离线音频播放页面
//        toast("点击进入离线音频播放页面")
        ARouter.getInstance().build(Paths.PAGE_AUDIO_PLAY)
                .withString(IntentParamsConstants.AUDIO_PARAMS_ID, trackBean.id)
                .withString(IntentParamsConstants.ALBUM_PARAMS_ID, trackBean.albumId)
                .withBoolean(IntentParamsConstants.AUDIO_PARAMS_OFFLINE, true)
                .navigation()
    }



}