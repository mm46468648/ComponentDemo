package com.mooc.search.adapter

import android.content.Intent
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.adapter.*
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.search.*
import com.mooc.commonbusiness.model.studyproject.StudyProject
import com.mooc.search.R
import com.mooc.search.model.SearchItemBean
import com.mooc.search.ui.SearchListActivity
import com.mooc.search.utils.SearchConstants
import com.mooc.statistics.LogUtil
import com.mooc.commonbusiness.constants.LogEventConstants2

/**
搜索的数据适配器
 * @Author limeng
 * @Date 2020/8/7-2:23 PM
 */
class SearchResultAdapter(data: MutableList<SearchItemBean>?) : BaseMultiItemQuickAdapter<SearchItemBean, BaseViewHolder>(data) {
    var keyword: String? = null
    var bean: SearchResultBean? = null
    var isVague: String? = null

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list ifjmdlskjgdgs created out of this one to avoid mutable list
     */
    init {
        addItemType(ResourceTypeConstans.TYPE_COURSE, R.layout.search_item_qv_header)
        addItemType(ResourceTypeConstans.TYPE_TRACK, R.layout.search_item_qv_header)
        addItemType(ResourceTypeConstans.TYPE_ALBUM, R.layout.search_item_qv_header)
        addItemType(ResourceTypeConstans.TYPE_ARTICLE, R.layout.search_item_qv_header)
        addItemType(ResourceTypeConstans.TYPE_PERIODICAL, R.layout.search_item_qv_header)
        addItemType(ResourceTypeConstans.TYPE_PUBLICATION, R.layout.search_item_qv_header)
        addItemType(ResourceTypeConstans.TYPE_E_BOOK, R.layout.search_item_qv_header)
        addItemType(ResourceTypeConstans.TYPE_MICRO_LESSON, R.layout.search_item_qv_header)
        addItemType(ResourceTypeConstans.TYPE_BAIKE, R.layout.search_item_qv_header)
        addItemType(ResourceTypeConstans.TYPE_STUDY_PLAN, R.layout.search_item_qv_header)
        addItemType(ResourceTypeConstans.TYPE_TU_LING, R.layout.search_item_qv_header)
        addItemType(ResourceTypeConstans.TYPE_SOURCE_FOLDER, R.layout.search_item_qv_header)
        addItemType(ResourceTypeConstans.TYPE_DEFAULT, R.layout.search_item_default)
    }

    override fun convert(holder: BaseViewHolder, item: SearchItemBean) {
        if (item.itemType != ResourceTypeConstans.TYPE_DEFAULT) {
            if (holder.layoutPosition == 0) {
                holder.setGone(R.id.gray, true)
            } else {
                holder.setGone(R.id.gray, false)
            }
        }
        when (item.itemType) {
//            SearchConstants.TYPE_HEADER -> setHeaderData(holder, item)
            ResourceTypeConstans.TYPE_COURSE -> setCourseData(holder, item)
            ResourceTypeConstans.TYPE_E_BOOK -> setEBookData(holder, item)
            ResourceTypeConstans.TYPE_MICRO_LESSON -> setMiroLessonData(holder, item)
            ResourceTypeConstans.TYPE_ARTICLE -> setArticleData(holder, item)
            ResourceTypeConstans.TYPE_TRACK -> setTrackData(holder, item)
            ResourceTypeConstans.TYPE_ALBUM -> setAlbumData(holder, item)
            ResourceTypeConstans.TYPE_PERIODICAL -> setPeriodicalData(holder, item)
            ResourceTypeConstans.TYPE_PUBLICATION -> setPublicationData(holder, item)
            ResourceTypeConstans.TYPE_BAIKE -> setBaiKeData(holder, item)
            ResourceTypeConstans.TYPE_TU_LING -> setTuLingData(holder, item)
            ResourceTypeConstans.TYPE_STUDY_PLAN -> setStudyProData(holder, item)
            ResourceTypeConstans.TYPE_SOURCE_FOLDER -> setStudyFolder(holder, item);
        }
    }

    private fun setStudyFolder(holder: BaseViewHolder, item: SearchItemBean) {
        var list = ArrayList<FolderBean>()
        if (bean?.folder?.items?.size!! <= 2) {
            list = bean?.folder?.items as ArrayList<FolderBean>
        } else {
            bean?.folder?.items?.elementAt(0)?.let { list.add(it) }
            bean?.folder?.items?.elementAt(1)?.let { list.add(it) }
        }
        val adapter = FolderListAdapter(list)
        onItemClick(adapter)
        holder.setText(R.id.title, context.getString(R.string.study_plan_list))
        holder.setText(R.id.show_more, "查看全部(${bean?.folder?.count})")
        val recycleView = holder.getViewOrNull<RecyclerView>(R.id.recycler_view)
        recycleView?.layoutManager = LinearLayoutManager(context)
        recycleView?.adapter = adapter
        val show_more = holder.getViewOrNull<TextView>(R.id.show_more)
        show_more?.setOnClickListener { v -> toMoreList(ResourceTypeConstans.TYPE_SOURCE_FOLDER, bean?.folder?.count.toString()) }
    }

