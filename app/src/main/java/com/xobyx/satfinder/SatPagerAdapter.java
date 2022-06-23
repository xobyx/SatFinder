package com.xobyx.satfinder;


import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Objects;

import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_SETTLING;

public class SatPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {


    private final MainActivity mainActivity;
    private final ArrayList<Fragment> arrayList;
    private final FragmentManager manager;

    private int pos;


    public SatPagerAdapter(MainActivity mainActivity, FragmentManager fragmentManager, ViewPager viewPager, ArrayList<Fragment> fragments) {

        this.mainActivity = mainActivity;
        this.pos = 0;
        this.arrayList = fragments;
        this.manager = fragmentManager;
        viewPager.setAdapter(this);
        viewPager.addOnPageChangeListener(this);
        for(Fragment fragment: fragments) {

            if(fragment.isAdded()) {
                continue;
            }

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(fragment, fragment.getClass().getSimpleName());
            transaction.commit();
            fragmentManager.executePendingTransactions();
        }
    }



    @Override  // android.support.v4.view.PagerAdapter
    public int getCount() {
        return this.arrayList.size();
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment view = this.arrayList.get(position);
        if(!view.isAdded()) {
            FragmentTransaction transaction = this.manager.beginTransaction();
            transaction.add(view, view.getClass().getSimpleName());
            transaction.commit();
            this.manager.executePendingTransactions();
        }

        if(Objects.requireNonNull(view.getView()).getParent() == null) {
            container.addView(view.getView());
        }

        return view.getView();
    }



    @Override  // android.support.v4.view.PagerAdapter
    public boolean isViewFromObject(@NonNull View view,@NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position,@NonNull Object object) {
        // TODO Auto-generated method stub
      //  super.destroyItem(container, position, object);
      //  ((ViewPager) container).removeView((View) object);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        (this.arrayList.get(this.pos)).onStart();
        if((this.arrayList.get(position)).isAdded()) {
            (this.arrayList.get(position)).onResume();
        }

        this.pos = position;

        updateUI(position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

        if(state == SCROLL_STATE_SETTLING && (mainActivity.getScanFragment().isScanRunning())) {
            mainActivity.getScanFragment().stopScan();
        }
    }






    public void updateUI(int position) {
        Log.i("MainActivity", "onExtraPageSelected " + position);
        if(position != 0) {
            if(position != 1) {
                if(position != 2) {
                    mainActivity.transponder_select().setVisibility(View.GONE);
                    return;
                }

                mainActivity.setCurrentFragment(2);
                mainActivity.getFieldStrengFrag().ensureAnimationInfo();

                for(int i = 0; i < 4; ++i) {
                    mainActivity.getFieldStrengFrag().update_vertical_progress(i, 0);
                }

                mainActivity.transponder_select().setVisibility(View.GONE);
                mainActivity.get_blue_img().reDraw();
                this.mainActivity.mSigQuality = 0;
                this.mainActivity.mSigStrength = 0;
                mainActivity.Update_Beep();
                mainActivity.Get_img_toolbar_channel().setEnabled(false);  // drawable:ic_channel_disable
                mainActivity.Get_img_toolbar_satellite().setEnabled(false);   // drawable:ic_satellite_disable
                mainActivity.get_blue_img().reDraw();
                return;
            }

            mainActivity.setCurrentFragment( 1);
            mainActivity.getTP_spinner().setEnabled(false);
            mainActivity.getSatellite_spinner().setEnabled(false);
            mainActivity.getFieldStrengFrag().setActiveFreq_color(0);
            mainActivity.getFieldStrengFrag().SendCurrentTp(0);

            for(int i = 0; i < 4; ++i) {
                mainActivity.getFieldStrengFrag().update_vertical_progress(i, 0);
            }

            mainActivity.getFieldStrengFrag().StartTimer();
            mainActivity.transponder_select().setVisibility(View.GONE);
            mainActivity.get_blue_img().reDraw();
            mainActivity.Get_img_toolbar_channel().setEnabled(false);   // drawable:ic_channel_disable
            mainActivity.Get_img_toolbar_satellite().setEnabled(false);   // drawable:ic_satellite_disable
            return;
        }

        this.mainActivity.SendSelectedSatTpLNBConfig();
        mainActivity.getTP_spinner().setEnabled(true);
        mainActivity.getSatellite_spinner().setEnabled(true);
        mainActivity.getFieldStrengFrag().ensureAnimationInfo();

        for(int i = 0; i < 4; ++i) {
            mainActivity.getFieldStrengFrag().update_vertical_progress(i, 0);
        }

        mainActivity.transponder_select().setVisibility(View.VISIBLE);
        mainActivity.get_blue_img().reDraw();
        mainActivity.setCurrentFragment( 0);
        mainActivity.Get_img_toolbar_channel().setEnabled(true);
        mainActivity.Get_img_toolbar_satellite().setEnabled(true);
    }

}

