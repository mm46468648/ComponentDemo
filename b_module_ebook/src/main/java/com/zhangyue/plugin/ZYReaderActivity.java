package com.zhangyue.plugin;


import com.mooc.commonbusiness.global.GlobalsUserManager;
import com.ireader.plug.activity.AbsZYReaderActivity;

/**
 * AbsZYReaderActivity 是一个抽象的activity，宿主与SDK交互，统一通过该activity来处理。
 * 该activity不需要 setContentView（它只负责SDK和宿主程序的一些逻辑交互）
 * <p>
 * note:SDK会调用 ZYReaderActivity，需要将它复制到宿主程序里面，路径必须是 com.zhangyue.plugin.ZYReaderActivity
 */
public class ZYReaderActivity extends AbsZYReaderActivity {
    /**
     * 这里通知宿主sdk离线缓存的书籍，这里不要有过重的操作
     *
     * @param bookId
     */
    @Override
    protected void addToBookshelf(int bookId) {

    }

    /**
     * 这里通知宿主sdk成功删除离线缓存的书籍列表，这里不要有过重的操作
     *
     * @param bookIds
     */
    @Override
    protected void removeFromBookshelf(int[] bookIds) {

    }

    /**
     * scheme协议进入SDK时需要：宿主用户唯一标识
     *
     * @return
     */
    @Override
    protected String getUid() {
        return GlobalsUserManager.INSTANCE.getUid();
    }

    /**
     * scheme协议进入SDK时需要：掌阅分配给宿主的企业ID
     *
     * @return
     */
    @Override
    protected String getPlatform() {
        return AccountHelper.sPlatform;
    }

    /**
     * scheme协议进入SDK时需要：鉴权依据
     *
     * @return
     */
    @Override
    protected String getToken() {
        return AccountHelper.getToken();
    }
}
