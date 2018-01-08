package com.pw.box.ui.fragments.data;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.pw.box.R;
import com.pw.box.bean.protobuf.Data;
import com.pw.box.bean.protobuf.Item;
import com.pw.box.core.bean.ItemTypes;
import com.pw.box.databinding.AdapterItemBinding;
import com.pw.box.ui.base.BaseRecyclerViewAdapterNew;
import com.pw.box.ui.fragments.home.ItemListAdapter;
import com.pw.box.utils.Range;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索结果的adapter
 * Created by danger on 16/10/16.
 */
public class SearchResultsAdapter extends BaseRecyclerViewAdapterNew<Data> {
    /**
     * 待搜索关键字在内容中的位置，如中国银行  中搜索国银  那么range为(1,2)
     */
    List<Range<Integer>> ranges;

    /**
     * 标识关键字在哪一条子数据中被找到，比如，一条数据有地址，邮箱，账号等数据列表，
     * position(i)指示该条数据的每几条子数据中出现的关键字
     */
    List<Integer> positions;

    public SearchResultsAdapter(Context context) {
        super(context, R.layout.adapter_item);
    }


    /**
     * @param results
     * @param ranges
     * @param positions
     * @see ranges
     * @see positions
     */
    public void setData(List<Data> results, List<Range<Integer>> ranges, List<Integer> positions) {
        super.setData(results);
        this.ranges = ranges;
        this.positions = positions;
    }


    @Override
    protected void bindData(Vh<Data> vh, Data data, int pos) {
        super.bindData(vh, data, pos);

        AdapterItemBinding binding = vh.getBinding();

        binding.tvName.setText(getName(pos));
        binding.tvAccount.setText(getValue(pos));

        // h.itemView.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        if (onItemClickListener != null) {
        //            onItemClickListener.onItemClick(null, null, position, 0);
        //        }
        //    }
        // );
    }

    List<Object> getSpans() {
        List<Object> spans = new ArrayList<>();
        spans.add(new ForegroundColorSpan(0xffff0000));
        return spans;
    }

    int getIndexByType(Data data, int type) {
        for (int i = 0; i < data.items.size(); i++) {
            if (data.items.get(i).type == type) {
                return i;
            }
        }
        return -1;
    }

    private CharSequence getName(int pos) {
        Data data = getItem(pos);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        // String name = data.getSubType();

        builder.append(data.sub_type);
        Range<Integer> range = ranges.get(pos);
        // 关键字在子类型出现，子类型如即时通讯下又有微信，qq等子分类
        if (positions.get(pos) == -1) {
            List<Object> spans = getSpans();
            for (Object span : spans) {
                builder.setSpan(span, range.getStart(), range.getEnd() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return builder;
        }

        // 关键字在备注或者 中出现
        for (int i = 0; i < data.items.size(); i++) {
            Item item = data.items.get(i);

            if (item.type == ItemTypes.SUB_TYPE_SUB_TYPE
                    || item.type == ItemTypes.SUB_TYPE_MARK
                    ) {
                // name = item.getValue() + " " + name;
                builder.insert(0, item.value);
                if (i == positions.get(pos)) {
                    List<Object> spans = getSpans();
                    for (Object span : spans) {
                        builder.setSpan(span, range.getStart(), range.getEnd() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        }
        if (builder.length() == 0) {
            // 如果第一行为空，并且类型为url
            if (data.type == ItemTypes.TYPE_WEBSITE) {
                int index = getIndexByType(data, ItemTypes.SUB_TYPE_URL);
                if (index != -1) {
                    builder.append(data.items.get(index).value);
                    if (positions.get(pos) == index) {
                        List<Object> spans = getSpans();
                        for (Object span : spans) {
                            builder.setSpan(span, range.getStart(), range.getEnd() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                }
            }
        }
        return builder;
    }

    private CharSequence getValue(int pos) {
        Data data = getItem(pos);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        Range<Integer> range = ranges.get(pos);


        builder.append(ItemListAdapter.getAccount(getItem(pos)));

        if (positions.get(pos) >= 0) {
            builder.append("\n");

            Item item = data.items.get(positions.get(pos));

            if (item.type >= 10 && item.type < 30) {
                builder.delete(0, builder.length());

            }
            String type = ItemTypes.getItemNameByType(context, data.type, item.type);
            if (!TextUtils.isEmpty(type)) {
                builder.append(type);
                builder.append(":");
            }

            int offset = builder.length();
            builder.append(item.value);

            List<Object> spans = getSpans();
            for (Object span : spans) {
                builder.setSpan(span, range.getStart() + offset, range.getEnd() + 1 + offset, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            // String name = data.getSubType();

        }
        return builder;
    }


    // private class Holder extends RecyclerView.ViewHolder {
    //     TextView tvName;
    //     TextView tvAccount;
    //
    //     public Holder(View itemView) {
    //         super(itemView);
    //         itemView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
    //         tvName = itemView.findViewById(R.id.tv_name);
    //         tvAccount = itemView.findViewById(R.id.tv_account);
    //     }
    // }
}
