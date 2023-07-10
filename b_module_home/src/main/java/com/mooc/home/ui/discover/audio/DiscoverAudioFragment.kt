package com.mooc.home.ui.discover.audio

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mooc.home.ui.discover.BaseDiscoverFragment

/**
 *
 * @ProjectName:发现页音频Fragment
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
class DiscoverAudioFragment : BaseDiscoverFragment() {

    companion object{
        fun getInstance(bundle: Bundle? = null): DiscoverAudioFragment {
            val fragment = DiscoverAudioFragment()
            bundle?.apply {
                fragment.arguments = this
            }
            return fragment
        }
    }

    override fun getListFragment(): Fragment?  = DiscoverAudioChildFragment.getInstance()

}