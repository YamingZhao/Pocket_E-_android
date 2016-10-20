package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by snox on 2016/1/16.
 */
public class Coupon implements Parcelable {

    public int CouponId;
    public String CouponName;
    public String Description;
    public String ImageUrl;

    public BigDecimal UserAmount;
    public BigDecimal Amount;

    public int UserId;
    public int Point;

    public String Category;

    public Date CreatedTime;
    public Date UsedTime;

    public int IsUserd;

    public Date ValidFrom;
    public Date ValidTo;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.CouponId);
        dest.writeString(this.CouponName);
        dest.writeString(this.Description);
        dest.writeString(this.ImageUrl);
        dest.writeSerializable(this.UserAmount);
        dest.writeSerializable(this.Amount);
        dest.writeInt(this.UserId);
        dest.writeInt(this.Point);
        dest.writeString(this.Category);
        dest.writeLong(CreatedTime != null ? CreatedTime.getTime() : -1);
        dest.writeLong(UsedTime != null ? UsedTime.getTime() : -1);
        dest.writeInt(this.IsUserd);
        dest.writeLong(ValidFrom != null ? ValidFrom.getTime() : -1);
        dest.writeLong(ValidTo != null ? ValidTo.getTime() : -1);
    }

    public Coupon() {
    }

    protected Coupon(Parcel in) {
        this.CouponId = in.readInt();
        this.CouponName = in.readString();
        this.Description = in.readString();
        this.ImageUrl = in.readString();
        this.UserAmount = (BigDecimal) in.readSerializable();
        this.Amount = (BigDecimal) in.readSerializable();
        this.UserId = in.readInt();
        this.Point = in.readInt();
        this.Category = in.readString();
        long tmpCreatedTime = in.readLong();
        this.CreatedTime = tmpCreatedTime == -1 ? null : new Date(tmpCreatedTime);
        long tmpUsedTime = in.readLong();
        this.UsedTime = tmpUsedTime == -1 ? null : new Date(tmpUsedTime);
        this.IsUserd = in.readInt();
        long tmpValidFrom = in.readLong();
        this.ValidFrom = tmpValidFrom == -1 ? null : new Date(tmpValidFrom);
        long tmpValidTo = in.readLong();
        this.ValidTo = tmpValidTo == -1 ? null : new Date(tmpValidTo);
    }

    public static final Creator<Coupon> CREATOR = new Creator<Coupon>() {
        public Coupon createFromParcel(Parcel source) {
            return new Coupon(source);
        }

        public Coupon[] newArray(int size) {
            return new Coupon[size];
        }
    };
}
