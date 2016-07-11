package com.chernandezgil.farmacias.model;

/**
 * Created by Carlos on 09/07/2016.
 */
public class CustomMarker {

    private String id;
    private Double latitude;
    private Double longitude;
    private String label;



    public CustomMarker(String id, Double latitude, Double longitude, String label) {

        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.label=label;
    }

    public CustomMarker() {
        this.id = "";
        this.latitude = 0.0;
        this.longitude = 0.0;
    }

    public String getCustomMarkerId() {
        return id;
    }

    public void setCustomMarkerId(String id) {
        this.id = id;
    }

    public Double getCustomMarkerLatitude() {
        return latitude;
    }

    public void setCustomMarkerLatitude(Double mLatitude) {
        this.latitude = mLatitude;
    }

    public Double getCustomMarkerLongitude() {
        return longitude;
    }

    public void setCustomMarkerLongitude(Double mLongitude) {
        this.longitude = mLongitude;
    }

    public void setCustomMarkerLabel(String label) {
        this.label=label;
    }
    public String getCustomMarkerLabel(){
        return label;
    }
}