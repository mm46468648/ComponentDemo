package com.mooc.studyroom.ui.activity

import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.util.contains
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayoutMediator
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyroom.R
import com.mooc.studyroom.databinding.StudyroomActivityScoreRankBinding
import com.mooc.studyroom.ui.adapter.ScoreRankViewpagerAdapter
import com.mooc.studyroom.viewmodel.FriendScoreRankAvtivityViewModel
//import kotlinx.android.synthetic.main.studyroom_activity_score_rank.*
//import kotlinx.android.synthetic.main.studyroom_activity_score_rank.tabLayout

/**
 * 好友积分排行页面
 */
@Route(path = Paths.PAGE_FRIEND_SCORE_RANK)
class FriendScoreRankActivity : BaseActivity(){

    val tabArray = arrayListOf<String>("总积分榜","今日积分榜","本周积分榜","本月积分榜")

    val mAdapter by lazy {
        ScoreRankViewpagerAdapter(this)
    }

    val mViewModel by viewModels<FriendScoreRankAvtivityViewModel>()

    private lateinit var inflater: StudyroomActivityScoreRankBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = StudyroomActivityScoreRankBinding.inflate(layoutInflater)
        setContentView(inflater.root)


        inflater.commonTitle.setOnLeftClickListener { finish() }
        mViewModel.myRankList.observe(this, Observer {
            updateMyRank(it)
        })

        inflater.viewPage2.adapter = mAdapter
        inflater.viewPage2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mViewModel.myRankList.value?.let {
                    updateMyRank(it) }
            }
        })
        val tabLayoutMediator =
            TabLayoutMediator(inflater.tabLayout, inflater.viewPage2,true) { tab, position ->
                tab.text = tabArray[position]
            }
        tabLayoutMediator.attach()
    }

    fun updateMyRank(sparseArray: SparseArray<Int>){
        val currentItem = inflater.viewPage2.currentItem
        if(sparseArray.indexOfKey(currentItem) >= 0){
            inflater.tvMyRank.setText("我的排名: ${sparseArray[currentItem]}")
        }
    }
}