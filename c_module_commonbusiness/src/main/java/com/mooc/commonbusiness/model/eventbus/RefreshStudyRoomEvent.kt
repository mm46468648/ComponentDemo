package com.mooc.commonbusiness.model.eventbus

/**
 * 往学习室添加资源，通知学习室列表刷新事件
 */
class RefreshStudyRoomEvent(var resourceType:Int)