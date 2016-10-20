package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author snox@live.com
 * @date 2015/10/27.
 *
 * {"CategoryId":1,"ModuleId":0,"Name":"保养配件","ParentID":0,"Description":"NULL","DisplayOrder":1,"Type":"product","Deleted":0}
 */
public class Category implements Parcelable {

    public int CategoryId;

    public int DisplayOrder;

    public String Name;

    public String Description;

    public String Type;

    public boolean isChecked;

    public Category() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.CategoryId);
        dest.writeInt(this.DisplayOrder);
        dest.writeString(this.Name);
        dest.writeString(this.Description);
        dest.writeString(this.Type);
        dest.writeByte(isChecked ? (byte) 1 : (byte) 0);
    }

    protected Category(Parcel in) {
        this.CategoryId = in.readInt();
        this.DisplayOrder = in.readInt();
        this.Name = in.readString();
        this.Description = in.readString();
        this.Type = in.readString();
        this.isChecked = in.readByte() != 0;
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
