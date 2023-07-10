package com.mooc.my.ui

import android.os.Bundle
import android.text.TextUtils
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.my.R
import com.mooc.my.fragment.UserFollowFragment
import com.mooc.commonbusiness.route.Paths
import com.mooc.my.databinding.ActivityQuestionMoreBinding
import com.mooc.my.databinding.ActivityUserFollowBinding
//import kotlinx.android.synthetic.main.activity_user_follow.*

/**
 *用户关注和粉丝列表页面
 * @author limeng
 * @date 2021/2/20
 */
@Route(path = Paths.PAGE_USERFOLLOW)
class UserFollowActivity : BaseActivity() {
    var user_id: String? = null
    var type: Int = 0

    private lateinit var inflate : ActivityUserFollowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflate = ActivityUserFollowBinding.inflate(layoutInflater)
        setContentView(inflate.root)
        inflate.commonTitle.setOnLeftClickListener { finish() }
        type = intent.getIntExtra(IntentParamsConstants.Follow_Fans, 0)//0 粉丝 1 关注的人
        user_id = intent.getStringExtra(IntentParamsConstants.MY_USER_ID)


        val fragment = UserFollowFragment()
        fragment.type = type
        fragment.user_id = user_id
        setTitle()

        supportFragmentManager.beginTransaction().add(R.id.fragment, fragment).commit()
    }

    private fun setTitle() {
        val userBean = GlobalsUserManager.userInfo
        if (!TextUtils.isEmpty(user_id) && userBean != null) {
            if (userBean.id.equals(user_id)) {
                when (type) {
                    0 -> {
                        inflate.commonTitle.middle_text = getString(R.string.text_user_fans_my)
                    }
                    1 -> {
                        inflate.commonTitle.middle_text = getString(R.string.text_user_follow_my)
                    }
                    else -> {
                    }
                }

            } else {
                when (type) {
                    0 -> {
                        inflate.commonTitle.middle_text = getString(R.string.text_user_fans_other)
                    }
                    1 -> {
                        inflate.commonTitle.middle_text = getString(R.string.text_user_follow_other)
                    }
                    else -> {
                    }
                }

            }
        }
    }
}