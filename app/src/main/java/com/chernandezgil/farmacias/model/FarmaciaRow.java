package com.chernandezgil.farmacias.model;

/**
 * Created by Carlos on 10/07/2016.
 */
public class FarmaciaRow extends FarmaciasCsvBean implements Comparable<FarmaciaRow>{


    Double distance;
    String hours;
    boolean isOpen;
    String order;

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

    public FarmaciaRow(){

    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        this.isOpen = open;
    }



    @Override
    public int compareTo(FarmaciaRow other) {
        return this.getDistance().compareTo(other.getDistance());
    }


}
