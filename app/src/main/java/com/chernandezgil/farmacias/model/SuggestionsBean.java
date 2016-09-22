package com.chernandezgil.farmacias.model;

/**
 * Created by Carlos on 08/09/2016.
 */
public class SuggestionsBean {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuggestionsBean that = (SuggestionsBean) o;


        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    private String name;
    private int imageId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public SuggestionsBean() {

    }
}
