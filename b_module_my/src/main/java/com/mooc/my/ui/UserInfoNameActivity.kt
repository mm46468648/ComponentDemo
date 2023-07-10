package com.mooc.my.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.common.bus.LiveDataBus
import com.mooc.common.ktextends.getColorRes
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.LiveDataBusEventConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.sp.SPUserUtils
import com.mooc.my.R
import com.mooc.my.databinding.ActivityUserInfoNameBinding
import com.mooc.my.viewmodel.UserInfoEditViewModel
//import kotlinx.android.synthetic.main.activity_user_info_name.*
//import kotlinx.android.synthetic.main.activity_user_info_name.commonTitle


@Route(path = Paths.PAGE_USERINFO_NAME)
class UserInfoNameActivity : BaseActivity() {


    val mViewModel by viewModels<UserInfoEditViewModel>()
    private lateinit var inflate : ActivityUserInfoNameBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflate = ActivityUserInfoNameBinding.inflate(layoutInflater)
        setContentView(inflate.root)

        inflate.commonTitle.setOnLeftClickListener { finish() }

        inflate.commonTitle.middle_text = "昵称"
        inflate.commonTitle.setOnRightTextClickListener {
            postUserName(inflate.nameTitle.text.toString())
        }

        //初始化数据
        GlobalsUserManager.userInfo?.apply {
            inflate.nameTitle.setText(this.name)
        }

        inflate.nameTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                updateCompeleteStatus(s?.length ?: 0 > 0)

                try {
                    val index: Int = inflate.nameTitle.getSelectionStart() - 1
                    if (index > 0) {
                        if (isEmojiCharacter(s!![index])) {
                            val edit: Editable? = inflate.nameTitle.getText()
                            edit?.delete(s.length - 2, s.length)
                        }
                        val srtByte = s.toString().toByteArray(charset("GBK"))
                        if (srtByte.size > 32) {
                            s.delete(s.length - 1, s.length)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun updateCompeleteStatus(isCanClick: Boolean) {

        val completeColor =
            if (isCanClick) getColorRes(R.color.colorPrimary) else getColorRes(R.color.color_9)
        inflate.commonTitle.tv_right?.setTextColor(completeColor)
        inflate.commonTitle.tv_right?.isEnabled = isCanClick
    }

    fun postUserName(userName: String) {
        if (userName.isEmpty()) {
            toast("昵称不能为空")
            return
        }
        //如果没变也不发送
        if (userName == GlobalsUserManager.userInfo?.name) {
            toast("你没有修改哦~")
            return
        }
        mViewModel.postUserInfo(userName).observe(this, Observer {
            if (it.isSuccess) {
                //将最新名字
                GlobalsUserManager.userInfo?.name = userName
                SPUserUtils.getInstance().saveUserInfo(GlobalsUserManager.userInfo)
                LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_USERINFO_CHANGE).postValue(
                    GlobalsUserManager.userInfo
                )
                finish()
            } else {
                toast(it.msg)
            }
        })
    }

    fun isEmojiCharacter(codePoint: Char): Boolean {
        return !(codePoint.toInt() == 0x0 || codePoint.toInt() == 0x9 || codePoint.toInt() == 0xA || codePoint.toInt() == 0xD || codePoint.toInt() >= 0x20 && codePoint.toInt() <= 0xD7FF) || codePoint.toInt() >= 0xE000 && codePoint.toInt() <= 0xFFFD || codePoint.toInt() >= 0x10000 && codePoint.toInt() <= 0x10FFFF
    }

}