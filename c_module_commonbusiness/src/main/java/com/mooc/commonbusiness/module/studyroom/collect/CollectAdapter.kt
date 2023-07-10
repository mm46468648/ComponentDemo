package com.mooc.commonbusiness.module.studyroom.collect

import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseProviderMultiAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.mooc.commonbusiness.model.home.OwnTrackBean
import com.mooc.commonbusiness.model.search.*
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.EditableAdapterInterface
import com.mooc.commonbusiness.module.studyroom.collect.mutiprovider.*
import com.mooc.commonbusiness.interfaces.StudyResourceEditable
import com.mooc.resource.widget.Space120FootView
import com.mooc.resource.widget.Space120LoadMoreView

class CollectAdapter(list:ArrayList<Any>) : BaseProviderMultiAdapter<Any>(list),LoadMoreModule ,EditableAdapterInterface{

    init {

        addItemProvider(ArticleProvider())
        addItemProvider(PeriodicalProvider())
        addItemProvider(AlbumProvider())
        addItemProvider(TrackProvider())
        addItemProvider(MicroProvider())
        addItemProvider(BaikeProvider())
        addItemProvider(OwnTrackProvider())

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

//        setFooterView(Space120FootView(context))
    }
    override fun getItemType(data: List<Any>, position: Int): Int {
        val any = data[position]
        return when{
            any is ArticleBean->ResourceTypeConstans.TYPE_ARTICLE
            any is PeriodicalBean->ResourceTypeConstans.TYPE_PERIODICAL
            any is AlbumBean->ResourceTypeConstans.TYPE_ALBUM
            any is TrackBean->ResourceTypeConstans.TYPE_TRACK
            any is MicroBean->ResourceTypeConstans.TYPE_MICRO_LESSON
            any is BaiKeBean->ResourceTypeConstans.TYPE_BAIKE
            any is OwnTrackBean->ResourceTypeConstans.TYPE_ONESELF_TRACK
            else -> ResourceTypeConstans.TYPE_UNDEFINE
        }
    }

    override fun onEditAbleChange(editable: StudyResourceEditable) {
        remove(editable)
    }


}