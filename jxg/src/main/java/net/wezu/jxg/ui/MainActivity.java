package net.wezu.jxg.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.service.UserService;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.jxg.ui.base.FragmentPagerAdapter;
import net.wezu.jxg.ui.service_order.user.ServicesFragment;
import net.wezu.jxg.ui.user.PersonInformationFragment;
import net.wezu.jxg.ui.service_order.worker.WorkerServiceFragment;
import net.wezu.jxg.ui.wiki.WikiFragment;
import net.wezu.viewpagerindicator.view.indicator.Indicator;
import net.wezu.viewpagerindicator.view.indicator.IndicatorViewPager;
import net.wezu.viewpagerindicator.view.viewpager.SViewPager;

import org.androidpn.client.NotificationService;

/**
 * @author snox@live.com
 * @date 2015/10/21.
 */
public class MainActivity extends BaseActivity {

    private static MainActivity _instance;

    public static MainActivity Instance() {
        return _instance;
    }

    public static final String START_TAB_INDEX = "start_tab_index";

    private IndicatorViewPager indicatorViewPager;

    private FragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        _instance = this;

        super.onCreate(savedInstanceState);

        if (!Application.getInstance().isLogin()) {
            logout(false);
            return;
        }

        setContentView(R.layout.activity_main);

        setTitle("口袋e修");

//        if (Application.getInstance().isWorkerPackage()) {
//            UpdateChecker.checkForDialog(this, LoginActivity.APP_UPDATE_WORKER_SERVER_URL);
//        } else {
//            UpdateChecker.checkForDialog(this, LoginActivity.APP_UPDATE_USER_SERVER_URL);
//        }

        SViewPager viewPager = (SViewPager)findViewById(R.id.main_viewPager);
        Indicator indicator = (Indicator)findViewById(R.id.main_indicator);

        indicator.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {

            }
        });

        adapter = new FragmentPagerAdapter(this){
            @Override
            public View getViewForTab(int position, View convertView, ViewGroup container) {

                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);

                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.tab_main, container, false);
                }

                TextView tvTitle = (TextView)convertView.findViewById(R.id.tv_title);
                tvTitle.setText(getTab(position).getTitle());

                ImageView icon = (ImageView)convertView.findViewById(R.id.image_icon);
                icon.setImageResource(getTab(position).getIconRes());

                return convertView;
            }
        };

        boolean worker = Application.getInstance().isWorkerPackage();

        adapter.addFragment("口袋e修", R.mipmap.nav_icon_1, worker ? new WorkerServiceFragment() : new ServicesFragment());
        //adapter.addFragment("商城", R.mipmap.nav_icon_2, new ProductCategoryFragment()); // UnderConstructionFragment());
        adapter.addFragment("商城", R.mipmap.nav_icon_2, new  UnderConstructionFragment());
        adapter.addFragment("购物车", R.mipmap.nav_icon_3, new UnderConstructionFragment());
        if (worker) {
            //adapter.addFragment("百科", R.mipmap.nav_icon_6, new UnderConstructionFragment());
            adapter.addFragment("百科", R.mipmap.nav_icon_6, new WikiFragment());
        }
        adapter.addFragment("我的", R.mipmap.nav_icon_4, new PersonInformationFragment());

        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        indicatorViewPager.setAdapter(adapter);

        indicatorViewPager.setOnIndicatorPageChangeListener(new IndicatorViewPager.OnIndicatorPageChangeListener() {
            @Override
            public void onIndicatorPageChange(int preItem, int currentItem) {
                updateTitle(currentItem);
            }
        });

        //viewPager.setCanScroll(true);
        viewPager.setOffscreenPageLimit(4);

        int index = 0;

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(START_TAB_INDEX)) {
            index = intent.getIntExtra(START_TAB_INDEX, 0);
        }

        indicatorViewPager.setCurrentItem(index, true);
    }

    @Override
    protected void onDestroy() {


        stopService(NotificationService.getIntent());
        _instance = null;
        //PushManager.getInstance().stopService(getApplicationContext());
        super.onDestroy();
    }

    private void updateTitle(int index) {

        String title = adapter.getTitle(index);
        currentIsWiki = "百科".equals(title);
        setTitle(title); //(MainFragmentAdapter) indicatorViewPager.getAdapter()).getTitle(index));

        if (currentIsWiki) {
            setBackButton(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backWebView();
                }
            });
        } else {
            hideBackButton();
        }
    }

    private boolean currentIsWiki;

    public void logout() {
        logout(true);
    }

    public void logout(boolean needUnbind) {
        Application.getInstance().setLoginResult(null);

        if (needUnbind) {
            UserService.unbind(new RequestManager.ResponseListener<Object>() {
                @Override
                public void success(Object result, String msg) {
                    setResult(RESULT_OK);

                    RequestManager.getInstance().clear();
                    finish();
                }

                @Override
                public void error(String msg) {
                    toast("解绑失败");
                }
            });
        } else {
            RequestManager.getInstance().clear();
            finish();
        }

        startActivity(new Intent(this, LoginActivity.class).putExtra("logout", true));
    }

//    private long mBackPressedTime = 0;

    @Override
    public void onBackPressed() {

        if (currentIsWiki) {
            if (backWebView()) return;
        }

        super.onBackPressed();

//        if ((System.currentTimeMillis() - mBackPressedTime) > 2000) {
//            mBackPressedTime = System.currentTimeMillis();
//            ToastUtils.show(this, "再按一次退出");
//        } else {
//            super.onBackPressed();
//
//            AppManager.getAppManager().AppExit(this);
//        }
    }

    private boolean backWebView() {
        WikiFragment fragment = getWifiFragment();

        if (fragment != null && fragment.canGoBack()) {
            fragment.goBack();
            return true;
        }
        return false;
    }

    private WikiFragment getWifiFragment() {
        ViewPager vp = indicatorViewPager.getViewPager();

        Fragment current = adapter.getFragmentForPage(vp.getCurrentItem());

        if (current instanceof WikiFragment) {
            return (WikiFragment) current;
        }
        return null;
    }
}
