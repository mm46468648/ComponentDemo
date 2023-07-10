package com.mooc.audio.manager

/**
 * 音频打点数据库管理类
 */
class AudioPointDBManager {

    fun saveToDb(audioPointBean: AudioPointBean){

    }

    fun deleteAll(){

    }

    fun queryAll():ArrayList<AudioPointBean>{
        return arrayListOf()
    }

    /**
     * 启动时候查询打点数据是否有上传失败的打点
     * 如果有上传到服务器
     */
    fun postDB(){
        if(queryAll().isNotEmpty()){
            //todo post to server
            val isDelete = true
            if( isDelete){
                deleteAll()
            }
        }
    }


}