package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 产品列表项
 *
 * Created by snox on 2015/11/30.
 */
public class ProductItemModel implements Parcelable {

    public int ProductId;
    public int CategoryId;

    public String ProductCode;
    public String ProductName;

    public String Type;

    public String ThumbUrl;
    public String ImageUrl1;
    public String ImageUrl2;
    public String ImageUrl3;
    public String ImageUrl4;

    public int Inventory;
    public int Point;

    public BigDecimal CostPrice;
    public BigDecimal Price;
    public BigDecimal OnSalePrice;

    public String Summary;
    public String Description;
    public String ParameterDescription;
    public String ServiceDescription;
    public String State;
    public String Remark;

    public Date CreatedTime;
    public Date UpdatedTime;
    public Date AvailableStartDateTime;
    public Date AvailableEndDateTime;

    public int OrderMinimumQuantity;
    public int OrderMaximumQuantity;
    public int SalesVolume;
    public int DisplayOrder;
    public int IsRecommend;

    public int TotalComments;
    public int GoodComments;
    public int TotalSales;

    public double GoodRate;
    public int Rating;
    public float AvgRating;

    public ProductItemModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ProductId);
        dest.writeInt(this.CategoryId);
        dest.writeString(this.ProductCode);
        dest.writeString(this.ProductName);
        dest.writeString(this.Type);
        dest.writeString(this.ThumbUrl);
        dest.writeString(this.ImageUrl1);
        dest.writeString(this.ImageUrl2);
        dest.writeString(this.ImageUrl3);
        dest.writeString(this.ImageUrl4);
        dest.writeInt(this.Inventory);
        dest.writeInt(this.Point);
        dest.writeSerializable(this.CostPrice);
        dest.writeSerializable(this.Price);
        dest.writeSerializable(this.OnSalePrice);
        dest.writeString(this.Summary);
        dest.writeString(this.Description);
        dest.writeString(this.ParameterDescription);
        dest.writeString(this.ServiceDescription);
        dest.writeString(this.State);
        dest.writeString(this.Remark);
        dest.writeLong(CreatedTime != null ? CreatedTime.getTime() : -1);
        dest.writeLong(UpdatedTime != null ? UpdatedTime.getTime() : -1);
        dest.writeLong(AvailableStartDateTime != null ? AvailableStartDateTime.getTime() : -1);
        dest.writeLong(AvailableEndDateTime != null ? AvailableEndDateTime.getTime() : -1);
        dest.writeInt(this.OrderMinimumQuantity);
        dest.writeInt(this.OrderMaximumQuantity);
        dest.writeInt(this.SalesVolume);
        dest.writeInt(this.DisplayOrder);
        dest.writeInt(this.IsRecommend);
        dest.writeInt(this.TotalComments);
        dest.writeInt(this.GoodComments);
        dest.writeInt(this.TotalSales);
        dest.writeDouble(this.GoodRate);
        dest.writeInt(this.Rating);
        dest.writeFloat(this.AvgRating);
    }

    protected ProductItemModel(Parcel in) {
        this.ProductId = in.readInt();
        this.CategoryId = in.readInt();
        this.ProductCode = in.readString();
        this.ProductName = in.readString();
        this.Type = in.readString();
        this.ThumbUrl = in.readString();
        this.ImageUrl1 = in.readString();
        this.ImageUrl2 = in.readString();
        this.ImageUrl3 = in.readString();
        this.ImageUrl4 = in.readString();
        this.Inventory = in.readInt();
        this.Point = in.readInt();
        this.CostPrice = (BigDecimal) in.readSerializable();
        this.Price = (BigDecimal) in.readSerializable();
        this.OnSalePrice = (BigDecimal) in.readSerializable();
        this.Summary = in.readString();
        this.Description = in.readString();
        this.ParameterDescription = in.readString();
        this.ServiceDescription = in.readString();
        this.State = in.readString();
        this.Remark = in.readString();
        long tmpCreatedTime = in.readLong();
        this.CreatedTime = tmpCreatedTime == -1 ? null : new Date(tmpCreatedTime);
        long tmpUpdatedTime = in.readLong();
        this.UpdatedTime = tmpUpdatedTime == -1 ? null : new Date(tmpUpdatedTime);
        long tmpAvailableStartDateTime = in.readLong();
        this.AvailableStartDateTime = tmpAvailableStartDateTime == -1 ? null : new Date(tmpAvailableStartDateTime);
        long tmpAvailableEndDateTime = in.readLong();
        this.AvailableEndDateTime = tmpAvailableEndDateTime == -1 ? null : new Date(tmpAvailableEndDateTime);
        this.OrderMinimumQuantity = in.readInt();
        this.OrderMaximumQuantity = in.readInt();
        this.SalesVolume = in.readInt();
        this.DisplayOrder = in.readInt();
        this.IsRecommend = in.readInt();
        this.TotalComments = in.readInt();
        this.GoodComments = in.readInt();
        this.TotalSales = in.readInt();
        this.GoodRate = in.readDouble();
        this.Rating = in.readInt();
        this.AvgRating = in.readFloat();
    }

    public static final Creator<ProductItemModel> CREATOR = new Creator<ProductItemModel>() {
        public ProductItemModel createFromParcel(Parcel source) {
            return new ProductItemModel(source);
        }

        public ProductItemModel[] newArray(int size) {
            return new ProductItemModel[size];
        }
    };
}
