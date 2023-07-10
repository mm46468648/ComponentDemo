package com.mooc.studyproject.viewmodel

import android.widget.TextView
import androidx.lifecycle.viewModelScope
import com.mooc.b_module_studyproject.httpserver.HttpService
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.studyproject.model.StudyPlanSource
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import java.util.ArrayList

/**

 * @Author limeng
 * @Date 3/9/22-4:51 PM
 */
class ChoseLearnViewModel  : BaseListViewModel<StudyPlanSource>() {
    var id:String?=null
    override suspend fun getData(): Deferred<List<StudyPlanSource>?> {
        return viewModelScope.async {
           if (id != null) {
               val bean = HttpService.userApi.getChoseLearnFinish(id!!, limit, offset).await()
               bean?.results
           }else{
               ArrayList<StudyPlanSource>()
           }

        }
    }
}