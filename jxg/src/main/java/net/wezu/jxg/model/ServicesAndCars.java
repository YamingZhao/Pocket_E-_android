package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 服务类目和车辆
 * Created by snox on 2016/1/21.
 */
public class ServicesAndCars implements Parcelable {

    public List<UserCar> cars;

    public List<Product> products;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(cars);
        dest.writeTypedList(products);
    }

    public ServicesAndCars() {
    }

    protected ServicesAndCars(Parcel in) {
        this.cars = in.createTypedArrayList(UserCar.CREATOR);
        this.products = in.createTypedArrayList(Product.CREATOR);
    }

    public static final Creator<ServicesAndCars> CREATOR = new Creator<ServicesAndCars>() {
        public ServicesAndCars createFromParcel(Parcel source) {
            return new ServicesAndCars(source);
        }

        public ServicesAndCars[] newArray(int size) {
            return new ServicesAndCars[size];
        }
    };
}
