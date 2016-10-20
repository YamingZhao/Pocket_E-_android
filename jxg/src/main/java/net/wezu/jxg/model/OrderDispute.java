package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by snox on 2015/12/14.
 */
public class OrderDispute implements Parcelable {
    public int OrderDisputeId;
    public int OrderId;
    public int UserId;
    public int WorkerId;
    public String Comment;
    public Date CreatedOn;
    public String Status;
    public String Memo;
    public Date ResolvedOn;
    public int ResolvedBy;


    public OrderDispute() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.OrderDisputeId);
        dest.writeInt(this.OrderId);
        dest.writeInt(this.UserId);
        dest.writeInt(this.WorkerId);
        dest.writeString(this.Comment);
        dest.writeLong(CreatedOn != null ? CreatedOn.getTime() : -1);
        dest.writeString(this.Status);
        dest.writeString(this.Memo);
        dest.writeLong(ResolvedOn != null ? ResolvedOn.getTime() : -1);
        dest.writeInt(this.ResolvedBy);
    }

    protected OrderDispute(Parcel in) {
        this.OrderDisputeId = in.readInt();
        this.OrderId = in.readInt();
        this.UserId = in.readInt();
        this.WorkerId = in.readInt();
        this.Comment = in.readString();
        long tmpCreatedOn = in.readLong();
        this.CreatedOn = tmpCreatedOn == -1 ? null : new Date(tmpCreatedOn);
        this.Status = in.readString();
        this.Memo = in.readString();
        long tmpResolvedOn = in.readLong();
        this.ResolvedOn = tmpResolvedOn == -1 ? null : new Date(tmpResolvedOn);
        this.ResolvedBy = in.readInt();
    }

    public static final Creator<OrderDispute> CREATOR = new Creator<OrderDispute>() {
        public OrderDispute createFromParcel(Parcel source) {
            return new OrderDispute(source);
        }

        public OrderDispute[] newArray(int size) {
            return new OrderDispute[size];
        }
    };
}
