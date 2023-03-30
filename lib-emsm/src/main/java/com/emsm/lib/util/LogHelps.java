package com.emsm.lib.util;

import android.util.Log;

/**
 * @Author emsm
 * @Time 2022/10/3 15:19
 * @Description 日志封装
 */
public class LogHelps {
    // 类名
    static String className = "";

    // 方法名
    static String methodName;

    // 文件名
    static String fileName;

    // 行数
    static int lineNumber;

    /**
     * 判断是否可以调试
     *
     * @return BuildConfig.DEBUG
     */
    public static boolean isDebuggable() {
        // return BuildConfig.DEBUG;
        return true;
    }

    private static String createLog(String log) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("(").append(fileName).append(":").append(lineNumber).append(") ");
        buffer.append(methodName);
        buffer.append(": ");
        buffer.append(log);
        return buffer.toString();
    }

    /**
     * 获取文件名、方法名、所在行数
     *
     * @param sElements 实参
     */
    private static void getMethodNames(StackTraceElement[] sElements) {
        // className = sElements[1].getClassName();
        methodName = sElements[1].getMethodName();
        fileName = sElements[1].getFileName();
        lineNumber = sElements[1].getLineNumber();
    }

    public static void e(String... message) {
        if (!isDebuggable())
            return;

        // 获取到堆栈轨迹的两种方法
        // Thread.currentThread().getStackTrace()
        getMethodNames(new Throwable().getStackTrace());
        Log.e(className, createLog(message));
    }

    public static void i(String... messages) {
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.i(className, createLog(messages));
    }

    public static void d(String... messages) {
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.d(className, createLog(messages));
    }

    public static void v(String... messages) {
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.v(className, createLog(messages));
    }

    public static void w(String... messages) {
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.w(className, createLog(messages));
    }

    private static String createLog(String... messages) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("(").append(fileName).append(":").append(lineNumber).append(") ");
        buffer.append(methodName);
        buffer.append(": ");
        if (messages != null) {
            for (int i = 0; i < messages.length; i++) {
                buffer.append("P" + i + ":");
                buffer.append(messages[i]);
                if (i != messages.length - 1) {
                    buffer.append(" , ");
                }
            }
        }
        return buffer.toString();
    }
}
