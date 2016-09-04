package com.chernandezgil.farmacias.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Carlos on 08/08/2016.
 */
public class Pharmacy extends PharmacyObjectMap implements Parcelable {

    private boolean optionsRow;


    public boolean isOptionsRow() {
        return optionsRow;
    }

    public void setOptionsRow(boolean optionsRow) {
        this.optionsRow = optionsRow;
    }

    public Pharmacy() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte(this.optionsRow ? (byte) 1 : (byte) 0);

    }

    protected Pharmacy(Parcel in) {
        super(in);
        this.optionsRow = in.readByte() != 0;

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
