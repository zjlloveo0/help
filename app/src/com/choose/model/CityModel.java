package com.choose.model;

import java.io.Serializable;
import java.util.List;

public class CityModel implements Serializable {
    private String name;

    public CityModel() {
        super();
    }

    public CityModel(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CityModel [name=" + name + "]";
    }

}
