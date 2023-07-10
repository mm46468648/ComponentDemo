package com.mooc.discover.adapter.style

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.column.ui.columnall.delegate.ColumnTagSeventDelegate
import com.mooc.common.utils.TimeUtils
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.search.AlbumBean
import com.mooc.commonbusiness.utils.TaskScoreUtils
import com.mooc.discover.R
import com.mooc.discover.model.MusicBean
import com.mooc.discover.model.RecommendColumn
import com.qmuiteam.qmui.util.QMUIDisplayHelper

class HomeTagSevenAdapter(data: ArrayList<RecommendColumn>?) :
    BaseDelegateMultiAdapter<RecommendColumn, BaseViewHolder>(data) {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    init {
        setMultiTypeDelegate(ColumnTagSeventDelegate())
    }

    override fun convert(holder: BaseViewHolder, item: RecommendColumn) {
        val itemViewType: Int = holder.itemViewType
        if (itemViewType == ResourceTypeConstans.TYPE_UNDEFINE) return

        //设置type类型
        holder.setText(R.id.tvSexTitle, item.title)
        holder.setText(R.id.tvSexSub, "")
        holder.setText(R.id.tvSexTime, "")
        holder.setText(R.id.tv_middle, "")
        holder.setGone(R.id.tv_middle, true);


        val value = ResourceTypeConstans.typeStringMap[item.type]
        val tvType = holder.getView<TextView>(R.id.tvSevenHomeType)
        val llType = holder.getView<View>(R.id.llType)
        tvType.text = value
        if (TextUtils.isEmpty(value)) {
            llType.visibility = View.GONE
        } else {
            llType.visibility = View.VISIBLE
        }
        tvType.textSize = 9f
        tvType.setTextColor(ContextCompat.getColor(context, R.color.color_AC))
        tvType.setBackgroundResource(R.drawable.shape_radius2_stroke05_c0c0c0)

        when (item.type) {
            ResourceTypeConstans.TYPE_SPECIAL -> {//合集
                if (!TextUtils.isEmpty(item.identity_name)) {//后台返回的右上角角标不为空，显示后台配置
                    tvType.text = item.identity_name
                }
                holder.setText(R.id.tv_middle, item.resource_info?.title)
                holder.setText(R.id.tvSexSub, item.resource_info?.updated_time + "更新")
            }
            ResourceTypeConstans.TYPE_COURSE -> {
                holder.setText(R.id.tvSexSub, item.org)
                holder.setText(R.id.tvSexTime, item.start_time)
                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    tvType.text = item.identity_name
                    tvType.textSize = 10f
                    tvType.setTextColor(ContextCompat.getColor(context, R.color.color_10955B))
                    tvType.setBackgroundResource(R.drawable.shape_radius2_stroke05_10955b)
                }
            }
            ResourceTypeConstans.TYPE_MICRO_LESSON -> {
                holder.setText(R.id.tvSexSub, item.source)
                if (!TextUtils.isEmpty(item.video_duration)) {
                    val video_duration: Long
                    video_duration = try {
                        item.video_duration.toLong()
                    } catch (e: java.lang.NumberFormatException) {
                        0
                    }
                    holder.setText(R.id.tvSexTime, TimeUtils.timeParse(video_duration))
                }
            }
            ResourceTypeConstans.TYPE_ARTICLE, ResourceTypeConstans.TYPE_COLUMN_ARTICLE, ResourceTypeConstans.TYPE_PERIODICAL -> {
                holder.setText(R.id.tvSexSub, item.staff)
                holder.setText(R.id.tvSexTime, item.source)
            }
            //电子书
            ResourceTypeConstans.TYPE_E_BOOK -> {
                holder.setText(R.id.tvSexSub, item.writer)
                holder.setText(R.id.tvSexTime, item.platform)
            }

            ResourceTypeConstans.TYPE_STUDY_PLAN -> {
                holder.setText(R.id.tvSexSub, item.staff)
                holder.setText(R.id.tvSexTime, item.student_num.toString() + "人")
            }

            ResourceTypeConstans.TYPE_ALBUM -> {
                val album: AlbumBean = item.album_data
                val num: String = album.track_count.toString() + "集"
                holder.setText(R.id.tvSexSub, num)
            }
            ResourceTypeConstans.TYPE_QUESTIONNAIRE -> {
                holder.setText(R.id.tvSexSub, item.source)
            }
            ResourceTypeConstans.TYPE_TEST_VOLUME -> {//测试卷
                holder.setText(R.id.tvSexSub, item.source)
            }
            ResourceTypeConstans.TYPE_MASTER_TALK -> {
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                val track = item.track_data
                if (track.subordinated_album != null) {
                    holder.setText(R.id.tvSexSub, track.subordinated_album!!.album_title)
                }
                holder.setText(R.id.tvSexTime, TimeUtils.timeParse(track.duration))
            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                val musicBean: MusicBean = item.audio_data
                holder.setText(R.id.tvSexTime, TimeUtils.timeParse(musicBean.audio_time))
                holder.setText(R.id.tvSexSub, "")
            }
            ResourceTypeConstans.TYPE_PUBLICATION -> {
                holder.setText(R.id.tvSexTime, item.periodicals_data.unit)
                holder.setText(
                    R.id.tvSexSub,
                    "更新至" + item.periodicals_data.year + "年" + "第" + item.periodicals_data.term + "期"
                )
            }
            ResourceTypeConstans.TYPE_TASK -> {
                holder.setText(R.id.tvSexTime, item.join_num+"人参与")
                if (TextUtils.isEmpty(item.success_score)) {
                    item.success_score = ""
                }
                holder.setText(
                    R.id.tvSexSub,
                    TaskScoreUtils.getSpaString(
                        item.success_score,
                        ContextCompat.getColor(context, R.color.color_5D5D5D),
                        ContextCompat.getColor(context, R.color.color_5D5D5D),
                        QMUIDisplayHelper.sp2px(context, 12),
                        QMUIDisplayHelper.sp2px(context, 15)
                    )
                )
            }

            ResourceTypeConstans.TYPE_ACTIVITY, ResourceTypeConstans.TYPE_ACTIVITY_TASK -> {
//                holder.setText(R.id.tv_middle, item.about)
//                holder.setGone(R.id.tv_middle, TextUtils.isEmpty(item.about))
//                holder.setText(R.id.tvSexTime, item.publish_time)
            }

            ResourceTypeConstans.TYPE_COLUMN -> {
                holder.setText(R.id.tv_middle, item.desc)
                holder.setGone(R.id.tv_middle, TextUtils.isEmpty(item.desc))
                holder.setText(R.id.tvSexTime, item.update_time)
            }

            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE-> {
                holder.setText(R.id.tv_middle, item.resource_info?.title)
                holder.setText(R.id.tvSexSub, item.resource_info?.updated_time + "更新")
            }
            else -> {
                holder.setGone(R.id.tv_middle, true)
            }

        }

        val tvSexSub = holder.getView<TextView>(R.id.tvSexSub)
        if(TextUtils.isEmpty(tvSexSub.text)){
            tvSexSub.visibility = View.GONE
        }else{
            tvSexSub.visibility = View.VISIBLE
        }

    }

}