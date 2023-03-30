package com.emsm.lib.util;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;


/**
 * @Author chentao 0000668668
 * @Time 2022/11/24
 * @Description Toast工具类
 */
public final class ToastHelps {

    private static Toast mToast;

    /**
     * 判断是否为主线程-方法一
     *
     * @return 是否主线程
     */
    public static boolean isMainLooper1() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * 判断是否为主线程-方法二
     */
    public static boolean isMainLooper2() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    public static synchronized void showToast(Context context, String content) {
        if (isMainLooper2()) {
            extracted(context, content);
            return;
        }
        HandlerUtil.getHandle().post(() -> extracted(context, content));
    }

    public static synchronized void showToast(Context context, String content, int gravity, int xOffset, int yOffset) {
        if (isMainLooper2()) {
            extracted(context, content, gravity, xOffset, yOffset);
            return;
        }

        HandlerUtil.getHandle().post(() -> {
            extracted(context, content, gravity, xOffset, yOffset);
        });
    }

    private static void extracted(Context context, String content) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }

        if (content == null || content.length() <= 0) {
            return;
        }

        mToast = Toast.makeText(context, content, Toast.LENGTH_LONG);
        mToast.show();
    }

    private static void extracted(Context context, String content, int gravity, int xOffset, int yOffset) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }

        if (content == null || content.length() <= 0) {
            return;
        }

        mToast = Toast.makeText(context, content, Toast.LENGTH_LONG);
        mToast.setGravity(gravity, xOffset, yOffset);
        mToast.show();
    }
}
