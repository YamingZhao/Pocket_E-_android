package net.wezu.jxg.service;

import android.os.Parcel;
import android.os.Parcelable;

import net.wezu.jxg.model.UserMessage;

import java.util.List;

/**
 * Created by snox on 2016/1/27.
 */
public class MessageResult implements Parcelable {

    public int TotalConversations;

    public int TotalNewThreads;

    public List<UserMessage> Conversations;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.TotalConversations);
        dest.writeInt(this.TotalNewThreads);
        dest.writeTypedList(Conversations);
    }

    public MessageResult() {
    }

    protected MessageResult(Parcel in) {
        this.TotalConversations = in.readInt();
        this.TotalNewThreads = in.readInt();
        this.Conversations = in.createTypedArrayList(UserMessage.CREATOR);
    }

    public static final Creator<MessageResult> CREATOR = new Creator<MessageResult>() {
        public MessageResult createFromParcel(Parcel source) {
            return new MessageResult(source);
        }

        public MessageResult[] newArray(int size) {
            return new MessageResult[size];
        }
    };
}
