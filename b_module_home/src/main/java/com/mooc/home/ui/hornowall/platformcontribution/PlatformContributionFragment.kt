package com.mooc.home.ui.hornowall.platformcontribution

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.base.BaseFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.home.R
import com.mooc.home.databinding.HomeFragmentPlatformContributionBinding
//import kotlinx.android.synthetic.main.home_fragment_platform_contribution.*

class PlatformContributionFragment : BaseFragment() {

    val mViewModel: MyContributionViewModel by viewModels()

    var type: String = "1"

    private var _binding : HomeFragmentPlatformContributionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = HomeFragmentPlatformContributionBinding.inflate(layoutInflater,container,false)
        return binding.root


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initListener()

    }

    fun initData() {
        mViewModel.getData(type)

    }

    fun initListener() {
        binding.tvDay.setOnClickListener {
            binding.tvDay.setBackgroundResource(R.mipmap.home_ic_honnor_green_left)
            binding.tvMonth.setBackgroundResource(R.mipmap.home_ic_honnor_gray_right)
            val dayType = "1"
            val bchildFragmentManager: PlatformContributionListFragment? =
                childFragmentManager.findFragmentById(R.id.platformListFragment) as PlatformContributionListFragment?
            bchildFragmentManager?.uodateType(dayType)

            mViewModel.getData(dayType)

        }
        binding.tvMonth.setOnClickListener {
            binding.tvDay.setBackgroundResource(R.mipmap.home_ic_honnor_gray_left)
            binding.tvMonth.setBackgroundResource(R.mipmap.home_ic_honnor_green_right)
            val dateType = "2"
            val bchildFragmentManager: PlatformContributionListFragment? =
                childFragmentManager.findFragmentById(R.id.platformListFragment) as PlatformContributionListFragment?
            bchildFragmentManager?.uodateType(dateType)
            mViewModel.getData(dateType)

        }

        //了解贡献榜
        binding.tvGetRule.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_UNDERSTAND_CONTRIBUTION)
                .withString(IntentParamsConstants.HOME_UNDERSTAND_TYPE, "3").navigation()
        }

        //贡献任务
        binding.llContributionTask.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_CONTRIBUTE_TASK).navigation()
        }

        //内容
        mViewModel.myContributiondata.observe(viewLifecycleOwner, Observer {
            binding.tvRanking.setText("我的贡献值：" + it.devote)
        })

    }

}