package com.mooc.setting.ui

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.runOnMainDelayed
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.sharepreference.SpDefaultUtils
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.SpConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.LoginService
import com.mooc.resource.widget.CommonSettingItem
import com.mooc.setting.R
import com.mooc.setting.SetApi
import com.mooc.setting.adapter.TestAccountAdapter
import com.mooc.setting.databinding.SetActivityControllerTestAccountBinding
import com.mooc.setting.model.TestAccountBean
import com.mooc.setting.ui.controller.SimpleItemTouchHelperCallback
//import kotlinx.android.synthetic.main.set_activity_controller_test_account.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

@Route(path = Paths.PAGE_CONTROLLER_TEST_ACCOUNT)
class ChooseTestAccountActivity : BaseActivity() {


    var testAccountArray = arrayListOf<TestAccountBean>()

    val testAccountAdapter by lazy {
        TestAccountAdapter(testAccountArray)
    }

    var saveCustomAccount = true //是否保存自定义账号

    private lateinit var inflater : SetActivityControllerTestAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflater = SetActivityControllerTestAccountBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        initData()

        initAdapter()

        initView()
    }

    private fun initData() {
        val gson = Gson()
        //从本地先读取
        val string = SpDefaultUtils.getInstance().getString(SpConstants.SP_TEST_ACCOUNT_ARRAY, "")
        if (string.isNotEmpty()) {
            testAccountArray.addAll(
                gson.fromJson<List<TestAccountBean>>(
                    string,
                    object : TypeToken<List<TestAccountBean>>() {}.type
                ) as ArrayList<TestAccountBean>
            )
            return
        }

        //本地为空，添加一些默认测试账号
        val defaulttestAccountJson =
            "[{\"username\":\"mingjuan\",\"userid\":\"60000\",\"openid\":\"vPuTwS891U2BkL_8YASWL49GRC8\",\"unionid\":\"oPWZdxLe5bpe8Q1alpFl39tWyjh8\"}," +
                    "{\"username\":\"xieyouming\",\"userid\":\"3306132\",\"openid\":\"osCoowEcu8RSIaeDz3zAGkJKPBuU\",\"unionid\":\"oPWZdxDjhk7a1V-TLkOXqlxTIjRI\"},"+
                    "{\"username\":\"limeng\",\"userid\":\"3306133\",\"openid\":\"osCoowL1K1EWnfEgIFjKUr-g6zS4\",\"unionid\":\"oPWZdxN3PzqIdfSkWetpSrzth3os\"},"+
                    "{\"username\":\"liuhuiying\",\"userid\":\"421\",\"openid\":\"ovPuTwZTOB1hKU7xDP86NYjD2y-4s\",\"unionid\":\"oPWZdxHJ8p-0BJ0e568dMZBcsRak\"},"+
                    "{\"username\":\"boxue\",\"userid\":\"3306134\",\"openid\":\"ovPuTwfgccjLJdtNN1FH_PJCVkvA\",\"unionid\":\"oPWZdxPJr_C90yW9LuWHCyGO45uY\"},"+
                    "{\"username\":\"luhaiye\",\"userid\":\"76\",\"openid\":\"oWaCDwXu364U8gtp1fOoYFj6Yha8\",\"unionid\":\"oPWZdxPYuMDfFr-tWvKzHb4hX64s\"},"+
                    "{\"username\":\"hhx\",\"userid\":\"698\",\"openid\":\"osCoowM-_nzFkv3TZT2Qh8Uj5bMM\",\"unionid\":\"oPWZdxGYGA4hx_hMtc6ru1tfH_vE\"},"+
                    "{\"username\":\"hzl\",\"userid\":\"940\",\"openid\":\"osCoowIJ50SFcE0MGCVSeBCUT0D8\",\"unionid\":\"oPWZdxFoVOVFYDeLuM7OsmjwQsDA\"},"+
                    "{\"username\":\"lihui2\",\"userid\":\"698\",\"openid\":\"oWaCDwfO_0XaorUUvjHB_L0HVNBU\",\"unionid\":\"oPWZdxC5AWCERk2HhXIlfvZe1pJs\"},"+
                    "{\"username\":\"lihui3\",\"userid\":\"698\",\"openid\":\"osCoowGG-nqGhcSRCPSGibeDrWgI\",\"unionid\":\"oPWZdxNFUdna5isaxUXrig2AuGaU\"},"+
                    "{\"username\":\"sisi\",\"userid\":\"698\",\"openid\":\"osCoowBYlT7uFuKRYm8W1SCvdPsw\",\"unionid\":\"oPWZdxHpqF6K7qc_LGRjrgULgFD0\"},"+
                    "{\"username\":\"xuejun\",\"userid\":\"698\",\"openid\":\"osCoowBYlT7uFuKRYm8W1SCvdPsw\",\"unionid\":\"oPWZdxHpqF6K7qc_LGRjrgULgFD0\"},"+
                    "{\"username\":\"xuejun3\",\"userid\":\"698\",\"openid\":\"osCoowPL_-TVF-YoAvc4LSDAi4NU\",\"unionid\":\"oPWZdxN7ERhO1cjFb_BYny5y9tKA\"},"+
                    "{\"username\":\"xuejun2\",\"userid\":\"777\",\"openid\":\"osCoowOPyEa-KX22mzyfZmZ6OGws\",\"unionid\":\"oPWZdxOwXiKaP9lCF9MkPJr3rFp8\"}]"
        testAccountArray.addAll(
            gson.fromJson<List<TestAccountBean>>(
                defaulttestAccountJson,
                object : TypeToken<List<TestAccountBean>>() {}.type
            ) as ArrayList<TestAccountBean>
        )

    }

    private fun initView() {
        inflater.commonTitleLayout.setOnLeftClickListener { finish() }

        //是否保存：
        inflater.csiSaveTextCount.setRightTextClickFunction {
            saveCustomAccount = !saveCustomAccount
            setSwitchIcon(inflater.csiSaveTextCount, saveCustomAccount)
        }
        //查询并登录
        inflater.csiSaveTextCount.setOnClickListener {
            val openIdText = inflater.etOpenId.text.toString()
            if (openIdText.isEmpty()) {
                toast("id为空")
                return@setOnClickListener
            }
            getOpenIdAndLogin(openIdText)
        }
    }

    fun getOpenIdAndLogin(id:String){
        lifecycleScope.launchWhenCreated {
            try {
                val bean = ApiService.retrofit.create(SetApi::class.java).getSet(id)


                val testAccountBean = TestAccountBean()
                testAccountBean.username = id
                testAccountBean.openid = bean.data.openid
                testAccountBean.unionid = bean.data.uid

                lifecycleScope.launch(Dispatchers.Main) {
                    if (saveCustomAccount) {
                        //将数据插入到列表头部
                        testAccountArray.add(0, testAccountBean)
                        testAccountAdapter.notifyDataSetChanged()
                        //保存到sp
                        val gson = Gson()
                        val toJson = gson.toJson(testAccountArray)
                        SpDefaultUtils.getInstance().putString(SpConstants.SP_TEST_ACCOUNT_ARRAY, toJson)
                    }
                    loginTextAccount(testAccountBean)
                }
            }catch (e:Exception){
                loge(e.toString())
            }


        }
    }

    private fun initAdapter() {

        inflater.rvAccount.layoutManager = LinearLayoutManager(this)


        val simpleItemTouchHelperCallback = SimpleItemTouchHelperCallback(testAccountAdapter)
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(inflater.rvAccount)

        testAccountAdapter.setOnItemClickListener { adapter, view, position ->
            //点击登录所选的账号
            val testAccountBean = adapter.data[position] as TestAccountBean
            loginTextAccount(testAccountBean)
        }
        inflater.rvAccount.adapter = testAccountAdapter
    }

    fun loginTextAccount(testAccountBean: TestAccountBean) {
        if (testAccountBean.openid.isEmpty() || testAccountBean.unionid.isEmpty()) {
            return
        }
        //退出登录
        GlobalsUserManager.clearUserInfo()
        val navigation = ARouter.getInstance().navigation(LoginService::class.java)
        val wxResponseMap = hashMapOf(
            "openid" to testAccountBean.openid,
            "unionid" to testAccountBean.unionid,
            "access_token" to "gKHElWXhJ",
            "event_name" to "android"
        )
        navigation.loginDebug(wxResponseMap)

        runOnMainDelayed(500) {
            ARouter.getInstance().build(Paths.PAGE_HOME).navigation()
        }
    }


    fun setSwitchIcon(controllerItem: CommonSettingItem, open: Boolean) {
        val csiOpenLogRightIcon =
            if (open) R.mipmap.set_ic_switch_open else R.mipmap.set_ic_switch_close
        controllerItem.rightIcon = csiOpenLogRightIcon
    }


}