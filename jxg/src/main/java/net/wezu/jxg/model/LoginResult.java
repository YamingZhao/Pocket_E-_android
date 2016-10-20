package net.wezu.jxg.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 登录返回结果
 * Created by snox on 2016/3/15.
 */
public class LoginResult implements Parcelable {

    private UserModel userModel;
    private UserAddress companyAddress;

    public LoginResult(UserModel userModel, UserAddress companyAddress) {
        this.userModel = userModel;
        this.companyAddress = companyAddress;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public UserAddress getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(UserAddress companyAddress) {
        this.companyAddress = companyAddress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.userModel, 0);
        dest.writeParcelable(this.companyAddress, 0);
    }

    public LoginResult() {
    }

    protected LoginResult(Parcel in) {
        this.userModel = in.readParcelable(UserModel.class.getClassLoader());
        this.companyAddress = in.readParcelable(UserAddress.class.getClassLoader());
    }

    public static final Creator<LoginResult> CREATOR = new Creator<LoginResult>() {
        public LoginResult createFromParcel(Parcel source) {
            return new LoginResult(source);
        }

        public LoginResult[] newArray(int size) {
            return new LoginResult[size];
        }
    };
}
