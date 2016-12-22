package com.chernandezgil.farmacias.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Carlos on 09/07/2016.
 */
public class FarmaciasCsvBean implements Parcelable {
    int _id;
    String address;
    String horario;
    Double lat;
    Double lon;
    String name;
    String phone;

    String locality;
    String province;
    String postal_code;


    public FarmaciasCsvBean(){

    }
    public FarmaciasCsvBean(int _id, String postal_code, String province, String locality, String phone, String name, Double lon, Double lat, String horario, String address) {
        this._id = _id;
        this.postal_code = postal_code;
        this.province = province;
        this.locality = locality;
        this.phone = phone;
        this.name = name;
        this.lon = lon;
        this.lat = lat;
        this.horario = horario;
        this.address = address;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FarmaciasCsvBean that = (FarmaciasCsvBean) o;

        return phone.equals(that.phone);

    }

    @Override
    public int hashCode() {
        return phone.hashCode();
    }
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        FarmaciasCsvBean that = (FarmaciasCsvBean) o;
//
//        return get_id() == that.get_id();
//
//    }
//
//    @Override
//    public int hashCode() {
//        return get_id();
//    }

    @Override
    public String toString() {
        return "FarmaciasCsvBean{" +
                "_id=" + _id +
                ", address='" + address + '\'' +
                ", horario='" + horario + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", locality='" + locality + '\'' +
                ", province='" + province + '\'' +
                ", postal_code='" + postal_code + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeString(this.address);
        dest.writeString(this.horario);
        dest.writeValue(this.lat);
        dest.writeValue(this.lon);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.locality);
        dest.writeString(this.province);
        dest.writeString(this.postal_code);
    }

    protected FarmaciasCsvBean(Parcel in) {
        this._id = in.readInt();
        this.address = in.readString();
        this.horario = in.readString();
        this.lat = (Double) in.readValue(Double.class.getClassLoader());
        this.lon = (Double) in.readValue(Double.class.getClassLoader());
        this.name = in.readString();
        this.phone = in.readString();
        this.locality = in.readString();
        this.province = in.readString();
        this.postal_code = in.readString();
    }

    public static final Parcelable.Creator<FarmaciasCsvBean> CREATOR = new Parcelable.Creator<FarmaciasCsvBean>() {
        @Override
        public FarmaciasCsvBean createFromParcel(Parcel source) {
            return new FarmaciasCsvBean(source);
        }

        @Override
        public FarmaciasCsvBean[] newArray(int size) {
            return new FarmaciasCsvBean[size];
        }
    };
}
