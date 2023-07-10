package com.mooc.download.db

import androidx.room.*
import com.mooc.download.DownloadModel
import com.mooc.newdowload.DownloadInfo

@Dao
interface DownloadDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(downloadModel: DownloadInfo)

    /**
     * 根据id获取
     * @param id
     */
    @Query("select *from downloadinfo where `id`=:id")
    fun getDownloadModle(id:Long): DownloadInfo?

//
//    /**
//     * @param downloadUrl 下载地址
//     * @param filePath 根据文件存储路径+文件名称，获取数据模型
//     */
//    @Query("select *from downloadinfo where `downloadUrl`=:downloadUrl")
//    fun getDownloadModleByUrl(downloadUrl:String): DownloadModel
//    /**
//     * 获取下载model,
//     * 只能拿到文件名，还不知道url的情况下
//     */
//    @Query("select *from downloadinfo where `downloadFileName`=:fileName")
//    fun getDownloadModleByName(fileName:String): DownloadModel
//    /**
//     * 根据类型，查询同类的资源
//     * 如果是一对多,这里可以写List<Cache>
//     */
//    @Query("select *from downloadinfo where `downloadUrl`=:url")
//    fun getDownloadModles(url:String) : List<DownloadModel>
//
    //只能传递对象昂,删除时根据Cache中的主键 来比对的
    @Delete
    fun delete(downloadModel: DownloadInfo): Int

    //只能传递对象昂,更新时根据Cache中的主键 来比对的
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(downloadModel: DownloadInfo): Int
}