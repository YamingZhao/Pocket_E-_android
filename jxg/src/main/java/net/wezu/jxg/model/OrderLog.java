package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by snox on 2015/11/18.
 */
public class OrderLog implements Parcelable {

    public int OrderLogId;

    public int OrderId;

    public String Message;

    public String CreatedOn;


    public OrderLog() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.OrderLogId);
        dest.writeInt(this.OrderId);
        dest.writeString(this.Message);
        dest.writeString(this.CreatedOn);
    }

    protected OrderLog(Parcel in) {
        this.OrderLogId = in.readInt();
        this.OrderId = in.readInt();
        this.Message = in.readString();
        this.CreatedOn = in.readString();
    }

    public static final Creator<OrderLog> CREATOR = new Creator<OrderLog>() {
        public OrderLog createFromParcel(Parcel source) {
            return new OrderLog(source);
        }

        public OrderLog[] newArray(int size) {
            return new OrderLog[size];
        }
    };
}
