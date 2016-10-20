package net.wezu.jxg.ui.map;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import java.util.List;

/**
 * address information
 *
 * Created by snox on 2015/12/31.
 */
public class AddressInformation implements Parcelable {

    public static AddressInformation fromReverseGeoCodeResult(LatLng latLng, ReverseGeoCodeResult result) {

        return fromReverseGeoCodeResult(latLng, result,
                (result != null && result.getPoiList() != null && result.getPoiList().size() > 0) ? result.getPoiList().get(0) : null);
    }

    public static AddressInformation fromReverseGeoCodeResult(ReverseGeoCodeResult result, PoiInfo poiInfo) {
        return fromReverseGeoCodeResult(null, result,
                (result != null && result.getPoiList() != null && result.getPoiList().size() > 0) ? result.getPoiList().get(0) : null);
    }

    public static AddressInformation fromReverseGeoCodeResult(LatLng latLng, ReverseGeoCodeResult result, PoiInfo poiInfo) {
        AddressInformation addressInformation = new AddressInformation();

        ReverseGeoCodeResult.AddressComponent address = result.getAddressDetail();

        addressInformation.street = address.street;

        if (poiInfo != null) {
            latLng = poiInfo.location;
            addressInformation.name = poiInfo.name;
            addressInformation.street = poiInfo.address;
        } else {
            addressInformation.name = address.streetNumber;
            addressInformation.street = address.street;
        }

        addressInformation.latitude = latLng.latitude;
        addressInformation.longitude = latLng.longitude;
        addressInformation.province = address.province;
        addressInformation.city = address.city;
        addressInformation.district = address.district;

        return addressInformation;
    }

    public static AddressInformation fromBdLocation(BDLocation location) {
        AddressInformation addressInformation = new AddressInformation();

        addressInformation.latitude = location.getLatitude();
        addressInformation.longitude = location.getLongitude();

        Address address = location.getAddress();

        addressInformation.street = address.street;
        addressInformation.province = address.province;
        addressInformation.city = address.city;
        addressInformation.district = address.district;

        addressInformation.name = address.streetNumber;

        List<Poi> pois = location.getPoiList();
        if (pois != null && pois.size() > 0) {
            addressInformation.name = pois.get(0).getName();
        }


        return addressInformation;
    }

    public static AddressInformation fromLocation(String location) {
        AddressInformation addressInformation = new AddressInformation();

        String[] addresses = location.split(",");
        if (addresses.length == 5) {
            addressInformation.province = addresses[0];
            addressInformation.city = addresses[1];
            addressInformation.district = addresses[2];
            addressInformation.street = addresses[3];
            addressInformation.name = addresses[4];
        } else if (addresses.length == 4) {
            addressInformation.province = addresses[0];
            addressInformation.city = addresses[1];
            addressInformation.district = addresses[2];
            addressInformation.street = addresses[3];
        }
        return addressInformation;
    }

    public String toLocation() {
        return province+","+city+","+district+","+street+","+ name;
    }

    public double latitude;
    public double longitude;

    public String province;
    public String city;
    public String district;
    public String street;
    public String name;


    public AddressInformation() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.district);
        dest.writeString(this.street);
        dest.writeString(this.name);
    }

    protected AddressInformation(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.province = in.readString();
        this.city = in.readString();
        this.district = in.readString();
        this.street = in.readString();
        this.name = in.readString();
    }

    public static final Creator<AddressInformation> CREATOR = new Creator<AddressInformation>() {
        public AddressInformation createFromParcel(Parcel source) {
            return new AddressInformation(source);
        }

        public AddressInformation[] newArray(int size) {
            return new AddressInformation[size];
        }
    };
}
