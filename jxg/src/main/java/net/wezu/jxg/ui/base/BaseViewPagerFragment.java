package net.wezu.jxg.ui.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import net.wezu.jxg.R;
import net.wezu.viewpagerindicator.view.indicator.Indicator;
import net.wezu.viewpagerindicator.view.indicator.IndicatorViewPager;
import net.wezu.viewpagerindicator.view.indicator.slidebar.ColorBar;
import net.wezu.viewpagerindicator.view.indicator.slidebar.ScrollBar;
import net.wezu.viewpagerindicator.view.indicator.transition.OnTransitionTextListener;

import butterknife.Bind;

/**
 *
 *
 * Created by snox on 2015/11/17.
 */
public abstract class BaseViewPagerFragment extends BaseFragment {

    @Bind(R.id.fragment_tabmain_viewPager) ViewPager viewPager;

    @Bind(R.id.fragment_tabmain_indicator) Indicator indicator;

    private IndicatorViewPager indicatorViewPager;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);

        setContentView(R.layout.fragment_view_pager);

        int selectColor = getResources().getColor(R.color.blue1);

        indicator.setScrollBar(new ColorBar(getApplicationContext(), selectColor, 5, ScrollBar.Gravity.BOTTOM_FLOAT));

        indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, Color.BLACK));//.setSize(selectSize, unSelectSize));

        viewPager.setOffscreenPageLimit(4);

        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);

        indicatorViewPager.setAdapter(setAdapter(new FragmentPagerAdapter(getActivity())));
    }

    protected abstract FragmentPagerAdapter setAdapter(FragmentPagerAdapter adapter);
}
