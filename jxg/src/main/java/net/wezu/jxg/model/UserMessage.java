package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by snox on 2016/1/27.
 */
public class UserMessage implements Parcelable {

    public int MessageID;

    public String To;
    public String From;

    public String Subject;
    public String Body;
    public String DisplayDate;

    public int ConversationId;

    public boolean ReplyAllAllowed;

    public UserMessage() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.MessageID);
        dest.writeString(this.To);
        dest.writeString(this.From);
        dest.writeString(this.Subject);
        dest.writeString(this.Body);
        dest.writeString(this.DisplayDate);
        dest.writeInt(this.ConversationId);
        dest.writeByte(ReplyAllAllowed ? (byte) 1 : (byte) 0);
    }

    protected UserMessage(Parcel in) {
        this.MessageID = in.readInt();
        this.To = in.readString();
        this.From = in.readString();
        this.Subject = in.readString();
        this.Body = in.readString();
        this.DisplayDate = in.readString();
        this.ConversationId = in.readInt();
        this.ReplyAllAllowed = in.readByte() != 0;
    }

    public static final Creator<UserMessage> CREATOR = new Creator<UserMessage>() {
        public UserMessage createFromParcel(Parcel source) {
            return new UserMessage(source);
        }

        public UserMessage[] newArray(int size) {
            return new UserMessage[size];
        }
    };
}
