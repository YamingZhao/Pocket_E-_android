package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

import net.wezu.jxg.data.ServiceOrderStatus;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by snox on 2015/11/16.
 */
public class OrderListItemModel implements Parcelable {

    public int OrderId;
    public int OrderInfoId;

    public String ServiceType;
    public String ServiceLocation;
    public String ServiceDestination;

    public double lng;
    public double lat;

    public double wlat;
    public double wlng;

    public Date ServiceTime;
//    public double TipFee;
//
//    public String Pic1;
//    public String Pic2;
//    public String Pic3;
//
//    public String RemarkDetail;
//
    public String ModalName;
//    public int Year;

    public String BrandName;
    public String Username;
    public String Firstname;
    public String Avatar;

    public String WorkerUsername;
    public String WorkerFirstname;
    public String WorkerAvatar;
    public String WorkerLocation;

//    public int RateAttitude;
//    public int RateSpeed;
//    public int RateQuality;
//    public int RateClean;
//    public int TotalComments;
    public int TotalOrders;
//
//    public String OrderType;
    public String OrderNo;

    public int UserId;
    public int CarModalId;

    public BigDecimal OrderTotal;
    public BigDecimal OrderSubTotal;
    public BigDecimal OrderDiscount;
    public BigDecimal RefundedAmount;
    public BigDecimal ShippingFee;
    public BigDecimal TipFee;

    public String ShippingType;
    public String ShippingID;
    public Date ShippingDate; //": "1900-01-01 00:00:00",
    public String UserCheckNo;
    public int UserAddressId;
    public Date CreatedTime; //": "2015-11-15 20:30:14",
    public Date PaymentTime; //": "1900-01-01 00:00:00",
    public Date FinishedTime; //": "1900-01-01 00:00:00",
    public String OrderStatus;//": "CREATED",
    public String PaymentStatus;//": "NOT_PAID",
    public String Remark;//": "沪MD6110",
    public String PayTypeId;//": "",
    public String InvoiceType;//": "",
    public String InvoiceTitle;//": "",
    public int WorkerId;//": 0,
//    public int Deleted;//": 0

    public String ClearedStatus;

    public boolean CanWithDraw;

    public String getOrderStatus() {
        switch (OrderStatus) {
            case ServiceOrderStatus.CREATED:    return "待抢";
            case ServiceOrderStatus.CATCHED:    return "待确认";
            case ServiceOrderStatus.CONFIRMED:  return "已确认";
            case ServiceOrderStatus.SERVICING:   return "服务中";
            case ServiceOrderStatus.WORKDONE:   return "待付款";
            case ServiceOrderStatus.CLOSED:     return "完成";
            case ServiceOrderStatus.CANCELLED:  return "已取消";
            case ServiceOrderStatus.TIMEOUT:  return "已过期";
            default: return OrderStatus;
        }
    }

    public String getClearedStatus() {
        switch (ClearedStatus) {
            case "NONE":    return "未提现";
            case "SUBMITTED":    return "提现中";
            case "CLEARED":    return "已完成";
            case "REJECTED":    return "申请失败";
            default: return ClearedStatus;
        }
    }

