package com.mooc.my.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.utils.NetUtils
import com.mooc.common.utils.SystemUtils
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.route.Paths
import com.mooc.my.R
import com.mooc.my.adapter.ApplyCerAdapter
import com.mooc.my.databinding.ActivityApplyCerBinding
import com.mooc.my.model.CertificateBean
import com.mooc.my.viewmodel.HonorViewModel
//import kotlinx.android.synthetic.main.activity_apply_cer.*

/**
 *证书申请列表页面
 * @author limeng
 * @date 2020/10/7
 */
@Route(path = Paths.PAGE_APPLYCER)
class ApplyCerActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    var mApplyCerAdapter: ApplyCerAdapter? = null
    var emptyView: View? = null

    private lateinit var inflate : ActivityApplyCerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflate = ActivityApplyCerBinding.inflate(layoutInflater)
        setContentView(inflate.root)
        initView()
        initData()
        initListener()
    }

    val mViewModel: HonorViewModel by lazy {
        ViewModelProviders.of(this)[HonorViewModel::class.java]
    }

    private fun initListener() {


        mViewModel.certificateBeanBean.observe(this, Observer {
            inflate.swipeLayout.isRefreshing = false
            mApplyCerAdapter?.setNewInstance(it)
        })
        mApplyCerAdapter?.addChildClickViewIds(R.id.tv_apply_cer)
        mApplyCerAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val bean: CertificateBean.ResultsBean? = mApplyCerAdapter?.getItem(position)
            if (!"0".equals(bean?.apply_status)) {
                if (view.id == R.id.tv_apply_cer) {
                    ARouter.getInstance().build(Paths.PAGE_APPLYCER_INPUT)
                            .withString(IntentParamsConstants.INTENT_CERTIFICATE_ID, bean?.id)
                            .withString(IntentParamsConstants.PARAMS_RESOURCE_TITLE, bean?.title).navigation()
                }

            }
        }
        inflate.commonTitleLayout.setOnLeftClickListener { finish() }
    }

    private fun getCertificateFromNet() {
        mViewModel.loadCertificateData()
    }

    private fun initData() {

    }

    override fun onResume() {
        super.onResume()
        if (NetUtils.isNetworkConnected()) {
            getCertificateFromNet()
        }
    }

    private fun initView() {
        emptyView = layoutInflater.inflate(R.layout.my_layout_empty_view, null)

        mApplyCerAdapter = ApplyCerAdapter(null)

        inflate.recyclerView.layoutManager = LinearLayoutManager(this)

        inflate.recyclerView.adapter = mApplyCerAdapter
        if (emptyView != null) {
            mApplyCerAdapter?.setEmptyView(emptyView!!)
        }
        inflate.swipeLayout.setOnRefreshListener(this)

    }

    override fun onRefresh() {
        getCertificateFromNet()
    }

}