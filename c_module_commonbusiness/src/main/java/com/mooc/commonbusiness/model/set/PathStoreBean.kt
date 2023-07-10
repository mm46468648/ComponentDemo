package com.mooc.commonbusiness.model.set

data class PathStoreBean(
        var storageBeans : List<PatshStore>
)

data class PatshStore(
        var checked : Boolean,
        var title : String,
        var path:String
)