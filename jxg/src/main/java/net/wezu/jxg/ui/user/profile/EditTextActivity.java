package net.wezu.jxg.ui.user.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.R;
import net.wezu.jxg.ui.user.profile.ChangProfileBaseActivity;
import net.wezu.jxg.util.FastClickUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author snox@live.com
 * @date 2015/10/24.
 */
public class EditTextActivity extends ChangProfileBaseActivity {

    @Bind(R.id.edit_label) TextView edtLabel;
    @Bind(R.id.edit_nickname) EditText edtNickname;

    private String field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_nickname);

        setTitle(getIntent().getStringExtra("title"));
        field = getIntent().getStringExtra("field");

        int max = getIntent().getIntExtra("max", 0);
        if (max > 0) {
            edtNickname.setMaxLines(max);
        }

        edtLabel.setText(getIntent().getStringExtra("label"));
        edtNickname.setHint(getIntent().getStringExtra("hint"));

        setDefaultBackButton();

        edtNickname.setText(getIntent().getStringExtra("value"));
    }

    @OnClick(R.id.btn_confirm) void onSave() {
        String nickName = edtNickname.getText().toString();
        if (TextUtils.isEmpty(nickName)) {
            ToastUtils.show(this, "不能为空");
        }

        updateProfile(field, nickName);
    }


}
