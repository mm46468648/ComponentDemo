package com.mooc.studyroom.viewmodel

import android.util.SparseArray
import android.util.SparseIntArray
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mooc.commonbusiness.base.BaseViewModel

class FriendScoreRankAvtivityViewModel:BaseViewModel() {

    //记录各个分类下的我得排名
    val myRankList = MutableLiveData<SparseArray<Int>>()

    init {
        myRankList.value = SparseArray<Int>()
    }

}