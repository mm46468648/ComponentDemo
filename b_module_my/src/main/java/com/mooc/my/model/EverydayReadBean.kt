package com.mooc.my.model

/**
 *数据bean  也为数据库的bean
 * @author limeng
 * @date 2020/9/1
 */

data class   EverydayReadBean(
        /**
     * count : 28
     * next : http://192.168.104.248:8000/resource/daily_read/app/?limit=10&offset=20
     * previous : http://192.168.104.248:8000/resource/daily_read/app/?limit=10
     * results : [{"date":"2017-05-26","image_url":"http://storage.xuetangx.com/moocnd/img/51ec703829d0f0955d1c3363f538cae5.jpg"},{"date":"2017-05-25","image_url":"http://storage.xuetangx.com/moocnd/img/0fa08c6ce83809ce47917e8983c9c8c1.jpg"},{"date":"2017-05-24","image_url":"http://storage.xuetangx.com/moocnd/img/18e0e504bb7ca1dac8c777270cd399c8.png"},{"date":"2017-05-23","image_url":"http://storage.xuetangx.com/moocnd/img/119f02666abb5d33458a8dcec9fb7cd8.png"},{"date":"2017-05-22","image_url":"http://storage.xuetangx.com/moocnd/img/7643d4f5e2efc59f2b0eeb18a59aa0aa.png"},{"date":"2017-05-21","image_url":"http://storage.xuetangx.com/moocnd/img/36c47e3bc39d541f3c8406912f25ec91.png"},{"date":"2017-05-20","image_url":"http://storage.xuetangx.com/moocnd/img/76ff8bb4a1ca582bbe311aa377aa77c9.png"},{"date":"2017-05-19","image_url":"http://storage.xuetangx.com/moocnd/img/b878c2542cb034c2deae147686c2caa6.png"},{"date":"2017-05-18","image_url":"http://storage.xuetangx.com/moocnd/img/e9629ea9a0448dd4c7d9574e094d6962.png"},{"date":"2017-05-17","image_url":"http://storage.xuetangx.com/moocnd/img/084f0ef20032a02e99540cea78c2d74c.png"}]
     */
        var count:Int = 0,
        var next: String? = null,
        var previous: String? = null,
        var results: ArrayList<ReadBean>? = null,
         var share_url: String? = null,

    //当天阅读的参数
        var date: String? = null,
        var image_url: String? = null

)