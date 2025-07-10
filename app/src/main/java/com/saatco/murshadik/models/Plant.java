package com.saatco.murshadik.models;

public class Plant {
    private String name;
    private int id;

    public Plant(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    // Override toString() to display the name in the Spinner
    @Override
    public String toString() {
        return name;
    }
}
