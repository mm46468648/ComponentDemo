package com.mooc.download.util

import android.os.Build.VERSION
import android.os.StatFs
import java.io.File

/**
 *
 */
class FileUtil {
    companion object{
        fun checkDownloadPathSize(downloadSize: Long, mSaveFilePath: String?): Boolean {
            val free_space = getAvailableMemorySize(File(mSaveFilePath))
            return free_space > 0L && downloadSize < free_space
        }

        fun getAvailableMemorySize(path: File?): Long {
            return try {
                if (null != path && path.exists()) {
                    val stat = StatFs(path.path)
                    val blockSize: Long = stat.blockSizeLong
                    val availableBlocks: Long = stat.availableBlocksLong
                    availableBlocks * blockSize
                } else {
                    -1L
                }
            } catch (var6: Exception) {
                -1L
            }
        }
    }

}