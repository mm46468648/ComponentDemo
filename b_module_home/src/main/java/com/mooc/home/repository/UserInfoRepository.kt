package com.mooc.home.repository

import com.mooc.commonbusiness.model.UserInfo
import com.mooc.home.HttpService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class UserInfoRepository {

    fun getUserInfo(): Flow<UserInfo> {
        return flow<UserInfo> {
            emit(HttpService.homeApi.getUserInfo())
        }.catch {

        }
    }
}