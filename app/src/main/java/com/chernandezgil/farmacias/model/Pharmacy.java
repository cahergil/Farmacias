package com.chernandezgil.farmacias.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Carlos on 08/08/2016.
 */
public class Pharmacy extends PharmacyObjectMap implements Parcelable {

    private boolean optionsRow;
    private boolean arrow_down;

    public boolean isArrow_down() {
        return arrow_down;
    }

    public void setArrow_down(boolean arrow_down) {
        this.arrow_down = arrow_down;
    }

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
        dest.writeByte(this.arrow_down ? (byte) 1 : (byte) 0);
    }

    protected Pharmacy(Parcel in) {
        super(in);
        this.optionsRow = in.readByte() != 0;
        this.arrow_down = in.readByte() != 0;
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
