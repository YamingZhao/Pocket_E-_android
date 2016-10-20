package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

/**
 * Created by snox on 2015/11/15.
 */
public class OrderInfoModel implements Parcelable {
    public int OrderInfoId;
    public int OrderId;

    public String ServiceType;
    public String ServiceLocation;
    public String ServiceDestination;
    public String Remark;

    public BigDecimal TipFee;

    public String pic1;
    public String pic2;
    public String pic3;

    public double lng;
    public double lat;
    public double wlng;
    public double wlat;

    public int PushCount;


    public OrderInfoModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.OrderInfoId);
        dest.writeInt(this.OrderId);
        dest.writeString(this.ServiceType);
        dest.writeString(this.ServiceLocation);
        dest.writeString(this.ServiceDestination);
        dest.writeString(this.Remark);
        dest.writeSerializable(this.TipFee);
        dest.writeString(this.pic1);
        dest.writeString(this.pic2);
        dest.writeString(this.pic3);
        dest.writeDouble(this.lng);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.wlng);
        dest.writeDouble(this.wlat);
        dest.writeInt(this.PushCount);
    }

    protected OrderInfoModel(Parcel in) {
        this.OrderInfoId = in.readInt();
        this.OrderId = in.readInt();
        this.ServiceType = in.readString();
        this.ServiceLocation = in.readString();
        this.ServiceDestination = in.readString();
        this.Remark = in.readString();
        this.TipFee = (BigDecimal) in.readSerializable();
        this.pic1 = in.readString();
        this.pic2 = in.readString();
        this.pic3 = in.readString();
        this.lng = in.readDouble();
        this.lat = in.readDouble();
        this.wlng = in.readDouble();
        this.wlat = in.readDouble();
        this.PushCount = in.readInt();
    }

    public static final Creator<OrderInfoModel> CREATOR = new Creator<OrderInfoModel>() {
        public OrderInfoModel createFromParcel(Parcel source) {
            return new OrderInfoModel(source);
        }

        public OrderInfoModel[] newArray(int size) {
            return new OrderInfoModel[size];
        }
    };
}
