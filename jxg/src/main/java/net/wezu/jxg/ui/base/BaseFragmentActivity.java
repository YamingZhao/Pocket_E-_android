package net.wezu.jxg.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.wezu.jxg.R;

import butterknife.Bind;

/**
 * Created by snox on 2015/11/16.
 */
public class BaseFragmentActivity extends BaseActivity {

    @Bind(R.id.fragment_content)
    FrameLayout content;

    @Bind(R.id.layout_loading) LinearLayout loading;
    @Bind(R.id.layout_error) LinearLayout error;
    @Bind(R.id.txt_error_message) TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment);
    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, fragment)
                .commitAllowingStateLoss();

        showFregment();
    }

    public void showLoading() {
        content.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
    }

    public void showFregment() {
        content.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
    }

    public void showError(String message) {
        content.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);

        errorMessage.setText(message);
    }
}
