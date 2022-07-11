package com.example.semestralka_pokus.field;

/**
 * Trida pro zaznam pole.
 */
public class Field {

    /* Nazev pro oznaceni pole. */
    private String name;

    /* Plocha pole. */
    private double area;

    /* Obvod pole. */
    private double perimeter;

    public Field(String name, double area, double perimeter) {
        this.name = name;
        this.area = area;
        this.perimeter = perimeter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getPerimeter() {
        return perimeter;
    }

    public void setPerimeter(double perimeter) {
        this.perimeter = perimeter;
    }
}
