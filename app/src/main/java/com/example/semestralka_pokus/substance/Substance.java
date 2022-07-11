package com.example.semestralka_pokus.substance;

/**
 * Trida pro ulozeni souradnic.
 */
public class Substance {

    /* Nazev pro oznaceni pripravku. */
    private String name;

    /* Hustota pouziti pripravku na m^2. */
    private double density;

    public Substance(String name, double density) {
        this.name = name;
        this.density = density;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }
}
