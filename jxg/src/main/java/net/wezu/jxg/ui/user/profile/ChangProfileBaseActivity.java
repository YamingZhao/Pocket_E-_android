package net.wezu.jxg.ui.user.profile;

import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.UserModel;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.jxg.util.FastClickUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author snox@live.com
 * @date 2015/10/25.
 */
public class ChangProfileBaseActivity extends BaseActivity {


    protected void updateProfile(String field, final String value){

        if (FastClickUtil.isFastClick()) return;

        Map<String, String> parameter = new HashMap<>();
        parameter.put("fields", field);
        parameter.put(field, value);

        RequestManager.getInstance().post("updateprofile", requestTag, parameter, UserModel.class, new RequestManager.ResponseListener<UserModel>() {

            @Override
            public void success(UserModel result, String msg) {
                Application.getInstance().setUserModel(result);

                ToastUtils.show(ChangProfileBaseActivity.this, "更新成功");
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void error(String msg) {
                ToastUtils.show(ChangProfileBaseActivity.this, "更新失败");
            }
        });
    }
}
