package com.mooc.common.ktextends

@Target(AnnotationTarget.TYPE)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class Orientation(){
    companion object {
        const val LEFT = "LEFT"
        const val TOP = "TOP"
        const val RIGHT = "RIGHT"
        const val BOTTOM = "BOTTOM"
    }
}
