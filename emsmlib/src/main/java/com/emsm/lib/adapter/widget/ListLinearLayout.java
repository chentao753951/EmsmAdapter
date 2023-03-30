package com.emsm.lib.adapter.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.emsm.lib.adapter.CoreAdapter;
import com.emsm.lib.adapter.ICoreAdapter;
import com.emsm.lib.util.DisplayHelps;
import com.emsm.lib.util.LogHelps;

import java.util.List;
// getMeasuredWidth() 在onMeasure执行的时就可以获取屏幕宽度像素
// getWidth()  在onLayout执行的时候才可以获取屏幕宽度像素

/**
 * @Author emsm
 * @Time 2022/9/5 19:11
 * @Description 不支持滚动和复用的LinearLayout
 */
public class ListLinearLayout extends LinearLayout {
    protected ECoreAdapter mCoreAdapter;
    private SparseArray<CoreAdapter.ViewHolder> mViews = new SparseArray<>();

    public ListLinearLayout(Context context) {
        this(context, null);
    }

    public ListLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LogHelps.i("execute0");
    }

    public void setAdapter(ECoreAdapter coreAdapter) {
        // 子View垂直排列
        setAdapter(coreAdapter, LinearLayout.VERTICAL);
    }

    public void setAdapter(ECoreAdapter coreAdapter, int orientation) {
        mCoreAdapter = coreAdapter;
        setOrientation(orientation);
        int childCount = getChildCount();
        LogHelps.i("execute1:" + childCount);
        DisplayHelps.ScreenSize.getInstance().test(getContext());
        populate();
    }

    // 按数据填充视图
    private void populate() {
        mViews.clear();
        // 清除掉之前的子View
        removeAllViews();
        // 开始新建子View
        int itemCount = mCoreAdapter.getItemCount();
        for (int position = 0; position < itemCount; position++) {
            int itemViewType = mCoreAdapter.getItemViewType(position);
            //创建ViewHolder
            CoreAdapter.ViewHolder viewHolder = mCoreAdapter.onCreateViewHolder(this, itemViewType, position);
            //设置Item的布局参数
            ViewGroup.LayoutParams itemLp = viewHolder.itemView.getLayoutParams();
            if (itemLp == null) {
                itemLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            }

            //渲染ViewHolder，内部会渲染布局
            mCoreAdapter.onBindViewHolder(viewHolder, position);
            mViews.put(position, viewHolder);
            //添加子View
            addView(viewHolder.itemView, itemLp);
        }
    }

    public void notifyDataSetChangedNew() {
        populate();
    }

    public void notifyItemChangedNew(int position) {
        CoreAdapter.ViewHolder viewHolder = mViews.get(position);
        mCoreAdapter.onBindViewHolder(viewHolder, position);
    }

    public abstract static class ECoreAdapter<T> extends CoreAdapter<T> {

        public ECoreAdapter(Context context, List<T> list, int layoutId) {
            super(context, list, layoutId);
        }

        public ECoreAdapter(Context context, List<T> list, ICoreAdapter<T> ICoreAdapter) {
            super(context, list, ICoreAdapter);
        }

        public ECoreAdapter(Context context, List<T> list, ICoreAdapter<T> iCoreAdapter, ICoreAdapter.ICall.IClick iClick) {
            super(context, list, iCoreAdapter, iClick);
        }

        public ECoreAdapter(Context context, List<T> list, ICoreAdapter<T> iCoreAdapter, ICoreAdapter.ICall.IClick iClick, ICoreAdapter.ICall.ILongClick iLongClick) {
            super(context, list, iCoreAdapter, iClick, iLongClick);
        }

        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType, int position) {
            LogHelps.i("recyclerView.getId:onCreateViewHolder");
            if (mICoreAdapter != null) {
                mLayoutId = mICoreAdapter.getLayoutId(viewType);
            }

            ViewHolder viewHolder = ViewHolder.createViewHolder(mContext, parent, mLayoutId);
            viewHolder.itemView.setOnClickListener(v -> {
                try {
                    // 注意该值不能放在外部
                    int adapterPosition = position;
                    onItemClick(v, viewHolder, adapterPosition, getItemViewType(adapterPosition), getData(adapterPosition));
                } catch (Exception e) {
                    LogHelps.e("Exception:" + e.getMessage());
                }
            });

            viewHolder.itemView.setOnLongClickListener(v -> {
                try {
                    // 注意该值不能放在外部
                    int adapterPosition = position;
                    return onItemLongClick(v, viewHolder, adapterPosition, getItemViewType(adapterPosition), getData(adapterPosition));
                } catch (Exception e) {
                    LogHelps.e("Exception:" + e.getMessage());
                }
                // 返回true 不会执行OnClick false会执行
                return true;
            });
            return viewHolder;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogHelps.i("");
        if (false) {
            mViews.clear();
            mViews = null;
            mCoreAdapter.clearData();
            mCoreAdapter = null;
        }
    }
}
