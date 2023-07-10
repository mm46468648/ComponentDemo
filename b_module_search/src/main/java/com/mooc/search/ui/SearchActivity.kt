package com.mooc.search.ui

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.dialog.CustomProgressDialog
import com.mooc.commonbusiness.model.search.SearchResultBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.sp.SpSearchUtils
import com.mooc.resource.utils.StatusBarUtil
import com.mooc.search.R
import com.mooc.search.adapter.SearchResultAdapter
import com.mooc.search.model.SearchItemBean
import com.mooc.search.model.SearchPopData
import com.mooc.search.utils.SearchConstants
import com.mooc.search.viewmodel.SearchViewModel
import com.mooc.search.widget.DeleteHistoryPop
import com.mooc.search.widget.SearchPrecisionPop
import com.mooc.search.widget.SearchWindow
import com.mooc.statistics.LogUtil
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.search.databinding.ActivitySearchBinding
//import kotlinx.android.synthetic.main.activity_search.*
import kotlin.collections.ArrayList

/**
 *新建了关于 搜索的 module
 * @author limeng
 * @date 2020/8/6
 */
//@Route(path = Paths.PAGE_SEARCH)
class SearchActivity : BaseActivity(), TextWatcher, TextView.OnEditorActionListener, SearchWindow.onDateChangeListener {
    private val exactSearch: Int = 1
    private val fuzzySearch: Int = 0
    var mSearchPop: SearchPrecisionPop? = null
    private var isVague: Int = exactSearch// 1是精准搜索  0是模糊搜索
    private var mAdapter: SearchResultAdapter? = null
    private val SEARCHPAGE: String = "2"//每组几个list
    var mSearchItemBeans: ArrayList<SearchItemBean>? = ArrayList()
    val popDataList = ArrayList<SearchPopData>()
    private val historyList: ArrayList<String> = ArrayList()

    protected var dialog: CustomProgressDialog? = null
    var mSearchPopWindow: SearchWindow? = null

