package net.wezu.jxg.ui.service_order.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.service.ServiceOrderService;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.Comment;
import net.wezu.jxg.model.OrderEntity;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.widget.RoundImageview.RoundedNetImageView;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 用户评价
 *
 * Created by snox on 2015/12/10.
 */
public class ServiceOrderAddCommentActivity extends BaseActivity {

    public static final int RC_ADD_COMMENT = 264;

    private static final String ORDER_TAG = "order_entity";

    public static void show(Activity activity, OrderEntity orderEntity) {
        activity.startActivityForResult(
                new Intent(activity, ServiceOrderAddCommentActivity.class)
                .putExtra(ORDER_TAG, orderEntity)
                , RC_ADD_COMMENT);
    }

    @Bind(R.id.img_user_avatar) RoundedNetImageView imgWorker;
    @Bind(R.id.txt_worker_name) TextView tvWorkerName;
    @Bind(R.id.rating_score) RatingBar tbScroe;
    @Bind(R.id.ll_distance) View panelDistance;

    @Bind(R.id.tv_order_id) TextView tvOrderNo;

    @Bind(R.id.rb_rateattitude)
    RatingBar ratingBarAttribute;

    @Bind(R.id.rb_ratespeed)
    RatingBar ratingBarSpeed;

    @Bind(R.id.rb_ratequality)
    RatingBar ratingBarQuality;

    @Bind(R.id.rb_rateclean)
    RatingBar ratingBarClean;

    @Bind(R.id.et_leave_words)
    EditText editMessage;

    @Bind(R.id.btn_submit)
    Button btnSubmit;

    private OrderEntity orderEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_service_order_add_comment);

        setTitle("订单评价");
        setDefaultBackButton();

        orderEntity = getIntent().getParcelableExtra(ORDER_TAG);
        if (orderEntity == null) {
            toast("无效的订单");
            finish();
            return;
        }

        panelDistance.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(orderEntity.order.WorkerAvatar)) {
            imgWorker.setImageUrl(orderEntity.order.WorkerAvatar, Application.getInstance().getImageLoader());
        }
        tvWorkerName.setText(orderEntity.order.WorkerFirstname);
        tbScroe.setMax(5);
        tbScroe.setRating(orderEntity.order.AvgRating);

        tvOrderNo.setText(orderEntity.order.OrderNo);

        ratingBarAttribute.setIsIndicator(false);
        ratingBarAttribute.setRating(5);
//        ratingBarAttribute.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                if (fromUser) {
//                    int progress = ratingBarAttribute.getProgress();
//                    toast("progress:" + progress + " rating :" + rating);
//                }
//            }
//        });

        ratingBarSpeed.setIsIndicator(false);
        ratingBarSpeed.setRating(5);
//        ratingBarSpeed.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                if (fromUser) {
//                    int progress = ratingBarAttribute.getProgress();
//                    toast("progress:" + progress + " rating :" + rating);
//                }
//            }
//        });

        ratingBarQuality.setIsIndicator(false);
        ratingBarQuality.setRating(5);
//        ratingBarQuality.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                if (fromUser) {
//                    int progress = ratingBarAttribute.getProgress();
//                    toast("progress:" + progress + " rating :" + rating);
//                }
//            }
//        });

        ratingBarQuality.setIsIndicator(false);
        ratingBarQuality.setRating(5);
//        ratingBarQuality.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                if (fromUser) {
//                    int progress = ratingBarAttribute.getProgress();
//                    toast("progress:" + progress + " rating :" + rating);
//                }
//            }
//        });
    }

    @OnClick(R.id.btn_submit) void submit() {

        btnSubmit.setEnabled(false);

        ServiceOrderService.addComment(requestTag, orderEntity.order.OrderId,
                ratingBarAttribute.getProgress(),
                ratingBarSpeed.getProgress(),
                ratingBarQuality.getProgress(),
                ratingBarClean.getProgress(),
                editMessage.getText().toString(),
                new RequestManager.ResponseListener<Comment>() {

                    @Override
                    public void success(Comment result, String msg) {
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void error(String msg) {
                        btnSubmit.setEnabled(true);
                        toast(msg);
                    }
                });
    }
}
