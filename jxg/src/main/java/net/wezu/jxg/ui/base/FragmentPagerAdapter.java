package net.wezu.jxg.ui.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.wezu.jxg.R;
import net.wezu.viewpagerindicator.view.indicator.IndicatorViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by snox on 2016/4/1.
 */
public class FragmentPagerAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

    private LayoutInflater inflate;
    private List<TabItem> tabs;

    public FragmentPagerAdapter(FragmentActivity activity) {
        super(activity.getSupportFragmentManager());

        inflate = LayoutInflater.from(activity);
        tabs = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Override
    public View getViewForTab(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = inflate.inflate(R.layout.tab_top, container, false);
        }

        TextView textView = (TextView) convertView;
        textView.setText(tabs.get(position).getTitle());
        textView.setTextSize(12);
        return convertView;
    }

    public String getTitle(int position) {
        return tabs.get(position).getTitle();
    }

    protected TabItem getTab(int position) {
        return tabs.get(position);
    }

    @Override
    public Fragment getFragmentForPage(int position) {
        return tabs.get(position).fragment;
    }

    public void addFragment(String title, BaseFragment fragment) {
        tabs.add(new TabItem(title, fragment));
    }

    public void addFragment(String title, int iconRes, BaseFragment fragment) {
        tabs.add(new TabItem(title, iconRes, fragment));
    }

    public class TabItem {

        public TabItem(String title, BaseFragment fragment) {
            this.title = title;
            this.fragment = fragment;
        }

        public TabItem(String title, int iconRes, BaseFragment fragment) {
            this(title, fragment);

            this.iconRes = iconRes;
        }

        private String title;

        private BaseFragment fragment;

        private int iconRes;

        public String getTitle() {
            return title;
        }

        public BaseFragment getFragment() {
            return fragment;
        }

        public int getIconRes() {
            return iconRes;
        }
    }
}
