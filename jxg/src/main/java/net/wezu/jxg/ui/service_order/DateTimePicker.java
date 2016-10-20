package net.wezu.jxg.ui.service_order;

import android.content.Context;

import net.wezu.jxg.R;
import net.wezu.widget.dialog.BaseDialog;

import java.util.Date;

/**
 * Created by snox on 2016/1/19.
 */
public class DateTimePicker extends BaseDialog {

    public DateTimePicker(Context context, Date startTime, int maxHours) {
        super(context);

        setContentView(R.layout.dialog_datetime_picker);
    }

}
