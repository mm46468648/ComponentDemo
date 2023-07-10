package com.mooc.home.ui.pop

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.lxj.xpopup.core.DrawerPopupView
import com.mooc.common.ktextends.loge
import com.mooc.resource.ktextention.dp2px
import com.mooc.resource.widget.flowLayout.FlowAdapter
import com.mooc.home.HttpService
import com.mooc.home.R
import com.mooc.home.databinding.HomePopMoocPlatformCatelogNewBinding
import com.mooc.home.model.PlatformBean
import kotlinx.coroutines.*

/**
 * 课程平台抽屉弹窗
 *
 */
class CoursePlatformDrawerPop(var mContext: Context) : DrawerPopupView(mContext) {

    override fun getImplLayoutId(): Int {
        return R.layout.home_pop_mooc_platform_catelog_new
    }


    //协程任务
    var launch: Job? = null

    //选中集合
    var checkPlatformList = arrayListOf<PlatformBean>()

    /**
     * 点击确定回调
     * 如果仅有两个全部，则返回空集合
     */
    var onCheckConfirmCallback: ((checkList: ArrayList<PlatformBean>) -> Unit)? = null

    private lateinit var inflater: HomePopMoocPlatformCatelogNewBinding
    override fun onCreate() {
        super.onCreate()
        inflater = HomePopMoocPlatformCatelogNewBinding.bind(popupImplView)
        //获取课程平台数据
        launch = GlobalScope.launch {

            try {

                val courseCateResponse = HttpService.homeApi.getPlatFormCates().await()
                //获取加盟平台集合
                val unionPlatforms = courseCateResponse.platform?.union_platforms as ArrayList
                val nameArr = unionPlatforms.map { it.name } as ArrayList<String>


                //获取其他平台集合
                val otherPlatforms = courseCateResponse.platform?.other_platforms as ArrayList
                val otherPlatformsNameArr = otherPlatforms.map { it.name } as ArrayList<String>

                //绑定数据
                withContext(Dispatchers.Main) {
                    //设置加盟平台流式布局适配器
                    val mFlowAdapter = MFlowAdapter(nameArr)
                    inflater.flUnion.setAdapter(mFlowAdapter)

                    inflater.flUnion.setOnItemClickListener { position, view ->

                        //如果这是一个取消操作,
                        if (view.isSelected == false) {
                            //如果其他列表中没有选中的了,需要再设置为选中
                            if (inflater.flOther.checkedViews.isEmpty() && inflater.flUnion.checkedViews.isEmpty() && inflater.tvAllPlatForm.isSelected == false) {
                                view.isSelected = true
                                inflater.flUnion.checkedViews.add(view)
                            }
                            //其他直接返回
                            return@setOnItemClickListener
                        }
                        changeSelectAll(false)
                    }

                    //设置其他平台流式布局适配器
                    val motherPlatformsFlowAdapter = MFlowAdapter(otherPlatformsNameArr)
                    inflater.flOther.setAdapter(motherPlatformsFlowAdapter)
                    inflater.flOther.setOnItemClickListener { position, view ->

                        //如果这是一个取消操作,直接返回
                        if (view.isSelected == false) {
                            //如果其他列表中没有选中的了,需要再设置为选中
                            if (inflater.flUnion.checkedViews.isEmpty() && inflater.flOther.checkedViews.isEmpty() && inflater.tvAllPlatForm.isSelected == false) {
                                view.isSelected = true
                                inflater.flOther.checkedViews.add(view)
                            }
                            //其他直接返回
                            return@setOnItemClickListener
                        }

                        changeSelectAll(false)
                    }


                    //点击确认
                    inflater.tvConfirm.setOnClickListener {
                        checkPlatformList.clear()
                        //如果全部选中,则全部添加
                        if (inflater.tvAllPlatForm.isSelected) {
                            unionPlatforms.forEach { platform ->
                                checkPlatformList.add(platform)
                            }
                            otherPlatforms.forEach { platform ->
                                checkPlatformList.add(platform)
                            }

                            onCheckConfirmCallback?.invoke(checkPlatformList)
                            return@setOnClickListener

                        }

                        //取出选中的类型
                        inflater.flUnion.checkedViews.forEach {
                            val toString = (it as TextView).text.toString()

                            unionPlatforms.forEach { platform ->
                                //如果有且仅有一个全部，则需要全部添加
                                if (inflater.flUnion.checkedViews.size == 1 && toString == "全部") {
                                    if (platform.name != "全部") {
                                        checkPlatformList.add(platform)
                                    }
                                } else {
                                    if (platform.name != "全部" && platform.name == toString) {
                                        checkPlatformList.add(platform)
                                    }
                                }

                            }
                        }

                        inflater.flOther.checkedViews.forEach {
                            val toString = (it as TextView).text.toString()
                            otherPlatforms.forEach { platform ->
                                if (inflater.flOther.checkedViews.size == 1 && toString == "全部") {
                                    if (platform.name != "全部") {
                                        checkPlatformList.add(platform)
                                    }
                                } else {
                                    if (platform.name != "全部" && platform.name == toString) {
                                        checkPlatformList.add(platform)
                                    }
                                }
                            }
                        }


                        //取出选中平台
                        loge(checkPlatformList.toString())
                        //根据选中的位置，和datalist对比，获取出来对应位置的数据
                        onCheckConfirmCallback?.invoke(checkPlatformList)
                    }

                    //点击重置
                    inflater.tvReset.setOnClickListener {
                        changeSelectAll(true)
                    }

                    //点击全部,取消选中其他全部平台
                    inflater.tvAllPlatForm.setOnClickListener {
                        changeSelectAll(true)
                    }

                    //默认选中全部
                    inflater.tvAllPlatForm.isSelected = true
                }

            } catch (e: Exception) {

            }
        }
    }

