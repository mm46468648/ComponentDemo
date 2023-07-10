package com.mooc.discover.column;

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.column.constans.DiscoverConstants
import com.mooc.column.ui.columnall.ColumnQuickFragment
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.ktextends.put
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
import com.mooc.resource.widget.CommonTitleLayout

/**
 * 查看快捷入口组合栏目页面
 */
@Route(path = Paths.PAGE_COLUMN_QUICK)
class ColumnQuickActivity : BaseActivity() {

    companion object {
        //发现
        const val QUICK_COLUMN_TITLE = "quick_column_title"
        const val QUICK_COLUMN_ID = "quick_column_id"
    }

    private val quickColumnTitle: String by extraDelegate(QUICK_COLUMN_TITLE, "")
    private val quickColumnId: String by extraDelegate(QUICK_COLUMN_ID, "")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_column_quick)

        val commonTitle = findViewById<CommonTitleLayout>(R.id.commonTitle)
        commonTitle?.middle_text = quickColumnTitle

        commonTitle?.setOnLeftClickListener { finish() }



        supportFragmentManager.beginTransaction().add(R.id.fragment, ColumnQuickFragment.getInstance(Bundle().put(DiscoverConstants.QUICK_ID, quickColumnId))).commit()

    }


}