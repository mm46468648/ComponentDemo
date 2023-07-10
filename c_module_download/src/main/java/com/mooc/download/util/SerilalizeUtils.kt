package com.mooc.download.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * 序列化工具
 */
object SerilalizeUtils {
    //反序列,把二进制数据转换成java object对象
    fun toObject(data: ByteArray): Any? {
        var bais: ByteArrayInputStream? = null
        var ois: ObjectInputStream? = null
        try {
            bais = ByteArrayInputStream(data)
            ois = ObjectInputStream(bais)
            return ois.readObject()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                bais?.close()
                ois?.close()
            } catch (ignore: Exception) {
                ignore.printStackTrace()
            }
        }
        return null
    }

    //序列化存储数据需要转换成二进制
    fun <T> toByteArray(body: T): ByteArray? {
        var baos: ByteArrayOutputStream? = null
        var oos: ObjectOutputStream? = null
        try {
            baos = ByteArrayOutputStream()
            oos = ObjectOutputStream(baos)
            oos.writeObject(body)
            oos.flush()
            return baos.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                baos?.close()
                oos?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return ByteArray(0)
    }
}