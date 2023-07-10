package com.mooc.studyproject.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.mooc.commonbusiness.utils.HtmlUtils
import com.mooc.studyproject.R
//import kotlinx.android.synthetic.main.studyproject_fragment_da_shi_intro.*

/**
 * 大师课项目介绍
 */
class DaShiIntroRuleFragment : Fragment() {
    var desc: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            desc = it.getString("info_desc")
        }
    }

    var wvInfo : WebView? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wvInfo = view.findViewById(R.id.wvInfo)
        initView()
        initData()
    }


    private fun initView() {
        wvInfo?.let { HtmlUtils.setWebView(it, requireContext()) }
    }

    private fun initData() {
        if (!TextUtils.isEmpty(desc)) {
            desc?.let {
                wvInfo?.loadDataWithBaseURL("", HtmlUtils.getHtmlMargin25(it), "text/html", "utf-8", null)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.studyproject_fragment_da_shi_intro, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(str: String?) =
                DaShiIntroRuleFragment().apply {
                    arguments = Bundle().apply {
                        putString("info_desc", str)
                    }
                }
    }

}