package debug

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mooc.home.repository.UserInfoRepository
import kotlin.concurrent.thread

class HomeLauncherViewModel(var userInfomation: String = "姓名张三，年龄28") : ViewModel() {

    val TAG = "HomeViewModel"

    val repository by lazy {
        UserInfoRepository()
    }
    @JvmField
    var testStr: MutableLiveData<String> = MutableLiveData()

    fun getUserInfo() {

        thread {
            print("开始获取用户信息")
            Thread.sleep(2000)
            testStr.postValue(userInfomation)
        }
    }

    fun getTestUrl() {
        //更新接口，自己加密(验证完毕)
//        HttpService.noEncyrptApi.getUpgrade("huawei")
//                ?.subscribeOn(Schedulers.io())
//                ?.observeOn(AndroidSchedulers.mainThread())
//                ?.subscribe(io.reactivex.functions.Consumer {
//            Log.e(TAG, it?.toString() ?: "")
//        }, Consumer {
//            Log.e(TAG, it?.toString() ?: "")
//        })

        //用户接口，e盾加密
//        HttpService.userApi.getScoreRule()
//                ?.subscribeOn(Schedulers.io())
//                ?.observeOn(AndroidSchedulers.mainThread())
//                ?.subscribe(io.reactivex.functions.Consumer {
//            val scoreRule = it
//            if (!TextUtils.isEmpty(scoreRule?.getUser_score_rule())) {
//                val strRule: String = scoreRule?.getUser_score_rule() ?: ""
//                Log.e(TAG, strRule)
////                strRule = Utils.getHtml(strRule)
////                mTvRule.loadDataWithBaseURL("", strRule, "text/html", "utf-8", null)
//            }
//        }, Consumer {
//            Log.e(TAG, it.toString())
//        })

//        HttpService.userApi.getScoreRule2().enqueue(object : Callback<ScoreRule?> {
//            override fun onFailure(call: Call<ScoreRule?>, t: Throwable) {
//            }
//
//            override fun onResponse(call: Call<ScoreRule?>, response: Response<ScoreRule?>) {
//
//                if (!TextUtils.isEmpty(response.body()?.getUser_score_rule())) {
//                    val strRule: String = response.body()?.getUser_score_rule() ?: ""
//                    Log.e(TAG, strRule)
//                }
//            }
//        }
//        )

        //学堂接口，无需加密
//        HttpService.xtUserApi.getHonorList(100, 0)
//                ?.subscribeOn(Schedulers.io())
//                ?.observeOn(AndroidSchedulers.mainThread())
//                ?.subscribe(Consumer<Response<HonorDataBean>>
//                {
//                    var toString = it?.body()?.results.toString()
//                    Log.e(TAG, toString)
//                }, Consumer
//                {
//                    Log.e(TAG, it.toString())
//                })
//        GlobalScope.launch {
//            try {
//                var response = repository.getUserInfo()
//                testStr.postValue(response?.user_score_rule)
//                // 主线程更新数据...
//            } catch (e: HttpException) {
//
//            } catch (e: Throwable) {
//
//            }
//        }
    }
}