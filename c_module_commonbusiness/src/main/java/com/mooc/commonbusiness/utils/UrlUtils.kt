package com.mooc.commonbusiness.utils

import java.util.*

/**
 *
 * @ProjectName:    URL工具类
 * @Package:
 * @ClassName:
 * @Description:    java类作用描述
 * @Author:         xym
 * @CreateDate:     2021/3/10 4:31 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2021/3/10 4:31 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class UrlUtils {
    companion object{
        /**
         * 解析出url参数中的键值对
         * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
         *
         * @param URL url地址
         * @return url请求参数部分
         */
        fun parseParams(URL: String): Map<String, String>? {
            val mapRequest: MutableMap<String, String> = HashMap()
            val arrSplit: Array<String>
            val strUrlParam: String = truncateUrlBefore(URL)
                    ?: return mapRequest
            //每个键值为一组
            arrSplit = strUrlParam.split("[&]".toRegex()).toTypedArray()
            for (strSplit in arrSplit) {
                var arrSplitEqual: Array<String>
                arrSplitEqual = strSplit.split("[=]".toRegex()).toTypedArray()

                //解析出键值
                if (arrSplitEqual.size > 1) {
                    //正确解析
                    mapRequest[arrSplitEqual[0]] = arrSplitEqual[1]
                } else {
                    if (arrSplitEqual[0] != "") {
                        //只有参数没有值，不加入
                        mapRequest[arrSplitEqual[0]] = ""
                    }
                }
            }
            return mapRequest
        }

        /**
         * 去掉url中的路径，留下请求参数部分
         *
         * @param strURL url地址
         * @return url请求参数部分
         */
        private fun truncateUrlBefore(strURL: String): String? {
            var strURL = strURL
            var strAllParam: String? = null
            val arrSplit: Array<String?>
            strURL = strURL.trim { it <= ' ' }.toLowerCase()
            arrSplit = strURL.split("[?]".toRegex()).toTypedArray()
            if (strURL.length > 1) {
                if (arrSplit.size > 1) {
                    if (arrSplit[arrSplit.size - 1] != null) {
                        strAllParam = arrSplit[arrSplit.size - 1]
                    }
                }
            }
            return strAllParam
        }

        /**
         * 拼接参数
         * @param url 原始链接
         * @param key 拼接的key
         * @param value 拼接的value
         */
        fun appendParams(url : String,key:String,value:String) : String{
            if(url.isEmpty() || key.isEmpty()) return url
            val appendStr = "${key}=${value}"
            val middleStr = if(url.contains("?")) "&" else "?"
            return "${url}${middleStr}$appendStr"
        }

        /**
         * @param url 原始链接
         * @param params 可变数组，传递key，value，key，value格式
         */
        fun appendParams(url : String,vararg params: String) : String{
            val sb = StringBuffer()
            params.forEachIndexed{index,s->
                sb.append(s)
                if(index and 1 == 1){
                    sb.append("&")
                }else{
                    sb.append("=")
                }
            }
            val middleStr = if(url.contains("?")) "&" else "?"
            return "${url}${middleStr}${sb}"
        }
    }
}