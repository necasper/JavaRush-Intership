package com.space.model;

import java.util.Date;

public class ShipTo {
    public String name;
    public String planet;
    public ShipType shipType;
    public Date prodDate;
    public Boolean isUsed;
    public Double speed;
    public Integer crewSize;

    public ShipTo() {
    }

    public ShipTo(String name, String planet, ShipType shipType, Long prodDate, Boolean isUsed, Double speed, Integer crewSize) {
        if (name != null)
            this.name = name;
        if (planet != null)
            this.planet = planet;
        if (shipType != null)
            this.shipType = shipType;
        if (prodDate != null)
            this.prodDate = new Date(prodDate);
        if (isUsed != null)
            this.isUsed = isUsed;
        if (speed != null)
            this.speed = speed;
        if (crewSize != null)
            this.crewSize = crewSize;
    }

    public ShipTo(String name, String planet, ShipType shipType, Boolean isUsed, Double speed, Integer crewSize) {
        if (name != null)
            this.name = name;
        if (planet != null)
            this.planet = planet;
        if (shipType != null)
            this.shipType = shipType;
        if (isUsed != null)
            this.isUsed = isUsed;
        if (speed != null)
            this.speed = speed;
        if (crewSize != null)
            this.crewSize = crewSize;
    }
}
