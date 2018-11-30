package com.example.forrestsu.zhdaily.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.example.forrestsu.zhdaily.fragment.NewsBannerFragment;

import java.util.List;

public class BannerFragmentPagerAdapter extends FragmentPagerAdapter {

    public List<Fragment> list;

    public BannerFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list == null || list.size() == 0) {
            return 0;
        } else {
            return list.size();
        }
    }

    @Override
    @NonNull
    public Object instantiateItem(@NonNull ViewGroup vp, int position) {
        return super.instantiateItem(vp, position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup vp, int position, @NonNull Object object) {
        super.destroyItem(vp, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        if (list == null || list.size() == 0) {
            return new NewsBannerFragment();
        } else {
            return list.get(position);
        }
    }


}
