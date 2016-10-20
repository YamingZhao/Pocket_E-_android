package net.wezu.jxg.model;

import android.graphics.Region;
import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by snox on 2015/12/20.
 */
public class ProductOrderListItem implements Parcelable {

    public int UserAddressId;
    public String Address;
    public String Country;
    public String District;
    public int IsDefault;
    public String ReceiverName;
    public String ReceiverPhone;
    public String Region;
    public String AddressType;

    public List<String> ThumbUrls;
    public List<ProductOrderDetail> details;
    //public String coupon":null,

    public int OrderId;
    public String OrderType;
    public String OrderNo;

    public int UserId;
    public int CarModalId;
    public BigDecimal OrderTotal;
    public BigDecimal OrderSubTotal;
    public BigDecimal OrderDiscount;
    public BigDecimal RefundedAmount;
    public BigDecimal ShippingFee;

    public String ShippingType;
    public String ShippingID;
    public Date ShippingDate;

    public Date CreatedTime;
    public Date PaymentTime;
    public Date FinishedTime;

    public String OrderStatus;//":"CREATED",
    public String UserCheckNo;
    public String PaymentStatus;//":"NOT_PAID",
    public String Remark;
    public String PayTypeId;
    public String InvoiceType;
    public String InvoiceTitle;
    public int WorkerId;
    public int Deleted;


    public ProductOrderListItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.UserAddressId);
        dest.writeString(this.Address);
        dest.writeString(this.Country);
        dest.writeString(this.District);
        dest.writeInt(this.IsDefault);
        dest.writeString(this.ReceiverName);
        dest.writeString(this.ReceiverPhone);
        dest.writeString(this.Region);
        dest.writeString(this.AddressType);
        dest.writeStringList(this.ThumbUrls);
        dest.writeTypedList(details);
        dest.writeInt(this.OrderId);
        dest.writeString(this.OrderType);
        dest.writeString(this.OrderNo);
        dest.writeInt(this.UserId);
        dest.writeInt(this.CarModalId);
        dest.writeSerializable(this.OrderTotal);
        dest.writeSerializable(this.OrderSubTotal);
        dest.writeSerializable(this.OrderDiscount);
        dest.writeSerializable(this.RefundedAmount);
        dest.writeSerializable(this.ShippingFee);
        dest.writeString(this.ShippingType);
        dest.writeString(this.ShippingID);
        dest.writeLong(ShippingDate != null ? ShippingDate.getTime() : -1);
        dest.writeLong(CreatedTime != null ? CreatedTime.getTime() : -1);
        dest.writeLong(PaymentTime != null ? PaymentTime.getTime() : -1);
        dest.writeLong(FinishedTime != null ? FinishedTime.getTime() : -1);
        dest.writeString(this.OrderStatus);
        dest.writeString(this.UserCheckNo);
        dest.writeString(this.PaymentStatus);
        dest.writeString(this.Remark);
        dest.writeString(this.PayTypeId);
        dest.writeString(this.InvoiceType);
        dest.writeString(this.InvoiceTitle);
        dest.writeInt(this.WorkerId);
        dest.writeInt(this.Deleted);
    }

    protected ProductOrderListItem(Parcel in) {
        this.UserAddressId = in.readInt();
        this.Address = in.readString();
        this.Country = in.readString();
        this.District = in.readString();
        this.IsDefault = in.readInt();
        this.ReceiverName = in.readString();
        this.ReceiverPhone = in.readString();
        this.Region = in.readString();
        this.AddressType = in.readString();
        this.ThumbUrls = in.createStringArrayList();
        this.details = in.createTypedArrayList(ProductOrderDetail.CREATOR);
        this.OrderId = in.readInt();
        this.OrderType = in.readString();
        this.OrderNo = in.readString();
        this.UserId = in.readInt();
        this.CarModalId = in.readInt();
        this.OrderTotal = (BigDecimal) in.readSerializable();
        this.OrderSubTotal = (BigDecimal) in.readSerializable();
        this.OrderDiscount = (BigDecimal) in.readSerializable();
        this.RefundedAmount = (BigDecimal) in.readSerializable();
        this.ShippingFee = (BigDecimal) in.readSerializable();
        this.ShippingType = in.readString();
        this.ShippingID = in.readString();
        long tmpShippingDate = in.readLong();
        this.ShippingDate = tmpShippingDate == -1 ? null : new Date(tmpShippingDate);
        long tmpCreatedTime = in.readLong();
        this.CreatedTime = tmpCreatedTime == -1 ? null : new Date(tmpCreatedTime);
        long tmpPaymentTime = in.readLong();
        this.PaymentTime = tmpPaymentTime == -1 ? null : new Date(tmpPaymentTime);
        long tmpFinishedTime = in.readLong();
        this.FinishedTime = tmpFinishedTime == -1 ? null : new Date(tmpFinishedTime);
        this.OrderStatus = in.readString();
        this.UserCheckNo = in.readString();
        this.PaymentStatus = in.readString();
        this.Remark = in.readString();
        this.PayTypeId = in.readString();
        this.InvoiceType = in.readString();
        this.InvoiceTitle = in.readString();
        this.WorkerId = in.readInt();
        this.Deleted = in.readInt();
    }

    public static final Creator<ProductOrderListItem> CREATOR = new Creator<ProductOrderListItem>() {
        public ProductOrderListItem createFromParcel(Parcel source) {
            return new ProductOrderListItem(source);
        }

        public ProductOrderListItem[] newArray(int size) {
            return new ProductOrderListItem[size];
        }
    };
}
