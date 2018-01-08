package com.pw.box.ui.fragments.home;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pw.box.BuildConfig;
import com.pw.box.R;
import com.pw.box.ads.Ad;
import com.pw.box.bean.protobuf.Group;
import com.pw.box.cache.Cache;
import com.pw.box.cache.Constants;
import com.pw.box.cache.GroupManager;
import com.pw.box.cache.User;
import com.pw.box.cache.UserStatusChangeListener;
import com.pw.box.core.Cm;
import com.pw.box.tool.DoubleTabHelper;
import com.pw.box.tool.UnLock;
import com.pw.box.ui.base.BaseFragment;
import com.pw.box.ui.base.ContainerActivity;
import com.pw.box.ui.fragments.accounts.LoginFragment;
import com.pw.box.ui.fragments.accounts.SetLockFragment;
import com.pw.box.ui.fragments.data.AddItemFragment;
import com.pw.box.ui.fragments.data.GroupListFragment;
import com.pw.box.ui.fragments.data.PasswordGenerateDialog;
import com.pw.box.ui.fragments.data.SearchFragment;
import com.pw.box.ui.fragments.setting.SettingFragment;
import com.pw.box.utils.DensitiUtil;
import com.pw.box.utils.PrefUtil;

import java.util.ArrayList;
import java.util.List;

// import com.pw.box.cache.Counter;


/**
 * 显示数据的首页由分组的tab组成
 * Created by danger on 16/9/12.
 */
