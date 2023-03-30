package com.emsm.lib.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

/**
 * @Author emsm
 * @Time 2022/10/12 23:31
 * @Description 屏幕像素操作类
 */
public final class DisplayHelps {

    public static View inflate(Context applicationContext, int layoutId) {
        return LayoutInflater.from(applicationContext).inflate(layoutId, null);
    }

    // px->dp
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    // dp->px
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    // 屏幕宽度-像素px
    public static int getScreenWidthPx() {
        // float ht_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ht, getResources().getDisplayMetrics());
        // float wt_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, wt, getResources().getDisplayMetrics());
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    // 屏幕高度-像素px
    public static int getScreenHeightPx() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    // 计算控件高
    public static int getMeasureHeight(View curView, int heightMeasureSpec, int sumHeight) {
        int result = 0;
        int mode = View.MeasureSpec.getMode(heightMeasureSpec);
        int size = View.MeasureSpec.getSize(heightMeasureSpec);
        // 如果为精确值模式，那么不用判断了，直接返回
        if (mode == View.MeasureSpec.EXACTLY) {
            result = size;
            return result;
        }
        result = sumHeight + curView.getPaddingTop() + curView.getPaddingBottom();
        if (mode == View.MeasureSpec.AT_MOST) {
            result = Math.min(size, result);
        }
        return result;
    }

    // 计算控件宽
    public static int getMeasureWidth(View curView, int widthMeasureSpec, int sumWidth) {
        int result = 0;
        int mode = View.MeasureSpec.getMode(widthMeasureSpec);
        int size = View.MeasureSpec.getSize(widthMeasureSpec);
        // 如果为精确值模式，那么不用判断了，直接返回
        if (mode == View.MeasureSpec.EXACTLY) {
            result = size;
            return result;
        }
        result = widthMeasureSpec + curView.getPaddingLeft() + curView.getPaddingRight();
        if (mode == View.MeasureSpec.AT_MOST) {
            result = Math.min(size, result);
        }
        return result;
    }

    public static class ScreenSize {
        private WindowManager mWindowManager;
        private DisplayMetrics mDisplayMetrics;

        public static ScreenSize getInstance() {
            return Holder.INSTANCE;
        }

        private static class Holder {
            private static final ScreenSize INSTANCE = new ScreenSize();
        }

        private ScreenSize() {

        }

        public void init(Context context) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics = new DisplayMetrics());
        }

        // 屏幕高度-像素px
        public int getScreenHeightPx() {
            return mDisplayMetrics.heightPixels;
        }

        // 屏幕宽度-像素px
        public int getScreenWidthPx() {
            return mDisplayMetrics.widthPixels;
        }

        // 屏幕高度-密度dp: 屏幕高度-像素px/屏幕密度
        public int getScreenHeightDip() {
            float density = mDisplayMetrics.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
            int densityDpi = mDisplayMetrics.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）

            return (int) (getScreenHeightPx() / density);// 屏幕高度(dp)
        }

        // 屏幕宽度-密度dp: 屏幕宽度-像素px/屏幕密度
        public int getScreenWidthDip() {
            float density = mDisplayMetrics.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
            int densityDpi = mDisplayMetrics.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）

            return (int) (getScreenWidthPx() / density);  // 屏幕宽度(dp)
        }

        public void test(Context context) {
            int screenWidthDip = getScreenWidthDip();
            int screenHeightDip = getScreenHeightDip();
            int screenWidthPx = getScreenWidthPx();
            int screenHeightPx = getScreenHeightPx();
            LogHelps.i("getScreenWidthPx:" + screenWidthPx +
                    " getScreenHeightPx:" + screenHeightPx +
                    " getScreenWidthDip:" + screenWidthDip +
                    " getScreenHeightDip:" + screenHeightDip);

            LogHelps.i("DisplayHelps.getScreenWidthPx:" + DisplayHelps.getScreenWidthPx() +
                    " DisplayHelps.getScreenHeightPx:" + DisplayHelps.getScreenHeightPx());

            if (context != null) {
                int w = DisplayHelps.dip2px(context, screenWidthDip);
                int h = DisplayHelps.dip2px(context, screenHeightDip);
                LogHelps.i("dip2px_w:" + w + " dip2px_h:" + h);

                int w1 = DisplayHelps.px2dip(context, screenWidthPx);
                int h1 = DisplayHelps.px2dip(context, screenHeightPx);
                LogHelps.i("px2dip(_w1:" + w1 + " px2dip_h1:" + h1);
            }
        }
    }
}
