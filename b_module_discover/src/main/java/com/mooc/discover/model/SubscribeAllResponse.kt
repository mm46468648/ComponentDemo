package com.mooc.discover.model

data class SubscribeAllResponse(
        var cat_title : String,
        var subscribe : ArrayList<SubscribeBean>,
        var not_subscribe : ArrayList<SubscribeBean>
)

data class SubscribeBean(
        var id : String,
        var title : String,

        var mAdapterType : Int = -1, //自定义的属性（标记在适配器的类型是专栏还是专题 默认-1，什么也不是 0，代表专栏，1代表专题
        var subscribe : Boolean, //自定义的属性（是否订阅，未订阅显示加号)
        var editMode : Boolean //自定义的属性（是否是编辑模式)

)