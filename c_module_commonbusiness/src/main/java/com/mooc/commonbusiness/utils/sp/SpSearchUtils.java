package com.mooc.commonbusiness.utils.sp;

import android.text.TextUtils;

import com.mooc.common.global.AppGlobals;
import com.mooc.common.utils.sharepreference.BaseSPreferenceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpSearchUtils extends BaseSPreferenceUtils {

    private static SpSearchUtils spUserUtils;
    private List<String> historyList;
    public static final String KEY_USER_SEARCH_HISTORY = "search_history";
    private SpSearchUtils() {
        super(AppGlobals.INSTANCE.getApplication(),  AppGlobals.INSTANCE.getApplication().getPackageName() + "_preferences");
    }

    public  static synchronized SpSearchUtils getInstance() {
        if (spUserUtils == null) {
            spUserUtils = new SpSearchUtils();
        }
        return spUserUtils;
    }


    /**
     * 获取历史记录
     *
     * @return
     */
    public String getSearchHistory() {
        return getString(KEY_USER_SEARCH_HISTORY, "");

    }
    /**
     * 保存搜索记录
     *
     * @param inputText 输入的历史记录
     */
    public void saveSearchHistory(String inputText) {

        if (TextUtils.isEmpty(inputText)) {
            return;
        }

        String longHistory = getSearchHistory();        //获取之前保存的历史记录
        String[] tmpHistory=new String[]{};
        if (!longHistory.isEmpty()) {
            tmpHistory = longHistory.split(",");
        }//逗号截取 保存在数组中


        historyList = new ArrayList<String>(Arrays.asList(tmpHistory));          //将改数组转换成ArrayList

        if (historyList.size() > 0) {
            //移除之前重复添加的元素
            for (int i = 0; i < historyList.size(); i++) {
                if (inputText.equals(historyList.get(i))) {
                    historyList.remove(i);
                    break;
                }
            }

            historyList.add(0, inputText);                           //将新输入的文字添加集合的第0位也就是最前面

            if (historyList.size() > 10) {
                historyList.remove(historyList.size() - 1);         //最多保存10条搜索记录 删除最早搜索的那一项
            }

            //逗号拼接
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < historyList.size(); i++) {
                if (historyList.size()==1) {
                    sb.append(historyList.get(i));
                }else{
                    sb.append(historyList.get(i) + ",");
                }
            }

            //保存到sp
            savaSearchHistory(sb.toString());
        } else {
            savaSearchHistory(inputText);
        }
    }
    public void savaSearchHistory(String string) {
        putString(KEY_USER_SEARCH_HISTORY, string);
    }

    /**
     * 清楚历史记录
     */
    public void removeHistory() {
        remove(KEY_USER_SEARCH_HISTORY);
    }
}