    private lateinit var inflater: ActivitySearchBinding

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        intView();
        intData();
        intListener();
    }

    val searchViewModel: SearchViewModel by lazy {
        ViewModelProviders.of(this)[SearchViewModel::class.java]
    }

    /**
     * 初始化内容
     */
    private fun intData() {
        dialog = CustomProgressDialog.createLoadingDialog(this, true)
        mSearchPopWindow = SearchWindow(this, inflater.clClassChose)
        mSearchPop = SearchPrecisionPop(this)
        mAdapter = SearchResultAdapter(null)

        inflater.recyclerview.layoutManager = LinearLayoutManager(this)
        inflater.recyclerview.adapter = mAdapter
    }

    private fun intView() {
        inflater.clRoot.post {   //沉浸式状态栏高度
            inflater.clRoot.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0)
        }
        initSearchHistoryData()

    }


    /**
     * 所有的监听事件
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun intListener() {
        inflater.tvSearch.setOnClickListener {
            val searchKey = inflater.etSearch.getText().toString().trim()
            if (!searchKey.isBlank()) {
                getSearchData(searchKey)
            }
        }
        mSearchPop?.feedPopClickListener = {//1 精准搜索/ 0 模糊搜索
            inflater.ivDown.setImageDrawable(resources.getDrawable(R.mipmap.search_ic_tria_down))
            inflater.ivSearrchType.setImageDrawable(resources.getDrawable(R.mipmap.search_ic_tria_down))
            if (it == exactSearch) {
                inflater.tvSearchType.setText(getString(R.string.search_text_exact))
                inflater.tvSearchTypeOne.setText(getString(R.string.search_text_exact))
                isVague = exactSearch
            } else if (it == fuzzySearch) {
                inflater.tvSearchType.setText(getString(R.string.search_text_fuzzy))
                inflater.tvSearchTypeOne.setText(getString(R.string.search_text_fuzzy))
                isVague = fuzzySearch

            }
        }
        inflater.etSearch.addTextChangedListener(this)
        inflater.etSearch.setOnEditorActionListener(this)
        inflater.tvSearchType.setOnClickListener { v ->
            inflater.ivDown.setImageDrawable(resources.getDrawable(R.mipmap.search_ic_tria_up))
            inflater.ivSearrchType.setImageDrawable(resources.getDrawable(R.mipmap.search_ic_tria_up))
            mSearchPop?.show(inflater.tvSearchType)
        }
       inflater.tvSearchTypeOne.setOnClickListener { v ->
           inflater.ivDown.setImageDrawable(resources.getDrawable(R.mipmap.search_ic_tria_up))
           inflater.ivSearrchType.setImageDrawable(resources.getDrawable(R.mipmap.search_ic_tria_up))
            mSearchPop?.show(inflater.tvSearchTypeOne)
        }

        inflater.ivBack.setOnClickListener { finish() }
        inflater.clClassChose.setOnClickListener { v ->
            inflater.ivXiabian.setImageDrawable(resources.getDrawable(R.mipmap.search_ic_tria_up))
            mSearchPopWindow?.setList(popDataList)
        }
        inflater.ivDelete.setOnClickListener { v ->
            if (historyList.size == 0) {
                toast("没有可删除记录")
            } else {
                showDeleteDistoryPop()
            }
        }
        inflater.ivEtDelete.setOnClickListener { v -> inflater.etSearch.setText("") }
        mSearchPopWindow?.setOnDateChangeListener(this)
        mSearchPopWindow?.dimissListener = {
            inflater.ivXiabian.setImageDrawable(resources.getDrawable(R.mipmap.search_ic_tria_down))

        }
        searchViewModel.getError().observe(this, Observer {
            if (dialog?.isShowing == true) {
                dialog?.dismiss()
            }
        })
        searchViewModel.searchData.observe(this, Observer<SearchResultBean> {
            setData(it)
            if (dialog?.isShowing == true) {
                dialog?.dismiss()
            }
        })
    }

    private fun showDeleteDistoryPop() {
        val courseChoosePop = DeleteHistoryPop(this)
        courseChoosePop.onConfirm = {
            historyList.clear() //清空集合
            SpSearchUtils.getInstance().removeHistory() //清空sp
            inflater.historyLayout.removeAllTags()
        }
        XPopup.Builder(this)
                .asCustom(courseChoosePop)
                .show()
    }

    /**
     * 监听键盘改变 显示隐藏布局
     */
    override fun afterTextChanged(s: Editable?) {
        if (s?.length == 0) {
            inflater.tvSearch.setBackgroundResource(R.drawable.shape_radius15_color_c4)
            inflater.ivEtDelete.setVisibility(View.GONE)

            inflater.recyMode.setVisibility(View.GONE)
            inflater.shadow.setVisibility(View.GONE)
            inflater.clHistory.setVisibility(View.VISIBLE)
            inflater.clSearchType.setVisibility(View.VISIBLE)
            inflater.recyclerview.setVisibility(View.GONE)
            inflater.empty.root.setVisibility(View.GONE)

        } else {
            inflater.tvSearch.setBackgroundResource(R.drawable.shape_radius15_color_primary)
            inflater.ivEtDelete.setVisibility(View.VISIBLE)
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//        iv_et_delete.setVisibility(View.GONE)

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    /**
     *键盘搜索点击事件
     */
    override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        if (p1 == EditorInfo.IME_ACTION_SEARCH) {
            val searchKey: String = inflater.etSearch.getText().toString().trim()
//                LogBeanUtil.getInstance().addSearchEvenLog(pageID.toString() + "#" + strSearchWord, ElementClass.EID_TO_SEARCH, ElementClass.BID_FORM, filter, pageID, ElementClass.PID_SEARCH.toString() + "#" + strSearchWord, true)
            if (!searchKey.isBlank()) {
                getSearchData(searchKey)
            }
        }
        return false
    }

    /**
     * 获取搜索数据
     */
    fun getSearchData(searchKey: String) {
        inflater.ivEtDelete.setVisibility(View.VISIBLE)
        inflater.recyMode.setVisibility(View.VISIBLE)
        inflater.shadow.setVisibility(View.VISIBLE)
        inflater.clHistory.setVisibility(View.GONE)
        inflater.clSearchType.setVisibility(View.GONE)
        inflater.recyclerview.setVisibility(View.VISIBLE)

        if (dialog?.isShowing == false) {
            dialog?.show()
        }
        // 存储到本地
        SpSearchUtils.getInstance().saveSearchHistory(searchKey)

//        insertSearchHistoryDB(searchKey)
        initSearchHistoryData()
        mAdapter?.keyword = searchKey
        mAdapter?.isVague = isVague.toString()
        val map = mapOf("word" to searchKey, "search_match_type" to isVague.toString(), "page_size" to SEARCHPAGE)

        val logName = if (isVague == 1) "精确" else "模糊"
        LogUtil.addClickLogNew(LogEventConstants2.P_SEARCH, searchKey, LogEventConstants2.ET_ICON, logName)

        searchViewModel.loadData(map);
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     * 设置数据组
     */
    private fun setData(lunchResponse: SearchResultBean) {
        popDataList.clear()
        mSearchItemBeans?.clear()
        popDataList.add(SearchPopData(SearchConstants.TYPE_HEADER, getString(R.string.all), 0, true))

        if (lunchResponse.micro_course != null && !lunchResponse.micro_course?.items?.isEmpty()!!) {
            mSearchItemBeans?.add(SearchItemBean(ResourceTypeConstans.TYPE_MICRO_LESSON))
            popDataList.add(SearchPopData(ResourceTypeConstans.TYPE_MICRO_LESSON, getString(R.string.micro_course), lunchResponse.micro_course?.count?:0, false));
        }

        if (lunchResponse.course != null && !lunchResponse.course?.items?.isEmpty()!!) {
            mSearchItemBeans?.add(SearchItemBean(ResourceTypeConstans.TYPE_COURSE))
            popDataList.add(SearchPopData(ResourceTypeConstans.TYPE_COURSE, getString(R.string.course), lunchResponse.course?.count?:0, false));
        }

        if (lunchResponse.folder != null && !lunchResponse.folder?.items?.isEmpty()!!) {
            mSearchItemBeans?.add(SearchItemBean(ResourceTypeConstans.TYPE_SOURCE_FOLDER))
            popDataList.add(SearchPopData(ResourceTypeConstans.TYPE_SOURCE_FOLDER, getString(R.string.study_plan_list), lunchResponse.folder?.count?:0, false));
        }

        if (lunchResponse.study_plan != null && !lunchResponse.study_plan?.items?.isEmpty()!!) {
            mSearchItemBeans?.add(SearchItemBean(ResourceTypeConstans.TYPE_STUDY_PLAN))
            popDataList.add(SearchPopData(ResourceTypeConstans.TYPE_STUDY_PLAN, getString(R.string.study_plan), lunchResponse.study_plan?.count?:0, false));
        }

        if (lunchResponse.ebook != null && !lunchResponse.ebook?.items?.isEmpty()!!) {
            mSearchItemBeans?.add(SearchItemBean(ResourceTypeConstans.TYPE_E_BOOK))
            popDataList.add(SearchPopData(ResourceTypeConstans.TYPE_E_BOOK, getString(R.string.ebook), lunchResponse.ebook?.count?:0, false));
        }

        if (lunchResponse.album != null && !lunchResponse.album?.items?.isEmpty()!!) {
            mSearchItemBeans?.add(SearchItemBean(ResourceTypeConstans.TYPE_ALBUM))
            popDataList.add(SearchPopData(ResourceTypeConstans.TYPE_ALBUM, getString(R.string.album), lunchResponse.album?.count?:0, false));

        }

        if (lunchResponse.track != null && !lunchResponse.track?.items?.isEmpty()!!) {
            mSearchItemBeans?.add(SearchItemBean(ResourceTypeConstans.TYPE_TRACK))
            popDataList.add(SearchPopData(ResourceTypeConstans.TYPE_TRACK, getString(R.string.track), lunchResponse.track?.count?:0, false));
        }

        if (lunchResponse.article != null && !lunchResponse.article?.items?.isEmpty()!!) {
            mSearchItemBeans?.add(SearchItemBean(ResourceTypeConstans.TYPE_ARTICLE))
            popDataList.add(SearchPopData(ResourceTypeConstans.TYPE_ARTICLE, getString(R.string.article), lunchResponse.article?.count?:0, false));
        }

        if (lunchResponse.chaoxing != null && !lunchResponse.chaoxing?.items?.isEmpty()!!) {
            mSearchItemBeans?.add(SearchItemBean(ResourceTypeConstans.TYPE_PERIODICAL))
            popDataList.add(SearchPopData(ResourceTypeConstans.TYPE_PERIODICAL, getString(R.string.periodical), lunchResponse.chaoxing?.count?:0, false));
        }

        if (lunchResponse.baike != null && !lunchResponse.baike?.items?.isEmpty()!!) {
            mSearchItemBeans?.add(SearchItemBean(ResourceTypeConstans.TYPE_BAIKE))
            popDataList.add(SearchPopData(ResourceTypeConstans.TYPE_BAIKE, getString(R.string.baike), lunchResponse.baike?.count?:0, false));
        }

        if (lunchResponse.tuling != null && !lunchResponse.tuling?.items?.isEmpty()!!) {
            mSearchItemBeans?.add(SearchItemBean(ResourceTypeConstans.TYPE_TU_LING))
            popDataList.add(SearchPopData(ResourceTypeConstans.TYPE_TU_LING, getString(R.string.tuling), lunchResponse.tuling?.count?:0, false));
        }
        if (lunchResponse.kanwu != null && !lunchResponse.kanwu?.items?.isEmpty()!!) {
            mSearchItemBeans?.add(SearchItemBean(ResourceTypeConstans.TYPE_PUBLICATION))
            popDataList.add(SearchPopData(ResourceTypeConstans.TYPE_PUBLICATION, getString(R.string.publicaton), lunchResponse.kanwu?.count?:0, false));
        }

        mAdapter?.bean = lunchResponse
        if (mSearchItemBeans?.size == 0) {
            inflater.recyclerview.visibility = View.GONE
            inflater.empty.root.visibility = View.VISIBLE
        } else {
            //搜索有值增加尾部默认布局
            mSearchItemBeans?.add(SearchItemBean(ResourceTypeConstans.TYPE_DEFAULT))
            inflater.recyclerview.visibility = View.VISIBLE
            inflater.empty.root.visibility = View.GONE
        }
        mSearchItemBeans?.let {
            mAdapter?.setNewInstance(it)
            mAdapter?.notifyDataSetChanged()
        }
    }


    /**
     * 初始化历史记录数据
     */
    private fun initSearchHistoryData() {
        inflater.historyLayout.removeAllTags()
        historyList.clear()
        val longHistory: String = SpSearchUtils.getInstance().searchHistory
        val tmpHistory = longHistory.split(",".toRegex()).toTypedArray() //split后长度为1有一个空串对象
        val strings = ArrayList(listOf(*tmpHistory))
        historyList.addAll(strings)
//        if (historyList.size == 1 && historyList.get(0) == "") {          //如果没有搜索记录，split之后第0位是个空串的情况下
//            historyList.clear() //清空集合，这个很关键
//        }
        historyList.remove("")
        historyList.let {
            for (bean in it) {

                inflater.historyLayout.addTag(bean)
            }
        }

        inflater.historyLayout.setOnTagClickListener { position, text ->
            inflater.etSearch.setText(text)
            inflater.etSearch.setSelection(text.length)
            getSearchData(text)
        }
    }

    override fun onBackPressed() {
        if (mSearchPopWindow?.mPopup?.isShowing == true) {
            mSearchPopWindow?.onDismiss()
        } else {
            super.onBackPressed()
        }
    }

    /**
     * 点击筛选的回调监听重新设置首页数据显示
     */
    override fun onDataChange(list: MutableList<SearchPopData>) {
        val newArrayList = ArrayList<SearchItemBean>()
        for (bean in list) {
            if (bean.isChecked) {
                if (bean.type == SearchConstants.TYPE_HEADER) {//显示全部
                    mAdapter?.setNewInstance(mSearchItemBeans)
                } else {
                    newArrayList.add(SearchItemBean(bean.type))
                    newArrayList.add(SearchItemBean(ResourceTypeConstans.TYPE_DEFAULT))
                    mAdapter?.setNewInstance(newArrayList)

                }
            }

        }


    }


}
