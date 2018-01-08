package com.pw.box.ui.base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pw.box.BR;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danger
 * on 2017/7/7.
 */

public class BaseRecyclerViewAdapterNew<T> extends RecyclerView.Adapter<BaseRecyclerViewAdapterNew.Vh<T>> {

    private static final int VIEW_TYPE_HEADER = 0x10000000;
    private static final int VIEW_TYPE_FOOTER = 0x20000000;
    private static final int VIEW_TYPE_ITEM = 0x1;
    private final List<T> dataList = new ArrayList<>();
    private final List<Object> headerList = new ArrayList<>();
    private final List<Object> footerList = new ArrayList<>();
    private final ArrayList<Integer> headerTypes = new ArrayList<>();
    private final ArrayList<Integer> footerTypes = new ArrayList<>();
    protected Context context;
    protected boolean showHeader = true;
    protected boolean showFooter = true;
    protected LayoutInflater layoutInflater;
    protected OnItemClickListener<T> onItemClickListener;
    protected OnItemLongClickListener<T> onItemLongClickListener;
    private int currentHeaderType = 0;
    private int currentFooterType = 0;
    @LayoutRes
    private int itemLayoutId = -1;


    public BaseRecyclerViewAdapterNew(Context context) {
        this(context, 0);
    }

    public BaseRecyclerViewAdapterNew(Context context, int itemLayoutId) {

        this.context = context;
        this.itemLayoutId = itemLayoutId;

        layoutInflater = LayoutInflater.from(context);
    }

    protected int getLayoutId(int viewType) {
        return itemLayoutId;
    }

    public Context getContext() {
        return context;
    }

    protected void bindData(Vh<T> vh, T t, int pos) {
        if (onItemClickListener != null) {
            vh.itemView.setOnClickListener(vh);
        }
        if (onItemLongClickListener != null) {
            vh.itemView.setOnLongClickListener(vh);
        }
        vh.bindData(t, pos);
    }

    protected void bindHeader(Vh<T> holder, int position, int headerPosition) {

    }

    protected void bindFooter(Vh<T> holder, int position, int footerPosition) {

    }

    /**
     * 注意不允许重写些方法，请重写onCreateViewHolder1
     * if(isHeader(viewType) || isFooter(viewType)) {
     * return super.onCreateViewHolder(parent, viewType);
     * }
     * return xxxxx;
     */
    @Override
    public final Vh<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        Vh vh;

        if (isHeader(viewType)) {
            Object viewOrLayoutId = headerList.get(headerTypes.indexOf(viewType - VIEW_TYPE_HEADER));
            if (viewOrLayoutId instanceof View) {
                vh = new Vh((View) viewOrLayoutId);
            } else {
                vh = new Vh(DataBindingUtil.inflate(layoutInflater, (Integer) viewOrLayoutId, parent, false));
            }

            // vh.itemView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        } else if (isFooter(viewType)) {
            Object viewOrLayoutId = footerList.get(footerTypes.indexOf(viewType - VIEW_TYPE_FOOTER));
            if (viewOrLayoutId instanceof View) {
                vh = new Vh((View) viewOrLayoutId);
            } else {
                vh = new Vh(DataBindingUtil.inflate(layoutInflater, (Integer) viewOrLayoutId, parent, false));
            }

            // vh.itemView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        } else {
            // if (getLayoutId(viewType) <= 0) return null;
            // vh = new Vh(DataBindingUtil.inflate(layoutInflater, getLayoutId(viewType), parent, false));
            vh = onCreateViewHolder1(parent, viewType);
        }

