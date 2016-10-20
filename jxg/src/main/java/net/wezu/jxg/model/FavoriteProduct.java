package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 收藏商品
 * Created by snox on 2016/1/27.
 */
public class FavoriteProduct extends ProductItemModel implements Parcelable {

    public int MyFavoriteId;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.MyFavoriteId);
    }

    public FavoriteProduct() {
    }

    protected FavoriteProduct(Parcel in) {
        super(in);
        this.MyFavoriteId = in.readInt();
    }

    public static final Creator<FavoriteProduct> CREATOR = new Creator<FavoriteProduct>() {
        public FavoriteProduct createFromParcel(Parcel source) {
            return new FavoriteProduct(source);
        }

        public FavoriteProduct[] newArray(int size) {
            return new FavoriteProduct[size];
        }
    };
}
