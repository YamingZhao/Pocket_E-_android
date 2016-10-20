package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author snox@live.com
 * @date 2015/10/24.
 */
public class Profile implements Parcelable {

    public String FirstName;

    public String FullName;

    public String IM;

    public String PhotoURL;

    public String Website;

    public List<ProfileProperty> ProfileProperties;

    public String getProperty(String name) {
        if (ProfileProperties != null) {
            for (ProfileProperty p : ProfileProperties) {
                if (p.PropertyName.equals(name) ) {
                    return p.PropertyValue;
                }
            }
        }
        return "";
    }

    public Profile() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.PhotoURL);
        dest.writeString(this.Website);
        dest.writeTypedList(ProfileProperties);
    }

    protected Profile(Parcel in) {
        this.PhotoURL = in.readString();
        this.Website = in.readString();
        this.ProfileProperties = in.createTypedArrayList(ProfileProperty.CREATOR);
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        public Profile createFromParcel(Parcel source) {
            return new Profile(source);
        }

        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
}
