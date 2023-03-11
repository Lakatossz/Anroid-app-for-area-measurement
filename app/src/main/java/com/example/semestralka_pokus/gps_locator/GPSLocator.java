package com.example.semestralka_pokus.gps_locator;

import android.location.Location;

import java.time.LocalDateTime;

public class GPSLocator {

    /* Promenna pro pristup k ziskani lokace. */
    boolean isGPSEnabled = false;

    /* Aktivitu lokatoru - pokud se po nejake dobe laktor neozve je neaktivni. */
    boolean locatorActive = false;

    /* Lokace lokatoru. */
    Location location;

    /* Datum a cas posledni aktualizace. */
    LocalDateTime localDateTime;

    public GPSLocator() {

    }

    public boolean isGPSEnabled() {
        return isGPSEnabled;
    }

    public void setGPSEnabled(boolean GPSEnabled) {
        isGPSEnabled = GPSEnabled;
    }

    public boolean isLocatorActive() {
        return locatorActive;
    }

    public void setLocatorActive(boolean locatorActive) {
        this.locatorActive = locatorActive;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
}
