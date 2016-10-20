package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

/**
 * @author snox@live.com
 * @date 2015/10/22.
 */
public class UserModel implements Parcelable {

    public int AffiliateID;

    public int Cacheability;

    public int CreatedByUserID;

    public Date CreatedOnDate;

    public String DisplayName;

    public String Email;

    public String FirstName;

    public String FullName;

    public boolean IsDeleted;

    public boolean IsSuperUser;

    public String LastIPAddress;

    public int LastModifiedByUserID;

    public Date LastModifiedOnDate;

    public String LastName;

    public int PortalID;

    public Profile Profile;

    public List<String> Roles;

    public int UserID;

    public String Username;

    public String getProperty(String name) {
        if (Profile != null) {
            return Profile.getProperty(name);
        }
        throw new IllegalArgumentException("没有找到对应的属性 " + name);
    }

    public UserModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.AffiliateID);
        dest.writeInt(this.Cacheability);
        dest.writeInt(this.CreatedByUserID);
        dest.writeLong(CreatedOnDate != null ? CreatedOnDate.getTime() : -1);
        dest.writeString(this.DisplayName);
        dest.writeString(this.Email);
        dest.writeString(this.FirstName);
        dest.writeString(this.FullName);
        dest.writeByte(IsDeleted ? (byte) 1 : (byte) 0);
        dest.writeByte(IsSuperUser ? (byte) 1 : (byte) 0);
        dest.writeString(this.LastIPAddress);
        dest.writeInt(this.LastModifiedByUserID);
        dest.writeLong(LastModifiedOnDate != null ? LastModifiedOnDate.getTime() : -1);
        dest.writeString(this.LastName);
        dest.writeInt(this.PortalID);
        dest.writeParcelable(this.Profile, 0);
        dest.writeStringList(this.Roles);
        dest.writeInt(this.UserID);
        dest.writeString(this.Username);
    }

    protected UserModel(Parcel in) {
        this.AffiliateID = in.readInt();
        this.Cacheability = in.readInt();
        this.CreatedByUserID = in.readInt();
        long tmpCreatedOnDate = in.readLong();
        this.CreatedOnDate = tmpCreatedOnDate == -1 ? null : new Date(tmpCreatedOnDate);
        this.DisplayName = in.readString();
        this.Email = in.readString();
        this.FirstName = in.readString();
        this.FullName = in.readString();
        this.IsDeleted = in.readByte() != 0;
        this.IsSuperUser = in.readByte() != 0;
        this.LastIPAddress = in.readString();
        this.LastModifiedByUserID = in.readInt();
        long tmpLastModifiedOnDate = in.readLong();
        this.LastModifiedOnDate = tmpLastModifiedOnDate == -1 ? null : new Date(tmpLastModifiedOnDate);
        this.LastName = in.readString();
        this.PortalID = in.readInt();
        this.Profile = in.readParcelable(net.wezu.jxg.model.Profile.class.getClassLoader());
        this.Roles = in.createStringArrayList();
        this.UserID = in.readInt();
        this.Username = in.readString();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        public UserModel createFromParcel(Parcel source) {
            return new UserModel(source);
        }

        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };
}
