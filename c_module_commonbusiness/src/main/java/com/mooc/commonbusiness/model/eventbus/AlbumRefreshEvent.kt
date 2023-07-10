package com.mooc.commonbusiness.model.eventbus

/**
 * 音频页面点击屏蔽
 * 通知专辑页面移除该屏蔽音频
 */
class AlbumRefreshEvent(var trackId:String)