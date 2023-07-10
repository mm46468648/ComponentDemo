package com.mooc.search.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mooc.common.ktextends.dp2px
import com.mooc.search.R
import com.mooc.search.adapter.SearchPopAdapter
import com.mooc.search.model.SearchPopData

/**

 * @Author limeng
 * @Date 2020/8/19-11:08 AM
 */
class SearchWindow(val mContext: Context, var parent: View) : PopupWindow.OnDismissListener {
    var mPopup: PopupWindow? = null
    var container: LinearLayout? = null
    private var mSearchPopAdapter: SearchPopAdapter? = null
    private var mRecycleView: RecyclerView? = null
    private var view_gray: View? = null
    private var mOnDateChangeListener: SearchWindow.onDateChangeListener? = null
    var mPopDataList: MutableList<SearchPopData> = ArrayList()
    var space1: View? = null
    var dimissListener: (()->Unit)?= null

    fun setList(list: MutableList<SearchPopData>) {
        mPopDataList.clear()
        this.mPopDataList.addAll(list)
        mSearchPopAdapter?.setNewData(mPopDataList)
        mSearchPopAdapter?.notifyDataSetChanged()
        show()
    }

    /**
     * 设置监听
     */
    fun setOnDateChangeListener(onDateChangeListener: SearchWindow.onDateChangeListener?) {
        mOnDateChangeListener = onDateChangeListener
    }

    /**
     * 初始化
     */
    init {
        initView()
        initData()
        initListener()
        initPopup()
    }

    /**
     * 初始化
     */
    private fun initPopup() {
        mPopup = PopupWindow(container, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false)
        mPopup?.contentView = container
        //	mPopup.setBackgroundDrawable(new ColorDrawable(0x66000000));
        mPopup?.isOutsideTouchable = true
        mPopup?.setOnDismissListener(this)
    }

    private fun initListener() {
        space1?.setOnClickListener { v ->
            dimissListener?.invoke()
            Dismiss()
        }
        view_gray?.setOnClickListener { v ->
            dimissListener?.invoke()
            Dismiss()
        }

    }

    @SuppressLint("InflateParams")
    private fun initView() {
        container = LayoutInflater.from(mContext).inflate(R.layout.search_item_pop, null) as LinearLayout
        mRecycleView = container?.findViewById(R.id.recycleView)
        space1 = container?.findViewById(R.id.space1)
        view_gray = container?.findViewById(R.id.view_gray)

    }

    private fun initData() {
        mSearchPopAdapter = SearchPopAdapter(mPopDataList)
        mRecycleView?.layoutManager = LinearLayoutManager(mContext)
        mRecycleView?.adapter = mSearchPopAdapter
        mSearchPopAdapter?.notifyDataSetChanged()
        mSearchPopAdapter?.setOnItemClickListener { adapter, view, position ->
//            if (true == mPopDataList[position].isChecked) {
//               return@setOnItemClickListener
//            }
             var checkBean= mPopDataList[position]
            var count=0
            for (searchPopData in mPopDataList) {
                if (searchPopData.isChecked) {
                    count+=1

                }
            }
            if (count==1) {
                for (searchPopData in mPopDataList) {
                    if (searchPopData.isChecked) {
                        if (searchPopData.title == checkBean.title) {
                            return@setOnItemClickListener
                        }
                    }
                }

            }
            if (position == 0) {
                for (i in mPopDataList.indices) {
                    mPopDataList[i].isChecked = false
                }
                mPopDataList[0].isChecked = true

            } else {
                mPopDataList[0].isChecked = false
                mPopDataList[position].isChecked = !mPopDataList[position].isChecked
            }
            adapter.notifyDataSetChanged()
        }
    }

    interface onDateChangeListener {
        fun onDataChange(list: MutableList<SearchPopData>)
    }

    override fun onDismiss() {
        mOnDateChangeListener?.onDataChange(mPopDataList)
        if (mPopup?.isShowing == true) {
            mPopup?.dismiss()

        }

    }

    fun show() {
        if (mPopup == null) {
            initPopup()
        }
//        container?.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
//        val location = IntArray(2)
//        val a = location[0]
//        var b = location[1]
//        parent?.getLocationOnScreen(location)
//        //        setBackgroundAlpha(0.5f);
//        //在控件下方显示
//        if (b == 0) {
//            val rect = Rect()
//            parent?.getGlobalVisibleRect(rect)
//            b = rect.top + 20.dp2px()
//        }
//        setMartTop(b)
//        //在控件下方显示
//        mPopup?.showAsDropDown(parent, 0, 0)
//



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val location = IntArray(2)
            val a = location[0]
            var b = location[1]
            parent.getLocationOnScreen(location)
            //        setBackgroundAlpha(0.5f);
            //在控件下方显示
            if (b == 0) {
                val rect = Rect()
                parent.getGlobalVisibleRect(rect)
                b = rect.top + 10.dp2px()
            }
            setMartTop(b)
            //在控件下方显示
            mPopup?.showAsDropDown(parent, 0, 0)
        } else {
            space1?.visibility = View.GONE
            mPopup?.showAsDropDown(parent, 0, 0)
        }



    }

    fun setMartTop(height: Int) {
        val params: ViewGroup.LayoutParams = space1!!.getLayoutParams()
        params.height = height
        space1?.setLayoutParams(params)
    }

    fun Dismiss() {
        mPopup?.dismiss()
    }
}