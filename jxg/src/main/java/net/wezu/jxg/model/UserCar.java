package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author snox@live.com
 * @date 2015/10/23.
 */
public class UserCar implements Parcelable {

    public int CarBrandId;
    public int CarSeriesIs;
    public int Year;

    public String ModalName;
    public String SeriesName;
    public String BrandName;

    public String ModalPic;

    public String SeriesLogo;

    public String BrandLogo;

    public int UserCarId;

    public int UserId;

    public int CarModelId;

    public String PlateNo;

    public String Color;

    public String LicensePic1;

    public String LicensePic2;

    public int IsDefault;

    public boolean isDefault() {
        return IsDefault == 1;
    }

    public UserCar() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.CarBrandId);
        dest.writeInt(this.CarSeriesIs);
        dest.writeInt(this.Year);
        dest.writeString(this.ModalName);
        dest.writeString(this.SeriesName);
        dest.writeString(this.BrandName);
        dest.writeString(this.ModalPic);
        dest.writeString(this.SeriesLogo);
        dest.writeString(this.BrandLogo);
        dest.writeInt(this.UserCarId);
        dest.writeInt(this.UserId);
        dest.writeInt(this.CarModelId);
        dest.writeString(this.PlateNo);
        dest.writeString(this.Color);
        dest.writeString(this.LicensePic1);
        dest.writeString(this.LicensePic2);
        dest.writeInt(this.IsDefault);
    }

    protected UserCar(Parcel in) {
        this.CarBrandId = in.readInt();
        this.CarSeriesIs = in.readInt();
        this.Year = in.readInt();
        this.ModalName = in.readString();
        this.SeriesName = in.readString();
        this.BrandName = in.readString();
        this.ModalPic = in.readString();
        this.SeriesLogo = in.readString();
        this.BrandLogo = in.readString();
        this.UserCarId = in.readInt();
        this.UserId = in.readInt();
        this.CarModelId = in.readInt();
        this.PlateNo = in.readString();
        this.Color = in.readString();
        this.LicensePic1 = in.readString();
        this.LicensePic2 = in.readString();
        this.IsDefault = in.readInt();
    }

    public static final Creator<UserCar> CREATOR = new Creator<UserCar>() {
        public UserCar createFromParcel(Parcel source) {
            return new UserCar(source);
        }

        public UserCar[] newArray(int size) {
            return new UserCar[size];
        }
    };
}
