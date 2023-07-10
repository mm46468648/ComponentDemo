package com.mooc.common.utils

import okhttp3.internal.closeQuietly
import org.junit.Assert
import org.junit.Test
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream


class ZipUtil {

    companion object{
        val TAG = "ZipUtil"

        @Test
        fun testZipFile() {
            val f1 = File("./build/ziptest/中文.txt")
            createFile(f1.path)
            val f2 = File("./build/ziptest/2.txt")
            createFile(f2.path)
            val f3 = File("./build/ziptest/3.txt")
            createFile(f3.path)

            zip(listOf(f1, f2, f3), "./build/ziptest.zip")

            val res = "./build/zipresult"
            unzip("./build/ziptest.zip", res)
            Assert.assertEquals(3, File(res).listFiles().size)
        }

        @Test
        fun mp3Zip() {
            val files = File("./build/douyin_music").listFiles().toList()
            zip(files, "./build/douyin.zip")

            unzip("./build/douyin.zip", "./build/douyin_res")

            Assert.assertEquals(files.size, File("./build/douyin_res").listFiles().size)
        }

        /**
         * 解压
         */
        fun unzip(zipFile: String, descDir: String) {
            val buffer = ByteArray(1024)
            var outputStream: OutputStream? = null
            var inputStream: InputStream? = null
            try {
                val zf = ZipFile(zipFile)
                val entries = zf.entries()
                while (entries.hasMoreElements()) {
                    val zipEntry: ZipEntry = entries.nextElement() as ZipEntry
                    val zipEntryName: String = zipEntry.name

                    inputStream = zf.getInputStream(zipEntry)
                    val descFilePath: String = descDir + File.separator + zipEntryName
                    val descFile: File = createFile(descFilePath)
                    outputStream = FileOutputStream(descFile)

                    var len: Int
                    while (inputStream.read(buffer).also { len = it } > 0) {
                        outputStream.write(buffer, 0, len)
                    }
                    inputStream.closeQuietly()
                    outputStream.closeQuietly()
                }
            } finally {
                inputStream?.closeQuietly()
                outputStream?.closeQuietly()
            }
        }

        private fun createFile(filePath: String): File {
            val file = File(filePath)
            val parentFile = file.parentFile!!
            if (!parentFile.exists()) {
                parentFile.mkdirs()
            }
            if (!file.exists()) {
                file.createNewFile()
            }
            return file
        }

        /**
         * 压缩
         */
        fun zip(files: List<File>, zipFilePath: String) {
            if (files.isEmpty()) return

            val zipFile = createFile(zipFilePath)
            val buffer = ByteArray(1024)
            var zipOutputStream: ZipOutputStream? = null
            var inputStream: FileInputStream? = null
            try {
                zipOutputStream = ZipOutputStream(FileOutputStream(zipFile))
                for (file in files) {
                    if (!file.exists()) continue
                    zipOutputStream.putNextEntry(ZipEntry(file.name))
                    inputStream = FileInputStream(file)
                    var len: Int
                    while (inputStream.read(buffer).also { len = it } > 0) {
                        zipOutputStream.write(buffer, 0, len)
                    }
                    zipOutputStream.closeEntry()
                }
            } finally {
                inputStream?.close()
                zipOutputStream?.close()
            }
        }

        /**
         * 压缩文件夹
         * @param fileDir 需要压缩的文件夹
         * @param zipFilePath 压缩后的名字
         */
        fun zipByFolder(fileDir: String, zipFilePath: String) {
            val folder = File(fileDir)
            if (folder.exists() && folder.isDirectory) {
                val files = folder.listFiles()
                val filesList: List<File> = files.toList()
                zip(filesList, zipFilePath)
            }
        }

        /**
         * 压缩文件和文件夹
         *
         * @param srcFileString 要压缩的文件或文件夹
         * @param zipFileString 压缩完成的Zip路径
         * @throws Exception
         */
        fun ZipFolder( srcFileString:String,  zipFileString:String)  {
            //创建ZIP
            val outZip =  ZipOutputStream( FileOutputStream(zipFileString));
            //创建文件
            val file =  File(srcFileString);
            //压缩
//            LogUtils.LOGE("---->"+file.getParent()+"==="+file.getAbsolutePath());
            ZipFiles(file.getParent()+ File.separator, file.getName(), outZip);
            //完成和关闭
            outZip.finish();
            outZip.close();
        }

        /**
         * 压缩文件
         *
         * @param folderString
         * @param fileString
         * @param zipOutputSteam
         * @throws Exception
         */
         fun ZipFiles( folderString:String,  fileString:String,  zipOutputSteam:ZipOutputStream){
//            LogUtils.LOGE("folderString:" + folderString + "\n" +
//                    "fileString:" + fileString + "\n==========================");
            if (zipOutputSteam == null)
                return;
            val file =  File(folderString + fileString);
            if (file.isFile()) {
                val zipEntry =  ZipEntry(fileString);
                val inputStream =  FileInputStream(file);
                zipOutputSteam.putNextEntry(zipEntry);
                var len = 0;
                val buffer = ByteArray(4096)
                while (((inputStream.read(buffer)).also { len = it }) != -1) {
                    zipOutputSteam.write(buffer, 0, len)
                }
                zipOutputSteam.closeEntry();
            } else {
                //文件夹
                val fileList = file.list();
                //没有子文件和压缩
                if (fileList.size <= 0) {
                    val zipEntry =  ZipEntry(fileString + File.separator);
                    zipOutputSteam.putNextEntry(zipEntry);
                    zipOutputSteam.closeEntry();
                }
                //子文件和递归
                for (i in 0 until fileList.size){
                    ZipFiles(folderString+fileString+"/",  fileList[i], zipOutputSteam);
                }                }
            }

    }



}