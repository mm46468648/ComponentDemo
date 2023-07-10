package com.mooc.home.ui.todaystudy

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.common.ktextends.loge
import com.mooc.home.model.TodayStudyData
import com.mooc.home.model.TodayStudyIconBean
import com.mooc.home.repository.TodayStudyRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect

class TodayViewModel : BaseViewModel() {
    private val repository = TodayStudyRepository()
    private val todayStudyData: MutableLiveData<TodayStudyData> by lazy {
        MutableLiveData<TodayStudyData>().also {
            loadData()
        }
    }

    private val todayStudyIcon: MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
            loadTodayStudyIcon()
        }
    }

    fun getTodayStudyData(): LiveData<TodayStudyData> {
        return todayStudyData
    }

    fun getTodayStudyIconData(): LiveData<String> {
        return todayStudyIcon
    }

    fun loadData() {
        launchUI({
            val scoreDetail = repository.getTodayStudy()
            todayStudyData.value = scoreDetail
        }, {
            loge(it.toString())
        })
    }

    fun loadTodayStudyIcon() {
        launchUI {
            repository.getTodayStudyIcon()
                .catch {
                    todayStudyIcon.value = ""
                }
                .collect {
                    todayStudyIcon.value = it.img_url
                }
        }
    }

}