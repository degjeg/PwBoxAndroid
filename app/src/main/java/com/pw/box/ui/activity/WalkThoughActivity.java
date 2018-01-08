//package com.pw.box.ui.activity;
//
//import android.view.Window;
//import android.view.WindowManager;
//
//import com.pw.box.ui.base.ContainerActivity;
//
///**
// * Created by danger on 2016/12/10.
// */
//
//public class WalkThoughActivity extends ContainerActivity {
//    /*@Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        init();
//        setContentView(R.layout.activity_empty_container);
//
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//        // Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
//        // if (currentFragment != null && !replaceMode) {
//        //     fragmentTransaction.addToBackStack(currentFragment.getClass().getName());
//        // }
//
//        WalkthoughFragment walkthoughFragment = (WalkthoughFragment) WalkthoughFragment.instantiate(this, WalkthoughFragment.class.getName());
//        fragmentTransaction.add(R.id.fragment_container, walkthoughFragment, null);
//        // fragmentTransaction.addToBackStack(tag);
//        fragmentTransaction.commitAllowingStateLoss();
//    }*/
//
//    protected void init() {
//
//        /*set it to be no title*/
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        /*set it to be full screen*/
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//    }
//}
