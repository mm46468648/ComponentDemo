package com.mooc.commonbusiness.net;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 用于重复请求相同的时间戳可能导致退登
 * 提供一个有不同的时间戳
 */
public class TimeUtil {

    private static final AtomicLong LAST_TIME_MS = new AtomicLong();
    public static long uniqueCurrentTimeMS() {
        long now = System.currentTimeMillis();
        while(true) {
            long lastTime = LAST_TIME_MS.get();
            if (lastTime >= now)
                now = lastTime+1;
            if (LAST_TIME_MS.compareAndSet(lastTime, now))
                return now;
        }
    }
}
