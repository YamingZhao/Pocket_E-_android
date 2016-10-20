package net.wezu.jxg.ui.user.cars;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.wezu.jxg.R;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.jxg.util.FastClickUtil;

import butterknife.Bind;

/**
 * @author snox@live.com
 * @date 2015/11/8.
 */
public class ColorSelectActivity extends BaseActivity {

    @Bind(android.R.id.list) ListView listView;

    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_car_color_select);

        setTitle("选择颜色");
        setDefaultBackButton();

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1) {

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (FastClickUtil.isFastClick()) return;

                        setResult(RESULT_OK, new Intent().putExtra("color", mAdapter.getItem(position)));
                        ColorSelectActivity.this.finish();
                    }
                });

                return view;
            }
        };

        String colors[] = new String[] { "红色", "黑色", "黄色", "灰色", "白色", "棕色", "银色", "绿色", "其它" };
        for (String color: colors) {
            mAdapter.add(color);
        }


        listView.setAdapter(mAdapter);
    }
}
