package com.hrydziushka.task4.entity;

import com.hrydziushka.task4.util.DockIdGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Dock {
    private static final Logger logger = LogManager.getLogger();
    private static final int MIN_PROCESS_TIME = 2;
    private static final int MAX_PROCESS_TIME = 5;
    private String id;

    public Dock() {
        id = DockIdGenerator.generateId();
    }

    public String getId() {
        return id;
    }

    public void processShip(Ship ship) {
        logger.info("the dock, dockId " + id + ",  began processing the ship: " + ship);
        ship.setShipState(Ship.ShipState.RUNNABLE);

        Port port = Port.getInstance();
        Random random = new Random();
        if (ship.isForUploading()) {

            int newCargo = random.nextInt(Ship.MAX_CARGO_CAPACITY) + 1;
            port.removeCargo(newCargo);
            ship.setCurrentCargo(newCargo);
            logger.info("the dock, dockId " + id + ",  loads the ship " + ship);
        } else {
            port.addCargo(ship.getCurrentCargo());
            ship.setCurrentCargo(0);
            logger.info("the dock, dockId " + id + ", unloads the ship " + ship);
        }

        try {

            int sleepTime = random.nextInt(MIN_PROCESS_TIME, MAX_PROCESS_TIME + 1);
            TimeUnit.SECONDS.sleep(sleepTime);
        } catch (InterruptedException e) {
//todo
        }
        ship.setShipState(Ship.ShipState.TERMINATED);
        logger.info("the dock, dockId "+id+", has finished processing the ship " + ship);
    }
}
