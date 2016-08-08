package com.chernandezgil.farmacias.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Carlos on 08/08/2016.
 */
public class Pharmacy extends CustomMarker implements Parcelable {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public Pharmacy() {
    }

    protected Pharmacy(Parcel in) {
        super(in);
    }

    public static final Creator<Pharmacy> CREATOR = new Creator<Pharmacy>() {
        @Override
        public Pharmacy createFromParcel(Parcel source) {
            return new Pharmacy(source);
        }

        @Override
        public Pharmacy[] newArray(int size) {
            return new Pharmacy[size];
        }
    };
}
