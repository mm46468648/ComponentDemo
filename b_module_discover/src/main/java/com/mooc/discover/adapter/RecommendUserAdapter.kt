package com.mooc.discover.adapter

import android.graphics.Color
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.changeskin.SkinManager
import com.mooc.common.ktextends.getColorRes
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.manager.BaseObserver
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.commonbusiness.widget.HeadView
import com.mooc.discover.R
import com.mooc.discover.httpserver.HttpService
import com.mooc.discover.model.RecommendUser
import com.mooc.resource.widget.MoocImageView
import org.json.JSONObject

class RecommendUserAdapter(list: ArrayList<RecommendUser>?) :
    BaseQuickAdapter<RecommendUser, BaseViewHolder>(R.layout.item_recommend_user_detail, list) {

    var dot = "、"

    override fun convert(holder: BaseViewHolder, item: RecommendUser) {
        SkinManager.getInstance().injectSkin(holder.itemView)
        holder.setText(R.id.tvName, item.nickname)
        holder.getView<HeadView>(R.id.mivAvatar).setHeadImage(item.avatar, item.avatar_identity)

        val tvFollow = holder.getView<TextView>(R.id.tvFollow)

        //关注
        val followStr = if (item.is_attention) "已关注" else "+ 关注"
        holder.setText(R.id.tvFollow, followStr)
        if (item.is_attention) {
            holder.setTextColor(R.id.tvFollow, context.getColorRes(R.color.color_82))
            holder.setBackgroundColor(R.id.tvFollow, Color.TRANSPARENT)
        } else {
            holder.setTextColor(R.id.tvFollow, Color.WHITE)
            //换肤
            if(SkinManager.getInstance().needChangeSkin()){
                val llAddStudyBg =
                    SkinManager.getInstance().resourceManager.getDrawableByName("shape_conrners1_5_color_primary")
                tvFollow.background = llAddStudyBg
            }else{
                holder.setBackgroundResource(R.id.tvFollow, R.drawable.shape_conrners1_5_color_primary)
            }
        }

        //描述
        val sb = StringBuilder()
        item.label_list?.forEach {
            sb.append(it)
            sb.append(dot)
        }
        var descStr = ""
        if (sb.isNotEmpty() && sb.endsWith(dot)) {
            descStr = sb.substring(0, sb.length - 1)
        }

        holder.setText(R.id.tvDesc, descStr)

        //分割线
        val dividerVisiable = holder.adapterPosition == data.size - 1
        holder.setGone(R.id.viewLine, dividerVisiable)


        val pbFollow = holder.getView<ProgressBar>(R.id.pbFollow)
        pbFollow.visibility = View.GONE
        tvFollow.visibility = View.VISIBLE

        tvFollow.setOnClickListener {
            //切换关注状态
            clickFollow(item, tvFollow, pbFollow)
        }
    }

    fun clickFollow(item: RecommendUser, textView: TextView, pb: ProgressBar) {
        pb.visibility = View.VISIBLE
        textView.visibility = View.INVISIBLE

        val status = if (item.is_attention) 0 else 1
        val requestData = JSONObject()
        requestData.put("follow_user_id", item.user_id)
        requestData.put("follow_status", status)
        HttpService.discoverApi
            .postFollowUser(RequestBodyUtil.fromJson(requestData))
            .compose(RxUtils.applySchedulers())
            .subscribe(object : BaseObserver<HttpResponse<Any>>(context) {
                override fun onSuccess(t: HttpResponse<Any>) {
                    item.is_attention = (!item.is_attention)
                    updateFollowNum(item.is_attention)
                    notifyDataSetChanged()
                }

                override fun onFailure(code: Int, message: String?) {
                    super.onFailure(code, message)

                    notifyDataSetChanged()
                    toast("操作失败")
                }
            });
    }

    /**
     * 更新关注数量
     * @param increase 是否增加
     */
    fun updateFollowNum(increase : Boolean){
//        GlobalsUserManager.userInfo?.apply {
//            val userFollowCount = if (increase) user_follow_count + 1 else user_follow_count - 1
//            this.user_follow_count = if(userFollowCount>0) userFollowCount else 0
//
//            SPUserUtils.getInstance().saveUserInfo(this)
//            LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_USERINFO_CHANGE).postValue(this)
//        }
    }


}