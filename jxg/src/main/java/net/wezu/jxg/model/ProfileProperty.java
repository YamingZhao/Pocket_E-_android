package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author snox@live.com
 * @date 2015/10/25.
 */
public class ProfileProperty implements Parcelable {

    public static final String PN_VAT = "VAT";
    public static final String PN_CompanyName = "CompanyName";
    public static final String PN_PaymentAccount = "PaymentAccount";
    public static final String PN_PointBalance = "PointBalance";
    public static final String PN_PaymentAccountVerified = "PaymentAccountVerified";
    public static final String PN_FirstName = "FirstName";
    public static final String PN_Gender = "Gender";

    public int DataType;

    public String PropertyCategory;

    public String PropertyName;

    public String PropertyValue;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.PropertyName);
        dest.writeString(this.PropertyValue);
    }

    public ProfileProperty() {
    }

    protected ProfileProperty(Parcel in) {
        this.PropertyName = in.readString();
        this.PropertyValue = in.readString();
    }

    public static final Creator<ProfileProperty> CREATOR = new Creator<ProfileProperty>() {
        public ProfileProperty createFromParcel(Parcel source) {
            return new ProfileProperty(source);
        }

        public ProfileProperty[] newArray(int size) {
            return new ProfileProperty[size];
        }
    };
}
