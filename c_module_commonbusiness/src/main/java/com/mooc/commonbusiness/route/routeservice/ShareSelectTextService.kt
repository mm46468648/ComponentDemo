package com.mooc.commonbusiness.route.routeservice

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider
import org.json.JSONObject

/**

 * @Author limeng
 * @Date 2021/5/14-11:15 AM
 */
interface ShareSelectTextService :IProvider {
    fun shareSelectText(context: Context, json: JSONObject, callback : ((success:Boolean)->Unit)? = null)

}