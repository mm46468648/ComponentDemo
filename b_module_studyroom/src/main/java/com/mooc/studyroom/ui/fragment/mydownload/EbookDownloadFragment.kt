package com.mooc.studyroom.ui.fragment.mydownload

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.base.BaseListFragment2
import com.mooc.commonbusiness.manager.ebook.ReadHistoryManager
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.EbookService
import com.mooc.studyroom.R
import com.mooc.studyroom.ui.adapter.mydownload.DownloadEbookAdapter
import com.mooc.studyroom.viewmodel.EbookDownloadViewModel
import com.mooc.studyroom.viewmodel.MyDownloadActivityViewModel

class EbookDownloadFragment : BaseListFragment2<EBookBean, EbookDownloadViewModel>() {

    val mActivityViewModel : MyDownloadActivityViewModel by lazy {
        ViewModelProviders.of(requireActivity())[MyDownloadActivityViewModel::class.java]
    }

    override fun initAdapter(): BaseQuickAdapter<EBookBean, BaseViewHolder>? {
        return mViewModel?.getPageData()?.value?.let {
            val downloadEbookAdapter = DownloadEbookAdapter(it)
            downloadEbookAdapter.setOnItemClickListener { adapter, view, position ->
                val ebook = adapter.data[position] as EBookBean
                val navigation = ARouter.getInstance().build(Paths.SERVICE_EBOOK).navigation() as EbookService


                navigation.turnToZYReader(requireContext(),ebook.ebook_id)
            }
            downloadEbookAdapter.addChildClickViewIds(R.id.btnDelete)
            downloadEbookAdapter.setOnItemChildClickListener { adapter, view, position ->
                if(view.id == R.id.btnDelete){
                    //删除浏览记录
                    it.get(position).let {book->
                        ReadHistoryManager.delete(book)
                        downloadEbookAdapter.remove(book)

                        //如果adapter空需要显示空状态
                        if(adapter.data.isEmpty()){
                            loadDataWithRrefresh()
                        }
                    }
                }
            }

            //监听编辑模式
            mActivityViewModel.editMode.observe(this, Observer {
                downloadEbookAdapter.editMode = it
            })
            downloadEbookAdapter
        }
    }


    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(requireContext(),3)
    }

    override fun initEmptyView() {
        emptyView.setTitle("你还没有完成下载的电子书")
//        emptyView.setEmptyIcon(R.mipmap.studyroom_ic_download_empty)
        emptyView.setGravityTop(40.dp2px())
    }

}