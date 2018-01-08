package com.pw.box.ui.fragments.setting;

import android.content.Context;

import com.pw.box.R;
import com.pw.box.cache.Cache;
import com.pw.box.databinding.ViewSettingsItemBinding;
import com.pw.box.ui.base.BaseRecyclerViewAdapterNew;

/**
 * 设置界面的adapter
 * Created by danger on 16/9/16.
 */
public class SettingsAdapter extends BaseRecyclerViewAdapterNew<SettingsItem> {


    public SettingsAdapter(Context context) {
        super(context, R.layout.view_settings_item);
    }

    @Override
    protected void bindData(Vh<SettingsItem> vh, SettingsItem item, int pos) {
        super.bindData(vh, item, pos);

        ViewSettingsItemBinding binding = vh.getBinding();
        binding.tvName.setText(item.getName());
        binding.tvValue.setText(item.getValue());

        switch (item.getType()) {

            case SettingFragment.SET_ITEM_ACCOUNT:
                binding.tvValue.setText(Cache.get().getUser().getAccount());
                break;
            case SettingFragment.SET_ITEM_EMAIL:

            case SettingFragment.SET_ITEM_PHONE:
                binding.tvValue.setText(context.getString(R.string.not_set));
                break;
            case SettingFragment.SET_ITEM_LOCK:
                if (Cache.get().getPatternUtil().havePattern()) {
                    binding.tvValue.setText(context.getString(R.string.is_set));
                } else {
                    binding.tvValue.setText(context.getString(R.string.not_set));
                }
                break;
            case SettingFragment.SET_ITEM_PROTECT:
                if (Cache.get().getUser().haveProtect()) {
                    binding.tvValue.setText(context.getString(R.string.is_set));
                } else {
                    binding.tvValue.setText(context.getString(R.string.not_set));
                }
                break;

            case SettingFragment.SET_ITEM_PASSWORD:
            default:
                break;
        }
    }
}
