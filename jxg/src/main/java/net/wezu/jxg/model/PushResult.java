package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by snox on 2015/11/22.
 */
public class PushResult implements Parcelable {

    public String result;

    public int count;

    public String message;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.result);
        dest.writeInt(this.count);
        dest.writeString(this.message);
    }

    public PushResult() {
    }

    protected PushResult(Parcel in) {
        this.result = in.readString();
        this.count = in.readInt();
        this.message = in.readString();
    }

    public static final Creator<PushResult> CREATOR = new Creator<PushResult>() {
        public PushResult createFromParcel(Parcel source) {
            return new PushResult(source);
        }

        public PushResult[] newArray(int size) {
            return new PushResult[size];
        }
    };
}
