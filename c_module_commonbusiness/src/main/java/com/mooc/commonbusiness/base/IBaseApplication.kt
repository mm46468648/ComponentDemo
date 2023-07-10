package com.mooc.commonbusiness.base

import android.app.Application
import android.content.Context

interface IBaseApplication {
    fun init()

    fun initWithContext(a : Application?){

    }
    fun onAttach(a : Application,c: Context){}
}