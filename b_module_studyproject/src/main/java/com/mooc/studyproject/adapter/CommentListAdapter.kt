package com.mooc.studyproject.adapter

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.adapter.DynamicImagesAdapter
import com.mooc.commonbusiness.utils.format.StringFormatUtil.Companion.getStrUserName
import com.mooc.commonbusiness.widget.HeadView
import com.mooc.commonbusiness.widget.VoicePlayerController
import com.mooc.studyproject.R
import com.mooc.studyproject.model.ItemComment
import com.mooc.commonbusiness.model.studyproject.StudyPlanDetailBean

/**
评论adapter
 * @Author limeng
 * @Date 2020/12/24-2:53 PM
 */
class CommentListAdapter(data: ArrayList<ItemComment>?) :
        BaseQuickAdapter<ItemComment, BaseViewHolder>(R.layout.studyproject_item_comment_list_new, data), LoadMoreModule {
    var isCanComment: Boolean = false
    private val mStudyPlanDetailBean: StudyPlanDetailBean? = null //学习相关数据
    private val mIsInitiator = false
    var foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#ff4a90e2"))

    override fun convert(holder: BaseViewHolder, item: ItemComment) {
        setItemView(holder, item)
    }


    private fun setItemView(holder: BaseViewHolder, item: ItemComment) {
        if (item.isShowExpand == true) {
            holder.setGone(R.id.rl_all, true)
            return
        }
        if (item.comment_type == 1) {            //音频评论
            holder.setGone(R.id.commentVoice, false)
            holder.setGone(R.id.tvContent, true)
            if (!item.comment_content.isNullOrEmpty()) {
                holder.setGone(R.id.tvVoiceDel, true)
                holder.setGone(R.id.commentVoice, false)
                holder.getView<VoicePlayerController>(R.id.commentVoice).setTotleTimeLength(item.comment_content_long)
                holder.getView<VoicePlayerController>(R.id.commentVoice).setPlayPath(item.comment_content)
            } else {
                holder.setGone(R.id.tvVoiceDel, false)
                holder.setGone(R.id.commentVoice, true)
            }
        } else {
            holder.setGone(R.id.tvVoiceDel, true)
            holder.setGone(R.id.commentVoice, true)
            holder.setGone(R.id.tvContent, false)
            holder.setText(R.id.tvContent, item.comment_content.toString() + "\t\t\t\t")
        }
        setCommentReplay(holder, item)
        if (!TextUtils.isEmpty(item.is_top)) {
            if ("1".equals(item.is_top)) {
                holder.setGone(R.id.tvCommentTop, false)

            } else {
                holder.setGone(R.id.tvCommentTop, true)

            }
        } else {
            holder.setGone(R.id.tvCommentTop, true)

        }
        holder.setText(R.id.tvTime, item.comment_time)
        holder.setText(R.id.tvAgree, item.like_num.toString())


        //防止复用，对于空的数据隐藏recyclerView
        if (item.comment_img_list?.isEmpty() == true) {
            holder.setGone(R.id.rvCommentImage, true)

        } else {     //如果是展开的样式则展开
            //动态中的images不为空，则展示动态图片
            val adapter = DynamicImagesAdapter(item.comment_img_list)
            val manager = GridLayoutManager(context, 3)
            holder.getView<RecyclerView>(R.id.rvCommentImage).setLayoutManager(manager)
            holder.getView<RecyclerView>(R.id.rvCommentImage).setAdapter(adapter)
            val text: String = holder.getView<TextView>(R.id.tvContentExpand).getText().toString()
            if ("展开" == text) {
                holder.setGone(R.id.rvCommentImage, true)
            } else if ("收起" == text) {
                holder.setGone(R.id.rvCommentImage, false)

            } else {
                holder.setGone(R.id.rvCommentImage, true)

            }
        }
        if (mStudyPlanDetailBean != null && mStudyPlanDetailBean.is_join == 0) {
            if (isCanComment) {
                holder.setGone(R.id.tvReply, false)

            } else {
                holder.setGone(R.id.tvReply, true)

            }

            holder.setGone(R.id.tvDel, true)
            holder.setGone(R.id.tvShied, true)
            holder.setGone(R.id.tvAgree, true)

        } else {
            if (isCanComment) {
                holder.setGone(R.id.tvReply, false)

            } else {
                holder.setGone(R.id.tvReply, true)

            }
            holder.setGone(R.id.tvAgree, false)

            if (mIsInitiator) {
                holder.setGone(R.id.tvShied, false)
                if (item.comment_status == 0) {
                    holder.setText(R.id.tvShied, context.resources.getString(R.string.study_plan_cancel_stop))
                } else {
                    holder.setText(R.id.tvShied, context.resources.getString(R.string.study_plan_stop))
                }

            } else {
                holder.setGone(R.id.tvShied, true)

            }
            if (item.is_comment_user == 1) {
                holder.setGone(R.id.tvDel, false)

            } else {
                holder.setGone(R.id.tvDel, true)
            }
        }


        val drawable: Drawable
        if (item.is_like) {
            drawable = context.getResources().getDrawable(R.mipmap.common_icon_red_like)
            //这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            holder.getView<TextView>(R.id.tvAgree).setCompoundDrawables(drawable, null, null, null)
        } else {
            drawable = context.getResources().getDrawable(R.mipmap.common_iv_plan_fill)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            holder.getView<TextView>(R.id.tvAgree).setCompoundDrawables(drawable, null, null, null)
        }

        if (item.is_from_cms == 1) {
            var name: String? = null
            val commentUserBean = item.from_cms_user_name
            if (commentUserBean != null) {
                if (item.is_studyplan_start_user) {
                    if (!TextUtils.isEmpty(commentUserBean.name)) {
                        name = java.lang.String.format(context.getResources().getString(R.string.text_initiator), commentUserBean.name?.let { getStrUserName(it) })
                    } else {
                        name = context.getResources().getString(R.string.text_str_initiator)
                    }
                } else {
                    name = commentUserBean.name?.let { getStrUserName(it) }.toString()
                }
                val head = holder.getViewOrNull<HeadView>(R.id.ivHeader) as HeadView
                head.setHeadImage(commentUserBean.avatar,commentUserBean.avatar_identity)


            } else {
                name = context.getResources().getString(R.string.text_str_initiator)
            }
            holder.setText(R.id.tvTitle, name)
        } else {
            val commentUserBean = item.comment_user
            var name: String? = null
            if (commentUserBean != null) {
                if (item.is_studyplan_start_user) {
                    if (!TextUtils.isEmpty(commentUserBean.name)) {
                        name = java.lang.String.format(context.getResources().getString(R.string.text_initiator),
                                commentUserBean.name?.let { getStrUserName(it) })
                    } else {
                        name = context.getResources().getString(R.string.text_str_initiator)
                    }
                } else {
                    name = commentUserBean?.name?.let { getStrUserName(it) }.toString()
                }

                val head = holder.getViewOrNull<HeadView>(R.id.ivHeader)
                head?.setHeadImage(commentUserBean.avatar, commentUserBean.avatar_identity)

            } else {
                name = context.getResources().getString(R.string.text_str_initiator)

            }
            holder.setText(R.id.tvTitle, name)
        }
//        如果有图片，则显示展开，点击展开再加载图片
        if (item.comment_img_list?.isEmpty() == true) {
            holder.setGone(R.id.tvContentExpand, true)

        } else {
            holder.setGone(R.id.tvContentExpand, false)

        }
        holder.getView<TextView>(R.id.tvContent).post(Runnable
        //根据行数，判断是否显示展开
        {
            if (holder.getView<TextView>(R.id.tvContent).getLineCount() > 2) {
                holder.setGone(R.id.tvContentExpand, false)

            }
        })
        holder.getView<TextView>(R.id.tvContentExpand).setOnClickListener(View.OnClickListener {
            val text = holder.getView<TextView>(R.id.tvContentExpand).getText().toString()
            if ("展开" == text) {
                holder.setText(R.id.tvContentExpand, "收起")
            } else {
                holder.setText(R.id.tvContentExpand, "展开")
            }
            notifyDataSetChanged()

        })
    }


    /**
     * 设置回复评论内容
     *
     * @param holder
     * @param position
     */
    private fun setCommentReplay(holder: BaseViewHolder, dataBean: ItemComment) {
        val infoBean = dataBean.comment_to_info
        if (infoBean == null) {
            holder.setGone(R.id.llToReply, true)
            return
        }

        //当图片不为空的时候就展示回复的评论区域
        val comment_content: String = infoBean.comment_content
        if (infoBean.comment_img_list != null && !infoBean.comment_img_list?.isEmpty()) {
            holder.setGone(R.id.llToReply, false)
        } else if (!TextUtils.isEmpty(comment_content)) {
            holder.setGone(R.id.llToReply, false)

        } else {
            holder.setGone(R.id.llToReply, true)
        }

        //判断音频是否显示
        if (infoBean.comment_type.equals("1")) {
            if (!infoBean.comment_content.isNullOrEmpty()) {//音频未被删除
                holder.setGone(R.id.replyTrackRl, false)
                holder.setGone(R.id.tvVoiceDelReply, true)
                (holder.getView<VoicePlayerController>(R.id.replyTrackRl)).setTotleTimeLength(infoBean.comment_content_long)
                (holder.getView<VoicePlayerController>(R.id.replyTrackRl)).playPath = infoBean.comment_content
            } else {
                holder.setGone(R.id.replyTrackRl, true)
                holder.setGone(R.id.tvVoiceDelReply, false)
            }

        } else {
            holder.setGone(R.id.replyTrackRl, true)
            holder.setGone(R.id.tvVoiceDelReply, true)
        }

        val name = infoBean?.name?.let { getStrUserName(it) }
        var titleTxt = "$name:"
        if (!TextUtils.isEmpty(comment_content) && !comment_content.endsWith(".mp3")) {
            titleTxt += " "
            titleTxt += comment_content + "\t\t\t\t"
        }

        val spannableString = SpannableString(titleTxt)
        spannableString.setSpan(foregroundColorSpan, 0, name?.length!! + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        holder.getView<com.mooc.resource.widget.expandabletextview.ExpandableTextView>(R.id.tvToReply).setContent(spannableString.toString())

    }


}