    private fun setAlbumData(holder: BaseViewHolder, item: SearchItemBean) {
        var list = ArrayList<AlbumBean>()
        if (bean?.album?.items?.size!! <= 2) {
            list = bean?.album?.items as ArrayList<AlbumBean>
        } else {
            bean?.album?.items?.elementAt(0)?.let { list.add(it) }
            bean?.album?.items?.elementAt(1)?.let { list.add(it) }
        }
        val adapter = CommonAlbumAdapter(list)
        onItemClick(adapter)
        holder.setText(R.id.title, context.getString(R.string.album))
        holder.setText(R.id.show_more, "查看全部(${bean?.album?.count})")
        val recycleView = holder.getViewOrNull<RecyclerView>(R.id.recycler_view)
        recycleView?.layoutManager = LinearLayoutManager(context)
        recycleView?.adapter = adapter
        val show_more = holder.getViewOrNull<TextView>(R.id.show_more)
        show_more?.setOnClickListener { v -> toMoreList(ResourceTypeConstans.TYPE_ALBUM, bean?.album?.count.toString()) }
    }

    private fun setArticleData(holder: BaseViewHolder, item: SearchItemBean) {
        var list = ArrayList<ArticleBean>()
        if (bean?.article?.items?.size!! <= 2) {
            list = bean?.article?.items as ArrayList<ArticleBean>
        } else {
            bean?.article?.items?.elementAt(0)?.let { list.add(it) }
            bean?.article?.items?.elementAt(1)?.let { list.add(it) }
        }
        val adapter = ArticleAdapter(list)
        onItemClick(adapter)
        holder.setText(R.id.title, context.getString(R.string.article))
        holder.setText(R.id.show_more, "查看全部(${bean?.article?.count})")

        val recycleView = holder.getViewOrNull<RecyclerView>(R.id.recycler_view)
        recycleView?.layoutManager = LinearLayoutManager(context)
        recycleView?.adapter = adapter
        val show_more = holder.getViewOrNull<TextView>(R.id.show_more)
        show_more?.setOnClickListener { v -> toMoreList(ResourceTypeConstans.TYPE_ARTICLE, bean?.article?.count.toString()) }
    }

    private fun setMiroLessonData(holder: BaseViewHolder, item: SearchItemBean) {
        var list = ArrayList<MicroBean>()
        if (bean?.micro_course?.items?.size!! <= 2) {
            list = bean?.micro_course?.items as ArrayList<MicroBean>
        } else {
            bean?.micro_course?.items?.elementAt(0)?.let { list.add(it) }
            bean?.micro_course?.items?.elementAt(1)?.let { list.add(it) }
        }
        val adapter = CommonSlightCourseAdapter(list, true)

        onItemClick(adapter)
        holder.setText(R.id.title, context.getString(R.string.micro_course))
        holder.setText(R.id.show_more, "查看全部(${bean?.micro_course?.count})")
        val recycleView = holder.getViewOrNull<RecyclerView>(R.id.recycler_view)
        recycleView?.layoutManager = LinearLayoutManager(context)
        recycleView?.adapter = adapter
        val show_more = holder.getViewOrNull<TextView>(R.id.show_more)
        show_more?.setOnClickListener { v -> toMoreList(ResourceTypeConstans.TYPE_MICRO_LESSON, bean?.micro_course?.count.toString()) }
    }

    /**
     * 设置图书数据
     */
    private fun setEBookData(holder: BaseViewHolder, item: SearchItemBean) {
        var list = ArrayList<EBookBean>()
        if (bean?.ebook?.items?.size!! <= 2) {
            list = bean?.ebook?.items as ArrayList<EBookBean>
        } else {
            bean?.ebook?.items?.elementAt(0)?.let { list.add(it) }
            bean?.ebook?.items?.elementAt(1)?.let { list.add(it) }
        }
        val adapter_course = CommonEBookAdapter(list)
        onItemClick(adapter_course)
        holder.setText(R.id.title, context.getString(R.string.ebook))
        holder.setText(R.id.show_more, "查看全部(${bean?.ebook?.count})")
        val recycleView = holder.getViewOrNull<RecyclerView>(R.id.recycler_view)
        recycleView?.layoutManager = LinearLayoutManager(context)
        recycleView?.adapter = adapter_course
        val show_more = holder.getViewOrNull<TextView>(R.id.show_more)
        show_more?.setOnClickListener { v -> toMoreList(ResourceTypeConstans.TYPE_E_BOOK, bean?.ebook?.count.toString()) }
    }


