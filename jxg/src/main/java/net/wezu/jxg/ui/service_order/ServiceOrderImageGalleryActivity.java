package net.wezu.jxg.ui.service_order;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import net.wezu.jxg.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 服务订单图片列表
 * Created by snox on 2016/3/15.
 */
public class ServiceOrderImageGalleryActivity extends AppCompatActivity {

    private List<String> mImages;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.vp) ViewPager mViewPager;

    private final ViewPager.OnPageChangeListener mViewPagerOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (mViewPager != null) {
                mViewPager.setCurrentItem(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_service_order_image_gallery);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("图片");
            actionBar.setShowHideAnimationEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mImages = extras.getStringArrayList("images");
            }
        }

        setupViewPager();
    }

    private void setupViewPager() {
        ArrayList<String> images = new ArrayList<>();
        images.addAll(mImages);

        FullScreenImageGalleryAdapter fullScreenImageGalleryAdapter = new FullScreenImageGalleryAdapter(images);
        mViewPager.setAdapter(fullScreenImageGalleryAdapter);
        //mViewPager.setOnPageChangeListener(mViewPagerOnPageChangeListener);
    }
}
