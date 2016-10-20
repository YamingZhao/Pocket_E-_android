package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.List;

/**
 * 提现列表
 * Created by snox on 2016/3/14.
 */
public class Withdraw implements Parcelable {

    public BigDecimal total;

    public BigDecimal totalPayable;

    public List<OrderListItemModel> records;

    public Withdraw() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.total);
        dest.writeSerializable(this.totalPayable);
        dest.writeTypedList(records);
    }

    protected Withdraw(Parcel in) {
        this.total = (BigDecimal) in.readSerializable();
        this.totalPayable = (BigDecimal) in.readSerializable();
        this.records = in.createTypedArrayList(OrderListItemModel.CREATOR);
    }

    public static final Creator<Withdraw> CREATOR = new Creator<Withdraw>() {
        public Withdraw createFromParcel(Parcel source) {
            return new Withdraw(source);
        }

        public Withdraw[] newArray(int size) {
            return new Withdraw[size];
        }
    };
}
