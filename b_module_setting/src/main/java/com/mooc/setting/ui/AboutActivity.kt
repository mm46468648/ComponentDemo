package com.mooc.setting.ui

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.setting.R
import com.mooc.setting.databinding.SetActivityAboutBinding
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths

/**
 * 关于我们
 */
@Route(path = Paths.PAGE_ABOUT)
class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentView = DataBindingUtil.setContentView<SetActivityAboutBinding>(this, R.layout.set_activity_about)
        contentView.commonTitleLayout.setOnLeftClickListener {
            finish()
        }
    }

}