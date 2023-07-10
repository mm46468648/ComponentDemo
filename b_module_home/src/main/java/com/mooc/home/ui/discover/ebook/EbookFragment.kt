package com.mooc.home.ui.discover.ebook

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mooc.home.ui.discover.BaseDiscoverFragment

/**
 *
 * @ProjectName:发现页电子书Fragment
 * @Package:
 * @ClassName:
 * @Description:    java类作用描述
 * @Author:         xym
 * @CreateDate:     2020/8/11 5:06 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/11 5:06 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class EbookFragment : BaseDiscoverFragment() {

    companion object{
        fun getInstance(bundle: Bundle? = null): EbookFragment {
            val fragment = EbookFragment()
            bundle?.apply {
                fragment.arguments = this
            }
            return fragment
        }
    }

    override fun getListFragment(): Fragment = EbookChildFragment.getInstance()
}