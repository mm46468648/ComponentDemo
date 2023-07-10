package com.mooc.home.ui.hornowall.talent

import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.model.UserInfo
import com.mooc.commonbusiness.widget.HeadView
import com.mooc.resource.widget.MoocImageView
import com.mooc.home.R
import com.mooc.home.model.HonorRollResponse
import org.jetbrains.annotations.NotNull

/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    荣誉墙达人
 * @Author:         xym
 * @CreateDate:     2020/8/18 7:26 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/18 7:26 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class TalentAdapter(mList: ArrayList<Any>) : BaseDelegateMultiAdapter<Any, BaseViewHolder>(mList),LoadMoreModule {


    companion object{
        const val TYPE_TITLE = 0
        const val TYPE_IMAGE = 1
        const val TYPE_DIVIDER = 2
    }


    init {
        // 第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<Any>() {
            override fun getItemType(@NotNull data: List<Any>, position: Int): Int {
                // 根据数据，自己判断应该返回的类型
                val any = data[position]
                return when{
                    any is HonorRollResponse -> TYPE_TITLE
                    any is UserInfo -> TYPE_IMAGE
                    any is String -> TYPE_DIVIDER
                    else -> 0
                }
            }
        })
        // 第二部，绑定 item 类型
        getMultiTypeDelegate()
                ?.addItemType(TYPE_TITLE, R.layout.home_item_honor_talent_title)
                ?.addItemType(TYPE_IMAGE, R.layout.home_item_honor_talent_image)
                ?.addItemType(TYPE_DIVIDER, R.layout.common_view_divider_e9e9e9_height10)
    }
    override fun convert(holder: BaseViewHolder, item: Any) {
        when(holder.itemViewType){
            TYPE_TITLE ->{
                val honorRollResponse = item as HonorRollResponse
                holder.setText(R.id.tvTitle,honorRollResponse.plan_name)
                holder.setText(R.id.tvNum,"${honorRollResponse.success_nums}人")
            }
            TYPE_IMAGE ->{
                val userInfo = item as UserInfo
                holder.getView<HeadView>(R.id.mivAvatar).setHeadImage(userInfo.avatar,userInfo.avatar_identity)
                holder.setText(R.id.tvTitle, userInfo.name?.let { nameStr(it) })

            }
        }

    }

    fun nameStr(mobiles: String): String {
        return if (mobiles.length> 5){
            mobiles.replace("(.{3}).*(.{2})".toRegex(), "$1****$2")
        }else mobiles
    }
}

