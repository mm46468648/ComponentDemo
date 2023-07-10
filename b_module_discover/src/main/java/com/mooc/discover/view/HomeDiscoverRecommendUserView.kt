package com.mooc.discover.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.changeskin.SkinManager
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.manager.BaseObserver
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
import com.mooc.discover.adapter.RecommendUserAdapter
import com.mooc.discover.databinding.DiscoverViewRecommendUserBinding
import com.mooc.discover.httpserver.HttpService
import com.mooc.discover.model.RecommendUser
//import kotlinx.android.synthetic.main.discover_view_recommend_user.view.*
import java.util.*

/**
 * 推荐相似用户
 */
class HomeDiscoverRecommendUserView @JvmOverloads constructor(
    var mContext: Context,
    var attributeSet: AttributeSet? = null,
    var defaultInt: Int = 0
) :
    FrameLayout(mContext, attributeSet, defaultInt) {
    val recommendUserAdapter by lazy {
        RecommendUserAdapter(null)
    }

    var inflater: DiscoverViewRecommendUserBinding =
        DiscoverViewRecommendUserBinding.inflate(LayoutInflater.from(context),this,true)

    init {
//        LayoutInflater.from(mContext).inflate(R.layout.discover_view_recommend_user, this)

        _init()
        SkinManager.getInstance().injectSkin(this)
    }


    fun _init() {

        //点击换一换
        inflater.tvChange.setOnClickListener {
            inflater.tvChange.setEnabled(false)
//            ivChange?.animate()?.rotation(1f)?.setDuration(2000)?.start()
            val icAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_ic_change_refresh)
            inflater.ivChange.startAnimation(icAnimation)
            loadData()
        }
        inflater.rvRecommendUser.layoutManager = LinearLayoutManager(mContext)
        inflater.rvRecommendUser.adapter = recommendUserAdapter

        //点击进入用户空间
        recommendUserAdapter.setOnItemClickListener { adapter, view, position ->
            val any = adapter.data[position] as RecommendUser
            ARouter.getInstance()
                .build(Paths.PAGE_USER_INFO)
                .withString(IntentParamsConstants.MY_USER_ID, any.user_id)
                .navigation()
        }

        loadData()
    }

    fun loadData() {
        HttpService.discoverApi.getRecommendUsers().compose(RxUtils.applySchedulers())
            .subscribe(object :
                BaseObserver<HttpResponse<ArrayList<RecommendUser>>>(mContext, false) {
                override fun onSuccess(arrayListHttpResponse: HttpResponse<ArrayList<RecommendUser>>) {
                    if (inflater.rvRecommendUser != null)
                        recommendUserAdapter.setNewInstance(arrayListHttpResponse.getData())
                    resetChageState()
                }

                override fun onFailure(code: Int, message: String?) {
                    resetChageState()
                    super.onFailure(code, message)
                }
            })
    }

    fun resetChageState() {
        if (inflater.ivChange != null && inflater.tvChange != null) {
            inflater.ivChange.clearAnimation()
            inflater.tvChange.setEnabled(true)
        }
    }

}