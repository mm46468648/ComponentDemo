package com.mooc.search.viewmodel

import com.mooc.commonbusiness.base.BaseListViewModel2
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.search.DataBean
import com.mooc.commonbusiness.net.ApiService
import com.mooc.search.api.SearchApi
import com.mooc.search.model.SearchItem
import com.mooc.search.model.SearchPopData
import com.mooc.search.utils.SearchConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchResultViewModel : BaseListViewModel2<SearchItem>() {

    var map: Map<String, String> = hashMapOf()

    var totalList = arrayListOf<SearchItem>()

    companion object {
        var typeList = arrayListOf<SearchPopData>()
    }

    override suspend fun getData(): Flow<List<SearchItem>> {
        return flow {

            val result =
                ApiService.getRetrofit().create(SearchApi::class.java).getSearchDataNew(map)
            totalList = parseResult(result)
            val resultList = parseResult(result)


            emit(resultList)
        }
    }

    fun parseResult(result: HashMap<String, DataBean<SearchItem>>): ArrayList<SearchItem> {
        typeList.clear()
        val arrayList = arrayListOf<SearchItem>()

        val searchPopData = SearchPopData(SearchConstants.TYPE_HEADER, "全部", 0, true);
        typeList.add(searchPopData)

        //按顺序添加搜素出来的数据
        ResourceTypeConstans.searchTypeSort.forEach { key ->
            val get = result.get(key)
            if (get != null && get.count > 0) {
                val resourceType = ResourceTypeConstans.typeAliasToResource[key] ?: -1
                val searchPopData = SearchPopData(
                    resourceType,
                    ResourceTypeConstans.typeAliasToName[key] ?: "",
                    get.count ?: 0,
                    false
                );
                typeList.add(searchPopData)

                get.items?.let { list ->
                    list.forEach { searchItem ->
                        searchItem.resource_type = resourceType ?: -1
                        searchItem.ownTotalCount = get.count
                    }
                    arrayList.addAll(list)
                }
            }

        }

        result.forEach { map ->


        }
        return arrayList;
    }
}