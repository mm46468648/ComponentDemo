package com.mooc.discover.adapter

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.changeskin.SkinManager
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.getColorRes
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.decoration.GrideItemDecoration
import com.mooc.commonbusiness.manager.BaseObserver
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.manager.ResourceUtil
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
import com.mooc.discover.adapter.style.*
import com.mooc.discover.httpserver.HttpService
import com.mooc.discover.model.RecommendColumn
import com.mooc.resource.widget.NoIntercepteRecyclerView
import com.mooc.statistics.LogUtil

/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    发现页推荐适配器
 * @Author:         xym
 * @CreateDate:     2020/8/14 5:33 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/14 5:33 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class DiscoverRecommendColumnAdapter(var list: ArrayList<RecommendColumn>) :
    BaseQuickAdapter<RecommendColumn, BaseViewHolder>(R.layout.view_discover_common_item, list),
    LoadMoreModule {


    override fun convert(holder: BaseViewHolder, item: RecommendColumn) {
        holder.setText(R.id.title, item.title)
        holder.setText(R.id.sub_title, item.about)
        holder.setGone(R.id.sub_title, item.about?.isEmpty() == true)
        val recyclerView = holder.getView<NoIntercepteRecyclerView>(R.id.rcy)


        //为item中的list_tag赋值,方便展示
        val map = item.data?.map {
            it.tag = item.tag
            it
        }


        //////每个专栏Item中的 列表布局Start//////
        val layoutManager = when (item.tag) {
            0 -> GridLayoutManager(context, 2)    //tag2时 网络
            2 -> GridLayoutManager(context, 3)
            3, 4 -> LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)   //3，5横向
            else -> LinearLayoutManager(context)   //其他竖向布局
        }


        while (recyclerView.getItemDecorationCount() > 0) {
            recyclerView.removeItemDecorationAt(0);
        }


        val space = 15.dp2px()
        if(item.tag == 0 || item.tag == 2){
            recyclerView.setPadding(space,0,0,0)
            recyclerView.addItemDecoration(GrideItemDecoration(space))
        }else if(item.tag == 3 || item.tag == 4){
            recyclerView.setPadding(space,0,0,0)
        } else{
            recyclerView.setPadding(space,0, space,0)
        }
        //设置是否禁止父控件拦截
        recyclerView.switch = (item.tag == 3 || item.tag == 4)
        recyclerView.layoutManager = layoutManager
        var tagAdapter: BaseDelegateMultiAdapter<RecommendColumn, BaseViewHolder>? = null
        //适配器
        when (item.tag) {
            0 -> tagAdapter = HomeTagOneAdapter(map as ArrayList<RecommendColumn>)
            1 -> tagAdapter = HomeTagTwoAdapter(map as ArrayList<RecommendColumn>)
            2 -> tagAdapter = HomeTagThreeAdapter(map as ArrayList<RecommendColumn>)
            3 -> tagAdapter = HomeTagFourAdapter(map as ArrayList<RecommendColumn>)
            4 -> tagAdapter = HomeTagFiveAdapter(map as ArrayList<RecommendColumn>)
            5 -> tagAdapter = HomeTagSixAdapter(map as ArrayList<RecommendColumn>)
            6 -> tagAdapter = HomeTagSevenAdapter(map as ArrayList<RecommendColumn>)
            7 -> tagAdapter = HomeTagEightAdapter(map as ArrayList<RecommendColumn>)
            else -> tagAdapter = HomeTagOneAdapter(map as java.util.ArrayList<RecommendColumn>)
        }


        //点击跳转
        tagAdapter.setOnItemClickListener { adapter, view, position ->
            val recommendColumn = map[position]

            LogUtil.addClickLogNew(
                "${LogEventConstants2.P_DISCOVER}#${item.id}",
                recommendColumn._resourceId,
                "${recommendColumn._resourceType}",
                recommendColumn.title,
                "${LogEventConstants2.typeLogPointMap[recommendColumn.type]}#${recommendColumn._resourceId}"
            )

            turnToResrouseDetailPage(recommendColumn)
        }
        recyclerView.adapter = tagAdapter
        //////每个专栏Item中的 列表布局 END //////


        showOrder(holder, item)

        showMore(item.position_one, holder, item)

        handlePositonTwo(holder, item, item.position_two)

    }

    /**
     * 跳转到资源详情页面
     */
    private fun turnToResrouseDetailPage(recommendColumn: RecommendColumn) {
        ResourceTurnManager.turnToResourcePage(recommendColumn)

        //除了合集类型都调用
        if (recommendColumn._resourceType != ResourceTypeConstans.TYPE_SPECIAL || recommendColumn._resourceType != ResourceTypeConstans.TYPE_COLUMN) {
            ResourceUtil.updateResourceRead(recommendColumn.id)
        }

    }

    fun showOrder(holder: BaseViewHolder, contentBean: RecommendColumn) {
        if (!contentBean.title.equals("友情链接") && !contentBean.title.equals("学习项目") && !contentBean.title.equals(
                "专栏"
            )
        ) {
            holder.setGone(R.id.llAddStudy, false)
            val llAddStudy: LinearLayout = holder.getView(R.id.llAddStudy)
            if (contentBean.is_subscribe) {
                holder.setBackgroundResource(R.id.ivAddStudy, R.mipmap.iv_add_study_true)
                holder.setBackgroundResource(R.id.llAddStudy, R.drawable.bg_add_study_shape)
                holder.setTextColor(R.id.tvAddStudy, context.getColorRes(R.color.color_82))
            } else {
                holder.setBackgroundResource(R.id.ivAddStudy, R.mipmap.iv_add_study_false)
                holder.setTextColor(R.id.tvAddStudy, context.getColorRes(R.color.color_white))

                //换肤
                if(SkinManager.getInstance().needChangeSkin()){
                    val llAddStudyBg =
                        SkinManager.getInstance().resourceManager.getDrawableByName("shape_conrners1_5_color_primary")
                    llAddStudy.background = llAddStudyBg
                }else{
                    holder.setBackgroundResource(
                        R.id.llAddStudy,
                        R.drawable.shape_conrners1_5_color_primary
                    )
                }
            }
            llAddStudy.isEnabled = !contentBean.is_subscribe

            llAddStudy.setOnClickListener {
                if (!contentBean.is_subscribe) {
                    addSubscribe(contentBean.id)
                }
            }
        } else {
            holder.setGone(R.id.llAddStudy, true)
        }
    }

    private fun addSubscribe(id: String) {
        HttpService.discoverApi.postSubscribeReq(id).compose(RxUtils.applySchedulers())
            .subscribe(object : BaseObserver<HttpResponse<Any>>(context) {
                override fun onSuccess(httpResponse: HttpResponse<Any>?) {
                    if (httpResponse?.isSuccess == true) {
                        toast(context.getResources().getString(R.string.subscribe_str_success))

                        for (i in data.indices) {
                            if (id == data[i].id) {
                                data[i].is_subscribe = true
                                notifyDataSetChanged()
                                break
                            }
                        }
                    }
                }
            });
    }


    /**
     * 返回tag对应类型加载的个数
     */
    fun getTagPageSize(tag: Int): Int {
        return when (tag) {
            1 -> 1
            2, 3, 4 -> 6
            else -> 4
        }
    }


    /**
     * 设置右上角的查看全部或者换一换
     */
    fun showMore(position_one: Int, helper: BaseViewHolder, item: RecommendColumn) {
//        val pagesize = getTagPageSize(item.tag)
        val pagesize = 30
        val showMore: RelativeLayout = helper.getView(R.id.show_more)
        val showMoreText: TextView = helper.getView(R.id.show_more_text)
        val showMoreImg: ImageView = helper.getView(R.id.show_moreicon)

        when (position_one) {
            0 -> helper.setGone(R.id.show_more, true)     //0隐藏
            1 -> {                                                //1，显示换一换
                helper.setGone(R.id.show_more, false)
                showMoreImg.visibility = View.VISIBLE
                helper.setText(
                    R.id.show_more_text,
                    context.getResources().getString(R.string.text_switch)
                )
                helper.setTextColor(R.id.show_more_text, context.getColorRes(R.color.color_A5A5A5))




                //换肤
                if(SkinManager.getInstance().needChangeSkin()){
                    val icExchangeDrawable =
                        SkinManager.getInstance().resourceManager.getDrawableByName("ic_column_exchange")
                    showMoreImg.setImageDrawable(icExchangeDrawable)
                }else{
                    showMoreImg.setImageResource(R.mipmap.ic_column_exchange)
                }
                showMore.setOnClickListener {
                    showMoreImg.animate().rotation(360f).setDuration(500).start()

                    HttpService.discoverApi.getExchangeRecommendListWithId(item.id, pagesize, 1)
                        .compose(RxUtils.applySchedulers())
                        .subscribe(object : BaseObserver<RecommendColumn?>(context, false) {
                            override fun onSuccess(contentBean: RecommendColumn?) {
                                if (contentBean != null) {
                                    refreshItemListData(contentBean, helper.layoutPosition)
                                }
                                showMoreImg.clearAnimation()
                            }

                            override fun onFailure(code: Int, message: String?) {
                                super.onFailure(code, message)
                                showMoreImg.clearAnimation()
                            }
                        })
                }
            }
            2 -> {
                showMore.visibility = View.VISIBLE
                showMoreImg.visibility = View.GONE
                showMoreText.text = context.resources.getString(R.string.text_feed_all)

                //换肤
                val showMoreColor =
                    if(SkinManager.getInstance().needChangeSkin()){
                        SkinManager.getInstance().resourceManager.getColor("colorPrimary")
                    }else{
                        context.getColorRes(R.color.colorPrimary)
                    }
                showMoreText.setTextColor(showMoreColor)
                showMore.setOnClickListener {

                    LogUtil.addClickLogNew(
                        LogEventConstants2.P_DISCOVER, "${item._resourceId}",
                        LogEventConstants2.ET_ALL, item.title
                    )

                    ARouter.getInstance().build(Paths.PAGE_RECOMMEND_SPECIAL)
                        .withString(IntentParamsConstants.PARAMS_RESOURCE_ID, item.id)
                        .navigation();
                }


            }
            else -> showMore.visibility = View.GONE
        }


    }

    private fun refreshItemListData(contentBean: RecommendColumn, position: Int) {
        val list: List<RecommendColumn>? = contentBean.data
        if (list?.isNotEmpty() == true && position != -1 && position < data.size) {
            data[position].data = list
            notifyItemChanged(position)
        }
    }

    /**
     * 处理底部查看全部或者
     * 换一换
     */
    private fun handlePositonTwo(
        helper: BaseViewHolder,
        contentBean: RecommendColumn,
        positionTwo: Int
    ) {
//        val pageSize: Int = getTagPageSize(contentBean.tag)
        val pageSize: Int = 30
        when (positionTwo) {
            0 -> {
                helper.setGone(R.id.show_more_bottom, true)
                helper.setGone(R.id.rl_exchange, true)
            }
            1 -> {
                helper.setGone(R.id.show_more_bottom, true)
                helper.setGone(R.id.rl_exchange, false)
                val view = helper.getView<RelativeLayout>(R.id.rl_exchange)
                val changeImageView = view.findViewById<ImageView>(R.id.icon)

                //换肤
                if(SkinManager.getInstance().needChangeSkin()){
                    val icExchangeDrawable =
                        SkinManager.getInstance().resourceManager.getDrawableByName("ic_column_exchange")
                    changeImageView.setImageDrawable(icExchangeDrawable)
                }else{
                    changeImageView.setImageResource(R.mipmap.ic_column_exchange)
                }
                view.setOnClickListener {
                    changeImageView.animate().rotation(360f).setDuration(500).start()
                    HttpService.discoverApi.getExchangeRecommendListWithId(contentBean.id, pageSize)
                        .compose(RxUtils.applySchedulers())
                        .subscribe(object : BaseObserver<RecommendColumn>(context) {
                            override fun onSuccess(contentBean: RecommendColumn?) {
                                if (contentBean != null) {
                                    refreshItemListData(contentBean, helper.layoutPosition)
                                }
                                changeImageView.clearAnimation()
                            }


                            override fun onFailure(code: Int, message: String?) {
                                super.onFailure(code, message)
                                changeImageView.clearAnimation()

                            }
                        });
                }
            }
            2 -> {
                helper.setGone(R.id.show_more_bottom, false)
                helper.setGone(R.id.rl_exchange, true)
                helper.setText(
                    R.id.show_more_bottom,
                    context.getResources().getString(R.string.text_show_all)
                )
                helper.getView<TextView>(R.id.show_more_bottom).setOnClickListener {

                    LogUtil.addClickLogNew(
                        LogEventConstants2.P_DISCOVER, "${contentBean._resourceId}",
                        LogEventConstants2.ET_ALL, contentBean.title
                    )

                    ARouter.getInstance().build(Paths.PAGE_RECOMMEND_SPECIAL)
                        .withString(IntentParamsConstants.PARAMS_RESOURCE_ID, contentBean.id)
                        .navigation();
                }

            }
        }
    }

}