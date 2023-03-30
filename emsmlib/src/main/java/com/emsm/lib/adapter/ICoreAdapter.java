package com.emsm.lib.adapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author emsm
 * @Time 2022/9/5 19:17
 * @Description 多功能适配器-布局类型接口
 */
public interface ICoreAdapter<T> {
    /**
     * 返回指定类型的itemType
     *
     * @param position 索引
     * @param t        对象
     * @return 返回指定类型的itemType
     */
    int getItemViewType(int position, T t);

    /**
     * 根据itemType类型 返回布局ID
     *
     * @param itemType 类型
     * @return 返回布局ID
     */
    int getLayoutId(int itemType);

    /**
     * @Author emsm
     * @Time 2022/9/5 19:17
     * @Description 多功能适配器-布局类型接口-Call
     */
    interface ICall<T> {

        interface IBindHolder<T> {
            void onBindHolder(CoreAdapter.ViewHolder holder, int position, int itemType, T t) throws Exception;
        }

        /**
         * @Author emsm
         * @Time 2022/9/5 19:17
         * @Description 多功能适配器-布局类型接口-Call
         */
        interface ILongClick<T> {
            /**
             * item的长按事件
             *
             * @param view     控件
             * @param holder   控件
             * @param position 索引
             * @return true 不会执行OnClick；false 会执行OnClick
             * @throws Exception 异常
             */
            boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position, int itemType, T t) throws Exception;
        }

        /**
         * @Author emsm
         * @Time 2022/9/5 19:17
         * @Description 多功能适配器-布局类型接口-Call
         */
        interface IClick<T> {
            /**
             * item的点击事件
             *
             * @param view     控件
             * @param holder   控件
             * @param position 索引
             * @throws Exception 异常
             */
            void onItemClick(View view, RecyclerView.ViewHolder holder, int position, int itemType, T t) throws Exception;
        }

        /**
         * @Author emsm
         * @Time 2022/9/5 19:17
         * @Description 多功能适配器-布局类型接口-ISpanGrid->GridLayoutManager
         */
        interface ISpanGrid<T> extends ICoreAdapter<T> {
            /**
             * spanCount 一行的份数
             * <p>比如说：spanCount=2 可展示2个item；spanCount=3 可展示3个item ...
             * SpanSize item所占据份数
             * <p>比如说：spanCount=2，SpanSize=2 item就占2份，说明item独占一行 《SpanSize==spanCount 说明item独占一行》
             *
             * @param t 对象
             * @return item所占据份数
             */
            int getSpanSize(T t);
        }

        /**
         * @Author emsm
         * @Time 2022/12/10 19:17
         * @Description 多功能适配器-布局类型接口-ISpanStaggeredGrid->StaggeredGridLayoutManager
         */
        interface ISpanStaggeredGrid<T> extends ICoreAdapter<T> {
            /**
             * @param t 对象
             * @return true 合并单元格 false 不合并正常显示
             */
            boolean isSpanStaggeredGrid(T t);
        }

        /**
         * @Author emsm
         * @Time 2022/9/5 19:17
         * @Description 多功能适配器-实现类->GridLayoutManager
         */
        abstract class ISpanGridImp<T> implements ISpanGrid<T> {

            @Override
            public int getItemViewType(int position, T t) {
                if (t instanceof ICoreAdapter.ItemBean) {
                    ItemBean itemBean = (ItemBean) t;
                    return itemBean.getItemViewType();
                }
                return 0;
            }

            @Override
            public int getSpanSize(T t) {
                if (t instanceof ICoreAdapter.ItemBean) {
                    ItemBean itemBean = (ItemBean) t;
                    return itemBean.getItemSpanSize();
                }
                return 0;
            }
        }

        /**
         * @Author emsm
         * @Time 2022/9/5 19:17
         * @Description 多功能适配器-实现类->StaggeredGridLayoutManager
         */
        abstract class ISpanStaggeredGridImp<T> implements ISpanStaggeredGrid<T> {

            @Override
            public int getItemViewType(int position, T t) {
                if (t instanceof ICoreAdapter.ItemBean) {
                    ItemBean itemBean = (ItemBean) t;
                    return itemBean.getItemViewType();
                }
                return 0;
            }

            @Override
            public boolean isSpanStaggeredGrid(T t) {
                if (t instanceof ICoreAdapter.ItemBean) {
                    ItemBean itemBean = (ItemBean) t;
                    return itemBean.isItemSpanStaggeredGrid();
                }
                return false;
            }
        }

        abstract class ICoreAdapterImp<T> implements ICoreAdapter<T> {

            @Override
            public int getItemViewType(int position, T t) {
                if (t instanceof ICoreAdapter.ItemBean) {
                    ItemBean itemBean = (ItemBean) t;
                    return itemBean.getItemViewType();
                }
                return 0;
            }
        }
    }

    /**
     * @Author emsm
     * @Time 2022/9/5 19:17
     * @Description 多功能适配器-ItemBean
     */
    class ItemBean {

        /**
         * 是否设置 StaggeredGridLayoutManager 合并单元格
         * See Also: {@link ICall.ISpanStaggeredGrid isSpanStaggeredGrid(t) }
         * true 合并单元格
         * false 不合并正常显示
         */
        private boolean isItemSpanStaggeredGrid;

        /**
         * 当前item要占用”几份“
         * See Also: {@link ICall.ISpanGrid getSpanSize(t) }
         * spanSize=0 说明该item不显示
         * spanSize=spanCount 说明该item独占一行
         */
        private int itemSpanSize;

        /**
         * 当前item展示那种类型的布局
         */
        private int itemViewType;

        /**
         * 当前item是否聚焦-说明：聚焦不代表按钮选中
         */
        private boolean isItemFocus;

        /**
         * 当前item是否选中-说明：选中那一定会聚焦
         */
        private boolean isItemSelect;

        /**
         * 当前item是否可以点击 默认全部为支持点击
         */
        private boolean isItemEnabled = true;

        /**
         * 可用作数据对象-基本数据对象
         */
        private Object itemObject;

        public Object getItemObject() {
            return itemObject;
        }

        public ItemBean setItemObject(Object itemObject) {
            this.itemObject = itemObject;
            return this;
        }

        public int getItemViewType() {
            return itemViewType;
        }

        public ItemBean setItemViewType(int itemViewType) {
            this.itemViewType = itemViewType;
            return this;
        }

        public ItemBean setItemSpanSize(int itemSpanSize) {
            this.itemSpanSize = itemSpanSize;
            return this;
        }

        public int getItemSpanSize() {
            return itemSpanSize;
        }

        public boolean isItemFocus() {
            return isItemFocus;
        }

        public ItemBean setItemFocus(boolean itemFocus) {
            isItemFocus = itemFocus;
            return this;
        }

        public boolean isItemEnabled() {
            return isItemEnabled;
        }

        public ItemBean setItemEnabled(boolean itemEnabled) {
            isItemEnabled = itemEnabled;
            return this;
        }

        public boolean isItemSelect() {
            return isItemSelect;
        }

        public ItemBean setItemSelect(boolean itemSelect) {
            isItemSelect = itemSelect;
            return this;
        }

        public boolean isItemSpanStaggeredGrid() {
            return isItemSpanStaggeredGrid;
        }

        public ItemBean setItemSpanStaggeredGrid(boolean itemSpanStaggeredGrid) {
            isItemSpanStaggeredGrid = itemSpanStaggeredGrid;
            return this;
        }
    }
}
