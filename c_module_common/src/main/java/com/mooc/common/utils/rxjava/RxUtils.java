package com.mooc.common.utils.rxjava;



import androidx.annotation.NonNull;

import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * Created by HugoXie on 16/5/19.
 * <p>
 * Email: Hugo3641@gamil.com
 * GitHub: https://github.com/xcc3641
 * Info: 封装 Rx 的一些方法
 */
public class RxUtils {

    public static <T> ObservableTransformer<T, T> applySchedulers() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull io.reactivex.Observable<T> observable) {
                return observable.subscribeOn(io.reactivex.schedulers.Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
