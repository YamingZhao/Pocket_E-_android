package net.wezu.jxg.ui.user.profile;

import android.os.Bundle;

import net.wezu.jxg.R;
import net.wezu.jxg.ui.user.profile.ChangProfileBaseActivity;

import butterknife.OnClick;

/**
 * @author snox@live.com
 * @date 2015/10/25.
 */
public class ChangeGenderActivity extends ChangProfileBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_gender);

        setTitle("修改性别");

        setDefaultBackButton();
    }

    @OnClick(R.id.gender_sec) void genderSec() {
        updateProfile("Gender", "");
    }

    @OnClick(R.id.gender_male) void genderSec1() {
        updateProfile("Gender", "M");
    }

    @OnClick(R.id.gender_female) void genderSec2() {
        updateProfile("Gender", "F");
    }
}
