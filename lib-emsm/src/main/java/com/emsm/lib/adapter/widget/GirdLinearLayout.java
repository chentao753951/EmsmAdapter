package com.emsm.lib.adapter.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.emsm.lib.adapter.ICoreAdapter;
import com.emsm.lib.util.DisplayHelps;
import com.emsm.lib.util.LogHelps;

/**
 * @Author emsm
 * @Time 2022/9/5 19:11
 * @Description 不支持滚动和复用的LinearLayout
 */
public class GirdLinearLayout extends ListLinearLayout {

    public GirdLinearLayout(Context context) {
        this(context, null);
    }

    public GirdLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GirdLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LogHelps.i("execute0");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        autoMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        autoLayout();
    }

    private void autoMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 获取子view的数量
        int childCount = this.getChildCount();
        LogHelps.i("execute2:" + childCount);

        // 获取到本view的宽度最大值
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec) - this.getPaddingLeft() - this.getPaddingRight();
        // 需要测量view的宽度以及view的高度。所有的合集 总高度
        int sumHeight = 0;
        // 一行的子类总高
        int childMaxHeight = 0;
        // 总宽度
        int sumWidth = 0;
        // 一行的子类总宽
        int sumChildWidth = 0;
        for (int i = 0; i < childCount; i++) {
            View childAt = this.getChildAt(i);
            measureChild(childAt, widthMeasureSpec, heightMeasureSpec);

            MarginLayoutParams layoutParams = (MarginLayoutParams) childAt.getLayoutParams();

            Object data = mCoreAdapter.getData(i);
            if (data instanceof ICoreAdapter.ItemBean) {
                ICoreAdapter.ItemBean itemBean = (ICoreAdapter.ItemBean) data;
                int spanW;
                int itemSpanSize = itemBean.getItemSpanSize();
                if (itemSpanSize == 0) {
                    spanW = 0;
                } else {
                    spanW = maxWidth / itemSpanSize;
                }
                if (childAt.getMeasuredWidth() != spanW) {
                    layoutParams.width = spanW;
                    childAt.setLayoutParams(layoutParams);
                }
            }

            int childWidth = childAt.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            int childHeight = childAt.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;

            // 如果小于两者相加，所以超了，需要计算高度
            // 取高度最大值，也就是所有控件的最大值
            // 加完之后要清除，否则下一行高度无法计算
            // 判断子类高度最大值
            if (maxWidth < (sumChildWidth + childWidth)) {
                // 跨行
                sumHeight += childMaxHeight;
                childMaxHeight = 0;
                // 跟自己比较，获取最大值，优先取最大
                sumWidth = Math.max(sumChildWidth, sumChildWidth);
                sumChildWidth = 0;
            }
            childMaxHeight = (int) Math.max(childMaxHeight, childHeight);
            // 取子类行总宽，需要判断父类的宽度
            sumChildWidth += childWidth;
        }
        // 因为最后一行可能没有超过，所以不会进入，则需要重新加一下最后一行
        sumHeight += (childMaxHeight);
        sumWidth = Math.max(sumChildWidth, sumWidth);
        setMeasuredDimension(DisplayHelps.getMeasureWidth(this, widthMeasureSpec, sumWidth),
                DisplayHelps.getMeasureHeight(this, heightMeasureSpec, sumHeight));
    }

    private void autoLayout() {
        int count = getChildCount();
        // 定义列宽
        int lineWidth = 0;
        // 定义行高
        int lineHeight = 0;
        // 定义上、左边距
        int top = 0, left = 0;
        for (int i = 0; i < count; i++) {
            View childAt = getChildAt(i);
            MarginLayoutParams layoutParams = (MarginLayoutParams) childAt.getLayoutParams();
            //因为onMeasure(int widthMeasureSpec, int heightMeasureSpec)方法已经执行完，所有这里我们可以直接调用
            //子view的宽+左右边距
            int childWidth = childAt.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            int childHeight = childAt.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
            //这里的if判断和onMeasure中是一样的逻辑，不再赘述
            if (childWidth + lineWidth > getMeasuredWidth()) {
                //累加top
                top += lineHeight;
                //因为换行了left置为0
                left = 0;
                lineHeight = childHeight;
                lineWidth = childWidth;
            } else {
                lineHeight = Math.max(lineHeight, childHeight);
                //行宽累加
                lineWidth += childWidth;
            }
            // 计算子view的左、上、右、下的值
            int lc = left;
            int tc = top;
            // 右边就等于自己的宽+左边的边距即lc
            int rc = lc + childAt.getMeasuredWidth();
            //底部逻辑同上
            int bc = tc + childAt.getMeasuredHeight();
            //布局
            childAt.layout(lc, tc, rc, bc);
            //这一句很重要，因为一行中有多个view，所有left是累加的关系。
            left += childWidth;
        }
    }
}
