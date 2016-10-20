package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单主表数据
 *
 * Created by snox on 2015/11/15.
 */
public class OrderModel implements Parcelable {
    public int OrderId;
    public int UserId;
    public int CarModalId;

    public String OrderNo;
    public String OrderType;
    public String OrderStatus;
    public String PaymentStatus;
    public String Remark;
    public String RemarkDetail;
    public String PayTypeId;

    public BigDecimal OrderTotal;
    public BigDecimal OrderSubTotal;
    public BigDecimal OrderDiscount;
    public BigDecimal RefundedAmount;
    public BigDecimal ShippingFee;
    public BigDecimal TipFee;

    public String ServiceType;
    public String ServiceLocation;
    public String ServiceDestination;
    public Date ServiceTime;

    public String ProductName;

    public double lat;
    public double lng;
    public double wlat;
    public double wlng;

    public String ModalName;
    public String Year;
    public String BrandName;

    public String Username;
    public String Avatar;

    public int WorkerId;
    public String WorkerUsername;
    public String WorkerFirstname;
    public String WorkerAvatar;
    public String WorkerLocation;

    public int RateAttitude;
    public int RateSpeed;
    public int RateQuality;
    public int RateClean;

    public float AvgRating;

    public String Pic1;
    public String Pic2;
    public String Pic3;

    public String ClearedStatus;

    public Date CreatedTime;
    public Date PaymentTime;
    public Date ShippingDate;
    public Date FinishedTime;
    public Date ClearedOn;

    public OrderModel() {
    }

    public WorkerEntity getWorkerEntity() {
        if (WorkerId == 0) return null;

        WorkerEntity entity = new WorkerEntity();
        entity.WorkerId = WorkerId;
        entity.WorkerUsername = WorkerUsername;
        entity.WorkerFirstname = WorkerFirstname;
        entity.WorkerAvatar = WorkerAvatar;
        entity.WorkerLocation = WorkerLocation;
        entity.AvgRating = AvgRating;

        return entity;
    }

    public boolean isUrgent() {
        return ServiceTime != null ? ServiceTime.getYear() == 0 : true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.OrderId);
        dest.writeInt(this.UserId);
        dest.writeInt(this.CarModalId);
        dest.writeString(this.OrderNo);
        dest.writeString(this.OrderType);
        dest.writeString(this.OrderStatus);
        dest.writeString(this.PaymentStatus);
        dest.writeString(this.Remark);
        dest.writeString(this.RemarkDetail);
        dest.writeString(this.PayTypeId);
        dest.writeSerializable(this.OrderTotal);
        dest.writeSerializable(this.OrderSubTotal);
        dest.writeSerializable(this.OrderDiscount);
        dest.writeSerializable(this.RefundedAmount);
        dest.writeSerializable(this.ShippingFee);
        dest.writeSerializable(this.TipFee);
        dest.writeString(this.ServiceType);
        dest.writeString(this.ServiceLocation);
        dest.writeString(this.ServiceDestination);
        dest.writeLong(ServiceTime != null ? ServiceTime.getTime() : -1);
        dest.writeString(this.ProductName);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeDouble(this.wlat);
        dest.writeDouble(this.wlng);
        dest.writeString(this.ModalName);
        dest.writeString(this.Year);
        dest.writeString(this.BrandName);
        dest.writeString(this.Username);
        dest.writeString(this.Avatar);
        dest.writeInt(this.WorkerId);
        dest.writeString(this.WorkerUsername);
        dest.writeString(this.WorkerFirstname);
        dest.writeString(this.WorkerAvatar);
        dest.writeString(this.WorkerLocation);
        dest.writeInt(this.RateAttitude);
        dest.writeInt(this.RateSpeed);
        dest.writeInt(this.RateQuality);
        dest.writeInt(this.RateClean);
        dest.writeFloat(this.AvgRating);
        dest.writeString(this.Pic1);
        dest.writeString(this.Pic2);
        dest.writeString(this.Pic3);
        dest.writeString(this.ClearedStatus);
        dest.writeLong(CreatedTime != null ? CreatedTime.getTime() : -1);
        dest.writeLong(PaymentTime != null ? PaymentTime.getTime() : -1);
        dest.writeLong(ShippingDate != null ? ShippingDate.getTime() : -1);
        dest.writeLong(FinishedTime != null ? FinishedTime.getTime() : -1);
        dest.writeLong(ClearedOn != null ? ClearedOn.getTime() : -1);
    }

    protected OrderModel(Parcel in) {
        this.OrderId = in.readInt();
        this.UserId = in.readInt();
        this.CarModalId = in.readInt();
        this.OrderNo = in.readString();
        this.OrderType = in.readString();
        this.OrderStatus = in.readString();
        this.PaymentStatus = in.readString();
        this.Remark = in.readString();
        this.RemarkDetail = in.readString();
        this.PayTypeId = in.readString();
        this.OrderTotal = (BigDecimal) in.readSerializable();
        this.OrderSubTotal = (BigDecimal) in.readSerializable();
        this.OrderDiscount = (BigDecimal) in.readSerializable();
        this.RefundedAmount = (BigDecimal) in.readSerializable();
        this.ShippingFee = (BigDecimal) in.readSerializable();
        this.TipFee = (BigDecimal) in.readSerializable();
        this.ServiceType = in.readString();
        this.ServiceLocation = in.readString();
        this.ServiceDestination = in.readString();
        long tmpServiceTime = in.readLong();
        this.ServiceTime = tmpServiceTime == -1 ? null : new Date(tmpServiceTime);
        this.ProductName = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.wlat = in.readDouble();
        this.wlng = in.readDouble();
        this.ModalName = in.readString();
        this.Year = in.readString();
        this.BrandName = in.readString();
        this.Username = in.readString();
        this.Avatar = in.readString();
        this.WorkerId = in.readInt();
        this.WorkerUsername = in.readString();
        this.WorkerFirstname = in.readString();
        this.WorkerAvatar = in.readString();
        this.WorkerLocation = in.readString();
        this.RateAttitude = in.readInt();
        this.RateSpeed = in.readInt();
        this.RateQuality = in.readInt();
        this.RateClean = in.readInt();
        this.AvgRating = in.readFloat();
        this.Pic1 = in.readString();
        this.Pic2 = in.readString();
        this.Pic3 = in.readString();
        this.ClearedStatus = in.readString();
        long tmpCreatedTime = in.readLong();
        this.CreatedTime = tmpCreatedTime == -1 ? null : new Date(tmpCreatedTime);
        long tmpPaymentTime = in.readLong();
        this.PaymentTime = tmpPaymentTime == -1 ? null : new Date(tmpPaymentTime);
        long tmpShippingDate = in.readLong();
        this.ShippingDate = tmpShippingDate == -1 ? null : new Date(tmpShippingDate);
        long tmpFinishedTime = in.readLong();
        this.FinishedTime = tmpFinishedTime == -1 ? null : new Date(tmpFinishedTime);
        long tmpClearedOn = in.readLong();
        this.ClearedOn = tmpClearedOn == -1 ? null : new Date(tmpClearedOn);
    }

    public static final Creator<OrderModel> CREATOR = new Creator<OrderModel>() {
        public OrderModel createFromParcel(Parcel source) {
            return new OrderModel(source);
        }

        public OrderModel[] newArray(int size) {
            return new OrderModel[size];
        }
    };
}