    /**
     * 取消选中其他平台
     */
    fun cancleOtherPlatfor() {
        val otherIterator = inflater.flOther.checkedViews.iterator()
        while (otherIterator.hasNext()) {
            val textView = otherIterator.next() as TextView
            //如果点了全部，则取消其他
            textView.isSelected = false
            otherIterator.remove()
        }

        val unionIterator = inflater.flUnion.checkedViews.iterator()
        while (unionIterator.hasNext()) {
            val textView = unionIterator.next() as TextView
            textView.isSelected = false
            unionIterator.remove()
        }
    }

    /**
     * 改变选中全部平台
     * @param select 是否选中
     */
    fun changeSelectAll(select: Boolean) {
        val stateArr = arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf())
        val colorArr = intArrayOf(
            ContextCompat.getColor(mContext, R.color.colorPrimary),
            Color.parseColor("#989898")
        )
        val colorStateList = ColorStateList(stateArr, colorArr)
        inflater.tvAllPlatForm.setTextColor(colorStateList)
        inflater.tvAllPlatForm.isSelected = select

        if (select) {
            cancleOtherPlatfor()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        //取消协程任务
        launch?.cancel()
    }


    val textPadding = 10.dp2px() //text padding
    val textMargin = 10.dp2px() //text margin

    /**
     * FlowLayout适配器
     */
    inner class MFlowAdapter(var datas: ArrayList<String>) :
        com.mooc.resource.widget.flowLayout.FlowAdapter<String>(datas) {
        var stateArr = arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf())
        var colorArr = intArrayOf(
            ContextCompat.getColor(mContext, R.color.colorPrimary),
            Color.parseColor("#989898")
        )
        var colorStateList = ColorStateList(stateArr, colorArr)
        override fun getView(position: Int): View {
            val platformString = datas[position]

            //构建TextView
            val textView = TextView(mContext)
            textView.gravity = Gravity.CENTER
            textView.setSingleLine()
            textView.setPadding(textPadding, textPadding, textPadding, textPadding)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.leftMargin = textMargin
            layoutParams.topMargin = textMargin
            textView.layoutParams = layoutParams
            textView.setTextColor(colorStateList)
            textView.setBackgroundResource(R.drawable.home_selector_flowlayout_child)
            textView.text = platformString
            if (platformString == "全部") {
                textView.isSelected = true
            }
            return textView
        }

    }
}