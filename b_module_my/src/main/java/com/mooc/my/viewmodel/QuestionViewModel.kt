package com.mooc.my.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.my.model.QuestionListBean
import com.mooc.my.model.QuestionMoreBean
import com.mooc.my.repository.MyModelRepository
import com.mooc.commonbusiness.base.BaseViewModel

/**
 * 常见问题
 * @Author limeng
 * @Date 2020/9/23-7:58 PM
 */
class QuestionViewModel :BaseViewModel() {
    private val repository = MyModelRepository()
    val questionListBean : MutableLiveData<List<QuestionListBean>> by lazy {
        MutableLiveData<List<QuestionListBean>>()
    }
    val questionMoreBean: MutableLiveData<QuestionMoreBean> by lazy {
        MutableLiveData<QuestionMoreBean>()
    }
    fun loadQuestionsListData(){
        launchUI {
           val medalresult= repository.getQuestionList()
            questionListBean.value=medalresult
            questionListBean.postValue(medalresult)
        }
    }

    fun loadQuestionMoreData(map: Map<String, String>) {
        launchUI {
            val medalresult = repository.getQuestionListMore(map)
            questionMoreBean.value = medalresult
            questionMoreBean.postValue(medalresult)
        }
    }
}