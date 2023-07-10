package com.mooc.discover.adapter
import android.widget.TextView
import androidx.annotation.Nullable
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.changeskin.SkinManager
import com.mooc.discover.R


/**
 * 发现页推荐分类标题适配器
 */

class DiscoverRecoomendTabAdapter(data: ArrayList<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_top_menu,data) {

    var selectPosition : Int = -1
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun convert(helper: BaseViewHolder, item: String) {
//        SkinManager.getInstance().injectSkin(helper.itemView)
        helper.setText(R.id.title, item)
        val tvTitle = helper.getView<TextView>(R.id.title)
        helper.setGone(R.id.left, helper.layoutPosition != 0)

        if (selectPosition == helper.layoutPosition) {
            helper.setTextColor(R.id.title, context.getResources().getColor(R.color.color_2))
            val needChangeSkin = SkinManager.getInstance().needChangeSkin()
//            if(needChangeSkin) {
            //换肤
                val drawableByName =
                    SkinManager.getInstance().resourceManager.getDrawableByName("discover_bg_recommend_tab")
                tvTitle.setBackground(drawableByName)
//            }else{
//                helper.setBackgroundResource(R.id.title, R.drawable.discover_bg_recommend_tab)
//            }
        } else {
            helper.setTextColor(R.id.title, context.getResources().getColor(R.color.color_98))
            helper.setBackgroundResource(R.id.title, R.drawable.shape_corner_micro_menu_gray)
        }

    }
}