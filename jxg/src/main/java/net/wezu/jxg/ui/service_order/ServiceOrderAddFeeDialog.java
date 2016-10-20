package net.wezu.jxg.ui.service_order;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.wezu.jxg.R;

import java.math.BigDecimal;


/**
 * Created by snox on 2015/12/10.
 */
public class ServiceOrderAddFeeDialog extends Dialog {

    private EditText edtAmount;
    private EditText edtDescription;
    private TextView tvAlarm;
    private Button btnSubmit;

    private final BigDecimal tipFee;

    public OnClickListener mListener;

    private TextWatcher textWatcher;

    public ServiceOrderAddFeeDialog(Context context, BigDecimal tipfee) {
        super(context);
        this.tipFee = tipfee;
    }

//    public ServiceOrderAddFeeDialog(Context context, int theme) {
//        super(context, theme);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("追单");
        setContentView(R.layout.dialog_service_order_add_fee);

        edtAmount = (EditText) findViewById(R.id.edt_amount);
        edtDescription = (EditText) findViewById(R.id.edt_description);
        tvAlarm = (TextView) findViewById(R.id.tv_alarm);
        tvAlarm.setVisibility(View.GONE);
        btnSubmit = (Button) findViewById(R.id.btn_ok);


        edtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSubmit.setEnabled(false);
                if (count == 0) return;

                BigDecimal value = new BigDecimal(s.toString());

                tvAlarm.setVisibility(View.GONE);

                btnSubmit.setEnabled(true);

//                if (value.compareTo(new BigDecimal(5000).subtract(tipFee)) == 1) {
//                    tvAlarm.setError("追单金额不能超过5000块");
//                    tvAlarm.setVisibility(View.VISIBLE);
//
//                    btnSubmit.setEnabled(false);
//                    //edtAmount.addTextChangedListener(this);
//
//                } else
                if (value.compareTo(new BigDecimal(1000).subtract(tipFee)) == 1) {
                    tvAlarm.setError("您输入的追单金额已经超过1000块");
                    tvAlarm.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener != null) {

                    try {
                        String amountStr = edtAmount.getText().toString();
                        if (TextUtils.isEmpty(amountStr)) return;

                        BigDecimal amount = new BigDecimal(amountStr);
                        String description = edtDescription.getText().toString();

                        if (BigDecimal.ZERO.compareTo(amount) == -1 && new BigDecimal(1000).subtract(tipFee).compareTo(amount) >= 0) {
                            mListener.onClick(amount, description);
                        } else if (BigDecimal.ZERO.compareTo(amount) >= 0) {
                            edtAmount.setError("追单金额要大于零。");
                            return;
                        } else {
                            edtAmount.setError("追单金额要不能多于1000元。");
                            return;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        edtAmount.setError("追单金额格式不正确");
                        return;
                    }
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
        void onClick(BigDecimal fee, String description);
    }
}
