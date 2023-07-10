package com.mooc.setting.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.Gson
import com.mooc.common.utils.sharepreference.SpDefaultUtils
import com.mooc.commonbusiness.constants.SpConstants
import com.mooc.setting.R
import com.mooc.setting.model.TestAccountBean
import com.mooc.setting.ui.controller.ItemTouchHelperAdapter

/**
 * 测试账号列表适配器
 */
class TestAccountAdapter(list:ArrayList<TestAccountBean>) : BaseQuickAdapter<TestAccountBean,BaseViewHolder>(
    R.layout.set_item_test_account,list),ItemTouchHelperAdapter {
    override fun convert(holder: BaseViewHolder, item: TestAccountBean) {

        holder.setText(R.id.tvName,item.username)
        holder.setText(R.id.tvOpenid,item.openid)
        holder.setText(R.id.tvUnionId,item.unionid)
    }

    override fun onItemDissmiss(position: Int) {
        data.removeAt(position)

        val gson = Gson()
        val toJson = gson.toJson(data)
        SpDefaultUtils.getInstance().putString(SpConstants.SP_TEST_ACCOUNT_ARRAY, toJson)

        notifyItemRemoved(position)
    }
}