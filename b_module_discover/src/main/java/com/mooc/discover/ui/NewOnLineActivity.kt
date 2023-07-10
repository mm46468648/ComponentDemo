package com.mooc.discover.ui

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.discover.R
import com.mooc.discover.fragment.NewLineFragment
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.resource.widget.CommonTitleLayout

/**
 *最新上线
 * @author limeng
 * @date 2020/11/16
 */
@Route(path = Paths.PAGE_NEW_ONLINE)
class NewOnLineActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_on_line)

        val commonTitle = findViewById<CommonTitleLayout>(R.id.commonTitle)
        commonTitle?.middle_text = getString(R.string.newline)

        commonTitle?.setOnLeftClickListener { finish() }



        supportFragmentManager.beginTransaction().add(R.id.fragment, NewLineFragment()).commit()
    }
}