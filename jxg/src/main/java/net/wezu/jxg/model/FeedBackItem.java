package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * 建议与反馈
 * Created by snox on 2016/4/9.
 */
public class FeedBackItem implements Parcelable {

    public int FeedbackId;

    public String Type;

    public int RelatedId;

    public String ExtraInfo;

    public String Comment;

    public int UserId;

    public Date FeedbackDate;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.FeedbackId);
        dest.writeString(this.Type);
        dest.writeInt(this.RelatedId);
        dest.writeString(this.ExtraInfo);
        dest.writeString(this.Comment);
        dest.writeInt(this.UserId);
        dest.writeLong(FeedbackDate != null ? FeedbackDate.getTime() : -1);
    }

    public FeedBackItem() {
    }

    protected FeedBackItem(Parcel in) {
        this.FeedbackId = in.readInt();
        this.Type = in.readString();
        this.RelatedId = in.readInt();
        this.ExtraInfo = in.readString();
        this.Comment = in.readString();
        this.UserId = in.readInt();
        long tmpFeedbackDate = in.readLong();
        this.FeedbackDate = tmpFeedbackDate == -1 ? null : new Date(tmpFeedbackDate);
    }

    public static final Creator<FeedBackItem> CREATOR = new Creator<FeedBackItem>() {
        public FeedBackItem createFromParcel(Parcel source) {
            return new FeedBackItem(source);
        }

        public FeedBackItem[] newArray(int size) {
            return new FeedBackItem[size];
        }
    };
}
