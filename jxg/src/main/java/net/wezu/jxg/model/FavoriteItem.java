package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 收藏中间表
 * Created by snox on 2016/1/27.
 */
public class FavoriteItem implements Parcelable {

    public int MyFavoriteId;

    public int UserId;

    public int RelateId;

    public String Type;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.MyFavoriteId);
        dest.writeInt(this.UserId);
        dest.writeInt(this.RelateId);
        dest.writeString(this.Type);
    }

    public FavoriteItem() {
    }

    protected FavoriteItem(Parcel in) {
        this.MyFavoriteId = in.readInt();
        this.UserId = in.readInt();
        this.RelateId = in.readInt();
        this.Type = in.readString();
    }

    public static final Creator<FavoriteItem> CREATOR = new Creator<FavoriteItem>() {
        public FavoriteItem createFromParcel(Parcel source) {
            return new FavoriteItem(source);
        }

        public FavoriteItem[] newArray(int size) {
            return new FavoriteItem[size];
        }
    };
}
