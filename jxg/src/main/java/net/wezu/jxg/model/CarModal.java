package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author snox@live.com
 * @date 2015/11/8.
 */
public class CarModal implements Parcelable {

    public int CarModalId;

    public int ModuleId;

    public int CarBrandId;

    public int CarSeriesId;

    public int Year;

    public String ModalName;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.CarModalId);
        dest.writeInt(this.ModuleId);
        dest.writeInt(this.CarBrandId);
        dest.writeInt(this.CarSeriesId);
        dest.writeInt(this.Year);
        dest.writeString(this.ModalName);
    }

    public CarModal() {
    }

    protected CarModal(Parcel in) {
        this.CarModalId = in.readInt();
        this.ModuleId = in.readInt();
        this.CarBrandId = in.readInt();
        this.CarSeriesId = in.readInt();
        this.Year = in.readInt();
        this.ModalName = in.readString();
    }

    public static final Creator<CarModal> CREATOR = new Creator<CarModal>() {
        public CarModal createFromParcel(Parcel source) {
            return new CarModal(source);
        }

        public CarModal[] newArray(int size) {
            return new CarModal[size];
        }
    };
}
