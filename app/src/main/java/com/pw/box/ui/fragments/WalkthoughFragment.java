//package com.pw.box.ui.fragments;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.pw.box.R;
//import com.pw.box.cache.Cache;
//import com.pw.box.ui.base.BaseFragment;
//import com.pw.box.ui.base.ContainerActivity;
//import com.pw.box.ui.fragments.accounts.LoginFragment;
//import com.pw.box.ui.fragments.walkthough.WalkThoughFragement1;
//import com.pw.box.ui.fragments.walkthough.WalkThoughFragement2;
//
///**
// * Created by danger on 16/8/27.
// */
//public class WalkthoughFragment extends BaseFragment {
//
//    int count = 0;
//    boolean hasJump = false;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.fragment_walkthough, null);
//
//        ViewPager pager = (ViewPager) v.findViewById(R.id.pager);
//        pager.setAdapter(new WalkthoughAdapter(getChildFragmentManager()));
//
//        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                // if(L.D) L.get().e("WalkthoughFragment", "onPageScrolled " + position + " " + positionOffset + " " + positionOffsetPixels);
//
//                if (hasJump) return;
//                if (position == 3 && positionOffsetPixels == 0) {
//                    count++;
//                } else {
//                    count = 0;
//                }
//                if (count >= 5) {
//                    activity.finish();
//                    if (!Cache.get().isLogin() && (Cache.get().getUser() == null
//                            || TextUtils.isEmpty(Cache.get().getUser().getAccount())
//                            || Cache.get().getUser().getRawKey() == null
//                            || Cache.get().getUser().getRawKeyByAnswer() == null
//                    )) {
//                        hasJump = true;
//                        ContainerActivity.go(activity, LoginFragment.class, null);
//                    }
//                }
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                // if(L.D) L.get().e("WalkthoughFragment", "onPageSelected " + position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                // if(L.D) L.get().e("WalkthoughFragment", "onPageScrollStateChanged " + state);
//            }
//        });
//        return v;
//    }
//
//    private class WalkthoughAdapter extends FragmentPagerAdapter {
//
//        String[] fs = {
//                WalkThoughFragement1.class.getName(),
//                WalkThoughFragement2.class.getName(),
//        };
//
//        public WalkthoughAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            Bundle arg = new Bundle();
//            switch (position) {
//                case 0:
//                    arg.putInt(WalkThoughFragement1.EXTRA_LAYOUT, R.layout.fragment_walkthough_page1);
//                    return Fragment.instantiate(getContext(), WalkThoughFragement1.class.getName(), arg);
//
//                case 1:
//                    arg.putInt(WalkThoughFragement1.EXTRA_LAYOUT, R.layout.fragment_walkthough_page2);
//                    return Fragment.instantiate(getContext(), WalkThoughFragement1.class.getName(), arg);
//
//
//                case 2:
//                    arg.putInt(WalkThoughFragement1.EXTRA_LAYOUT, R.layout.fragment_walkthough_page3);
//                    return Fragment.instantiate(getContext(), WalkThoughFragement1.class.getName(), arg);
//
//                case 3:
//                default:
//                    arg.putInt(WalkThoughFragement1.EXTRA_LAYOUT, R.layout.fragment_walkthough_page4);
//                    return Fragment.instantiate(getContext(), WalkThoughFragement1.class.getName(), arg);
//
//            }
//        }
//
//        @Override
//        public int getCount() {
//            return 4;
//        }
//    }
//
//
//}
