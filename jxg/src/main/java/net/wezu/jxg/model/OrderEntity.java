package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 订单详情
 *
 * Created by snox on 2015/11/15.
 */
public class OrderEntity implements Parcelable {

    public OrderModel order;

    public OrderInfoModel orderinfo;

    public Category servicecategory;

    //public PushResult pushresult;

    public Comment comment;

    public UserModel client;

    public OrderEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.order, 0);
        dest.writeParcelable(this.orderinfo, 0);
        dest.writeParcelable(this.servicecategory, 0);
        dest.writeParcelable(this.comment, 0);
        dest.writeParcelable(this.client, flags);
    }

    protected OrderEntity(Parcel in) {
        this.order = in.readParcelable(OrderModel.class.getClassLoader());
        this.orderinfo = in.readParcelable(OrderInfoModel.class.getClassLoader());
        this.servicecategory = in.readParcelable(Category.class.getClassLoader());
        this.comment = in.readParcelable(Comment.class.getClassLoader());
        this.client = in.readParcelable(UserModel.class.getClassLoader());
    }

    public static final Creator<OrderEntity> CREATOR = new Creator<OrderEntity>() {
        public OrderEntity createFromParcel(Parcel source) {
            return new OrderEntity(source);
        }

        public OrderEntity[] newArray(int size) {
            return new OrderEntity[size];
        }
    };
}
