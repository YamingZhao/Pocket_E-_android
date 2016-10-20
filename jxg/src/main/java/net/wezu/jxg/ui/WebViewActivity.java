package net.wezu.jxg.ui;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.wezu.jxg.R;
import net.wezu.jxg.ui.base.BaseActivity;

import butterknife.Bind;

/**
 * Created by snox on 2016/4/9.
 */
public class WebViewActivity extends BaseActivity {

    @Bind(R.id.wv) WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web_view);

        setTitle(getIntent().getStringExtra("title"));
        setDefaultBackButton();

        WebViewClient client = new WebViewClient();
        webView.setWebViewClient(client);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(getIntent().getStringExtra("url"));
    }
}
