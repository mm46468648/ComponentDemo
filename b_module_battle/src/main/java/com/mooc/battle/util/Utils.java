package com.mooc.battle.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.mooc.battle.R;
import com.mooc.battle.databinding.LayoutEmptyGameRefreshBinding;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class Utils {

    public static boolean checkAllNet(Context context) {
        if (context == null) {
            return false;
        }
        // 获取手机所有连接管理对象（wi_fi,net等连接的管理）
        ConnectivityManager manger = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manger != null) {
            NetworkInfo[] info = manger.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo networkInfo : info) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } else {
            return false;
        }
        return false;

    }

    public static View getGameEmptyView(Context context, int emptyImg, String tip) {
        LayoutEmptyGameRefreshBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_empty_game_refresh, null, false);
        binding.tvRefresh.setText(tip);
        binding.imgEmpty.setImageResource(emptyImg);
        return binding.getRoot();
    }

    public static RequestBody getRequestBody(JSONObject requestData) {
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestData.toString());
    }
}
