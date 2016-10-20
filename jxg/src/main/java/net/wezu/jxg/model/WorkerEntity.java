package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Worker entity
 * Created by snox on 2016/5/8.
 */
public class WorkerEntity implements Parcelable {

    public int WorkerId;
    public String WorkerUsername;
    public String WorkerFirstname;
    public String WorkerAvatar;
    public String WorkerLocation;

    public float AvgRating;

    public WorkerEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.WorkerId);
        dest.writeString(this.WorkerUsername);
        dest.writeString(this.WorkerFirstname);
        dest.writeString(this.WorkerAvatar);
        dest.writeString(this.WorkerLocation);
        dest.writeFloat(this.AvgRating);
    }

    protected WorkerEntity(Parcel in) {
        this.WorkerId = in.readInt();
        this.WorkerUsername = in.readString();
        this.WorkerFirstname = in.readString();
        this.WorkerAvatar = in.readString();
        this.WorkerLocation = in.readString();
        this.AvgRating = in.readFloat();
    }

    public static final Creator<WorkerEntity> CREATOR = new Creator<WorkerEntity>() {
        public WorkerEntity createFromParcel(Parcel source) {
            return new WorkerEntity(source);
        }

        public WorkerEntity[] newArray(int size) {
            return new WorkerEntity[size];
        }
    };
}
