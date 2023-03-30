package com.emsm.lib.util;

import android.os.Handler;
import android.os.Looper;

/**
 * @Author emsm
 * @Time 2022/10/13 8:52
 * @Description
 */
public class HandlerUtil {
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    public static Handler getHandle() {
        return mHandler;
    }
}
