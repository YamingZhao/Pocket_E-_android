package net.wezu.jxg.ui.service_order;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import net.wezu.jxg.R;

/**
 * Created by snox on 2015/12/14.
 */
public class ServiceOrderAppealDialog extends Dialog {

    private EditText edtAmount;
    private EditText edtDescription;

    public OnClickListener mListener;

    public ServiceOrderAppealDialog(Context context) {
        super(context);
    }

    public ServiceOrderAppealDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("服务订单申诉");
        setContentView(R.layout.dialog_service_order_appeal);

        edtDescription = (EditText) findViewById(R.id.edt_description);

        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick(edtDescription.getText().toString());
                }

                dismiss();
            }
        });

        // 取消
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setOnClickListener(OnClickListener listener) {
        mListener = listener;
    }

    public interface OnClickListener {
        void onClick(String description);
    }
}
