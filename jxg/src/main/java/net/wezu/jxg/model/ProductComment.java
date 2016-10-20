package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 商品评论
 *
 * Created by snox on 2015/12/19.
 */
public class ProductComment implements Parcelable {

    public List<ProductCommentItem> comments;

    public int TotalGood;
    public int TotalNormal;
    public int TotalBad;
    public int Total;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(comments);
        dest.writeInt(this.TotalGood);
        dest.writeInt(this.TotalNormal);
        dest.writeInt(this.TotalBad);
        dest.writeInt(this.Total);
    }

    public ProductComment() {
    }

    protected ProductComment(Parcel in) {
        this.comments = in.createTypedArrayList(ProductCommentItem.CREATOR);
        this.TotalGood = in.readInt();
        this.TotalNormal = in.readInt();
        this.TotalBad = in.readInt();
        this.Total = in.readInt();
    }

    public static final Creator<ProductComment> CREATOR = new Creator<ProductComment>() {
        public ProductComment createFromParcel(Parcel source) {
            return new ProductComment(source);
        }

        public ProductComment[] newArray(int size) {
            return new ProductComment[size];
        }
    };
}