    /**
     * 设置课程数据
     */
    private fun setCourseData(holder: BaseViewHolder, item: SearchItemBean) {
        var list = ArrayList<CourseBean>()
        if (bean?.course?.items?.size!! <= 2) {
            list = bean?.course?.items as ArrayList<CourseBean>
        } else {
            bean?.course?.items?.elementAt(0)?.let { list.add(it) }
            bean?.course?.items?.elementAt(1)?.let { list.add(it) }
        }
        val adapter_course = CommonCourseAdapter(list, true)
        adapter_course.isGoneTag = true
        onItemClick(adapter_course)
        holder.setText(R.id.title, context.getString(R.string.course))
        holder.setText(R.id.show_more, "查看全部(${bean?.course?.count})")
        val recycleView = holder.getViewOrNull<RecyclerView>(R.id.recycler_view)
        recycleView?.layoutManager = LinearLayoutManager(context)
        recycleView?.adapter = adapter_course
        val show_more = holder.getViewOrNull<TextView>(R.id.show_more)
        show_more?.setOnClickListener { v -> toMoreList(ResourceTypeConstans.TYPE_COURSE, bean?.course?.count.toString()) }
    }

    /**
     * 设置期刊数据
     */
    private fun setPeriodicalData(holder: BaseViewHolder, item: SearchItemBean) {
        var list = ArrayList<PeriodicalBean>()
        if (bean?.chaoxing?.items?.size!! <= 2) {
            list = bean?.chaoxing?.items as ArrayList<PeriodicalBean>
        } else {
            bean?.chaoxing?.items?.elementAt(0)?.let { list.add(it) }
            bean?.chaoxing?.items?.elementAt(1)?.let { list.add(it) }
        }
        val adapter_periofical = PeriodicalAdapter(list)
        onItemClick(adapter_periofical)
        holder.setText(R.id.title, context.getString(R.string.periodical))
        holder.setText(R.id.show_more, "查看全部(${bean?.chaoxing?.count})")
        val recycleView = holder.getViewOrNull<RecyclerView>(R.id.recycler_view)
        recycleView?.layoutManager = LinearLayoutManager(context)
        recycleView?.adapter = adapter_periofical
        val show_more = holder.getViewOrNull<TextView>(R.id.show_more)
        show_more?.setOnClickListener { v -> toMoreList(ResourceTypeConstans.TYPE_PERIODICAL, bean?.chaoxing?.count.toString()) }
    }

    /**
     * 设置刊物数据
     */
    private fun setPublicationData(holder: BaseViewHolder, item: SearchItemBean) {
        var list = ArrayList<PublicationBean>()
        if (bean?.kanwu?.items?.size!! <= 2) {
            list = bean?.kanwu?.items as ArrayList<PublicationBean>
        } else {
            bean?.kanwu?.items?.elementAt(0)?.let { list.add(it) }
            bean?.kanwu?.items?.elementAt(1)?.let { list.add(it) }
        }
        val adapter_publication = PublicationAdapter(list)
        onItemClick(adapter_publication)
        holder.setText(R.id.title, context.getString(R.string.publicaton))
        holder.setText(R.id.show_more, "查看全部(${bean?.kanwu?.count})")
        val recycleView = holder.getViewOrNull<RecyclerView>(R.id.recycler_view)
        recycleView?.layoutManager = LinearLayoutManager(context)
        recycleView?.adapter = adapter_publication
        val show_more = holder.getViewOrNull<TextView>(R.id.show_more)
        show_more?.setOnClickListener { v -> toMoreList(ResourceTypeConstans.TYPE_PUBLICATION, bean?.kanwu?.count.toString()) }
    }

    /**
     * 设置音频数据
     */
    private fun setTrackData(holder: BaseViewHolder, item: SearchItemBean) {
        var list = ArrayList<TrackBean>()
        if (bean?.track?.items?.size!! <= 2) {
            list = bean?.track?.items as ArrayList<TrackBean>
        } else {
            bean?.track?.items?.elementAt(0)?.let { list.add(it) }
            bean?.track?.items?.elementAt(1)?.let { list.add(it) }
        }
        val adapter_periofical = TrackAdapter(list)
        onItemClick(adapter_periofical)
        holder.setText(R.id.title, context.getString(R.string.track))
        holder.setText(R.id.show_more, "查看全部(${bean?.track?.count})")
        val recycleView = holder.getViewOrNull<RecyclerView>(R.id.recycler_view)
        recycleView?.layoutManager = LinearLayoutManager(context)
        recycleView?.adapter = adapter_periofical
        val show_more = holder.getViewOrNull<TextView>(R.id.show_more)
        show_more?.setOnClickListener { v -> toMoreList(ResourceTypeConstans.TYPE_TRACK, bean?.track?.count.toString()) }
    }

