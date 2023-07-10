package com.mooc.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.login.manager.LoginManager
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.base.BaseRepository
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.UMUtil
import com.mooc.login.databinding.ActivityLoginBinding
import com.umeng.socialize.UMShareAPI
//import kotlinx.android.synthetic.main.activity_login.*

@Route(path = Paths.PAGE_LOGIN)
class LoginActivity : BaseActivity() {

    val TAG = "LoginActivity"

    companion object {
        const val PARAMS_REQUEST_CODE = "PARAMS_REQUEST_CODE";   //跳转登录页的请求码
        const val PARAMS_REQUEST_CODE_NONE = -1;   //没有任何请求码
        const val PARAMS_REQUEST_CODE_SPLASH = 0;   //splash页面请求登录
        const val PARAMS_REQUEST_CODE_HOME_MY = 1;   //Home myFragment页面请求登录
    }

    private lateinit var inflater : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflater = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        //页面打点
//        LogUtil.addLoadLog(LogPageConstants.PID_LOGIN)

        inflater.commonTitleLayout.setOnLeftClickListener {
            finish()
        }

        inflater.tvLogin.setOnClickListener {       //点击登录
            //点击事件打点
//            LogUtil.addClickLog(LogPageConstants.PID_LOGIN,LogEventConstants.EID_TO_LOGIN)

            setLoginSuccessListener()


//            if(DebugUtil.debugMode){      //测试账号登录
//                LoginDebugManager.toTestLogin()
//                return@setOnClickListener
//            }

//            LoginManager.toWxLogin()
            UMUtil.toAuthWeiXin(this as Activity)

        }
    }

    private fun setLoginSuccessListener() {

        val requstCode = intent.getIntExtra(PARAMS_REQUEST_CODE, PARAMS_REQUEST_CODE_NONE)
        if (requstCode == PARAMS_REQUEST_CODE_NONE) {
            finish()
            return
        }

        LoginManager.onSuccess = {     //设置登录成功回调

            when (requstCode) {
                PARAMS_REQUEST_CODE_SPLASH, PARAMS_REQUEST_CODE_HOME_MY -> {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onDestroy() {
        super.onDestroy()

        BaseRepository.loginActivityStart = false
    }

}
