package com.mooc.commonbusiness.manager

import android.text.TextUtils
import com.mooc.commonbusiness.constants.ResourceTypeConstans.Companion.TYPE_MICRO_LESSON
import com.mooc.commonbusiness.model.ResourcePostStudyBean
import org.json.JSONObject
import java.lang.String

/**
 * 资源跳转管理类
 */
class MoveResourceParametersManager {

    companion object{
        fun setMoveFolderRequestParameters(jsonObject: JSONObject, commitBean: ResourcePostStudyBean?) {
            when (commitBean?.resourceType) {
                10 -> {
                    jsonObject.put("title", commitBean.title)
                    jsonObject.put("url", commitBean.url)
                    jsonObject.put("source", String.valueOf(commitBean.source))
                    jsonObject.put("resource_type", String.valueOf(commitBean.resourceType))
                    jsonObject.put("content", commitBean.content)
                }
                14, 18 -> {
                    jsonObject.put("url", String.valueOf(commitBean.url))
                    jsonObject.put("resource_type", String.valueOf(commitBean.resourceType))
                    jsonObject.put("content", commitBean.content)
                    if (!TextUtils.isEmpty(commitBean.other_resource_id) && "0" != commitBean.other_resource_id) {
                        jsonObject.put("other_resource_id", commitBean.other_resource_id)
                    }
                }
                11 -> {
                    jsonObject.put("title", commitBean.title)
                    jsonObject.put("url", String.valueOf(commitBean.url))
                    jsonObject.put("source", String.valueOf(commitBean.source))
                    jsonObject.put("resource_type", String.valueOf(commitBean.resourceType))
                    jsonObject.put("basic_publisher", commitBean.basic_publisher)
                    jsonObject.put("basic_source_name", commitBean.basic_source_name)
                    jsonObject.put("basic_page", commitBean.basic_page)
                    jsonObject.put("basic_creator", commitBean.basic_creator)
                    jsonObject.put("basic_cover_url", commitBean.basic_cover_url)
                    jsonObject.put("basic_date_time", commitBean.basic_date_time)
                    jsonObject.put("basic_description", commitBean.basic_description)
                    jsonObject.put("basic_title_url", commitBean.basic_title_url)
                    jsonObject.put("basic_url", commitBean.basic_url)
                }
                12 -> {
                    jsonObject.put("title", commitBean.title)
                    jsonObject.put("url", String.valueOf(commitBean.url))
                    jsonObject.put("source", String.valueOf(commitBean.source))
                    jsonObject.put("resource_type", String.valueOf(commitBean.resourceType))
                    jsonObject.put("enroll_num", commitBean.enroll_num)
                    jsonObject.put("cover_image", commitBean.cover_image)
                    jsonObject.put("desc", commitBean.desc)
                }
                21, 22, 31, TYPE_MICRO_LESSON -> {
                    jsonObject.put("resource_type", String.valueOf(commitBean.resourceType))
                    jsonObject.put("other_resource_id", commitBean.other_resource_id)
                }
                26 -> {
                    jsonObject.put("url", String.valueOf(commitBean.url))
                    jsonObject.put("resource_type", String.valueOf(commitBean.resourceType))
                    if (!TextUtils.isEmpty(commitBean.other_resource_id) && "0" != commitBean.other_resource_id) {
                        jsonObject.put("other_resource_id", commitBean.other_resource_id)
                    }
                    if (commitBean.source == 1) {
                        jsonObject.put("source", String.valueOf(commitBean.source))
                    }
                    jsonObject.put("title", commitBean.title)
                    jsonObject.put("column_id", commitBean.column_id)
                    jsonObject.put("column_title", commitBean.column_title)
                    jsonObject.put("content", commitBean.content)
                    jsonObject.put("source_resource_type", commitBean.source_resource_type)
                    if (!TextUtils.isEmpty(commitBean.note_id)) {
                        jsonObject.put("note_id", commitBean.note_id)
                    }
                }
                5 -> {
                    jsonObject.put("url", String.valueOf(commitBean.url))
                    jsonObject.put("resource_type", String.valueOf(commitBean.resourceType))
                    if (!TextUtils.isEmpty(commitBean.other_resource_id) && "0" != commitBean.other_resource_id) {
                        jsonObject.put("other_resource_id", commitBean.other_resource_id)
                    }
                }
            }
        }
    }
}