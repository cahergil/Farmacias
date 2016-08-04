package com.chernandezgil.farmacias.model;

/**
 * Created by Carlos on 10/07/2016.
 */
public class CustomMarker extends FarmaciasCsvBean implements Comparable<CustomMarker>{


    Double distance;
    String hours;
    Boolean isOpen;
    String order;
    String addressFormatted;

    public Boolean getOpen() {
        return isOpen;
    }

    public String getAddressFormatted() {
        return addressFormatted;
    }

    public void setAddressFormatted(String addressFormatted) {
        this.addressFormatted = addressFormatted;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public CustomMarker(){

    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Boolean isOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        this.isOpen = open;
    }



    @Override
    public int compareTo(CustomMarker other) {
        int res=other.isOpen().compareTo(this.isOpen());
        if(res==0) {
            return this.getDistance().compareTo(other.getDistance());
        }
        return res;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CustomMarker that = (CustomMarker) o;

        if (!getDistance().equals(that.getDistance())) return false;
        return getOrder().equals(that.getOrder());

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getDistance().hashCode();
        result = 31 * result + getOrder().hashCode();
        return result;
    }
}
