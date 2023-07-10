package com.mooc.my.model

import java.util.*

/**

 * @Author limeng
 * @Date 2020/10/7-5:23 PM
 */
data class CertificateBean(
        val count: String,
        val results: ArrayList<ResultsBean>
){
    data class ResultsBean(
            /**
             * id : 3
             * title : 测试证书
             * type : 1
             * study_evaluate : 优秀
             */
            var id: String,
            var title: String?,
            var type: String,
            var study_evaluate: String? = null,
            var apply_status: String

    )
}

