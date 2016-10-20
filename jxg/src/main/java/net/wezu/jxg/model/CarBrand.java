package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author snox@live.com
 * @date 2015/11/7.
 */
public class CarBrand implements Parcelable {

    public String FirstLetter;

    public int CardBrandId;

    public int ModuleId;

    public String BrandName;

    public String Type;

    public int ParentBrandId;

    public String BrandLogo;

    public String Category;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.FirstLetter);
        dest.writeInt(this.CardBrandId);
        dest.writeInt(this.ModuleId);
        dest.writeString(this.BrandName);
        dest.writeString(this.Type);
        dest.writeInt(this.ParentBrandId);
        dest.writeString(this.BrandLogo);
        dest.writeString(this.Category);
    }

    public CarBrand() {
    }

    protected CarBrand(Parcel in) {
        this.FirstLetter = in.readString();
        this.CardBrandId = in.readInt();
        this.ModuleId = in.readInt();
        this.BrandName = in.readString();
        this.Type = in.readString();
        this.ParentBrandId = in.readInt();
        this.BrandLogo = in.readString();
        this.Category = in.readString();
    }

    public static final Creator<CarBrand> CREATOR = new Creator<CarBrand>() {
        public CarBrand createFromParcel(Parcel source) {
            return new CarBrand(source);
        }

        public CarBrand[] newArray(int size) {
            return new CarBrand[size];
        }
    };
}