public class HomeFragment extends BaseFragment implements
        ViewPager.OnPageChangeListener,
        View.OnClickListener,
        UserStatusChangeListener,
        GroupManager.GroupChangeListener {


    HomePageAdapter pagerAdapter;
    Holder holder;
    // Handler handler;
    boolean hasShowSetPattern = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        holder = new Holder(inflater.inflate(R.layout.fragment_home, container, false));

        // holder.titleBar.mButtonLeft.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);


        // holder.tab.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        // holder.tab.setAdapter(tabAdapter);
        // holder.tab.addItemDecoration(new TopBottomItemDecoration(DensitiUtil.dp2px(getContext(), 40)));

        // init pager
        if (pagerAdapter == null) {
            pagerAdapter = new HomePageAdapter(getChildFragmentManager());
        }

        holder.pager.setAdapter(pagerAdapter);
        holder.pager.addOnPageChangeListener(this);
        // holder.pager.seco

        holder.tab.setupWithViewPager(holder.pager);
        // holder.tab.setTabMode(TabLayout.MODE_SCROLLABLE);

        holder.rootView.findViewById(R.id.tv_title).setOnClickListener(this);
        holder.rootView.findViewById(R.id.btn_search).setOnClickListener(this);
        holder.rootView.findViewById(R.id.btn_more).setOnClickListener(this);
        holder.rootView.findViewById(R.id.tv_add).setOnClickListener(this);

        LoginFragment.Companion.initTest(holder.rootView.findViewById(R.id.tv_title));
        onPageSelected(0);

        Cache.get().addUserStatusChangeListener(this);
        // if (Counter.get().needUnLock()) {
        //     finish();
        // }

       /* Cm.get().getDataList(new N.NetHandler<M.GetItemListRes>() {
            @Override
            public void onSuccess(AbstractMessage reqPack, M.GetItemListRes retPack) {
                if (retPack.getRetCode() == ErrorCodes.SUCCESS) {
                    toast("获取到" + retPack.getItemsCount() + "条记录");
                    Cache.get().addDatas(retPack.getItemsList());

                    // pagerAdapter = new HomePageAdapter(getChildFragmentManager());
                    // List<Type> tabs = Cache.get().getAllTypes();
                    // pagerAdapter.types = tabs;
                    // holder.pager.setAdapter(pagerAdapter);
                } else {
                    toast(ErrorCodes.getErrorDescription(retPack.getRetCode()));
                }
            }

            @Override
            public void onFail(int localErrorCode, AbstractMessage reqPack, Exception e) {
                toast(R.string.failed);
            }
        });*/

        notifyDataUpdate();

        showSePatternDialog();
        onUserSyncDataSuccess();
        GroupManager.get().addListener(this);
        return holder.rootView;
    }

    @Override
    public boolean onBackPressed() {
        DoubleTabHelper.INSTANCE.pressAgainToExit(getContext());
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pagerAdapter != null) {
            notifyDataUpdate();
        }

        UnLock.unLockIfNeeded((ContainerActivity) getActivity());
    }

    private void showSePatternDialog() {
        if (hasShowSetPattern || Cache.get().getPatternUtil().havePattern()) {
            return;
        }
        hasShowSetPattern = true;

        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(getContext());
        dlgBuilder.setTitle(R.string.tip);
        dlgBuilder.setMessage(R.string.message_please_set_pattern);
        dlgBuilder.setNegativeButton(R.string.latter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                toast(R.string.set_pattern_is_canceled);
            }
        });

        dlgBuilder.setPositiveButton(R.string.set_protection_right_now, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle arg = new Bundle();
                arg.putInt(SetLockFragment.EXTRA_TYPE, SetLockFragment.EXTRA_TYPE_SET);
                ContainerActivity.go(activity, SetLockFragment.class, arg);
            }
        });
        dlgBuilder.setCancelable(false);
        dlgBuilder.create().show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Cache.get().removeUserStatusChangeListener(this);
        GroupManager.get().removeListener(this);
        holder = null;
    }


    public void notifyDataUpdate() {
        // init table
        List<Group> tabs = Cache.get().getAllTypes();

        if (tabs.isEmpty()) {
            holder.emptyView.setVisibility(View.VISIBLE);
        } else {
            holder.emptyView.setVisibility(View.GONE);
        }

        if (tabs.size() <= 4) {
            holder.tab.setTabMode(TabLayout.MODE_FIXED);
        } else {
            holder.tab.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        pagerAdapter.setTabs(tabs);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        // tabAdapter.setSelectedItem(position);
        /*switch (position) {
            case 0:
                holder.titleBar.mButtonLeft.setText("常用");
                break;
            case 1:
                holder.titleBar.mButtonLeft.setText("全部");
                break;
        }*/
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_title:
                /*if (Cm.get().getDataStatus() != ConnectionListener.STATUS_SYNCDATA_SUCCESS)*/
            {
                if (Cm.get().getConnection() == null) {
                    User user = Cache.get().getUser(); // 无连接，使用登录账号的
                    if (user != null) {
                        showLoadingStatus();
                        Cm.get().connect(user.getAccount());
                    }
                }
                // addConnectionListener(new DefaultConnectionStatusListener(this));
            }
            break;
            case R.id.btn_search:
                ContainerActivity.go(activity, SearchFragment.class, null);
                break;
            case R.id.btn_more:
                showDropdownMenu(holder.rootView.findViewById(R.id.btn_more));
                break;
            case R.id.tv_add:
                ContainerActivity.go(activity, AddItemFragment.class, null);
                break;
        }
    }

    private void showDropdownMenu(View view) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.pop_window, null);
        // 设置按钮的点击事件

        final PopupWindow popupWindow = new PopupWindow(contentView,
                DensitiUtil.dp2px(getContext(), 240), ViewGroup.LayoutParams.WRAP_CONTENT, true);

        View.OnClickListener onItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                switch (v.getId()) {
                    case R.id.item_add:
                        ContainerActivity.go(activity, AddItemFragment.class, null);
                        break;
                    case R.id.item_settings:
                        ContainerActivity.go(activity, SettingFragment.class, null);
                        break;
                    case R.id.item_gpedit:
                        ContainerActivity.go(activity, GroupListFragment.class, null);
                        break;
                    case R.id.item_genpw:
                        PasswordGenerateDialog passwordGenerateDialog = new PasswordGenerateDialog();
                        passwordGenerateDialog.show(getFragmentManager(), null);
                        break;
                    case R.id.item_supportus:
                        showAdDialog();
                        break;
                    // case R.id.item_crypto:
                    //
                    //      ContainerActivity.go(getActivity(), OfflineFragment.class, null);
                    //   break;
                }
            }
        };


        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));

        // 设置好参数之后再show
        popupWindow.showAsDropDown(view);

        contentView.findViewById(R.id.item_add).setOnClickListener(onItemClickListener);
        contentView.findViewById(R.id.item_genpw).setOnClickListener(onItemClickListener);
        contentView.findViewById(R.id.item_settings).setOnClickListener(onItemClickListener);
        contentView.findViewById(R.id.item_gpedit).setOnClickListener(onItemClickListener);

        if (BuildConfig.FLAVOR.equals("anzhi")) { // 特殊平台-安智不允许弹出插屏广告
            contentView.findViewById(R.id.item_supportus).setVisibility(View.GONE);
        }
        contentView.findViewById(R.id.item_supportus).setOnClickListener(onItemClickListener);
        // contentView.findViewById(R.id.item_crypto).setOnClickListener(onItemClickListener);
    }

    private void showAdDialog() {
        int cnt = PrefUtil.getInt(getContext(), Constants.PREF_KEY_SUPPORTUS, 0);

        PrefUtil.setInt(getContext(), Constants.PREF_KEY_SUPPORTUS, ++cnt);
        if (cnt > 5) {
            Ad.showInterstitial(getActivity());
            return;
        }

        new AlertDialog.Builder(getContext())
                .setMessage(R.string.tip_support_us)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.go_see, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Ad.showInterstitial(getActivity());
                    }
                })
                .create()
                .show();
    }

    void showFailedStatus() {
        holder.ivStatus.setVisibility(View.VISIBLE);
        holder.progressBar.setVisibility(View.GONE);
        toast(R.string.error_network_connecting);
    }

    void showLoadingStatus() {
        holder.ivStatus.setVisibility(View.GONE);
        holder.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUserLoginStart() {
        showLoadingStatus();
    }

    @Override
    public void onUserLoginSuccess() {
        holder.ivStatus.setVisibility(View.GONE);
        holder.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onUserLoginFail() {
        showFailedStatus();
    }

    @Override
    public void onUserSyncDataStart() {
        showLoadingStatus();
    }

    @Override
    public void onConnectionClosed() {
        showFailedStatus();
    }

    @Override
    public void onUserSyncDataSuccess() {
        holder.ivStatus.setVisibility(View.GONE);
        holder.progressBar.setVisibility(View.GONE);

        notifyDataUpdate();
    }

    @Override
    public void onUserSyncDataFail() {
        showFailedStatus();
    }

    @Override
    public void onGroupChanged(List<Group> gps) {
        notifyDataUpdate();
    }

    private class Holder {
        public View emptyView;
        View rootView;
        // TitleBar titleBar;
        ViewPager pager;
        TabLayout tab;
        TextView tvTitle;
        ImageView ivStatus;
        ProgressBar progressBar;

        public Holder(View v) {
            this.rootView = v;
            // titleBar = (TitleBar) v.findViewById(R.id.title);
            emptyView = v.findViewById(R.id.empty_view);
            pager = v.findViewById(R.id.pager);
            tab = v.findViewById(R.id.tab);

            tvTitle = v.findViewById(R.id.tv_title);
            ivStatus = v.findViewById(R.id.iv_sts);
            progressBar = v.findViewById(R.id.pb);
        }
    }

    private class HomePageAdapter extends FragmentPagerAdapter {

        List<Group> types = new ArrayList<>();

        private FragmentManager fm;
        // private List<Fragment> list = new ArrayList<>();

        public HomePageAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public long getItemId(int position) {
            return types.get(position).hashCode(); /*super.getItemId(position);*/
        }

       /* @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ItemListFragment f = new ItemListFragment(); // (ItemListFragment); super.instantiateItem(container, position);

            Bundle arg = new Bundle();
            arg.putInt("type_id", types.get(position).getType());
            arg.putString("type_name", types.get(position).getValue());
            return f;
        }*/

        @Override
        public Fragment getItem(int position) {
            /*Fragment fragment = list.get(position);

            if(L.D) L.get().e("ItemListFragment-", "getItem:" + position + fragment);
            return fragment;*/

            ItemListFragment f = new ItemListFragment(); // (ItemListFragment); super.instantiateItem(container, position);

            Bundle arg = new Bundle();
            Group type = types.get(position);

            arg.putInt("type_id", type.id);
            arg.putString("type_name", type.name);

            f.setArguments(arg);
            return f;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String s = types.get(position).name;
            if (s.length() > 10) {
                s = s.substring(0, 10) + "...";
            }
            if (s.trim().length() == 0) {
                s = getString(R.string.not_group);
            }
            return s;
        }


        @Override
        public int getCount() {
            int count = types.size();
            // if(L.D) L.get().e("ItemListFragment-", "size:" + count);

            return count;
        }

        /*public void setFragments(ArrayList<Fragment> fragments) {
            ///////////

            *//*for (int i = 0; i < list.size(); i++) {
                Fragment fragment = list.get(i);
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fragment);
                ft.commitAllowingStateLoss();
            }*//*

            ///////////

            this.list.clear();
            this.list.addAll(fragments);

            notifyDataSetChanged();
        }*/

        public void setTabs(List<Group> tabs) {
            if (tabs != null) {
                this.types = tabs;
            } else {
                this.types.clear();
            }
            notifyDataSetChanged();


           /* ArrayList<Fragment> listTmp = new ArrayList<>();
            for (Type type : types) {
                Bundle arg = new Bundle();
                arg.putInt("type_id", type.getType());
                arg.putString("type_name", type.getValue());

                ItemListFragment f = new ItemListFragment();
                // f = (ItemListFragment) ItemListFragment.instantiate(getContext(), ItemListFragment.class.getName(), arg);
                f.setArguments(arg);
                listTmp.add(f);
            }

            setFragments(listTmp);*/
        }
    }

}

