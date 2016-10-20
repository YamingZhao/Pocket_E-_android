package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品订单的商品项
 *
 * Created by snox on 2015/12/20.
 */
public class ProductOrderDetail implements Parcelable {

    public int OrderDetailId;
    public int OrderId;
    public int ProductId;
    public String ProductName;
    public String ThumbUrl;
    public BigDecimal SubTotal;
    public BigDecimal UnitPrice;
    public int Number;
    public int GotPoints;
    public Date CreatedTime;
    public BigDecimal DiscountAmount;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.OrderDetailId);
        dest.writeInt(this.OrderId);
        dest.writeInt(this.ProductId);
        dest.writeString(this.ProductName);
        dest.writeString(this.ThumbUrl);
        dest.writeSerializable(this.SubTotal);
        dest.writeSerializable(this.UnitPrice);
        dest.writeInt(this.Number);
        dest.writeInt(this.GotPoints);
        dest.writeLong(CreatedTime != null ? CreatedTime.getTime() : -1);
        dest.writeSerializable(this.DiscountAmount);
    }

    public ProductOrderDetail() {
    }

    protected ProductOrderDetail(Parcel in) {
        this.OrderDetailId = in.readInt();
        this.OrderId = in.readInt();
        this.ProductId = in.readInt();
        this.ProductName = in.readString();
        this.ThumbUrl = in.readString();
        this.SubTotal = (BigDecimal) in.readSerializable();
        this.UnitPrice = (BigDecimal) in.readSerializable();
        this.Number = in.readInt();
        this.GotPoints = in.readInt();
        long tmpCreatedTime = in.readLong();
        this.CreatedTime = tmpCreatedTime == -1 ? null : new Date(tmpCreatedTime);
        this.DiscountAmount = (BigDecimal) in.readSerializable();
    }

    public static final Creator<ProductOrderDetail> CREATOR = new Creator<ProductOrderDetail>() {
        public ProductOrderDetail createFromParcel(Parcel source) {
            return new ProductOrderDetail(source);
        }

        public ProductOrderDetail[] newArray(int size) {
            return new ProductOrderDetail[size];
        }
    };
}
