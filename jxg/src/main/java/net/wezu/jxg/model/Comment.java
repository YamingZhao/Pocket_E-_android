package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by snox on 2015/12/11.
 */
public class Comment implements Parcelable {

    public String Avatar;
    public float AvgRating;
    public int CarModalId;
    public String CarModalName;
    public String Comment;
    public Date CommentDate;
    public String FirstName;
    public int ModuleId;
    public int OrderId;
    public String PlateNo;
    public int RateAttitude;
    public int RateClean;
    public int RateQuality;
    public int RateSpeed;
    public String ServiceType;
    public int UserCommentId;
    public int UserId;
    public String Username;
    public int WorkerId;


    public Comment() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Avatar);
        dest.writeFloat(this.AvgRating);
        dest.writeInt(this.CarModalId);
        dest.writeString(this.CarModalName);
        dest.writeString(this.Comment);
        dest.writeLong(CommentDate != null ? CommentDate.getTime() : -1);
        dest.writeString(this.FirstName);
        dest.writeInt(this.ModuleId);
        dest.writeInt(this.OrderId);
        dest.writeString(this.PlateNo);
        dest.writeInt(this.RateAttitude);
        dest.writeInt(this.RateClean);
        dest.writeInt(this.RateQuality);
        dest.writeInt(this.RateSpeed);
        dest.writeString(this.ServiceType);
        dest.writeInt(this.UserCommentId);
        dest.writeInt(this.UserId);
        dest.writeString(this.Username);
        dest.writeInt(this.WorkerId);
    }

    protected Comment(Parcel in) {
        this.Avatar = in.readString();
        this.AvgRating = in.readFloat();
        this.CarModalId = in.readInt();
        this.CarModalName = in.readString();
        this.Comment = in.readString();
        long tmpCommentDate = in.readLong();
        this.CommentDate = tmpCommentDate == -1 ? null : new Date(tmpCommentDate);
        this.FirstName = in.readString();
        this.ModuleId = in.readInt();
        this.OrderId = in.readInt();
        this.PlateNo = in.readString();
        this.RateAttitude = in.readInt();
        this.RateClean = in.readInt();
        this.RateQuality = in.readInt();
        this.RateSpeed = in.readInt();
        this.ServiceType = in.readString();
        this.UserCommentId = in.readInt();
        this.UserId = in.readInt();
        this.Username = in.readString();
        this.WorkerId = in.readInt();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
