package net.wezu.jxg.ui.user.settings;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.widget.EditText;

import net.wezu.jxg.R;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.FeedBackItem;
import net.wezu.jxg.service.UserService;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.jxg.util.FastClickUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 反馈
 * Created by snox on 2016/4/9.
 */
public class FeedbackActivity extends BaseActivity {

    @Bind(R.id.et_content) EditText etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);

        setTitle("建议与反馈");
        setDefaultBackButton();
    }

    @OnClick(R.id.btn_submit) void submit() {
        if (FastClickUtil.isFastClick()) return;

        String content = etContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            etContent.setError("请输入您的建议与反馈");
            return;
        }

        UserService.feedback(requestTag, content, new RequestManager.ResponseListener<FeedBackItem>() {
            @Override
            public void success(FeedBackItem result, String msg) {
                toast("已经收到您提交的建议与反馈");
                finish();
            }

            @Override
            public void error(String msg) {
                toast(msg);
            }
        });
    }
}
