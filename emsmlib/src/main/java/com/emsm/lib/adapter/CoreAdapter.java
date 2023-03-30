package com.emsm.lib.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.emsm.lib.util.LogHelps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author emsm
 * @Time 2022/9/5 19:11
 * @Description 多功能适配器
 */
public abstract class CoreAdapter<T> extends RecyclerView.Adapter<CoreAdapter.ViewHolder> implements ICoreAdapter.ICall.IBindHolder<T>, ICoreAdapter.ICall.IClick<T>, ICoreAdapter.ICall.ILongClick<T> {
    protected ICoreAdapter mICoreAdapter;
    protected ICoreAdapter.ICall.IBindHolder mIBindHolder;
    protected ICoreAdapter.ICall.IClick mIClick;
    protected ICoreAdapter.ICall.ILongClick mILongClick;

    private RecyclerView mRecyclerView;

    protected Context mContext;
    protected List<T> mData;
    protected int mLayoutId;

    public CoreAdapter setIBindHolder(ICoreAdapter.ICall.IBindHolder mIBindHolder) {
        this.mIBindHolder = mIBindHolder;
        return this;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    private CoreAdapter(Context context, List<T> list) {
        if (context == null) {
            throw new NullPointerException("context is not null");
        }
        mContext = context;
        this.mData = (list == null ? new ArrayList<>() : list);
    }

    public CoreAdapter(Context context, List<T> list, int layoutId) {
        this(context, list);
        mLayoutId = layoutId;
    }

    public CoreAdapter(Context context, List<T> list, ICoreAdapter<T> ICoreAdapter) {
        this(context, list);
        mICoreAdapter = ICoreAdapter;
    }

    public CoreAdapter(Context context, List<T> list, ICoreAdapter<T> iCoreAdapter, ICoreAdapter.ICall.IClick iClick) {
        this(context, list, iCoreAdapter);
        mIClick = iClick;
    }

    public CoreAdapter(Context context, List<T> list, ICoreAdapter<T> iCoreAdapter, ICoreAdapter.ICall.IClick iClick, ICoreAdapter.ICall.ILongClick iLongClick) {
        this(context, list, iCoreAdapter, iClick);
        mILongClick = iLongClick;
    }

    @Override
    public int getItemCount() {
        if (mData.size() <= 0) {
            return 0;
        }
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.size() <= 0) {
            return 0;
        }

        if (mICoreAdapter == null) {
            return 0;
        }
        return mICoreAdapter.getItemViewType(position, mData.get(position));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LogHelps.i("recyclerView.getId:onCreateViewHolder");
        if (mICoreAdapter != null) {
            mLayoutId = mICoreAdapter.getLayoutId(viewType);
        }

        ViewHolder viewHolder = ViewHolder.createViewHolder(mContext, parent, mLayoutId);

        viewHolder.itemView.setOnClickListener(v -> {
            try {
                // 注意该值不能放在外部
                int adapterPosition = viewHolder.getAdapterPosition();
                onItemClick(v, viewHolder, adapterPosition, getItemViewType(adapterPosition), getData(adapterPosition));
            } catch (Exception e) {
                LogHelps.e("Exception:" + e.getMessage());
            }
        });

        viewHolder.itemView.setOnLongClickListener(v -> {
            try {
                // 注意该值不能放在外部
                int adapterPosition = viewHolder.getAdapterPosition();
                return onItemLongClick(v, viewHolder, adapterPosition, getItemViewType(adapterPosition), getData(adapterPosition));
            } catch (Exception e) {
                LogHelps.e("Exception:" + e.getMessage());
            }
            // 返回true 不会执行OnClick false会执行
            return true;
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LogHelps.i("recyclerView.getId:onBindViewHolder");
        try {
            onBindHolder(holder, position, getItemViewType(position), mData.get(position));
        } catch (Exception e) {
            LogHelps.e("Exception:" + e.getMessage());
        }

        try {
            if (mIBindHolder == null) {
                return;
            }
            mIBindHolder.onBindHolder(holder, position, getItemViewType(position), mData.get(position));
        } catch (Exception e) {
            LogHelps.e("Exception:" + e.getMessage());
        }
    }

    // 用于设置网格- 在RecyclerView提供数据的时候调用
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        LogHelps.i("recyclerViewGetId:" + recyclerView.getId());

        if (mData == null) {
            LogHelps.e("mData is null");
            return;
        }
        if (!(mICoreAdapter instanceof ICoreAdapter.ICall.ISpanGrid)) {
            // LogHelps.w("mMultiItemTypeSupport instanceof MultiItemTypeSupport.ISpanGrid error: " + mMultiItemTypeSupport);
            return;
        }
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        // 如果设置合并单元格就占用SpanCount那个多个位置
        if ((layoutManager instanceof GridLayoutManager)) {
            final ICoreAdapter.ICall.ISpanGrid iSpan = (ICoreAdapter.ICall.ISpanGrid) mICoreAdapter;
            final GridLayoutManager manager = (GridLayoutManager) layoutManager;
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int spanSize = iSpan.getSpanSize(mData.get(position));
                    // 如果当前设置spanSize大于总SpanCount 那么就选中最小值
                    return Math.min(spanSize, manager.getSpanCount());
                }
            });
        }
    }

    // 用于设置流式网格
    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        int layoutPosition = holder.getLayoutPosition();
        int adapterPosition = holder.getAdapterPosition();
        LogHelps.i("layoutPosition_" + layoutPosition + " adapterPosition_" + adapterPosition);

        if (mData == null) {
            LogHelps.e("mData is null");
            return;
        }
        if (!(mICoreAdapter instanceof ICoreAdapter.ICall.ISpanStaggeredGrid)) {
            // LogHelps.w("mMultiItemTypeSupport instanceof MultiItemTypeSupport.ISpanStaggeredGrid error: " + mMultiItemTypeSupport);
            return;
        }

        final ICoreAdapter.ICall.ISpanStaggeredGrid iSpan = (ICoreAdapter.ICall.ISpanStaggeredGrid) mICoreAdapter;
        final ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();

        // 操作流式布局 判断是否设置合并单元格
        if (iSpan.isSpanStaggeredGrid(mData.get(layoutPosition)) && layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
            p.setFullSpan(true);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        LogHelps.i("recyclerView.getId:" + recyclerView.getId());
    }

    /**
     * item的点击事件
     *
     * @param view     控件
     * @param holder   控件
     * @param position 索引
     * @param itemType 布局类型
     * @param t        对象
     * @throws Exception 异常
     */
    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position, int itemType, T t) throws Exception {
        LogHelps.e("onItemClick_position" + position + "_itemType_" + itemType);
        if (mIClick == null) {
            return;
        }
        mIClick.onItemClick(view, holder, position, itemType, t);
    }

    /**
     * item的长按事件
     *
     * @param view     控件
     * @param holder   控件
     * @param position 索引
     * @param itemType 布局类型
     * @param t        对象
     * @return true 不会执行OnClick；false 会执行OnClick
     * @throws Exception 异常
     */
    @Override
    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position, int itemType, T t) throws Exception {
        LogHelps.e("onItemLongClick_position " + position + "_itemType_" + itemType);
        if (mILongClick == null) {
            return false;
        }
        return mILongClick.onItemLongClick(view, holder, position, itemType, t);
    }

    public final String getString(@StringRes int resId) {
        return mContext.getString(resId);
    }

    public final Resources getResources() {
        return mContext.getResources();
    }

    public final int getColor(@ColorRes int id) {
        return mContext.getColor(id);
    }

    public T getData(int position) {
        if (position < 0 || position > (mData.size() - 1)) {
            return null;
        }
        return mData.get(position);
    }

    public List<T> getData() {
        return mData == null ? new ArrayList<>() : mData;
    }

    public int getDataSize() {
        return mData == null ? 0 : mData.size();
    }

    public boolean addData(Collection<? extends T> c) {
        if (c == null) {
            return false;
        }

        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.addAll(c);

        notifyDataSetChanged();
        return true;
    }

    public boolean addData(T data) {
        return addData(-1, data);
    }

    public boolean addData(int position, T data) {
        if (data == null) {
            return false;
        }

        if (mData == null) {
            mData = new ArrayList<>();
        }

        if (position >= 0 && position < mData.size()) {
            mData.add(position, data);
            notifyDataSetChanged();
            return true;
        }

        mData.add(data);
        notifyDataSetChanged();
        return true;
    }

    public boolean addData(List<T> data) {
        return addData(-1, data);
    }

    public boolean addData(int position, List<T> data) {
        if (data == null || data.size() <= 0) {
            return false;
        }

        if (mData == null) {
            mData = new ArrayList<>();
        }

        if (position >= 0 && position < mData.size()) {
            mData.addAll(position, data);
            notifyItemChanged(position);
            return true;
        }

        mData.addAll(data);
        notifyDataSetChanged();
        return true;
    }

    public boolean removedData(int position) {
        if (position < 0 || position > (mData.size() - 1)) {
            return false;
        }
        mData.remove(position);
        notifyItemRemoved(position);
        return true;
    }

    public boolean removedData(int positionStart, int itemCount) {
        if (positionStart < 0 || positionStart > (mData.size() - 1)) {
            return false;
        }
        notifyItemRangeRemoved(positionStart, itemCount);
        return false;
    }

    public void clearData() {
        mData.clear();
        notifyDataSetChanged();
    }

    public void replaceAll(List<T> elem) {
        mData.clear();
        mData.addAll(elem);
        notifyDataSetChanged();
    }

    /**
     * @Author emsm
     * @Time 2022/9/5 19:08
     * @Description
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final SparseArray<View> mViews;
        private final Context mContext;

        public ViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mViews = new SparseArray<>();
        }

        public static ViewHolder createViewHolder(Context context, View itemView) {
            return new ViewHolder(context, itemView);
        }

        public static ViewHolder createViewHolder(Context context, ViewGroup parent, int layoutId) {
            View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
            return new ViewHolder(context, itemView);
        }

        /**
         * 通过viewId获取控件
         *
         * @param viewId
         * @return 返回控件
         */
        public <T extends View> T getView(int viewId) {
            View view = mViews.get(viewId);
            if (view == null) {
                view = itemView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            return (T) view;
        }

        /**
         * 设置TextView的值
         *
         * @param viewId 控件ID
         * @param text   文本
         * @return 返回当前控件
         */
        public ViewHolder setText(int viewId, Object text) {
            TextView tv = getView(viewId);
            if (tv == null || text == null) {
                return this;
            }
            tv.setText(text.toString());
            return this;
        }

        /**
         * 内存缓存，优先加载，速度最快
         * 本地缓存，次优先加载，速度快
         * 网络缓存，最后加载，速度慢，浪费流量
         *
         * @param viewId 控件ID
         * @param imgUrl url
         * @return 返回当前控件
         */
        public ViewHolder setImageUrl(int viewId, String imgUrl) {
            ImageView view = getView(viewId);
            if (view == null || imgUrl == null) {
                return this;
            }
            if (view != null && mContext != null) {
                // Glide.with(mContext).load(imgUrl).into(view);
            }
            return this;
        }

        public ViewHolder setImageResource(int viewId, int resId) {
            ImageView view = getView(viewId);
            if (view == null) {
                return this;
            }
            view.setImageResource(resId);
            return this;
        }

        public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
            ImageView view = getView(viewId);
            if (view == null) {
                return this;
            }
            view.setImageBitmap(bitmap);
            return this;
        }

        public ViewHolder setImageDrawable(int viewId, Drawable drawable) {
            ImageView view = getView(viewId);
            if (view == null) {
                return this;
            }
            view.setImageDrawable(drawable);
            return this;
        }

        public ViewHolder setBackgroundColor(int viewId, int color) {
            View view = getView(viewId);
            if (view == null) {
                return this;
            }
            view.setBackgroundColor(color);
            return this;
        }

        public ViewHolder setBackgroundRes(int viewId, int backgroundRes) {
            View view = getView(viewId);
            if (view == null) {
                return this;
            }
            view.setBackgroundResource(backgroundRes);
            return this;
        }

        public ViewHolder setTextColor(int viewId, int textColor) {
            TextView view = getView(viewId);
            if (view == null) {
                return this;
            }
            view.setTextColor(textColor);
            return this;
        }

        public ViewHolder setTextColorRes(int viewId, int textColorRes) {
            TextView view = getView(viewId);
            if (view == null) {
                return this;
            }
            view.setTextColor(mContext.getResources().getColor(textColorRes));
            return this;
        }

        @SuppressLint("NewApi")
        public ViewHolder setAlpha(int viewId, float value) {
            View view = getView(viewId);
            if (view == null) {
                return this;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                view.setAlpha(value);
            } else {
                AlphaAnimation alpha = new AlphaAnimation(value, value);
                alpha.setDuration(0);
                alpha.setFillAfter(true);
                view.startAnimation(alpha);
            }
            return this;
        }

        public ViewHolder setVisible(int viewId, boolean visible) {
            View view = getView(viewId);
            if (view == null) {
                return this;
            }
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
            return this;
        }

        public ViewHolder linkify(int viewId) {
            TextView view = getView(viewId);
            if (view == null) {
                return this;
            }
            Linkify.addLinks(view, Linkify.ALL);
            return this;
        }

        public ViewHolder setTypeface(Typeface typeface, int... viewIds) {
            for (int viewId : viewIds) {
                TextView view = getView(viewId);
                if (view == null) {
                    return this;
                }
                view.setTypeface(typeface);
                view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            }
            return this;
        }

        public ViewHolder setProgress(int viewId, int progress) {
            ProgressBar view = getView(viewId);
            if (view == null) {
                return this;
            }
            view.setProgress(progress);
            return this;
        }

        public ViewHolder setProgress(int viewId, int progress, int max) {
            ProgressBar view = getView(viewId);
            if (view == null) {
                return this;
            }
            view.setMax(max);
            view.setProgress(progress);
            return this;
        }

        public ViewHolder setMax(int viewId, int max) {
            ProgressBar view = getView(viewId);
            if (view == null) {
                return this;
            }
            view.setMax(max);
            return this;
        }

        public ViewHolder setRating(int viewId, float rating) {
            RatingBar view = getView(viewId);
            if (view == null) {
                return this;
            }
            view.setRating(rating);
            return this;
        }

        public ViewHolder setRating(int viewId, float rating, int max) {
            RatingBar view = getView(viewId);
            if (view == null) {
                return this;
            }
            view.setMax(max);
            view.setRating(rating);
            return this;
        }

        public ViewHolder setTag(int viewId, Object tag) {
            View view = getView(viewId);
            if (view == null) {
                return this;
            }
            view.setTag(tag);
            return this;
        }

        public ViewHolder setTag(int viewId, int key, Object tag) {
            View view = getView(viewId);
            if (view == null) {
                return this;
            }
            view.setTag(key, tag);
            return this;
        }

        public ViewHolder setChecked(int viewId, boolean checked) {
            Checkable view = getView(viewId);
            if (view == null) {
                return this;
            }
            view.setChecked(checked);
            return this;
        }

        /**
         * 关于事件的
         */
        public ViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
            View view = getView(viewId);
            if (view == null) {
                return this;
            }
            view.setOnClickListener(listener);
            return this;
        }

        public ViewHolder setOnTouchListener(int viewId, View.OnTouchListener listener) {
            View view = getView(viewId);
            if (view == null) {
                return this;
            }
            view.setOnTouchListener(listener);
            return this;
        }

        public ViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
            View view = getView(viewId);
            if (view == null) {
                return this;
            }
            view.setOnLongClickListener(listener);
            return this;
        }
    }
}