    /**
     * 设置知识库数据
     */
    private fun setTuLingData(holder: BaseViewHolder, item: SearchItemBean) {
        var list = ArrayList<TulingBean>()
        if (bean?.tuling?.items?.size!! <= 2) {
            list = bean?.tuling?.items as ArrayList<TulingBean>
        } else {
            bean?.tuling?.items?.elementAt(0)?.let { list.add(it) }
            bean?.tuling?.items?.elementAt(1)?.let { list.add(it) }
        }
        val adapter = TulingAdapter(list)
        onItemClick(adapter)
        holder.setText(R.id.title, context.getString(R.string.tuling))
        holder.setText(R.id.show_more, "查看全部(${bean?.tuling?.count})")
        val recycleView = holder.getViewOrNull<RecyclerView>(R.id.recycler_view)
        recycleView?.layoutManager = LinearLayoutManager(context)
        recycleView?.adapter = adapter
        val show_more = holder.getViewOrNull<TextView>(R.id.show_more)
        show_more?.setOnClickListener { v -> toMoreList(ResourceTypeConstans.TYPE_TU_LING, bean?.tuling?.count.toString()) }
    }

    /**
     * 设学习项目数据
     */
    private fun setStudyProData(holder: BaseViewHolder, item: SearchItemBean) {
        var list = ArrayList<StudyProject>()
        if (bean?.study_plan?.items?.size!! <= 2) {
            list = bean?.study_plan?.items as ArrayList<StudyProject>
        } else {
            bean?.study_plan?.items?.elementAt(0)?.let { list.add(it) }
            bean?.study_plan?.items?.elementAt(1)?.let { list.add(it) }
        }
        val adapter = StudyProAdapter(list)
        onItemClick(adapter)
        holder.setText(R.id.title, context.getString(R.string.study_plan))
        holder.setText(R.id.show_more, "查看全部(${bean?.study_plan?.count})")
        val recycleView = holder.getViewOrNull<RecyclerView>(R.id.recycler_view)
        val show_more = holder.getViewOrNull<TextView>(R.id.show_more)
        recycleView?.layoutManager = LinearLayoutManager(context)
        recycleView?.adapter = adapter
        show_more?.setOnClickListener { v -> toMoreList(ResourceTypeConstans.TYPE_STUDY_PLAN, bean?.study_plan?.count.toString()) }

    }

    /**
     * 设百科
     */
    private fun setBaiKeData(holder: BaseViewHolder, item: SearchItemBean) {
        var list = ArrayList<BaiKeBean>()
        if (bean?.baike?.items?.size!! <= 2) {
            list = bean?.baike?.items as ArrayList<BaiKeBean>
        } else {
            bean?.baike?.items?.elementAt(0)?.let { list.add(it) }
            bean?.baike?.items?.elementAt(1)?.let { list.add(it) }
        }


        val adapter = BaiKeAdapter(list)

        onItemClick(adapter)
        holder.setText(R.id.title, context.getString(R.string.baike))
        holder.setText(R.id.show_more, "查看全部(${bean?.baike?.count})")
        val recycleView = holder.getViewOrNull<RecyclerView>(R.id.recycler_view)

        recycleView?.layoutManager = LinearLayoutManager(context)
        recycleView?.adapter = adapter
        val show_more = holder.getViewOrNull<TextView>(R.id.show_more)
        show_more?.setOnClickListener { v -> toMoreList(ResourceTypeConstans.TYPE_BAIKE, bean?.baike?.count.toString()) }

    }

    fun toMoreList(type: Int, num: String) {
        val intent = Intent()
        intent.setClass(context, SearchListActivity().javaClass)
        intent.putExtra(SearchConstants.RESCOURSE_TYPE, type)
        intent.putExtra(SearchConstants.RESCOURSE_SEARCH_NUM, num)
        intent.putExtra(SearchConstants.RESCOURSE_WORD, keyword)
        intent.putExtra(SearchConstants.RESCOURSE_ISVAGUE, isVague)
        context.startActivity(intent)


    }

    private fun <T, VH : BaseViewHolder> onItemClick(adapter: BaseQuickAdapter<T, VH>?) {
        adapter?.setOnItemClickListener { adapter, view, position ->
            val any = adapter.data[position]
            if (any is BaseResourceInterface) { //并不是所有资源都要跳转 （比如图灵）

                LogUtil.addClickLogNew(
                        LogEventConstants2.P_SEARCH,
                        any._resourceId, "${any._resourceType}",
                        any._other?.get(IntentParamsConstants.WEB_PARAMS_TITLE)
                                ?: "", "${LogEventConstants2.typeLogPointMap[any._resourceType]}#${any._resourceId}")

                ResourceTurnManager.turnToResourcePage(any)
            }
        }
    }


}