package com.ubiqlog.vis.common;

/**
 * Created by Aaron on 12/16/2015.
 */
public class GeoPoint {

    private int lat;
    private int lgt;

    public GeoPoint(int lat, int lgt) {
        this.lat = lat;
        this.lgt = lgt;
    }

    public void setLatitudeE6(int lat) {
        this.lat = lat;
    }

    public void setLongitudeE6(int lgt) {
        this.lgt = lgt;
    }

    public int getLatitudeE6() {
        return this.lat;
    }

    public int getLongitudeE6() {
        return this.lgt;
    }
}
