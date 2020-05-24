package com.space.controller;

import com.space.exception.BadRequestException;
import com.space.model.Ship;
import com.space.model.ShipTo;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.List;

@RestController
public class ShipRestController {
    @Autowired
    private ShipService shipService;

    @GetMapping(value = "/rest/ships", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Ship>> getAllShipsList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String planet,
            @RequestParam(required = false) ShipType shipType,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean isUsed,
            @RequestParam(required = false) Double minSpeed,
            @RequestParam(required = false) Double maxSpeed,
            @RequestParam(required = false) Integer minCrewSize,
            @RequestParam(required = false) Integer maxCrewSize,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating,
            @RequestParam(required = false) ShipOrder order,
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize
    ) {
        return new ResponseEntity<>(shipService.getAllShipsList(
                name,
                planet,
                shipType,
                after,
                before,
                isUsed,
                minSpeed,
                maxSpeed,
                minCrewSize,
                maxCrewSize,
                minRating,
                maxRating,
                order,
                pageNumber,
                pageSize
        ), HttpStatus.OK);
    }

    @GetMapping(value = "/rest/ships/count", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Integer> getShipsCount(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String planet,
            @RequestParam(required = false) ShipType shipType,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean isUsed,
            @RequestParam(required = false) Double minSpeed,
            @RequestParam(required = false) Double maxSpeed,
            @RequestParam(required = false) Integer minCrewSize,
            @RequestParam(required = false) Integer maxCrewSize,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating
    ) {
        return new ResponseEntity<>(shipService.getShipsCount(
                name,
                planet,
                shipType,
                after,
                before,
                isUsed,
                minSpeed,
                maxSpeed,
                minCrewSize,
                maxCrewSize,
                minRating,
                maxRating
        ), HttpStatus.OK);
    }

    @PostMapping(value = "/rest/ships", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        if (ship.getName() != null &&
                ship.getPlanet() != null &&
                ship.getShipType() != null &&
                ship.getProdDate() != null &&
                ship.getSpeed() != null &&
                ship.getCrewSize() != null) {
            ship.setSpeed(round(ship.getSpeed(), 2));
            if ((ship.getName().length() <= 50 && ship.getPlanet().length() <= 50 &&
                    !ship.getName().isEmpty() && !ship.getPlanet().isEmpty()) &&
                    (ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() >= 2800 && ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() <= 3019) &&
                    (0.01d <= ship.getSpeed() && ship.getSpeed() <= 0.99d) &&
                    (1 <= ship.getCrewSize() && ship.getCrewSize() <= 9999)) {

                if (ship.getUsed() == null) {
                    ship.setUsed(false);
                }

                ship.updateRating();
                return new ResponseEntity<>(shipService.createShip(ship), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
//        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        throw new BadRequestException();
    }

    @GetMapping(value = "/rest/ships/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> getShip(@PathVariable("id") Long id) {
        if (isValidId(id)) {
            Ship ship = this.shipService.getShipById(id);

            if (ship == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            return new ResponseEntity<>(ship, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/rest/ships/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> updateShip(
            @PathVariable("id") Long id,
            @RequestBody Ship ship
    ) {
        if (isValidId(id)) {
            if (ship.getSpeed() != null) {
                ship.setSpeed(round(ship.getSpeed(), 2));
            }
            ShipTo shipTo;
            if (ship.getProdDate() != null) {
                shipTo = new ShipTo(
                        ship.getName(),
                        ship.getPlanet(),
                        ship.getShipType(),
                        ship.getProdDate().getTime(),
                        ship.getUsed(),
                        ship.getSpeed(),
                        ship.getCrewSize());
            } else {
                shipTo = new ShipTo(
                        ship.getName(),
                        ship.getPlanet(),
                        ship.getShipType(),
                        ship.getUsed(),
                        ship.getSpeed(),
                        ship.getCrewSize());
            }
            return shipService.updateShip(id, shipTo);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/rest/ships/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> deleteShip(@PathVariable("id") Long id) {
        if (isValidId(id)) {
            Ship ship = this.shipService.getShipById(id);
            if (ship == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            shipService.deleteShip(id);

            return new ResponseEntity<>(HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private static boolean isValidId(Long id) {
        return id > 0;
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
