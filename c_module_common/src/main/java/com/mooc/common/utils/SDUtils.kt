package com.mooc.common.utils

import android.os.Environment
import android.os.StatFs
import android.text.TextUtils
import java.io.File
import java.io.IOException

/**

 * @Author limeng
 * @Date 2020/10/7-10:10 AM
 */
class SDUtils {
    companion object{
        fun getSDFile(parent: String?, filename: String?): File {
            val dir = File(Environment.getExternalStorageDirectory(), parent)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, filename)
            if (!file.exists()) {
                try {
                    file.createNewFile()
                } catch (e: IOException) {
                    //LogUtils.postErrorLog("IO" + e.getMessage());
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
            }
            return file
        }
        fun checkFile(filePath: String?): Boolean {
            if (TextUtils.isEmpty(filePath)) {
                return false
            }
            val file = File(filePath)
            return if (file.exists() && file.length() > 0) {
                true
            } else {
                false
            }
        }


        /**
         * 获取文件夹空间信息.
         * @param path 存储路径.
         * @return bytes[0]可用的byte, bytes[1]总的byte.
         */
        fun getDirSize(path: String): LongArray {
            val bytes = LongArray(2)
            try {
                val sf = StatFs(path)
                val blockSize = sf.blockSizeLong
                val blockCount = sf.blockCountLong
                val availCount = sf.availableBlocksLong
                val availByte = blockSize * availCount
                val allByte = blockSize * blockCount
                bytes[0] = availByte
                bytes[1] = allByte
            } catch (e: Exception) {
                bytes[0] = 0
                bytes[1] = 0
            }
            return bytes
        }

        /**
         * 获取手机内部空间大小，单位为byte
         */
        fun getTotalInternalSpace(): Long {
            var totalSpace = -1L
            try {
                val path = Environment.getDataDirectory().path
                val stat = StatFs(path)
                val blockSize = stat.blockSizeLong
                val totalBlocks = stat.blockCountLong // 获取该区域可用的文件系统数
                totalSpace = totalBlocks * blockSize
            } catch (e: java.lang.Exception) {
            }
            return totalSpace
        }

        /**
         * 获取手机内部可用空间大小，单位为byte
         */
        fun getAvailableInternalMemorySize(): Long {
            var availableSpace = -1L
            try {
                val path = Environment.getDataDirectory().path // 获取 Android 数据目录
                val stat = StatFs(path) // 一个模拟linux的df命令的一个类,获得SD卡和手机内存的使用情况
                val blockSize = stat.blockSizeLong // 返回 Long ，大小，以字节为单位，一个文件系统
                val availableBlocks = stat.availableBlocksLong // 返回 Long ，获取当前可用的存储空间
                availableSpace = availableBlocks * blockSize
            } catch (e: java.lang.Exception) {
            }
            return availableSpace
        }
    }



}