        vh.viewType = viewType;
        return vh;
    }

    protected Vh<T> onCreateViewHolder1(ViewGroup parent, int viewType) {
        if (getLayoutId(viewType) <= 0) return null;
        return new Vh<>(DataBindingUtil.inflate(layoutInflater, getLayoutId(viewType), parent, false));
    }

    @Override
    public final void onBindViewHolder(Vh<T> holder, int position) {
        int headerCount = getHeaderCount();

        holder.headerCount = headerCount;

        if (position < headerCount) {
            bindHeader(holder, position, position);
        } else if (position < headerCount + dataList.size()) {
            T t = getItem(position - headerCount);
            if (holder.binding != null)
                holder.binding.setVariable(BR.data, t);
            holder.onItemClickListener = this.onItemClickListener;
            holder.onItemLongClickListener = this.onItemLongClickListener;
            holder.t = t;
            bindData(holder, t, position - headerCount);
        } else {
            bindFooter(holder, position, position - dataList.size() - headerCount);
        }

        // holder.binding.invalidateAll();
    }


    /**
     * 此方法不允许重写，请重写getItemViewType1
     * 注意如果重写此方法,写法应该是
     * int type = super.getItemViewType(position);
     * if(isHeader(viewType) || isFooter(viewType)) {
     * return type;
     * }
     * return xxxxx;
     */
    @Override
    public final int getItemViewType(int position) {
        int headerCount = getHeaderCount();
        if (position < headerCount) { // 在header范围内
            return VIEW_TYPE_HEADER + headerTypes.get(position);
        } else if (position < headerCount + dataList.size()) {
            return getItemViewType1(position - headerCount);
        } else {
            return VIEW_TYPE_FOOTER + footerTypes.get(position - dataList.size() - headerCount);
        }
    }

    protected int getItemViewType1(int position) {
        return VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return dataList.size() + getHeaderCount() + getFooterCount();
    }


    public List<T> getData() {
        return dataList;
    }

    public void setData(List<T> data) {
        setData(data, true);
    }

    public void setData(List<T> data, boolean refresh) {
        this.dataList.clear();
        if (data != null) {
            this.dataList.addAll(data);
        }
        if (refresh)
            notifyDataSetChanged();
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setItemLayoutId(int itemLayoutId) {
        this.itemLayoutId = itemLayoutId;
    }

    public boolean isShowHeader() {
        return showHeader;
    }

    public void setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
    }

    public boolean isShowFooter() {
        return showFooter;
    }

    public void setShowFooter(boolean showFooter) {
        this.showFooter = showFooter;
    }

    public void setShowHeaderAndFooter(boolean show) {
        boolean changed = showHeader != show || showFooter != show;
        if (!changed) return;
        setShowHeader(show);
        setShowFooter(show);
        notifyDataSetChanged();
    }

    public void addItem(T t) {
        addItem(t, false);
    }

    public void removeItem(int pos) {
        this.dataList.remove(pos);

    }

    public void removeItem(T t) {
        removeItem(t, false);
    }

    public void addItem(T t, boolean refresh) {
        this.dataList.add(t);
        if (refresh) notifyDataSetChanged();
    }

    public void removeItem(T t, boolean refresh) {
        this.dataList.remove(t);
        if (refresh) notifyDataSetChanged();
    }

    public T getItem(int position) {
        if (position < 0 || position >= dataList.size()) return null;
        return dataList.get(position);
    }


    public void addHeader(int layoutId) {
        if (layoutId <= 0) return;

        // View headerView = LayoutInflater.from(context).inflate(layoutId, null);
        // headerView.setTag(layoutId);

        headerTypes.add(++currentHeaderType);
        headerList.add(layoutId);
    }

    public void addHeader(View headerView) {
        headerTypes.add(++currentHeaderType);
        headerList.add(headerView);
    }

    /*public void removeHeader(int layoutId) {
        for (int i = 0; i < headerList.size(); i++) {
            if (headerList.get(i).getTag() == layoutId) {
                removeHeader(headerList.get(i--));
            }
        }
    }

    public void removeHeader(View headerView) {
        headerList.remove(headerView);
    }*/

    public void removeAllHeader() {
        headerList.clear();
        headerTypes.clear();
    }

    public void addFooter(int layoutId) {
        if (layoutId <= 0) return;

        // View footerView = layoutInflater.inflate(layoutId, null);
        // footerView.setTag(layoutId);
        footerTypes.add(++currentFooterType);
        footerList.add(layoutId);
    }

    public void addFooter(View footerView) {
        footerTypes.add(++currentFooterType);
        footerList.add(footerView);
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public boolean isHeader(int viewType) {
        return 0 != (viewType & VIEW_TYPE_HEADER);
    }

    public boolean isFooter(int viewType) {
        return 0 != (viewType & VIEW_TYPE_FOOTER);
    }

    public int getHeaderCount() {
        return showHeader ? headerList.size() : 0;
    }

    public int getFooterCount() {

        return showFooter ? footerList.size() : 0;
    }

    /*public void removeFooter(View footerView) {
        footerList.remove(footerView);
    }

    public void removeFooter(int layoutId) {
        for (int i = 0; i < footerList.size(); i++) {
            if (footerList.get(i).getTag() == layoutId) {
                removeFooter(footerList.get(i--));
            }
        }
    }*/

    public void removeAllFooter() {
        footerList.clear();
        footerTypes.clear();
    }


    public interface OnItemClickListener<T> {
        void onItemClick(T t, int pos);
    }

    public interface OnItemLongClickListener<T> {
        void onItemLongClick(T t, int pos);
    }

    public static class Vh<T> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public T t;
        public int viewType;
        public int headerCount;
        protected OnItemClickListener<T> onItemClickListener;
        protected OnItemLongClickListener<T> onItemLongClickListener;
        private ViewDataBinding binding;

        public Vh(View itemView) {
            super(itemView);
        }


        public Vh(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public <T> T getBinding() {
            return (T) binding;
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(t, position());
            }
        }

        public void bindData(T t, int pos) {

        }

        public int position() {
            return getAdapterPosition() - headerCount;
        }

        @Override
        public boolean onLongClick(View v) {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(t, position());
            }
            return true;
        }
    }

}
