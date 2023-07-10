package com.mooc.battle.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.mooc.battle.R
import com.mooc.battle.databinding.ActivitySkillRankBinding
import com.mooc.battle.fragment.GameRankListFragment
import com.mooc.battle.model.EventData
import com.mooc.battle.model.RankDeatilsBean
import com.mooc.common.ktextends.extraDelegate
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.resource.utils.AppBarStateChangeListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = Paths.PAGE_SKILL_RANK)
class SkillRankActivity : BaseActivity() {
    var inflate: ActivitySkillRankBinding? = null
    var fragment: GameRankListFragment? = null

    //    var tournament_id: String? = "8"//比武id
    val tournament_id by extraDelegate(IntentParamsConstants.TOURNAMENT_ID, "18")
    var bean: RankDeatilsBean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflate = ActivitySkillRankBinding.inflate(layoutInflater)
        setContentView(inflate?.root)
        EventBus.getDefault().register(this)
        fragment = GameRankListFragment()
        fragment?.tournament_id = tournament_id
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment!!).commit()
        initDataListener()
    }

    private fun initDataListener() {
        inflate?.commonTitle?.setOnLeftClickListener { finish() }
        inflate?.appBar?.addOnOffsetChangedListener(object : com.mooc.resource.utils.AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: Int) {
                when (state) {
                    State.EXPANDED -> {//展开状态
                        //展开状态
                        inflate?.clBottom?.setVisibility(View.GONE)
                        fragment?.changeBack(R.drawable.shape_rank_bottom)

                    }
                    State.COLLAPSED -> { //折叠状态
                        //折叠状态
                        inflate?.clBottom?.setVisibility(View.VISIBLE)
                        fragment?.changeBack(R.drawable.shape_rank_bottom_all)

                    }
                    else -> {//中间状态
                        //中间状态
                        fragment?.changeBack(R.drawable.shape_rank_bottom)
                        inflate?.clBottom?.setVisibility(View.GONE)

                    }
                }
            }

        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: EventData) {
        bean = event.bean
        inflate?.commonTitle?.middle_text = bean?.tournament_info?.title
        inflate?.tvRankNum?.setText("当前我的排名：" + bean?.self_rank_num)
        inflate?.tvBottomRankNum?.setText("当前我的排名：" + bean?.self_rank_num)
        if (bean?.tournament_rank_list != null) {
//            bean?.tournament_rank_list?.forEach {
                if (bean?.tournament_rank_list?.size!!>0) {
                    Glide.with(this).load(bean?.tournament_rank_list?.get(0)?.cover).circleCrop()
                        .placeholder(R.mipmap.common_ic_user_head_default)
                        .error(R.mipmap.common_ic_user_head_default).into(inflate?.ivCenter!!)
                    inflate?.tvCenterName?.setText(bean?.tournament_rank_list?.get(0)?.nickname)
                    inflate?.tvCenterScore?.setText(bean?.tournament_rank_list?.get(0)?.total_score + "分")
                }
                if (bean?.tournament_rank_list?.size!!>1) {
                    Glide.with(this).load(bean?.tournament_rank_list?.get(1)?.cover).circleCrop()
                        .placeholder(R.mipmap.common_ic_user_head_default)
                        .error(R.mipmap.common_ic_user_head_default).into(inflate?.ivLeft!!)
                    inflate?.tvLeftName?.setText(bean?.tournament_rank_list?.get(1)?.nickname)
                    inflate?.tvLeftScore?.setText(bean?.tournament_rank_list?.get(1)?.total_score + "分")
                }
                if (bean?.tournament_rank_list?.size!!>2) {
                    Glide.with(this).load(bean?.tournament_rank_list?.get(2)?.cover).circleCrop()
                        .placeholder(R.mipmap.common_ic_user_head_default)
                        .error(R.mipmap.common_ic_user_head_default).into(inflate?.ivRight!!)
                    inflate?.tvRightName?.setText(bean?.tournament_rank_list?.get(2)?.nickname)
                    inflate?.tvRightScore?.setText(bean?.tournament_rank_list?.get(2)?.total_score + "分")
                }

//            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}