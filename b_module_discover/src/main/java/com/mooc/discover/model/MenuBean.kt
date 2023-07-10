package com.mooc.discover.model

/**
 * Created by huangzuoliang on 2018/1/22.
 */
class MenuBean {
    var name: String? = null
    var type = 0
    var isCheck = false
    override fun toString(): String {
        return "MenuBean{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", check=" + isCheck +
                '}'
    }
}