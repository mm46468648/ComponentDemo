package com.mooc.search.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.adapter.SearchShareBookAdapter
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.NormalConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.manager.BaseObserver
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.commonbusiness.model.search.SearchResultBean
import com.mooc.commonbusiness.net.ApiService
import com.mooc.search.R
import com.mooc.search.api.SearchApi
import com.mooc.search.databinding.SearchActivityInviteReadBookBinding
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.route.Paths
import org.json.JSONException

/**
 * 搜索分享的书籍页面
 */
@Route(path = Paths.PAGE_SEARCH_ADD_BOOK)
class SearchShareBookActivity : BaseActivity() {

    lateinit var activitySearchBookBinding: SearchActivityInviteReadBookBinding
    var boosList = ArrayList<EBookBean>()   //搜索出来的书籍集合

    //    var checkBooksId :ArrayList<Int>?= null
    var checkBooksList = arrayListOf<EBookBean>()   //已选中的列表的集合
    val searBookAdapter = SearchShareBookAdapter(boosList, 1, checkBooksList)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySearchBookBinding = DataBindingUtil.setContentView<SearchActivityInviteReadBookBinding>(this, R.layout.search_activity_invite_read_book)
        initView()
        initListener()
        initData()

    }

    fun initView() {
        //设置adapter，和点击加号事件
        activitySearchBookBinding.rvBook.layoutManager = LinearLayoutManager(this)
        activitySearchBookBinding.rvBook.adapter = searBookAdapter
        (activitySearchBookBinding.rvBook.getItemAnimator() as SimpleItemAnimator).setSupportsChangeAnimations(false)
        searBookAdapter.addChildClickViewIds(R.id.ivAdd)
        searBookAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.ivAdd) {
                //点击传递选中的
                val book = boosList.get(position)
                if (checkBooksList.size>0){
                    var already = false
                    for (i in 0 until checkBooksList.size) {
                        val alreadyBook:EBookBean  = checkBooksList.get(i)
                        if(alreadyBook.id==book.id){
                            already=true
                        }
                    }
                    if (!already){
                        if (checkBooksList.size >= 5) {
                            Toast.makeText(this, NormalConstants.SHARE_NUM_TIP2, Toast.LENGTH_SHORT).show()
                            return@setOnItemChildClickListener
                        }
                    }
                }

                onOItemClick(book)
                searBookAdapter.notifyItemChanged(position)
            }
        }
    }

    /**
     * 点击添加或者移除
     */
    private fun onOItemClick(book: EBookBean) {
        //遍历已经添加的书籍列表
        val iterator = checkBooksList.iterator()
        var already = false
        while (iterator.hasNext()) {
            val alreadyBook = iterator.next()
            if (alreadyBook.id == book.id) {
                already = true;
                iterator.remove()
            }
        }

        if (!already) {
            checkBooksList.add(book)
        }


    }

    fun initListener() {
        activitySearchBookBinding.ivBack.setOnClickListener { onBackPressed() }
        //监听editText改变，显示隐藏删除按钮
        activitySearchBookBinding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.length == 0) {
                    activitySearchBookBinding.ivDel.setVisibility(View.GONE)
                } else {
                    activitySearchBookBinding.ivDel.setVisibility(View.VISIBLE)
                }
            }
        })
        //监听系统按键点击搜索
        activitySearchBookBinding.etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val strSearchWord: String = activitySearchBookBinding.etSearch.getText().toString().trim({ it <= ' ' })
                if (!TextUtils.isEmpty(strSearchWord)) {
                    searchBook(strSearchWord)
                }
            }
            false
        })

        //点击清空editext,和搜索结果
        activitySearchBookBinding.ivDel.setOnClickListener {
            activitySearchBookBinding.etSearch.setText("")
            boosList.clear()
            searBookAdapter.notifyDataSetChanged()
        }
    }

    fun initData() {
        val intentCheckBooksList: ArrayList<EBookBean>? = intent.getParcelableArrayListExtra<EBookBean>(IntentParamsConstants.INTENT_BOOKS_PARAMS)
        val notEmpty = intentCheckBooksList?.isNotEmpty() == true
        if (intentCheckBooksList != null && notEmpty) {
            checkBooksList.clear()
            checkBooksList.addAll(intentCheckBooksList)
        }
    }

    var pageOffset = 1

    /**
     * 查询搜索的书名
     * 需要用模糊搜索
     */
    private fun searchBook(strSearchWord: String) {
        //模拟搜索三本书籍
        boosList.clear()


        val map = hashMapOf<String, String>()
        map.put("word", strSearchWord);
        map["search_match_type"] = "0"
        map["page_size"] = "30"
        map["page"] = pageOffset.toString()
        map["type"] = ResourceTypeConstans.TYPE_E_BOOK.toString()

        ApiService.getRetrofit().create(SearchApi::class.java).getResSearchObservabal(map)
                .compose(RxUtils.applySchedulers())
                .subscribe(object : BaseObserver<SearchResultBean>(this) {
                    override fun onSuccess(t: SearchResultBean) {
                        try {
                            val geteBookBeans = t.ebook?.items
                            if (geteBookBeans?.isNotEmpty() == true) {
                                boosList.addAll(geteBookBeans)
                                searBookAdapter.notifyDataSetChanged()
                            } else {
                                Toast.makeText(this@SearchShareBookActivity, "搜索结果为空", Toast.LENGTH_SHORT).show()
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                })
    }

    override fun onBackPressed() {
        //将选中列表返回
        if (checkBooksList.isNotEmpty()) {
            val intent = Intent()
            intent.putParcelableArrayListExtra(IntentParamsConstants.INTENT_BOOKS_PARAMS, checkBooksList)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else {
            super.onBackPressed()
        }
    }

}