package com.mooc.studyroom.ui.fragment.mydownload

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.common.utils.FileUtils
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.base.BaseListFragment2
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.model.audio.AlbumListResponse
import com.mooc.commonbusiness.model.db.AlbumDB
import com.mooc.commonbusiness.model.search.AlbumBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.AudioDownloadService
import com.mooc.studyroom.R
import com.mooc.studyroom.ui.activity.download.DownloadAlbumDetailActivity
import com.mooc.studyroom.ui.adapter.mydownload.DownloadAlbumAdapter
import com.mooc.studyroom.viewmodel.AlbumDownloadViewModel
import com.mooc.studyroom.viewmodel.MyDownloadActivityViewModel
import java.io.File

class AlbumDownloadFragment : BaseListFragment2<AlbumDB, AlbumDownloadViewModel>() {

//    val mActivityViewModel : MyDownloadActivityViewModel by lazy {
//        ViewModelProviders.of(requireActivity())[MyDownloadActivityViewModel::class.java]
//    }

    val mActivityViewModel : MyDownloadActivityViewModel by viewModels(ownerProducer={ requireActivity()})



    override fun loadDataWithRrefresh() {

    }

    /**
     * 获取焦点更新下数据，有可能
     * 下载数量数据变化
     */
    override fun onResume() {
        super.onResume()

        mViewModel?.initData()
    }
    override fun initAdapter(): BaseQuickAdapter<AlbumDB, BaseViewHolder>? {
        return mViewModel?.getPageData()?.value.let {
            val downloadAlbumdapter = initMAdapter(it)
            downloadAlbumdapter
        }
    }

    private fun initMAdapter(it: ArrayList<AlbumDB>?): DownloadAlbumAdapter {
        val downloadAlbumdapter = DownloadAlbumAdapter(it)
        downloadAlbumdapter.setOnItemClickListener { adapter, view, position ->
            //点击进入音频详情
            val albumBean = it?.get(position)
            val convert =
                GsonManager.getInstance().convert(albumBean?.data, AlbumListResponse::class.java)
            enterDownloadAlbumDetail(convert)
        }
        downloadAlbumdapter.addChildClickViewIds(R.id.btnDelete)
        downloadAlbumdapter.setOnItemChildClickListener { adapter, view, position ->
            if(view.id == R.id.btnDelete){

                it?.get(position)?.let { albumBean ->
                    deleteAllAlbumFile(albumBean)
                    adapter.remove(albumBean)
                }

                //如果adapter空需要显示空状态
                if(adapter.data.isEmpty()){
                    mViewModel?.initData()
                }

            }
        }

        //监听编辑模式
        mActivityViewModel.editMode.observe(this, Observer {
            downloadAlbumdapter.editMode = it
        })
        return downloadAlbumdapter
    }

    /**
     * 删除所有该专辑下的文件
     * 删除缓存数据库的数据
     * 删除所有缓存音频数据库数据
     * 删除专辑数据库数据
     */
    private fun deleteAllAlbumFile(albumBean: AlbumDB) {
        val convert = GsonManager.getInstance().convert(albumBean.data, AlbumBean::class.java)
        //删除文件夹
        val albumFiles = File(DownloadConfig.audioLocation,convert.id)
        FileUtils.deleteByParentPath(albumFiles)

        //删除音频数据库
        val navigation = ARouter.getInstance().navigation(AudioDownloadService::class.java)
        navigation.deleteAlbum(albumBean)


    }

    private fun enterDownloadAlbumDetail(albumBean: AlbumListResponse?) {
        ARouter.getInstance().build(Paths.PAGE_DOWNLOAD_ALBUM_DETAIL)
                .withString(IntentParamsConstants.ALBUM_PARAMS_ID,albumBean?.id)
                .withString(DownloadAlbumDetailActivity.PARAMS_ALBUM_TITLE,albumBean?.album_title)
                .navigation()
    }


    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(requireContext(),3)
    }

    override fun initEmptyView() {
        emptyView.setTitle("你还没有完成下载的音频课")
//        emptyView.setEmptyIcon(R.mipmap.studyroom_ic_download_empty)
        emptyView.setGravityTop(40.dp2px())
    }
}