//package com.pw.box.ui.base;
//
//import android.content.Context;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.widget.AdapterView;
//
//import com.jcodecraeer.xrecyclerview.XRecyclerView;
//import com.pw.box.core.bean.Item;
//import com.pw.box.core.db.bean.SearchHistory;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
///**
//
//
// */
//public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter {
//    protected final List<T> data = new ArrayList<>();
//    protected Context context;
//
//    XRecyclerView recyclerView;
//
//    protected AdapterView.OnItemClickListener onItemClickListener;
//    protected AdapterView.OnItemLongClickListener onItemLongClickListener;
//
//
//    public BaseRecyclerViewAdapter(Context context) {
//        this.context = context;
//    }
//
//    public BaseRecyclerViewAdapter(Context context, XRecyclerView recyclerView) {
//        this.context = context;
//        this.recyclerView = recyclerView;
//    }
//
//    public void setData(List<T> data) {
//        this.data.clear();
//        if (data != null) {
//            this.data.addAll(data);
//        }
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public int getItemCount() {
//        return data.size();
//    }
//
//    public T getItem(int pos) {
//        if (pos >= 0 && pos < data.size()) {
//            return data.get(pos);
//        }
//        return null;
//    }
//
//
//    public void delete(T hi) {
//        data.remove(hi);
//        notifyDataSetChanged();
//    }
//
//    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
//        this.onItemClickListener = onItemClickListener;
//    }
//
//    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
//        this.onItemLongClickListener = onItemLongClickListener;
//    }
//
//    public void setRecyclerView(XRecyclerView recyclerView) {
//        this.recyclerView = recyclerView;
//    }
//
//    public Context getContext() {
//        return context;
//    }
//
//    public T remove(int pos) {
//        return data.remove(pos);
//    }
//
//    public List<T> getData() {
//        return data;
//    }
//
//    public void add(T item) {
//        data.add(item);
//    }
//}
