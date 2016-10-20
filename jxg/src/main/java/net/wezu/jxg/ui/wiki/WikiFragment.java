package net.wezu.jxg.ui.wiki;

import android.os.Bundle;

import net.wezu.jxg.R;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.service.ServiceAddressConstant;
import net.wezu.jxg.ui.base.BaseFragment;

import butterknife.Bind;

/**
 * Created by i310736(Yaming.Zhao@sap.com) on 11/10/2016.
 */

public class WikiFragment extends BaseFragment {

    @Bind(R.id.wv) ProgressWebView webView;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);

        setContentView(R.layout.fragment_webview);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);

        gotoHomePage();
    }

    public void gotoHomePage() {
        webView.loadUrl(ServiceAddressConstant.ROOT_ADDRESS + "/www/wiki/wiki.html?token=" + RequestManager.getInstance().getToken()+"&t="+ String.valueOf(System.currentTimeMillis()));
    }

    public boolean canGoBack() {
        return webView.canGoBack();
    }

    public void goBack() {
        webView.goBack();
    }
}
