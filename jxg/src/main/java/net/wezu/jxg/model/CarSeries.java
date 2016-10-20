package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 车系
 *
 * @author snox@live.com
 * @date 2015/11/8.
 */
public class CarSeries implements Parcelable {

    public int CarSeriesId;

    public int CarBrandId;

    public String SeriesName;
    public String SeriesLogo;

    public String FirstLetter;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.CarSeriesId);
        dest.writeInt(this.CarBrandId);
        dest.writeString(this.SeriesName);
        dest.writeString(this.SeriesLogo);
        dest.writeString(this.FirstLetter);
    }

    public CarSeries() {
    }

    protected CarSeries(Parcel in) {
        this.CarSeriesId = in.readInt();
        this.CarBrandId = in.readInt();
        this.SeriesName = in.readString();
        this.SeriesLogo = in.readString();
        this.FirstLetter = in.readString();
    }

    public static final Creator<CarSeries> CREATOR = new Creator<CarSeries>() {
        public CarSeries createFromParcel(Parcel source) {
            return new CarSeries(source);
        }

        public CarSeries[] newArray(int size) {
            return new CarSeries[size];
        }
    };
}
