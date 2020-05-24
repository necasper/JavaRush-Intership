package com.space.service;

import com.space.controller.ShipOrder;
import com.space.exception.BadRequestException;
import com.space.exception.NotFoundException;
import com.space.model.Ship;
import com.space.model.ShipTo;
import com.space.model.ShipType;
import com.space.repository.ShipRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShipService {
    @Autowired
    ShipRepo shipRepo;

    public List<Ship> getAllShipsList(
            String name,
            String planet,
            ShipType shipType,
            Long after,
            Long before,
            Boolean isUsed,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating,
            ShipOrder order,
            Integer pageNumber,
            Integer pageSize
    ) {
        List<Ship> list = shipRepo.findAll();
        List<Ship> result;
        if (pageNumber == null) {
            pageNumber = 0;
        }
        if (pageSize == null) {
            pageSize = 3;
        }
        result = list
                .stream()
                .filter(o -> name == null || o.getName().contains(name))
                .filter(o -> planet == null || o.getPlanet().contains(planet))
                .filter(o -> shipType == null || o.getShipType().equals(shipType))
                .filter(o -> after == null || o.getProdDate().after(new Date(after)))
                .filter(o -> before == null || o.getProdDate().before(new Date(before)))
                .filter(o -> isUsed == null || o.getUsed().equals(isUsed))
                .filter(o -> minSpeed == null || o.getSpeed() >= minSpeed)
                .filter(o -> maxSpeed == null || o.getSpeed() <= maxSpeed)
                .filter(o -> minCrewSize == null || o.getCrewSize() >= minCrewSize)
                .filter(o -> maxCrewSize == null || o.getCrewSize() <= maxCrewSize)
                .filter(o -> minRating == null || o.getRating() >= minRating)
                .filter(o -> maxRating == null || o.getRating() <= maxRating).sorted((o1, o2) -> {
                    if (order != null) {
                        switch (order) {
                            case ID:
                                if (o1.getId().equals(o2.getId()))
                                    return 0;
                                else
                                    return o1.getId() > o2.getId() ? 1 : -1;

                            case DATE:
                                if (o1.getProdDate().getTime() == o2.getProdDate().getTime())
                                    return 0;
                                else
                                    return o1.getProdDate().getTime() > o2.getProdDate().getTime() ? 1 : -1;

                            case SPEED:
                                if (o1.getSpeed().equals(o2.getSpeed()))
                                    return 0;
                                else
                                    return o1.getSpeed() > o2.getSpeed() ? 1 : -1;

                            case RATING:
                                if (o1.getRating().equals(o2.getRating()))
                                    return 0;
                                else
                                    return o1.getRating() > o2.getRating() ? 1 : -1;
                            default:
                                if (o1.getId().equals(o2.getId()))
                                    return 0;
                                else
                                    return o1.getId() > o2.getId() ? 1 : -1;
                        }
                    } else {
                        if (o1.getId().equals(o2.getId()))
                            return 0;
                        else
                            return o1.getId() > o2.getId() ? 1 : -1;
                    }
                })
                .collect(Collectors.toList());
        result = result.subList(pageNumber * pageSize,
                (Math.min(result.size(), (pageNumber + 1) * pageSize)));
        return result;
    }

    public Integer getShipsCount(
            String name,
            String planet,
            ShipType shipType,
            Long after,
            Long before,
            Boolean isUsed,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating
    ) {
        List<Ship> list = shipRepo.findAll();
        return (int) list
                .stream()
                .filter(o -> name == null || o.getName().contains(name))
                .filter(o -> planet == null || o.getPlanet().contains(planet))
                .filter(o -> shipType == null || o.getShipType().equals(shipType))
                .filter(o -> after == null || o.getProdDate().after(new Date(after)))
                .filter(o -> before == null || o.getProdDate().before(new Date(before)))
                .filter(o -> isUsed == null || o.getUsed().equals(isUsed))
                .filter(o -> minSpeed == null || o.getSpeed() > minSpeed)
                .filter(o -> maxSpeed == null || o.getSpeed() < maxSpeed)
                .filter(o -> minCrewSize == null || o.getCrewSize() > minCrewSize)
                .filter(o -> maxCrewSize == null || o.getCrewSize() < maxCrewSize)
                .filter(o -> minRating == null || o.getRating() > minRating)
                .filter(o -> maxRating == null || o.getRating() < maxRating)
                .count();
    }

    public Ship createShip(
            Ship ship
    ) {
        return shipRepo.save(ship);
    }


    public Ship getShipById(Long id) {
        return shipRepo.findById(id).orElse(null);
    }

    public ResponseEntity<Ship> updateShip(Long id, ShipTo shipTo) {
        if (isValidId(id)) {
            Ship ship = this.getShipById(id);
            if (ship != null) {
                if ((shipTo.name != null ||
                        shipTo.planet != null ||
                        shipTo.shipType != null ||
                        shipTo.prodDate != null ||
                        shipTo.isUsed != null ||
                        shipTo.speed != null ||
                        shipTo.crewSize != null)) {
                    ship.updateRating();
                    if (shipTo.name != null) {
                        if (shipTo.name.length() <= 50 && !shipTo.name.isEmpty()) {
                            ship.setName(shipTo.name);
                        } else throw new BadRequestException();
                    }

                    if (shipTo.planet != null) {
                        if (shipTo.planet.length() <= 50 && !shipTo.planet.isEmpty()) {
                            ship.setPlanet(shipTo.planet);
                        } else throw new BadRequestException();

                    }

                    if (shipTo.shipType != null) {
                        ship.setShipType(shipTo.shipType);
                    }

                    if (shipTo.prodDate != null) {
                        if (shipTo.prodDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() >= 2800 &&
                                shipTo.prodDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() <= 3019) {
                            ship.setProdDate(shipTo.prodDate);
                        } else throw new BadRequestException();
                    }

                    if (shipTo.isUsed != null) {
                        ship.setUsed(shipTo.isUsed);
                    }

                    if (shipTo.speed != null) {
                        shipTo.speed = round(shipTo.speed, 2);
                        if (0.01d <= shipTo.speed && shipTo.speed <= 0.99d) {
                            ship.setSpeed(shipTo.speed);
                        } else throw new BadRequestException();
                    }

                    if (shipTo.crewSize != null) {
                        if (1 <= shipTo.crewSize && shipTo.crewSize <= 9999) {
                            ship.setCrewSize(shipTo.crewSize);
                        } else throw new BadRequestException();
                    }

                    ship.updateRating();

                    return new ResponseEntity<>(shipRepo.save(ship), HttpStatus.OK);

                }
                return new ResponseEntity<>(ship, HttpStatus.OK);
            } else throw new NotFoundException();
        } else throw new BadRequestException();
    }

    public void deleteShip(Long id) {
        shipRepo.deleteById(id);
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private static boolean isValidId(Long id) {
        return id > 0;
    }
}