    public OrderListItemModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.OrderId);
        dest.writeInt(this.OrderInfoId);
        dest.writeString(this.ServiceType);
        dest.writeString(this.ServiceLocation);
        dest.writeString(this.ServiceDestination);
        dest.writeDouble(this.lng);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.wlat);
        dest.writeDouble(this.wlng);
        dest.writeLong(ServiceTime != null ? ServiceTime.getTime() : -1);
        dest.writeString(this.ModalName);
        dest.writeString(this.BrandName);
        dest.writeString(this.Username);
        dest.writeString(this.Firstname);
        dest.writeString(this.Avatar);
        dest.writeString(this.WorkerUsername);
        dest.writeString(this.WorkerFirstname);
        dest.writeString(this.WorkerAvatar);
        dest.writeString(this.WorkerLocation);
        dest.writeInt(this.TotalOrders);
        dest.writeString(this.OrderNo);
        dest.writeInt(this.UserId);
        dest.writeInt(this.CarModalId);
        dest.writeSerializable(this.OrderTotal);
        dest.writeSerializable(this.OrderSubTotal);
        dest.writeSerializable(this.OrderDiscount);
        dest.writeSerializable(this.RefundedAmount);
        dest.writeSerializable(this.ShippingFee);
        dest.writeSerializable(this.TipFee);
        dest.writeString(this.ShippingType);
        dest.writeString(this.ShippingID);
        dest.writeLong(ShippingDate != null ? ShippingDate.getTime() : -1);
        dest.writeString(this.UserCheckNo);
        dest.writeInt(this.UserAddressId);
        dest.writeLong(CreatedTime != null ? CreatedTime.getTime() : -1);
        dest.writeLong(PaymentTime != null ? PaymentTime.getTime() : -1);
        dest.writeLong(FinishedTime != null ? FinishedTime.getTime() : -1);
        dest.writeString(this.OrderStatus);
        dest.writeString(this.PaymentStatus);
        dest.writeString(this.Remark);
        dest.writeString(this.PayTypeId);
        dest.writeString(this.InvoiceType);
        dest.writeString(this.InvoiceTitle);
        dest.writeInt(this.WorkerId);
        dest.writeString(this.ClearedStatus);
        dest.writeByte(CanWithDraw ? (byte) 1 : (byte) 0);
    }

    protected OrderListItemModel(Parcel in) {
        this.OrderId = in.readInt();
        this.OrderInfoId = in.readInt();
        this.ServiceType = in.readString();
        this.ServiceLocation = in.readString();
        this.ServiceDestination = in.readString();
        this.lng = in.readDouble();
        this.lat = in.readDouble();
        this.wlat = in.readDouble();
        this.wlng = in.readDouble();
        long tmpServiceTime = in.readLong();
        this.ServiceTime = tmpServiceTime == -1 ? null : new Date(tmpServiceTime);
        this.ModalName = in.readString();
        this.BrandName = in.readString();
        this.Username = in.readString();
        this.Firstname = in.readString();
        this.Avatar = in.readString();
        this.WorkerUsername = in.readString();
        this.WorkerFirstname = in.readString();
        this.WorkerAvatar = in.readString();
        this.WorkerLocation = in.readString();
        this.TotalOrders = in.readInt();
        this.OrderNo = in.readString();
        this.UserId = in.readInt();
        this.CarModalId = in.readInt();
        this.OrderTotal = (BigDecimal) in.readSerializable();
        this.OrderSubTotal = (BigDecimal) in.readSerializable();
        this.OrderDiscount = (BigDecimal) in.readSerializable();
        this.RefundedAmount = (BigDecimal) in.readSerializable();
        this.ShippingFee = (BigDecimal) in.readSerializable();
        this.TipFee = (BigDecimal) in.readSerializable();
        this.ShippingType = in.readString();
        this.ShippingID = in.readString();
        long tmpShippingDate = in.readLong();
        this.ShippingDate = tmpShippingDate == -1 ? null : new Date(tmpShippingDate);
        this.UserCheckNo = in.readString();
        this.UserAddressId = in.readInt();
        long tmpCreatedTime = in.readLong();
        this.CreatedTime = tmpCreatedTime == -1 ? null : new Date(tmpCreatedTime);
        long tmpPaymentTime = in.readLong();
        this.PaymentTime = tmpPaymentTime == -1 ? null : new Date(tmpPaymentTime);
        long tmpFinishedTime = in.readLong();
        this.FinishedTime = tmpFinishedTime == -1 ? null : new Date(tmpFinishedTime);
        this.OrderStatus = in.readString();
        this.PaymentStatus = in.readString();
        this.Remark = in.readString();
        this.PayTypeId = in.readString();
        this.InvoiceType = in.readString();
        this.InvoiceTitle = in.readString();
        this.WorkerId = in.readInt();
        this.ClearedStatus = in.readString();
        this.CanWithDraw = in.readByte() != 0;
    }

    public static final Creator<OrderListItemModel> CREATOR = new Creator<OrderListItemModel>() {
        public OrderListItemModel createFromParcel(Parcel source) {
            return new OrderListItemModel(source);
        }

        public OrderListItemModel[] newArray(int size) {
            return new OrderListItemModel[size];
        }
    };
}
