package debug

import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.common.ktextends.runOnMainDelayed
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.model.UserInfo
import com.mooc.commonbusiness.route.Paths


class EbookLauncherActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val testJson = "{\n" +
                "\t\"id\": 3306132,\n" +
                "\t\"nickname\": \"解忧杂货铺9b50a2c757784273\",\n" +
                "\t\"avatar\": \"https://static.moocnd.ykt.io/ucloud/moocnd/img/c770b2a4e7f4f96ecf06f8fdc747e55b.jpg\",\n" +
                "\t\"email\": \"\",\n" +
                "\t\"telephone\": \"\",\n" +
                "\t\"name\": \"解忧杂货铺\",\n" +
                "\t\"gender\": \"\",\n" +
                "\t\"birthday\": \"\",\n" +
                "\t\"location\": \"\",\n" +
                "\t\"level_of_education\": \"\",\n" +
                "\t\"profession\": \"\",\n" +
                "\t\"hobby\": \"\",\n" +
                "\t\"introduction\": \"\",\n" +
                "\t\"check_name_result\": 1,\n" +
                "\t\"shuoshuo\": \"\",\n" +
                "\t\"study_index\": false,\n" +
                "\t\"user_follow_count\": 9,\n" +
                "\t\"user_be_followed_count\": 1,\n" +
                "\t\"uuid\": \"dddf663656\",\n" +
                "\t\"is_checkin\": false\n" +
                "}"

        GlobalsUserManager.userInfo = GsonManager.getInstance().convert(testJson, UserInfo::class.java)
    }

    override fun onResume() {
        super.onResume()


        runOnMainDelayed(2000) {
            ARouter.getInstance().build(Paths.PAGE_EBOOK_DETAIL).withString(IntentParamsConstants.EBOOK_PARAMS_ID,"619").greenChannel().navigation()
        }

    }
}