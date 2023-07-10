package com.mooc.commonbusiness.model.xuetang

/**
 * 证书状态bean
 * @param cert_status //"ready"代表可申请,"finish，paper，verified"-已申请，"processing，notpassing"-不可申请
 */
data class VerifyStatusBean(var cert_status : String)