package net.wezu.jxg.ui.service_order;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.TouchNetworkImageView;

import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;

import java.util.List;

/**
 * Created by snox on 2016/3/15.
 */
public class FullScreenImageGalleryAdapter extends PagerAdapter {

    // region Member Variables
    private final List<String> mImages;
    // endregion

    public FullScreenImageGalleryAdapter(List<String> images) {
        mImages = images;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) container.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.fullscreen_image, null);

        TouchNetworkImageView imageView = (TouchNetworkImageView) view.findViewById(R.id.iv);
        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll);

        String image = mImages.get(position);

        imageView.setImageUrl(image, Application.getInstance().getImageLoader());

        container.addView(view, 0);

        return view;
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
