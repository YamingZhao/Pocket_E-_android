package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * 商品评论项
 *
 * Created by snox on 2015/12/19.
 */
public class ProductCommentItem implements Parcelable {
    public int ProductCommentId;
    public int ModuleId;
    public int ProductId;
    public String Comment;
    public int UserId;
    public int WorkerId;
    public String Type;
    public int Rating;
    public int SupportCount;
    public int NagCount;
    public Date CommentDate;
    public String Avatar;
    public String Username;
    public String Firstname;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ProductCommentId);
        dest.writeInt(this.ModuleId);
        dest.writeInt(this.ProductId);
        dest.writeString(this.Comment);
        dest.writeInt(this.UserId);
        dest.writeInt(this.WorkerId);
        dest.writeString(this.Type);
        dest.writeInt(this.Rating);
        dest.writeInt(this.SupportCount);
        dest.writeInt(this.NagCount);
        dest.writeLong(CommentDate != null ? CommentDate.getTime() : -1);
        dest.writeString(this.Avatar);
        dest.writeString(this.Username);
        dest.writeString(this.Firstname);
    }

    public ProductCommentItem() {
    }

    protected ProductCommentItem(Parcel in) {
        this.ProductCommentId = in.readInt();
        this.ModuleId = in.readInt();
        this.ProductId = in.readInt();
        this.Comment = in.readString();
        this.UserId = in.readInt();
        this.WorkerId = in.readInt();
        this.Type = in.readString();
        this.Rating = in.readInt();
        this.SupportCount = in.readInt();
        this.NagCount = in.readInt();
        long tmpCommentDate = in.readLong();
        this.CommentDate = tmpCommentDate == -1 ? null : new Date(tmpCommentDate);
        this.Avatar = in.readString();
        this.Username = in.readString();
        this.Firstname = in.readString();
    }

    public static final Creator<ProductCommentItem> CREATOR = new Creator<ProductCommentItem>() {
        public ProductCommentItem createFromParcel(Parcel source) {
            return new ProductCommentItem(source);
        }

        public ProductCommentItem[] newArray(int size) {
            return new ProductCommentItem[size];
        }
    };
}
