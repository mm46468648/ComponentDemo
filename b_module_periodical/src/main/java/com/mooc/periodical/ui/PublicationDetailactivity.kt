package com.mooc.periodical.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.facade.annotation.Route
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.ktextends.put
import com.mooc.commonbusiness.base.BaseResourceActivity
import com.mooc.commonbusiness.base.BaseViewModelFactory
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.route.Paths
import com.mooc.periodical.viewmodel.PeriodicalDetailViewModel
import com.mooc.periodical.R
import com.mooc.periodical.databinding.PeriodicalActivityDetailBinding
import com.mooc.periodical.model.PeriodicalDetail
import com.mooc.periodical.model.Term
//import kotlinx.android.synthetic.main.periodical_activity_detail.*

/**
 * 刊物详情
 */
@Route(path = Paths.PAGE_PUBLICATION_DETAIL)
class PublicationDetailactivity : BaseResourceActivity() {

    val publicationId by extraDelegate(IntentParamsConstants.PARAMS_RESOURCE_ID,"")

    val mViewModel by lazy {
//        ViewModelProviders.of(this,BaseViewModelFactory(publicationId))[PeriodicalDetailViewModel::class.java]

        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return PeriodicalDetailViewModel(publicationId) as T
            }
        }).get(PeriodicalDetailViewModel::class.java)
    }

    private lateinit var inflater : PeriodicalActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = PeriodicalActivityDetailBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        initView()

        mViewModel.detailLiveData.observe(this, {
            bindDetialInfo(it)
        })
    }

    override fun getResourceType(): Int = ResourceTypeConstans.TYPE_PUBLICATION


    private fun initView() {
        titleLayoutData.value = inflater.commonTitle
        //选择期刊
        inflater.llTerm.setOnClickListener {
            mViewModel.detailLiveData.value?.terms?.let { it1 -> showCheckDialog(it1) }
        }
    }

    /**
     * 设置详情数据
     */
    fun bindDetialInfo(detail:PeriodicalDetail){
        inflater.tvDes.visibility = View.VISIBLE
        inflater.mivCover.setImageUrl(detail.coverurl,2.dp2px())
        inflater.tvTitle.setText(detail.magname)
        inflater.tvOrg.setText(detail.unit)
        //获取最新一期
        if(detail.terms?.isNotEmpty() == true){
            detail.terms?.get(0)?.let {
                if(it.value.isNotEmpty()){
                    val updateStr = "更新至${it.year}年 第${it.value.get(0)}期"
                    inflater.tvUpdate.setText(updateStr)
                    val checkStr = "${it.year}年  第 ${it.value.get(0)} 期"
                    inflater.tvDate.setText(checkStr)


                    //初始化期刊列表fragment
                    val periodicalListFragment = PeriodicalListFragment()
                    periodicalListFragment.arguments = Bundle()
                        .put(IntentParamsConstants.PARAMS_RESOURCE_ID, publicationId)
                    supportFragmentManager.beginTransaction().replace(R.id.flContainer, periodicalListFragment).commit()
                }
            }
        }

        inflater.etvDes.setContent(detail.summary)



    }

    /**
     * 展示选择期刊弹窗
     */
    fun showCheckDialog(terms: List<Term>) {
        val termPickDialog = TermPickDialog(this, terms)
        termPickDialog.onConfirm = {y,t->
//            toast("${y}年${t}期")
            val checkStr = "${y}年  第 ${t} 期"
            inflater.tvDate.setText(checkStr)
            mViewModel.currentSelectTerm.value = Pair(y,t)
            //调用appbar的收起
            inflater.appBar.setExpanded(false)
        }
        XPopup.Builder(this).asCustom(termPickDialog).show()
    }
}