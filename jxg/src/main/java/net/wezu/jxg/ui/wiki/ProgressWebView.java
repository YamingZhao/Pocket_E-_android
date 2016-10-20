package net.wezu.jxg.ui.wiki;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import net.wezu.jxg.R;

/**
 * Created by snox on 2016/5/13.
 */
public class ProgressWebView extends WebView {

    private ProgressBar progressBar;

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 8);
        progressBar.setLayoutParams(params);

        Drawable drawable = context.getResources().getDrawable(R.drawable.web_progress_bar_states);
        progressBar.setProgressDrawable(drawable);

        addView(progressBar);

        setWebChromeClient(new WebChromeClient());
        setWebViewClient(new WebViewClient());
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressBar.setVisibility(GONE);
            } else {
                if (progressBar.getVisibility() == GONE) {
                    progressBar.setVisibility(VISIBLE);
                }
                progressBar.setProgress(newProgress);
            }

            super.onProgressChanged(view, newProgress);
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressBar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressBar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
