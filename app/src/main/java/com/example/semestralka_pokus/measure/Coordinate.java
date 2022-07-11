package com.example.semestralka_pokus.measure;

/**
 * Trida pro zaznam souradnic.
 */
public class Coordinate {

    /* Hodnota zemepisne sirky. */
    private double latitude;

    /* Hodnota zemepisne delky. */
    private double longitude;

    Coordinate(double latitude, double longtitude) {
        this.latitude = latitude;
        this.longitude = longtitude;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void getLongitude(double longtitude) {
        this.longitude = longtitude;
    }
}
