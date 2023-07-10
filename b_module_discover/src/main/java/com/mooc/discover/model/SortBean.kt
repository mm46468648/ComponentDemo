package com.mooc.discover.model

class SortBean {
    var title: String? = null
    var id: String? = null
    var isBottom = false
    var isSelected = false
    var childList: List<SortChild>? = null
}