package com.mooc.discover.column;

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.discover.R
import com.mooc.column.ui.columnall.ColumnAllFragment
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.resource.widget.CommonTitleLayout

//import kotlinx.android.synthetic.main.activity_column_all.*

/**
 * 查看全部专栏页面
 */
@Route(path = Paths.PAGE_COLUMN_ALL)
class ColumnAllActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_column_all)
        val commonTitle = findViewById<CommonTitleLayout>(R.id.commonTitle)
        commonTitle?.middle_text = getString(R.string.all_column)

        commonTitle?.setOnLeftClickListener { finish() }



        supportFragmentManager.beginTransaction().add(R.id.fragment, ColumnAllFragment()).commit()

    }


}