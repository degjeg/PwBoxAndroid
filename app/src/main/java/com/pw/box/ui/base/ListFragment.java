package com.pw.box.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.pw.box.R;
import com.pw.box.ui.widgets.StateView;
import com.pw.box.ui.widgets.TitleBar;

import java.util.ArrayList;
import java.util.List;


/**


 */
public abstract class ListFragment<T> extends BaseFragment implements StateView.StateChangeListener {

    protected final List<T> dataList = new ArrayList<>();
    protected ViewHolder holder;
    protected BaseRecyclerViewAdapterNew<T> adapter;
    protected Boolean loadDataSuccess = null;

    public ListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        holder = new ViewHolder(inflater, getLayoutId());

        if (dataList.isEmpty()) {
            if (loadDataSuccess == null) {
                holder.stateView.setState(StateView.STATE_LOADING);
                loadData(true, 0); // 自动加载数据
            } else if (loadDataSuccess) {
                holder.stateView.setState(StateView.STATE_EMPTY);
            } else {
                holder.stateView.setState(StateView.STATE_FAIL);
            }
        } else {
            holder.stateView.setState(StateView.STATE_CONTENT);
            if (adapter == null) {
                createAdapter();
            }
            adapter.setData(dataList);
        }

        return holder.rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        holder = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.empty_view:
                onClickEmptyView(v);
                break;
            case R.id.fail_view:
                onClickFailView(v);
                break;
        }
    }

    @Override
    public void onStateChange(int state) {
        switch (state) {
            case StateView.STATE_CONTENT:
                instantiateContentView(holder.stateView);
                break;
            case StateView.STATE_FAIL:
                instantiateFailView(holder.stateView);
                break;
            case StateView.STATE_EMPTY:
                instantiateEmptyView(holder.stateView);
                break;
            case StateView.STATE_LOADING:
                instantiateLoadingView(holder.stateView);
                break;
        }
    }

    public int getLayoutId() {
        return R.layout.fragment_stateview_with_title;
    }

    protected abstract void doLoadData(boolean isRefresh, int currentSize);

    protected void loadData(boolean isRefresh, int currentSize) {
        if (adapter == null || adapter.getItemCount() == 0) {
            holder.stateView.showLoadingView(0, 0);
        }

        doLoadData(isRefresh, currentSize);
    }

    /**
     * 如果loadFinished为true,禁用底部加载功能
     */
    protected void loadDataFinish(boolean isRefresh, List<T> dataList, boolean loadFinished) {
        if (isRefresh) {
            this.dataList.clear();
        }
        if (dataList != null) {
            this.dataList.addAll(dataList);
        }

        loadDataSuccess = true;

        if (holder == null) {
            return;
        }

        if (holder.recyclerView != null) {
            holder.recyclerView.refreshComplete();
            holder.recyclerView.loadMoreComplete();
        }

        if (this.dataList.size() > 0) {
            holder.stateView.setState(StateView.STATE_CONTENT);
            adapter.setData(this.dataList);

            holder.recyclerView.setLoadingMoreEnabled(!loadFinished);
        } else {
            holder.stateView.setState(StateView.STATE_EMPTY);
        }
    }

    public abstract void createAdapter();

    protected void loadDataFailed() {
        loadDataSuccess = false;
        if (holder == null) {
            return;
        }

        if (dataList.isEmpty()) {
            holder.stateView.setState(StateView.STATE_FAIL);
        } else {
            holder.stateView.setState(StateView.STATE_CONTENT);
        }
    }

    public void onClickEmptyView(View view) {
        loadData(true, 0);
    }

    public void onClickFailView(View view) {
        loadData(true, 0);
    }

    protected void instantiateLoadingView(StateView stateView) {

    }

    protected void instantiateEmptyView(StateView stateView) {

    }

    protected void instantiateFailView(StateView stateView) {

    }

    protected void instantiateContentView(StateView stateView) {
        if (adapter == null) {
            createAdapter();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.recyclerView.setAdapter(adapter);
    }

    public class ViewHolder implements XRecyclerView.LoadingListener, StateView.StateChangeListener {
        public View rootView;
        public TitleBar titleView;
        public StateView stateView;
        public XRecyclerView recyclerView;

        public ViewHolder(LayoutInflater inflater, int layoutId) {
            rootView = inflater.inflate(layoutId, null, false);

            titleView = rootView.findViewById(R.id.title_view);
            stateView = rootView.findViewById(R.id.state_view);

            stateView.setStateChangeListener(this);
        }

        @Override
        public void onRefresh() {
            int currSize = adapter == null ? 0 : adapter.getItemCount();
            loadData(true, currSize);
        }

        @Override
        public void onLoadMore() {
            int currSize = adapter == null ? 0 : adapter.getItemCount();
            loadData(false, currSize);
        }

        @Override
        public void onStateChange(int state) {
            if (state == StateView.STATE_CONTENT) {
                recyclerView = rootView.findViewById(R.id.recycler_view);
                recyclerView.setLoadingListener(this);
                // RecyclerView.Adapter
            } else if (state == StateView.STATE_FAIL) {
                rootView.findViewById(R.id.fail_view).setOnClickListener(ListFragment.this);
            } else if (state == StateView.STATE_EMPTY) {
                rootView.findViewById(R.id.empty_view).setOnClickListener(ListFragment.this);
            }
            ListFragment.this.onStateChange(state);
        }
    }
}
