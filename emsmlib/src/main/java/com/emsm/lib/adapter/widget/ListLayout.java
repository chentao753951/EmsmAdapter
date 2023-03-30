package com.emsm.lib.adapter.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/hezihaog/ListLayout
 * 垂直列表布局，支持类似RecyclerView的Adapter、ViewHolder的写法，但没有它的条目复用和滚动能力
 * 为了和其他滚动控件协调滚动的灵活性，所以需要滚动效果时，需要包一层NestScrollView来实现
 */
public class ListLayout extends LinearLayout {
    public static final long NO_ID = -1;
    /**
     * 适配器
     */
    private Adapter mAdapter;
    /**
     * 列表数据观察者，当数据notifyChange时，重新填充子View
     */
    private final ListLayoutDataObserver mObserver = new ListLayoutDataObserver();

    public ListLayout(Context context) {
        this(context, null);
    }

    public ListLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        //子View垂直排列
        setOrientation(LinearLayout.VERTICAL);
    }

    /**
     * 设置适配器
     */
    public void setAdapter(Adapter adapter) {
        //旧的Adapter取消订阅
        if (mAdapter != null) {
            mAdapter.unregisterAdapterDataObserver(mObserver);
        }
        //新的Adapter设置订阅
        mAdapter = adapter;
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
        }
        //重新填充视图
        populate();
    }

    /**
     * 获取适配器
     */
    public Adapter getAdapter() {
        return mAdapter;
    }

    /**
     * 按数据填充视图
     */
    private void populate() {
        //清除掉之前的子View
        removeAllViews();
        //开始新建子View
        int itemCount = mAdapter.getItemCount();
        for (int position = 0; position < itemCount; position++) {
            long itemId = mAdapter.getItemId(position);
            int itemViewType = mAdapter.getItemViewType(position);
            //创建ViewHolder
            ViewHolder viewHolder = mAdapter.onCreateViewHolder(this, itemViewType);
            //设置Item的布局参数
            ViewGroup.LayoutParams itemLp = viewHolder.itemView.getLayoutParams();
            if (itemLp == null) {
                itemLp = new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                );
            }
            //设置相关属性
            viewHolder.setAdapter(mAdapter);
            viewHolder.setItemViewType(itemViewType);
            viewHolder.setAdapterPosition(position);
            viewHolder.setItemId(itemId);
            //渲染ViewHolder，内部会渲染布局
            mAdapter.onBindViewHolder(viewHolder, position);
            //添加子View
            addView(viewHolder.itemView, itemLp);
        }
    }

    public abstract static class Adapter<VH extends ViewHolder> {
        private final AdapterDataObservable mObservable = new AdapterDataObservable();

        public abstract VH onCreateViewHolder(ViewGroup parent, int itemType);

        public abstract void onBindViewHolder(VH holder, int position);

        public int getItemViewType(int position) {
            return 0;
        }

        public long getItemId(int position) {
            return NO_ID;
        }

        public abstract int getItemCount();

        /**
         * 是否有观察者
         */
        public final boolean hasObservers() {
            return mObservable.hasObservers();
        }

        /**
         * 注册列表数据观察者
         */
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            mObservable.registerObserver(observer);
        }

        /**
         * 取消注册数据观察者
         */
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            mObservable.unregisterObserver(observer);
        }

        /**
         * 通知数据更新
         */
        public final void notifyDataSetChanged() {
            if (hasObservers()) {
                mObservable.notifyChanged();
            }
        }
    }

    public abstract static class ViewHolder {
        long mItemId = NO_ID;

        /**
         * 适配器
         */
        private Adapter adapter;
        /**
         * 条目View
         */
        public final View itemView;
        /**
         * 条目类型
         */
        private int mItemViewType;
        /**
         * 位置
         */
        private int adapterPosition;
        /**
         * 条目Id
         */
        private long itemId;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }

        public void setItemViewType(int itemViewType) {
            mItemViewType = itemViewType;
        }

        public int getItemViewType() {
            return mItemViewType;
        }

        public void setAdapter(Adapter adapter) {
            this.adapter = adapter;
        }

        public Adapter getAdapter() {
            return adapter;
        }

        public void setAdapterPosition(int adapterPosition) {
            this.adapterPosition = adapterPosition;
        }

        public int getAdapterPosition() {
            return adapterPosition;
        }

        public void setItemId(long itemId) {
            this.mItemId = itemId;
        }

        public long getItemId() {
            return mItemId;
        }
    }

    public abstract static class AdapterDataObserver {
        public void onChanged() {
        }
    }

    /**
     * 列表数据观察者，当数据notifyChange时，重新填充子View
     */
    private class ListLayoutDataObserver extends AdapterDataObserver {
        ListLayoutDataObserver() {
        }

        @Override
        public void onChanged() {
            super.onChanged();
            //数据更新，重新填充子View
            populate();
        }
    }

    /**
     * 适配器被观察者
     */
    private static class AdapterDataObservable extends Observable<AdapterDataObserver> {
        /**
         * 是否有观察者
         */
        public boolean hasObservers() {
            return !mObservers.isEmpty();
        }

        /**
         * 通知观察者数据改变
         */
        public void notifyChanged() {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged();
            }
        }
    }

    /**
     * 被观察者
     */
    private static class Observable<T> {
        /**
         * 观察者列表
         */
        protected final List<T> mObservers = new ArrayList<T>();

        /**
         * 注册观察者
         */
        public void registerObserver(T observer) {
            if (observer == null) {
                throw new IllegalArgumentException("observer不能为空");
            }
            synchronized (mObservers) {
                if (!mObservers.contains(observer)) {
                    mObservers.add(observer);
                }
            }
        }

        /**
         * 取消注册观察者
         */
        public void unregisterObserver(T observer) {
            if (observer == null) {
                throw new IllegalArgumentException("observer不能为空");
            }
            synchronized (mObservers) {
                mObservers.remove(observer);
            }
        }

        /**
         * 取消订阅所有观察者
         */
        public void unregisterAll() {
            synchronized (mObservers) {
                mObservers.clear();
            }
        }
    }
}