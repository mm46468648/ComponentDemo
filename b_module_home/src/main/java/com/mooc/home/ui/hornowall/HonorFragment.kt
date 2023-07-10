package com.mooc.home.ui.hornowall

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.mooc.commonbusiness.constants.LiveDataBusEventConstants
import com.mooc.statistics.LogUtil
import com.mooc.common.bus.LiveDataBus
import com.mooc.home.R
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.home.databinding.HomeFragmentHomeHonorBinding
//import kotlinx.android.synthetic.main.home_fragment_home_honor.*

class HonorFragment : Fragment() {


//    val ivLogoResArr = arrayOf( R.mipmap.home_ic_honor_platform,R.mipmap.home_ic_honor_medal, R.mipmap.home_ic_honor_trophy)
    val ivLogoResArr = arrayOf( R.mipmap.home_ic_honor_platform, R.mipmap.home_ic_honor_trophy)
//    val defaultTabDatas = arrayOf("学习项目达人","平台贡献榜","活动排行榜")
    val defaultTabDatas = arrayOf("学习项目达人","活动排行榜")

    private var _binding : HomeFragmentHomeHonorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = HomeFragmentHomeHonorBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        LogUtil.addLoadLog(LogPageConstants.PID_CATEGORY)
        binding.viewPage2.adapter = HonorWallFragmentAdapter(this)
        binding.simpleTabLayout.setTabStrs(defaultTabDatas)
        binding.simpleTabLayout.setViewPager2(binding.viewPage2)
        binding.viewPage2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position in ivLogoResArr.indices)
                    binding.ivlogo.setImageResource(ivLogoResArr[position])

                LogUtil.addClickLogNew(LogEventConstants2.P_HONOUR, "${position+1}", LogEventConstants2.ET_TAB, defaultTabDatas[position])
            }
        })


        //通过intent切换子tab
        LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_CHILDE_FRAGMENT_SELECTPOSITION).observerSticky(this, Observer<Pair<Int, Int>>  {
            if(it.first == 3){ //需要切换到推荐
               if(binding.viewPage2.childCount > it.second){
                   binding.viewPage2.currentItem = it.second
               }
            }
        }, true)
    }
}