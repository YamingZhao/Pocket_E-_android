package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author snox@live.com
 * @date 2015/10/23.
 */
public class UserAddress implements Parcelable {

    public int UserAddressId;

    public int UserId;

    public String Region;

    public String District;

    public String Address;

    public int IsDefault;

    public String ReceiverName;

    public String ReceiverPhone;

    public String Type;

    public String getShortAddress() {
        String add = Region + District + Address;
        try {
            return add.substring(0, 10);
        } catch (IndexOutOfBoundsException e) {
            return add;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.UserAddressId);
        dest.writeInt(this.UserId);
        dest.writeString(this.Region);
        dest.writeString(this.District);
        dest.writeString(this.Address);
        dest.writeInt(this.IsDefault);
        dest.writeString(this.ReceiverName);
        dest.writeString(this.ReceiverPhone);
        dest.writeString(this.Type);
    }

    public UserAddress() {
    }

    protected UserAddress(Parcel in) {
        this.UserAddressId = in.readInt();
        this.UserId = in.readInt();
        this.Region = in.readString();
        this.District = in.readString();
        this.Address = in.readString();
        this.IsDefault = in.readInt();
        this.ReceiverName = in.readString();
        this.ReceiverPhone = in.readString();
        this.Type = in.readString();
    }

    public static final Creator<UserAddress> CREATOR = new Creator<UserAddress>() {
        public UserAddress createFromParcel(Parcel source) {
            return new UserAddress(source);
        }

        public UserAddress[] newArray(int size) {
            return new UserAddress[size];
        }
    };
}
