package com.mooc.discover.allColumnSubscribe

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.discover.R
import com.mooc.discover.model.SubscribeBean
import com.mooc.common.ktextends.loge
import com.mooc.resource.widget.flowLayout.AutoFlowLayout
import com.mooc.resource.widget.flowLayout.FlowAdapter
import org.jetbrains.annotations.NotNull

class AllColumnSubscribeAdapter(list: ArrayList<Any>) : BaseDelegateMultiAdapter<Any, BaseViewHolder>(list) {

    companion object {
        const val ITEM_TYPE_0 = 0
        const val ITEM_TYPE_1 = 1

        const val STR_MY_SUBSCRIBE = "我订阅的栏目"
        const val STR_CAN_SUBSCRIBE = "可订阅的栏目"
    }

    init {
        // 第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<Any>() {
            override fun getItemType(@NotNull data: List<Any>, position: Int): Int {
                // 根据数据，自己判断应该返回的类型
                //如果类型，不属于任何样式，返回默认样式
                val any = data[position]
                return when {
                    any is String -> 0
                    else -> 1
                }
            }
        })
        // 第二部，绑定 item 类型
        getMultiTypeDelegate()
                ?.addItemType(ITEM_TYPE_0, R.layout.column_item_subscribe_title)
                ?.addItemType(ITEM_TYPE_1, R.layout.column_item_subscribe_list)


    }

    override fun convert(holder: BaseViewHolder, item: Any) {

        if (holder.itemViewType == ITEM_TYPE_0) {    //一级头部，已订阅，未订阅
            val value = item as String
            holder.setText(R.id.tvTitle, value)
            //我的订阅显示编辑
            holder.setGone(R.id.tvEdit, STR_MY_SUBSCRIBE != value)
        } else if (holder.itemViewType == ITEM_TYPE_1) {    //订阅，未订阅列表
            if(item is ArrayList<*>){
                loge(item.toString())
                val list = item as ArrayList<SubscribeBean>
                holder.setGone(R.id.tvName, list.isEmpty())
                holder.setGone(R.id.alfName, list.isEmpty())

                if(list.isNotEmpty()){
                    val mAdapterType = list.get(0).mAdapterType
                    val titleStr = if(mAdapterType == 0) "专栏" else "专题"
                    holder.setText(R.id.tvName, titleStr)

                    val autoFlowLayout = holder.getView<com.mooc.resource.widget.flowLayout.AutoFlowLayout<SubscribeBean>>(R.id.alfName)
                    autoFlowLayout.clearViews()
                    val mFlowAdapter = MFlowAdapter(list)
                    autoFlowLayout.setAdapter(mFlowAdapter)
                    autoFlowLayout.setOnItemClickListener { position, view ->
                        onFlowItemClick?.invoke(position,list[position])
                    }
                }
            }

        }
    }

    var onFlowItemClick : ((position:Int,subscribeBean: SubscribeBean)->Unit)? = null
    /**
     * FlowLayout适配器
     */
    inner class MFlowAdapter(var datas: ArrayList<SubscribeBean>) : com.mooc.resource.widget.flowLayout.FlowAdapter<SubscribeBean>(datas) {
        //
        override fun getView(position: Int): View {
            val inflate = LayoutInflater.from(context).inflate(R.layout.column_item_subscribe_name, null)
            val tvName = inflate.findViewById<TextView>(R.id.tvName)
            val ivDelete = inflate.findViewById<ImageView>(R.id.ivDelete)
            val ivAdd = inflate.findViewById<ImageView>(R.id.ivAdd)
            val subscribeBean = datas[position]
            tvName.text = subscribeBean.title
            val deleteVisibal = if(subscribeBean.editMode) View.VISIBLE else View.GONE
            ivDelete.visibility = deleteVisibal
            val addVisibal = if(subscribeBean.subscribe) View.GONE else View.VISIBLE
            ivAdd.visibility = addVisibal
            return inflate
        }

    }
}