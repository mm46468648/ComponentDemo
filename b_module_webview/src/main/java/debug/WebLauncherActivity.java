package debug;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.mooc.commonbusiness.route.Paths;

public class WebLauncherActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        request();
        ARouter.getInstance().build(Paths.PAGE_WEB)
                .withString("params_url", "https://www.baidu.com")
                .withString("params_title", "百度一下，你就知道")
                .navigation();
        finish();
    }

    //申请读写权限
    private void request() {

    }
}
