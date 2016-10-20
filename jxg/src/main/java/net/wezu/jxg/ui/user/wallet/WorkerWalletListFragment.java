package net.wezu.jxg.ui.user.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.wezu.framework.eventbus.EventBus;
import net.wezu.framework.eventbus.Subscribe;
import net.wezu.framework.eventbus.ThreadMode;
import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.OrderListItemModel;
import net.wezu.jxg.model.Withdraw;
import net.wezu.jxg.service.ServiceOrderService;
import net.wezu.jxg.ui.base.BaseListFragment;
import net.wezu.jxg.ui.base.BaseViewHolder;
import net.wezu.jxg.util.FastClickUtil;
import net.wezu.jxg.util.NumicUtil;
import net.wezu.widget.RoundImageview.RoundedNetImageView;

import java.text.SimpleDateFormat;

import butterknife.Bind;

/**
 * 提现列表
 * Created by snox on 2016/3/30.
 */
public class WorkerWalletListFragment extends BaseListFragment<OrderListItemModel, BaseViewHolder<OrderListItemModel>> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private boolean except;

    @Override
    protected void refreshData() {
        String clearState = getArguments().getString("CLEAR_STATE");

        ServiceOrderService.getWithDrawList(requestTag, clearState, new RequestManager.ResponseListener<Withdraw>() {
            @Override
            public void success(Withdraw result, String msg) {
                clear();
                addDataItems(result.records);
            }

            @Override
            public void error(String msg) {
                setError(msg);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WalletRefreshMessage message) {
        if (!except) {
            clear();
            refreshData();
        }
        except = false;
    }

    @Override
    protected boolean equalItem(OrderListItemModel item1, OrderListItemModel item2) {
        return false;
    }

    @Override
    protected int getListItemLayoutResourceId() {
        return R.layout.listitem_worker_wallet;
    }

    @Override
    protected BaseViewHolder<OrderListItemModel> createViewHolder(Context context, View convertView) {
        return new WalletItemViewHolder(context, convertView);
    }

    private void withdraw(final OrderListItemModel data) {
        ServiceOrderService.requestWithdraw(requestTag, data.OrderId, new RequestManager.ResponseListener<OrderListItemModel>() {
            @Override
            public void success(OrderListItemModel result, String msg) {
                removeItem(data);
                except = true;
                EventBus.getDefault().post(new WalletRefreshMessage());
                showDetail(result);
            }

            @Override
            public void error(String msg) {
                toast(msg);
            }
        });
    }

    private void showDetail(OrderListItemModel data) {
        Intent intent = new Intent(getActivity(), ServiceOrderDrawoutDetailActivity.class);
        intent.putExtra("OrderListItemModel", data);
        getActivity().startActivity(intent);
    }

    public class WalletItemViewHolder extends BaseViewHolder<OrderListItemModel> {
        @Bind(R.id.tv_order_no)TextView tvOrderNo;
        @Bind(R.id.tv_order_date)TextView tvOrderDate;
        @Bind(R.id.img_user_avatar)RoundedNetImageView imgUserAvatar;
        @Bind(R.id.tv_user_turename)TextView tvUserName;
        @Bind(R.id.txt_service_type)TextView tvServiceType;
        @Bind(R.id.txt_car_type)TextView tvModelName;
        @Bind(R.id.txt_plate_no)TextView tvPlateNo;
        @Bind(R.id.tv_amount)TextView tvAmount;
        @Bind(R.id.tv_detail)TextView tvDetail;
        @Bind(R.id.btn_submit)Button btnSubmit;

        public WalletItemViewHolder(Context context, View view) {
            super(context, view);
        }

        @Override
        public void setData(final OrderListItemModel data) {
            if (!TextUtils.isEmpty(data.Avatar)) {
                imgUserAvatar.setImageUrl(data.Avatar, Application.getInstance().getImageLoader());
            }

            tvOrderNo.setText(data.OrderNo);
            tvOrderDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(data.FinishedTime));
            tvUserName.setText(data.Firstname);
            tvServiceType.setText(data.ServiceType);
            tvModelName.setText(data.BrandName + data.ModalName);
            tvPlateNo.setText(data.Remark);
            tvAmount.setText(NumicUtil.formatDouble(data.OrderTotal));

            tvDetail.setVisibility(View.GONE);
            btnSubmit.setEnabled(false);
            if (TextUtils.isEmpty(data.ClearedStatus) || "NONE".equals(data.ClearedStatus)) {
                btnSubmit.setText("申请提现");
                btnSubmit.setEnabled(data.CanWithDraw);
            } else if ("SUBMITTED".equals(data.ClearedStatus)) {
                btnSubmit.setText("审核中");
                tvDetail.setVisibility(View.VISIBLE);
            } else if ("REJECTED".equals(data.ClearedStatus)) {
                btnSubmit.setText("重新申请");
                btnSubmit.setEnabled(data.CanWithDraw);
                tvDetail.setVisibility(View.VISIBLE);
            } else if ("CLEARED".equals(data.ClearedStatus)) {
                btnSubmit.setText("已完成");
            }

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (FastClickUtil.isFastClick()) return;

                    withdraw(data);
                }
            });

            tvDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetail(data);
                }
            });
            //tvDetail.setText(data.OrderNo);
        }
    }
}
