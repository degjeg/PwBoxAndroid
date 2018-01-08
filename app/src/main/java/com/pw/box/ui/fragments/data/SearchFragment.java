package com.pw.box.ui.fragments.data;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.pw.box.R;
import com.pw.box.bean.protobuf.Data;
import com.pw.box.cache.Cache;
import com.pw.box.core.db.Db;
import com.pw.box.core.db.SearchHistoryDao;
import com.pw.box.core.db.bean.SearchHistory;
import com.pw.box.core.db.bean.SearchType;
import com.pw.box.ui.base.BaseFragment;
import com.pw.box.ui.base.BaseRecyclerViewAdapterNew;
import com.pw.box.ui.base.ContainerActivity;
import com.pw.box.ui.widgets.RecycleViewDivider;
import com.pw.box.utils.L;
import com.pw.box.utils.PinyinUtils;
import com.pw.box.utils.Range;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;


/**
 * 搜索密码数据的工具类
 * Created by danger on 16/9/12.
 */
public class SearchFragment extends BaseFragment implements
        View.OnClickListener, SearchKeywordsAdapter.Listener {

    private static final String TAG = "SF__________";
    Holder holder;

    HandlerThread searchThread;
    Handler searchHandler;
    Handler uiHandler;

    SearchResultsAdapter searchResultsAdapter;
    SearchKeywordsAdapter searchKeywordsAdapter;

    String currentKey = null;
    private TextWatcher textChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String word = holder.etSearch.getText().toString();
            search(word, false);
        }
    };
    private BaseRecyclerViewAdapterNew.OnItemClickListener onItemClickListener = new BaseRecyclerViewAdapterNew.OnItemClickListener<Data>() {
        @Override
        public void onItemClick(Data data, int pos) {

            Bundle args = new Bundle();

            args.putLong("id", data.id);
            args.putString("keyWord", currentKey);
            ContainerActivity.go(activity, ViewItemFragment.class, args);

            recordKeyword(holder.etSearch.getText().toString().trim());
        }
    };

    /*@Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            byte[] rawData = data.getByteArrayExtra("data");

            try {
                this.data = M.Data.parseFrom(rawData);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
                return;
            }
            initList();
        }
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        holder = new Holder(inflater.inflate(R.layout.fragment_search, container, false));
        initData();

        holder.rootView.findViewById(R.id.btn_back).setOnClickListener(this);
        holder.rootView.findViewById(R.id.btn_clear).setOnClickListener(this);
        // holder.rootView.findViewById(R.id.bts).setOnClickListener(this);

        searchThread = new HandlerThread("");
        searchThread.start();
        searchHandler = new Handler(searchThread.getLooper());
        uiHandler = new Handler();

        SearchHistoryDao searchHistoryDao = Db.getSession().getSearchHistoryDao();

        final List<SearchHistory> histories = searchHistoryDao
                .queryBuilder()
                .where(
                        SearchHistoryDao.Properties.Type.eq(SearchType.TYPE_DATA)
                )
                .orderDesc(SearchHistoryDao.Properties.Time)
                .list();

        searchKeywordsAdapter = new SearchKeywordsAdapter(getContext());
        searchResultsAdapter = new SearchResultsAdapter(getContext());
        searchResultsAdapter.setOnItemClickListener(onItemClickListener);

        searchKeywordsAdapter.setListener(this);
        searchKeywordsAdapter.setData(histories);
        holder.xRecyclerView.setAdapter(searchKeywordsAdapter);

        // if(holder.etSearch.getText().to)
        currentKey = null;
        textChangeListener.afterTextChanged(null);
        return holder.rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        searchThread.quit();
        // currentKey = null;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_clear:
                holder.etSearch.setText("");
                search(null, false);
                break;
        }
    }

    private void initData() {
    }

    public void search(String keyWord, boolean saveDb) {

        searchHandler.removeCallbacksAndMessages(null);
        searchHandler.post(new SearchTask(keyWord, saveDb));
    }

    @Override
    public void onDelete(SearchHistory hi) {
        SearchHistoryDao searchHistoryDao = Db.getSession().getSearchHistoryDao();
        searchHistoryDao.delete(hi);
        searchKeywordsAdapter.removeItem(hi);
    }

    @Override
    public void onSearch(SearchHistory hi) {
        search(hi.getContent(), false);
        holder.etSearch.setText(hi.getContent());
    }

    private void recordKeyword(String keyWord) {
        SearchHistoryDao searchHistoryDao = Db.getSession().getSearchHistoryDao();

        SearchHistory his = new SearchHistory(null, keyWord, SearchType.TYPE_DATA, System.currentTimeMillis());

        QueryBuilder<SearchHistory> x = searchHistoryDao.queryBuilder().where(
                SearchHistoryDao.Properties.Content.eq(keyWord),
                SearchHistoryDao.Properties.Type.eq(SearchType.TYPE_DATA)
        );

        List<SearchHistory> history = x.build().list();
        if (history != null && history.size() > 0) {
            his.setId(history.get(0).getId());
        }
        searchHistoryDao.insertOrReplace(his);
    }

    private class SearchTask implements Runnable {
        String keyWord = null;
        boolean saveDb = false;

        public SearchTask(String keyWord, boolean saveDb) {
            this(keyWord);
            this.saveDb = saveDb;
        }

        public SearchTask(String keyWord) {
            if (keyWord == null) {
                return;
            }
            keyWord = keyWord.trim();

            if (keyWord.isEmpty()) {
                return;
            }

            this.keyWord = keyWord;
        }

        @Override
        public void run() {

            if (L.E) L.get().e(TAG, "run():" + currentKey + "    " + keyWord);
            if (TextUtils.equals(currentKey, keyWord)) {
                return;
            }
            currentKey = keyWord;

            SearchHistoryDao searchHistoryDao = Db.getSession().getSearchHistoryDao();
            /**
             * 1.save to db
             */
            if (saveDb && keyWord != null) {

                recordKeyword(keyWord);
            }

            if (keyWord == null) {
                if (L.E) L.get().e(TAG, "keyWord == null");
                final List<SearchHistory> histories = searchHistoryDao
                        .queryBuilder()
                        .where(
                                SearchHistoryDao.Properties.Type.eq(SearchType.TYPE_DATA)
                        )
                        .orderDesc(SearchHistoryDao.Properties.Time)
                        .list();

                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.xRecyclerView.setAdapter(searchKeywordsAdapter);
                        searchKeywordsAdapter.setData(histories);
                        searchKeywordsAdapter.notifyDataSetChanged();
                    }
                });
                return;
            }

            searchKeywordIndata(keyWord);
        }


        private void searchKeywordIndata(String keyWord) {
            final List<Data> results = new ArrayList<>();
            final List<Range<Integer>> ranges = new ArrayList<>();
            final List<Integer> positions = new ArrayList<>();

            List<Data> datas = new ArrayList<>(Cache.get().getDataList()); // 拷贝一份,以防止多处对数据操作造成错误

            if (L.E) L.get().e(TAG, "startSearch:" + keyWord);
            for (Data data : datas) {
                // 在子类型中进行关键这搜索
                Range<Integer> matchResult = PinyinUtils.matchsPinYin(data.sub_type, keyWord);
                if (matchResult != null) {
                    results.add(data);
                    ranges.add(matchResult);
                    positions.add(-1);
                    continue;
                }

                // 对每一条数据进行搜索
                for (int i = 0; i < data.items.size(); i++) {
                    matchResult = PinyinUtils.matchsPinYin(data.items.get(i).value, keyWord);
                    if (matchResult != null) {
                        results.add(data);
                        ranges.add(matchResult);
                        positions.add(i);
                        break;
                    }
                }
            }
            // searchResultsAdapter.setData();
            // searchResultsAdapter

            if (L.E) L.get().e(TAG, "results:" + results.size());
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    searchResultsAdapter.setData(results, ranges, positions);
                    holder.xRecyclerView.setAdapter(searchResultsAdapter);
                    searchResultsAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private class Holder {
        View rootView;
        EditText etSearch;
        XRecyclerView xRecyclerView;

        public Holder(View v) {
            this.rootView = v;
            etSearch = v.findViewById(R.id.et_search);
            xRecyclerView = v.findViewById(R.id.recycler_view);

            xRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            xRecyclerView.addItemDecoration(new RecycleViewDivider(getContext()));

            xRecyclerView.setPullRefreshEnabled(false);
            xRecyclerView.setLoadingMoreEnabled(false);

            etSearch.addTextChangedListener(textChangeListener);
            etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    String keyWord = holder.etSearch.getText().toString();
                    search(keyWord, true);

                    return true;
                }
            });
        }
    }
}

