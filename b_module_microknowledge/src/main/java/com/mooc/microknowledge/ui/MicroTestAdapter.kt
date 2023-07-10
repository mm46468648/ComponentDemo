package com.mooc.microknowledge.ui

import android.graphics.Color
import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.getColorRes
import com.mooc.commonbusiness.model.TestVolume
import com.mooc.microknowledge.R

class MicroTestAdapter(list : ArrayList<TestVolume>)
    : BaseQuickAdapter<TestVolume,BaseViewHolder>(R.layout.knowledge_item_test_volume,list) {


    override fun convert(holder: BaseViewHolder, item: TestVolume) {
        holder.setText(R.id.tvName,item.test_paper_title)

        //设置测试按钮文案
        if(item.exam_status == "3"){
            holder.setText(R.id.tvGoTest,"查看答题情况")
        }else{
            holder.setText(R.id.tvGoTest,"去测试")
        }
        //没有证书id,直接隐藏
        if(TextUtils.isEmpty(item.certificate_id) || item.certificate_id == "0"){
            holder.setGone(R.id.tvToApply,true)
        }else{
            holder.setGone(R.id.tvToApply,false)

            //设置证书状态
            when(item.certificate_status){
                "0"->{    //可申请但,没资格置灰
                    holder.setBackgroundResource(R.id.tvToApply,R.drawable.shape_bg_apply)
                    holder.setText(R.id.tvToApply,"申请学习证明")
                    holder.setTextColor(R.id.tvToApply,context.getColorRes(R.color.c7))
                }
                "1"->{   //可申请,有资格,未申请
                    holder.setBackgroundResource(R.id.tvToApply,R.drawable.shape_bg_go_test)
                    holder.setText(R.id.tvToApply,"申请学习证明")
                    holder.setTextColor(R.id.tvToApply, Color.BLACK)
                }
                "2"->{   //可申请,申请中
                    holder.setBackgroundResource(R.id.tvToApply,R.drawable.shape_bg_apply)
                    holder.setText(R.id.tvToApply,"申请学习证明")
                    holder.setTextColor(R.id.tvToApply,context.getColorRes(R.color.c7))
                }
                "3"->{    //可申请,已通过
                    holder.setText(R.id.tvToApply,"查看学习证明")
                    holder.setBackgroundResource(R.id.tvToApply,R.drawable.shape_bg_go_test)
                    holder.setTextColor(R.id.tvToApply, Color.BLACK)
                }
            }
        }

        //最后一行隐藏分割线
        if(holder.layoutPosition == data.size-1){
            holder.setGone(R.id.bottomLine,true)
        }else{
            holder.setGone(R.id.bottomLine,false)
        }

    }


}