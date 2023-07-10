package com.mooc.studyproject.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.common.ktextends.extraDelegate
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.model.studyproject.StudyDynamic
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyproject.R
import com.mooc.studyproject.databinding.StudyprojectActivityNominationBinding
import com.mooc.studyproject.viewmodel.SelfRecommendViewModel
//import kotlinx.android.synthetic.main.studyproject_activity_nomination.*
import org.greenrobot.eventbus.EventBus

@Route(path = Paths.PAGE_NOMINATION)
class NominationActivity : BaseActivity() {
    companion object {
        //动态id
        const val ACTIVITY_ID = "activity_id"
    }

    private val activityId: String by extraDelegate(ACTIVITY_ID, "")
    private var studyDynamic: StudyDynamic? = null

    val model: SelfRecommendViewModel by viewModels()
    lateinit var inflater: StudyprojectActivityNominationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = StudyprojectActivityNominationBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        initView()
        initData()
        initListener()
    }

    fun initView() {
        inflater.commonTitleSelf.right_text = resources.getString(R.string.hint_str_self_recommend_submit)
        updateSubmit(0)
    }

    private fun updateSubmit(length: Int) {
        var limit = 50
        if (length >= 50) {
            inflater.tvSelfRecommendLimit.visibility = View.GONE
            @Suppress("DEPRECATION")
            inflater.commonTitleSelf.tv_right?.setTextColor(resources.getColor(R.color.color_10955B))
            inflater.commonTitleSelf.tv_right?.isEnabled = true
        } else {
            inflater.tvSelfRecommendLimit.visibility = View.VISIBLE
            limit -= length
            @Suppress("DEPRECATION")
            inflater.commonTitleSelf.tv_right?.setTextColor(resources.getColor(R.color.color_BC))
            inflater.commonTitleSelf.tv_right?.isEnabled = false
        }
        inflater.tvSelfRecommendLimit.text = String.format(resources.getString(R.string.hint_str_self_recommend_limit), limit)

    }

    fun initData() {
        model.postSelfResult.observe(this, Observer {
            if (it != null) {
                if (it.code == 200) {
                    studyDynamic = it.data
                    onBackPressed()
                }
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    fun initListener() {
        inflater.commonTitleSelf?.setOnLeftClickListener {
            hideKeyboard(inflater.commonTitleSelf.tv_right!!)
            onBackPressed()
        }
        inflater.etSelfRecommend.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                updateSubmit(s.length)
            }
        })
        inflater.etSelfRecommend.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            if (event.action == MotionEvent.ACTION_UP) {
                v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
        inflater.commonTitleSelf.tv_right?.setOnClickListener {
            hideKeyboard(inflater.commonTitleSelf.tv_right!!)
            model.activityId = activityId
            model.nominationReason = inflater.etSelfRecommend.text.toString()
            model.postSelfRecommend()
        }
    }

    override fun onBackPressed() {
        if (studyDynamic != null) {
            EventBus.getDefault().post(studyDynamic)
        }
        super.onBackPressed()
    }

    /**
     * 隐藏软键盘
     */
    private fun hideKeyboard(view: View) {
        val imm = view.context
                .getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}