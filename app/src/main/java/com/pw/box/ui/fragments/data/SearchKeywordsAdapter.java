package com.pw.box.ui.fragments.data;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.View;
import android.view.ViewGroup;

import com.pw.box.R;
import com.pw.box.core.db.bean.SearchHistory;
import com.pw.box.databinding.AdapterKeywordBinding;
import com.pw.box.ui.base.BaseRecyclerViewAdapterNew;

/**
 * 搜索的adapter
 * Created by danger on 16/10/16.
 */
public class SearchKeywordsAdapter extends BaseRecyclerViewAdapterNew<SearchHistory> {

    Listener listener;

    public SearchKeywordsAdapter(Context context) {
        super(context, R.layout.adapter_keyword);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected Vh<SearchHistory> onCreateViewHolder1(ViewGroup parent, int viewType) {
        AdapterKeywordBinding binding = DataBindingUtil.inflate(layoutInflater, getLayoutId(viewType), parent, false);
        return new Holder(binding);
    }


    public interface Listener {
        void onDelete(SearchHistory hi);

        void onSearch(SearchHistory hi);
    }

    private class Holder extends Vh<SearchHistory> implements View.OnClickListener {


        public Holder(AdapterKeywordBinding binding) {
            super(binding);

            binding.btnDelete.setOnClickListener(this);
            binding.getRoot().setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_delete:
                    listener.onDelete(getItem(position()));
                    break;
                default:
                    listener.onSearch(getItem(position()));
            }
        }

        @Override
        public void bindData(SearchHistory searchHistory, int pos) {
            super.bindData(searchHistory, pos);

            AdapterKeywordBinding binding = getBinding();
            binding.tv.setText(searchHistory.getContent());
        }
    }

}
