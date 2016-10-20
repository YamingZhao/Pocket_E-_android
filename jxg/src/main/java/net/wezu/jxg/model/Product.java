package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 产品，服务
 * Created by snox on 2015/11/15.
 */
public class Product implements Parcelable {

    public int ProductId;
    public int CategoryId;

    public BigDecimal Price;
    public BigDecimal CostPrice;
    public BigDecimal OnSalePrice;

    public String ProductName;
    public String ProductCode;

    public String Summary;
    public String Description;
    public String ParameterDescription;
    public String ServiceDescription;

//    public Date CreatedTime;
//    public Date UpdatedTime;
//    public Date AvailableStartDateTime;
//    public Date AvailableEndDateTime;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ProductId);
        dest.writeInt(this.CategoryId);
        dest.writeSerializable(this.Price);
        dest.writeSerializable(this.CostPrice);
        dest.writeSerializable(this.OnSalePrice);
        dest.writeString(this.ProductName);
        dest.writeString(this.ProductCode);
        dest.writeString(this.Summary);
        dest.writeString(this.Description);
        dest.writeString(this.ParameterDescription);
        dest.writeString(this.ServiceDescription);
//        dest.writeLong(CreatedTime != null ? CreatedTime.getTime() : -1);
//        dest.writeLong(UpdatedTime != null ? UpdatedTime.getTime() : -1);
//        dest.writeLong(AvailableStartDateTime != null ? AvailableStartDateTime.getTime() : -1);
//        dest.writeLong(AvailableEndDateTime != null ? AvailableEndDateTime.getTime() : -1);
    }

    public Product() {
    }

    protected Product(Parcel in) {
        this.ProductId = in.readInt();
        this.CategoryId = in.readInt();
        this.Price = (BigDecimal) in.readSerializable();
        this.CostPrice = (BigDecimal) in.readSerializable();
        this.OnSalePrice = (BigDecimal) in.readSerializable();
        this.ProductName = in.readString();
        this.ProductCode = in.readString();
        this.Summary = in.readString();
        this.Description = in.readString();
        this.ParameterDescription = in.readString();
        this.ServiceDescription = in.readString();
//        long tmpCreatedTime = in.readLong();
//        this.CreatedTime = tmpCreatedTime == -1 ? null : new Date(tmpCreatedTime);
//        long tmpUpdatedTime = in.readLong();
//        this.UpdatedTime = tmpUpdatedTime == -1 ? null : new Date(tmpUpdatedTime);
//        long tmpAvailableStartDateTime = in.readLong();
//        this.AvailableStartDateTime = tmpAvailableStartDateTime == -1 ? null : new Date(tmpAvailableStartDateTime);
//        long tmpAvailableEndDateTime = in.readLong();
//        this.AvailableEndDateTime = tmpAvailableEndDateTime == -1 ? null : new Date(tmpAvailableEndDateTime);
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
