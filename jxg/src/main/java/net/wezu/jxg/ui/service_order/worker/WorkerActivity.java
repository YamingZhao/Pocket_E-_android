package net.wezu.jxg.ui.service_order.worker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.Comment;
import net.wezu.jxg.model.WorkerEntity;
import net.wezu.jxg.service.ServiceOrderService;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.jxg.ui.base.BaseListActivity;
import net.wezu.jxg.ui.base.BasePagedListActivity;
import net.wezu.jxg.ui.base.BaseViewHolder;
import net.wezu.jxg.util.FormatUtil;
import net.wezu.widget.RoundImageview.RoundedNetImageView;

import java.util.List;

import butterknife.Bind;

/**
 * Created by snox on 2016/5/8.
 */
public class WorkerActivity extends BaseListActivity<Comment, BaseViewHolder<Comment>> {

    public static void startActivity(Activity activity, WorkerEntity entity) {
        Intent intent = new Intent(activity, WorkerActivity.class);
        intent.putExtra("worker", entity);

        activity.startActivity(intent);
    }

    private WorkerEntity workerEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        workerEntity = getIntent().getParcelableExtra("worker");
        if (workerEntity == null) {
            finish();
            return;
        }

        setTitle(workerEntity.WorkerFirstname + "的评论");
        setDefaultBackButton();

    }

    @Override
    protected void refreshData() {
        ServiceOrderService.getWorkerComment(requestTag, workerEntity.WorkerId, new RequestManager.ResponseListener<List<Comment>>() {
            @Override
            public void success(List<Comment> result, String msg) {
                addDataItems(result);
            }

            @Override
            public void error(String msg) {
                toast(msg);
            }
        });
    }

    @Override
    protected boolean equalItem(Comment item1, Comment item2) {
        return false;
    }

    @Override
    protected int getListItemLayoutResourceId() {
        return R.layout.listitem_comment;
    }

    @Override
    protected BaseViewHolder<Comment> createViewHolder(Context context, View convertView) {
        return new CommentViewHolder(context, convertView);
    }

    public class CommentViewHolder extends BaseViewHolder<Comment> {
        @Bind(R.id.img_avatar) RoundedNetImageView imageView;
        @Bind(R.id.tv_worker_name) TextView tvWorkerName;
        @Bind(R.id.tv_item) TextView tvServiceType;
        @Bind(R.id.tv_comment_date) TextView tvCommentDate;
        @Bind(R.id.rb_rating) RatingBar ratingBar;

        public CommentViewHolder(Context context, View convertView) {
            super(context, convertView);
        }

        @Override
        public void setData(Comment data) {
            if (!TextUtils.isEmpty(data.Avatar)) {
                imageView.setImageUrl(data.Avatar, Application.getInstance().getImageLoader());
            }
            tvWorkerName.setText(workerEntity.WorkerFirstname);

            tvServiceType.setText(data.ServiceType);

            ratingBar.setMax(5);
            ratingBar.setRating(data.AvgRating);

            tvCommentDate.setText(FormatUtil.formatDate(data.CommentDate));
        }
    }

}
