package org.androidpn.client;

import android.os.Parcel;
import android.os.Parcelable;

import org.jivesoftware.smack.packet.IQ;

/**
 * 提醒消息载体
 *
 * @author snox@live.com
 * @date 2015/10/29.
 */
public class NotificationIQ extends IQ implements Parcelable {

    private String id;
    private String apiKey;
    private String title;
    private String message;
    private String uri;

    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public NotificationIQ() {

    }

    @Override
    public String getChildElementXML() {

        StringBuilder builder = new StringBuilder();
        builder.append("<").append("notification").append(" xmlns=\"")
                .append("androidpn:iq:notification").append("\">");
        if (id != null) {
            builder.append("<id>").append(id).append("</id>");
        }
        builder.append("</").append("notification").append(">");

        return builder.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.apiKey);
        dest.writeString(this.title);
        dest.writeString(this.message);
        dest.writeString(this.uri);
        dest.writeInt(this.index);
    }

    protected NotificationIQ(Parcel in) {
        this.id = in.readString();
        this.apiKey = in.readString();
        this.title = in.readString();
        this.message = in.readString();
        this.uri = in.readString();
        this.index = in.readInt();
    }

    public static final Creator<NotificationIQ> CREATOR = new Creator<NotificationIQ>() {
        public NotificationIQ createFromParcel(Parcel source) {
            return new NotificationIQ(source);
        }

        public NotificationIQ[] newArray(int size) {
            return new NotificationIQ[size];
        }
    };
}
