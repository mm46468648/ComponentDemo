package com.mooc.search.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.adapter.CommonAlbumAdapter
import com.mooc.commonbusiness.adapter.CommonCourseAdapter
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.dialog.CustomProgressDialog
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.microknowledge.MicroKnowBean
import com.mooc.commonbusiness.model.search.*
import com.mooc.commonbusiness.model.studyproject.StudyProject
import com.mooc.search.adapter.*
import com.mooc.search.databinding.ActivitySearchListBinding
import com.mooc.search.utils.SearchConstants
import com.mooc.search.viewmodel.SearchViewModel

//import kotlinx.android.synthetic.main.activity_search_list.*

class SearchListActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener,
    OnLoadMoreListener {

    var type: Int = 0;
    var word: String = ""
    var num: String? = null
    var isVague: String? = null
    private var page = 1
    private var limit = 10

    val model: SearchViewModel = SearchViewModel()
    protected var dialog: CustomProgressDialog? = null
    var mSearchResultBean: SearchResultBean? = null;

    var adapter_course: CommonCourseAdapter? = null

    var adapter_album: CommonAlbumAdapter? = null

    var adapter_e_book: SearchBookAdapter? = null

    var adapter_folder: FolderListAdapter? = null;

    var adapter_track: TrackAdapter? = null

    var adapter_artical: ArticleAdapter? = null

    var adapter_periodical: PeriodicalAdapter? = null

    var adapter_publiction: PublicationAdapter? = null

    var adapter_micro: SmallCourseAdapter? = null
    var adapter_baike: BaiKeAdapter? = null
    var adapter_tuling: TulingAdapter? = null
    var adapter_study: StudyProAdapter? = null
    var adapter_micro_know: MicroKnowAdapter? = null

    private lateinit var inflater: ActivitySearchListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflater = ActivitySearchListBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        initView()
        intData()
        initListener()
    }

    private fun initListener() {
        inflater.commonTitleLayout.setOnLeftClickListener {
            finish()
        }

    }

    val searchViewModel: SearchViewModel by viewModels()

    fun initView() {
        dialog = CustomProgressDialog.createLoadingDialog(this, true)
        inflater.recView.layoutManager = LinearLayoutManager(this)
        inflater.swipeRefreshLayout.setOnRefreshListener(this)
    }

    fun intData() {
        searchViewModel.getError().observe(this, Observer {
            if (dialog?.isShowing == true) {
                dialog?.dismiss()
            }
        })
        searchViewModel.searchData.observe(this, Observer<SearchResultBean> {
            if (dialog?.isShowing == true) {
                dialog?.dismiss()
            }
            setData(it)
        })
        type = intent.getIntExtra(SearchConstants.RESCOURSE_TYPE, type)
        word = intent.getStringExtra(SearchConstants.RESCOURSE_WORD) ?: ""
        num = intent.getStringExtra(SearchConstants.RESCOURSE_SEARCH_NUM)
        val numString = "(" + num + ")"
        isVague = intent.getStringExtra(SearchConstants.RESCOURSE_ISVAGUE)
        when (type) {
            ResourceTypeConstans.TYPE_COURSE -> {
                inflater.commonTitleLayout.middle_text = "课程" + numString
                adapter_course = CommonCourseAdapter(null, true)
                onItemClick(adapter_course)
                inflater.recView.adapter = adapter_course
                adapter_course?.loadMoreModule?.setOnLoadMoreListener(this)
            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                inflater.commonTitleLayout.middle_text = "音频课" + numString
                adapter_album = CommonAlbumAdapter(null)
                onItemClick(adapter_album)
                inflater.recView.adapter = adapter_album
                adapter_album?.loadMoreModule?.setOnLoadMoreListener(this)


            }
            ResourceTypeConstans.TYPE_TRACK -> {
                inflater.commonTitleLayout.middle_text = "音频" + numString
                adapter_track = TrackAdapter(null)
                onItemClick(adapter_track)
                inflater.recView.adapter = adapter_track
                adapter_track?.loadMoreModule?.setOnLoadMoreListener(this)


            }
            ResourceTypeConstans.TYPE_E_BOOK -> {

                inflater.commonTitleLayout.middle_text = "电子书" + numString
                adapter_e_book = SearchBookAdapter(null)
                onItemClick(adapter_e_book)
                inflater.recView.adapter = adapter_e_book
                adapter_e_book?.loadMoreModule?.setOnLoadMoreListener(this)


            }
            ResourceTypeConstans.TYPE_MICRO_LESSON -> {
                inflater.commonTitleLayout.middle_text = "微课程" + numString
                adapter_micro = SmallCourseAdapter(null)
                onItemClick(adapter_micro)
                inflater.recView.adapter = adapter_micro
                adapter_micro?.loadMoreModule?.setOnLoadMoreListener(this)

            }
            ResourceTypeConstans.TYPE_ARTICLE -> {
                inflater.commonTitleLayout.middle_text = "文章" + numString
                adapter_artical = ArticleAdapter(null)
                onItemClick(adapter_artical)
                inflater.recView.adapter = adapter_artical
                adapter_artical?.loadMoreModule?.setOnLoadMoreListener(this)

            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
                inflater.commonTitleLayout.middle_text = "期刊" + numString
                adapter_periodical = PeriodicalAdapter(null)
                onItemClick(adapter_periodical)
                inflater.recView.adapter = adapter_periodical
                adapter_periodical?.loadMoreModule?.setOnLoadMoreListener(this)

            }
            ResourceTypeConstans.TYPE_PUBLICATION -> {
                inflater.commonTitleLayout.middle_text = "刊物" + numString
                adapter_publiction = PublicationAdapter(null)
                onItemClick(adapter_publiction)
                inflater.recView.adapter = adapter_publiction
                adapter_publiction?.loadMoreModule?.setOnLoadMoreListener(this)

            }
            ResourceTypeConstans.TYPE_BAIKE -> {
                inflater.commonTitleLayout.middle_text = "百科" + numString
                adapter_baike = BaiKeAdapter(null)
                onItemClick(adapter_baike)
                inflater.recView.adapter = adapter_baike
                adapter_baike?.loadMoreModule?.setOnLoadMoreListener(this)

            }
            ResourceTypeConstans.TYPE_TU_LING -> {
                inflater.commonTitleLayout.middle_text = "知识点" + numString
                adapter_tuling = TulingAdapter(null)
                onItemClick(adapter_tuling)
                inflater.recView.adapter = adapter_tuling
                adapter_tuling?.loadMoreModule?.setOnLoadMoreListener(this)

            }
            ResourceTypeConstans.TYPE_STUDY_PLAN -> {
                inflater.commonTitleLayout.middle_text = "学习项目" + numString
                adapter_study = StudyProAdapter(null)
                onItemClick(adapter_study)
                inflater.recView.adapter = adapter_study
                adapter_study?.loadMoreModule?.setOnLoadMoreListener(this)

            }
            ResourceTypeConstans.TYPE_SOURCE_FOLDER -> {
                inflater.commonTitleLayout.middle_text = "学习清单" + numString
                adapter_folder = FolderListAdapter(null)
                onItemClick(adapter_folder)
                inflater.recView.adapter = adapter_folder
                adapter_folder?.loadMoreModule?.setOnLoadMoreListener(this)

            }
            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> {
                inflater.commonTitleLayout.middle_text = "微知识" + numString
                adapter_micro_know = MicroKnowAdapter(null)
                onItemClick(adapter_micro_know)
                inflater.recView.adapter = adapter_micro_know
                adapter_micro_know?.loadMoreModule?.setOnLoadMoreListener(this)

            }

        }
        if (dialog?.isShowing == false) {
            dialog?.show()
        }
        getData()
    }

    private fun <T, VH : BaseViewHolder> onItemClick(adapter: BaseQuickAdapter<T, VH>?) {
        adapter?.setOnItemClickListener { adapter, view, position ->
            ResourceTurnManager.turnToResourcePage(adapter.data[position] as BaseResourceInterface)
        }
    }

    private fun getData() {

        val map = mapOf(
            "type" to type.toString(),
            "word" to word,
            "search_match_type" to isVague.toString(),
            "page_size" to limit.toString(),
            "page" to page.toString()
        )
        searchViewModel.loadData(map)
    }


    /**
     * 设置数据组
     */
    private fun setData(lunchResponse: SearchResultBean?) {//ToDo  补齐 音频和期刊
        inflater.swipeRefreshLayout.isRefreshing = false
        mSearchResultBean = lunchResponse;
        if (lunchResponse?.micro_course != null) {
            lunchResponse.micro_course?.items?.let { setMicroData(it) }
        }
        if (lunchResponse?.course != null) {
            lunchResponse.course?.items?.let { setCourseData(it) }
        }
        if (lunchResponse?.study_plan != null) {
            lunchResponse.study_plan?.items?.let { setStudyData(it) }
        }
        if (lunchResponse?.album != null) {
            lunchResponse.album?.items?.let { setAlbumData(it) }
        }
        if (lunchResponse?.track != null) {
            lunchResponse.track?.items?.let { setTrackData(it) }
        }
        if (lunchResponse?.article != null) {
            lunchResponse.article?.items?.let { setArticalData(it) }
        }
        if (lunchResponse?.ebook != null) {
            lunchResponse.ebook?.items?.let { setEbookData(it) }
        }
        if (lunchResponse?.baike != null) {
            lunchResponse.baike?.items?.let { setBaikeData(it) }
        }
        if (lunchResponse?.chaoxing != null) {
            lunchResponse.chaoxing?.items?.let { setPeriodicalData(it) }
        }
        if (lunchResponse?.kanwu != null) {
            lunchResponse.kanwu?.items?.let { setPublicationData(it) }
        }
        if (lunchResponse?.tuling != null) {
            lunchResponse.tuling?.items?.let { setTuLingData(it) }
        }
        if (lunchResponse?.folder != null) {
            lunchResponse.folder?.items?.let { setFolderData(it) }
        }
        if (lunchResponse?.micro_knowledge != null) {
            lunchResponse.micro_knowledge?.items?.let { setMicroKnowData(it) }
        }

    }

    /**
     *课程数据
     */
    private fun setCourseData(beans: MutableList<CourseBean>) {
        if (page == 1) {
            if (beans.size < limit) {
                adapter_course?.setNewInstance(beans)
                adapter_course?.loadMoreModule?.loadMoreEnd()
            } else {
                adapter_course?.setNewInstance(beans)
                adapter_course?.loadMoreModule?.loadMoreComplete()

            }
        } else {
            if (beans.size < limit) {
                adapter_course?.addData(beans)
                adapter_course?.loadMoreModule?.loadMoreEnd()

            } else {
                adapter_course?.addData(beans)
                adapter_course?.loadMoreModule?.loadMoreComplete()
            }
        }
    }

    private fun setTrackData(beans: MutableList<TrackBean>) {
        if (page == 1) {
            if (beans.size < limit) {
                adapter_track?.setNewInstance(beans)
                adapter_track?.loadMoreModule?.loadMoreEnd()
            } else {
                adapter_track?.setNewInstance(beans)
                adapter_track?.loadMoreModule?.loadMoreComplete()

            }
        } else {
            if (beans.size < limit) {
                adapter_track?.addData(beans)
                adapter_track?.loadMoreModule?.loadMoreEnd()

            } else {
                adapter_track?.addData(beans)
                adapter_track?.loadMoreModule?.loadMoreComplete()
            }
        }
    }

    /**
     *音频课数据
     */
    private fun setAlbumData(beans: MutableList<AlbumBean>) {
        if (page == 1) {
            if (beans.size < limit) {
                adapter_album?.setNewInstance(beans)
                adapter_album?.loadMoreModule?.loadMoreEnd()
            } else {
                adapter_album?.setNewInstance(beans)
                adapter_album?.loadMoreModule?.loadMoreComplete()

            }
        } else {
            if (beans.size < limit) {
                adapter_album?.addData(beans)
                adapter_album?.loadMoreModule?.loadMoreEnd()

            } else {
                adapter_album?.addData(beans)
                adapter_album?.loadMoreModule?.loadMoreComplete()
            }
        }
    }

    /**
     *电子书数据
     */
    private fun setEbookData(beans: MutableList<EBookBean>) {
        if (page == 1) {
            if (beans.size < limit) {
                adapter_e_book?.setNewInstance(beans)
                adapter_e_book?.loadMoreModule?.loadMoreEnd()
            } else {
                adapter_e_book?.setNewInstance(beans)
                adapter_e_book?.loadMoreModule?.loadMoreComplete()

            }
        } else {
            if (beans.size < limit) {
                adapter_e_book?.addData(beans)
                adapter_e_book?.loadMoreModule?.loadMoreEnd()

            } else {
                adapter_e_book?.addData(beans)
                adapter_e_book?.loadMoreModule?.loadMoreComplete()
            }
        }
    }

    /**
     *微课程数据
     */
    private fun setMicroData(beans: MutableList<MicroBean>) {
        if (page == 1) {
            if (beans.size < limit) {
                adapter_micro?.setNewInstance(beans)
                adapter_micro?.loadMoreModule?.loadMoreEnd()
            } else {
                adapter_micro?.setNewInstance(beans)
                adapter_micro?.loadMoreModule?.loadMoreComplete()

            }
        } else {
            if (beans.size < limit) {
                adapter_micro?.addData(beans)
                adapter_micro?.loadMoreModule?.loadMoreEnd()

            } else {
                adapter_micro?.addData(beans)
                adapter_micro?.loadMoreModule?.loadMoreComplete()
            }
        }
    }

    /**
     *百科数据
     */
    private fun setBaikeData(beans: MutableList<BaiKeBean>) {
        if (page == 1) {
            if (beans.size < limit) {
                adapter_baike?.setNewInstance(beans)
                adapter_baike?.loadMoreModule?.loadMoreEnd()
            } else {
                adapter_baike?.setNewInstance(beans)
                adapter_baike?.loadMoreModule?.loadMoreComplete()

            }
        } else {
            if (beans.size < limit) {
                adapter_baike?.addData(beans)
                adapter_baike?.loadMoreModule?.loadMoreEnd()

            } else {
                adapter_baike?.addData(beans)
                adapter_baike?.loadMoreModule?.loadMoreComplete()
            }
        }
    }

    /**
     *文章数据
     */
    private fun setArticalData(beans: MutableList<ArticleBean>) {
        if (page == 1) {
            if (beans.size < limit) {
                adapter_artical?.setNewInstance(beans)
                adapter_artical?.loadMoreModule?.loadMoreEnd()
            } else {
                adapter_artical?.setNewInstance(beans)
                adapter_artical?.loadMoreModule?.loadMoreComplete()

            }
        } else {
            if (beans.size < limit) {
                adapter_artical?.addData(beans)
                adapter_artical?.loadMoreModule?.loadMoreEnd()

            } else {
                adapter_artical?.addData(beans)
                adapter_artical?.loadMoreModule?.loadMoreComplete()
            }
        }
    }

    /**
     *期刊数据
     */
    private fun setPeriodicalData(beans: MutableList<PeriodicalBean>) {
        if (page == 1) {
            if (beans.size < limit) {
                adapter_periodical?.setNewInstance(beans)
                adapter_periodical?.loadMoreModule?.loadMoreEnd()
            } else {
                adapter_periodical?.setNewInstance(beans)
                adapter_periodical?.loadMoreModule?.loadMoreComplete()

            }
        } else {
            if (beans.size < limit) {
                adapter_periodical?.addData(beans)
                adapter_periodical?.loadMoreModule?.loadMoreEnd()

            } else {
                adapter_periodical?.addData(beans)
                adapter_periodical?.loadMoreModule?.loadMoreComplete()
            }
        }
    }

    /**
     *刊物数据
     */
    private fun setPublicationData(beans: MutableList<PublicationBean>) {
        if (page == 1) {
            if (beans.size < limit) {
                adapter_publiction?.setNewInstance(beans)
                adapter_publiction?.loadMoreModule?.loadMoreEnd()
            } else {
                adapter_publiction?.setNewInstance(beans)
                adapter_publiction?.loadMoreModule?.loadMoreComplete()

            }
        } else {
            if (beans.size < limit) {
                adapter_publiction?.addData(beans)
                adapter_publiction?.loadMoreModule?.loadMoreEnd()

            } else {
                adapter_publiction?.addData(beans)
                adapter_publiction?.loadMoreModule?.loadMoreComplete()
            }
        }
    }

    /**
     *文章数据
     */
    private fun setTuLingData(beans: MutableList<TulingBean>) {
        if (page == 1) {
            if (beans.size < limit) {
                adapter_tuling?.setNewInstance(beans)
                adapter_tuling?.loadMoreModule?.loadMoreEnd()
            } else {
                adapter_tuling?.setNewInstance(beans)
                adapter_tuling?.loadMoreModule?.loadMoreComplete()

            }
        } else {
            if (beans.size < limit) {
                adapter_tuling?.addData(beans)
                adapter_tuling?.loadMoreModule?.loadMoreEnd()

            } else {
                adapter_tuling?.addData(beans)
                adapter_tuling?.loadMoreModule?.loadMoreComplete()
            }
        }
    }

    /**
     *文件夹数据
     */
    private fun setFolderData(beans: MutableList<FolderBean>) {
        if (page == 1) {
            if (beans.size < limit) {
                adapter_folder?.setNewInstance(beans)
                adapter_folder?.loadMoreModule?.loadMoreEnd()
            } else {
                adapter_folder?.setNewInstance(beans)
                adapter_folder?.loadMoreModule?.loadMoreComplete()

            }
        } else {
            if (beans.size < limit) {
                adapter_folder?.addData(beans)
                adapter_folder?.loadMoreModule?.loadMoreEnd()

            } else {
                adapter_folder?.addData(beans)
                adapter_folder?.loadMoreModule?.loadMoreComplete()
            }
        }
    }

    /**
     *学习项目数据
     */
    private fun setStudyData(beans: MutableList<StudyProject>) {
        if (page == 1) {
            if (beans.size < limit) {
                adapter_study?.setNewInstance(beans)
                adapter_study?.loadMoreModule?.loadMoreEnd()
            } else {
                adapter_study?.setNewInstance(beans)
                adapter_study?.loadMoreModule?.loadMoreComplete()

            }
        } else {
            if (beans.size < limit) {
                adapter_study?.addData(beans)
                adapter_study?.loadMoreModule?.loadMoreEnd()

            } else {
                adapter_study?.addData(beans)
                adapter_study?.loadMoreModule?.loadMoreComplete()
            }
        }
    }

    /**
     *学习项目数据
     */
    private fun setMicroKnowData(beans: MutableList<MicroKnowBean>) {
        if (page == 1) {
            if (beans.size < limit) {
                adapter_micro_know?.setNewInstance(beans)
                adapter_micro_know?.loadMoreModule?.loadMoreEnd()
            } else {
                adapter_micro_know?.setNewInstance(beans)
                adapter_micro_know?.loadMoreModule?.loadMoreComplete()

            }
        } else {
            if (beans.size < limit) {
                adapter_micro_know?.addData(beans)
                adapter_micro_know?.loadMoreModule?.loadMoreEnd()

            } else {
                adapter_micro_know?.addData(beans)
                adapter_micro_know?.loadMoreModule?.loadMoreComplete()
            }
        }
    }

    fun setOnItemClick(
        adapter: BaseQuickAdapter<BaseResourceInterface, BaseViewHolder>,
        item: BaseResourceInterface
    ) {

    }

    /**
     * 刷新
     */
    override fun onRefresh() {
        page = 1
        getData()
    }

    /**
     * 加载更多监听
     */
    override fun onLoadMore() {
        inflater.recView.postDelayed(Runnable {
            page++
            getData()
        }, 1000)
    }

}
