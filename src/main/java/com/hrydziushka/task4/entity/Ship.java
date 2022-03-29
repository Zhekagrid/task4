package com.hrydziushka.task4.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class Ship extends Thread {
    public static final int MAX_CARGO_CAPACITY = 300;
    private static final Logger logger = LogManager.getLogger();
    private String shipId;
    private int currentCargo;
    private ShipState shipState;
    private boolean forUploading;

    public Ship(String shipId, boolean forUploading) {
        this.shipId = shipId;
        this.forUploading = forUploading;
        this.shipState = ShipState.NEW;
        if (forUploading) {
            currentCargo=0;
        } else {
            Random random=new Random();
            currentCargo=random.nextInt(MAX_CARGO_CAPACITY)+1;
        }
    }

    public int getCurrentCargo() {
        return currentCargo;
    }

    public void setCurrentCargo(int currentCargo) {
        this.currentCargo = currentCargo;
    }

    public boolean isForUploading() {
        return forUploading;
    }

    public void setForUploading(boolean forUploading) {
        this.forUploading = forUploading;
    }


    public ShipState getShipState() {
        return shipState;
    }

    public void setShipState(ShipState state) {
        this.shipState = state;
    }

    @Override
    public void run() {
        Port port = Port.getInstance();
        Dock dock = port.getDock();
        logger.info("The ship, shipId " + shipId + ", received a dock, dockId " + dock.getId());
        dock.processShip(this);
        port.releaseDock(dock);
        logger.info("The ship, shipId " + shipId + ", has released a dock, dockId " + dock.getId());
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Ship{");
        sb.append("shipId=").append(shipId);
        sb.append(", currentCargo=").append(currentCargo);
        sb.append(", shipState=").append(shipState);
        sb.append(", forUploading=").append(forUploading);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ship)) return false;

        Ship ship = (Ship) o;

        if (getCurrentCargo() != ship.getCurrentCargo()) return false;
        if (isForUploading() != ship.isForUploading()) return false;
        if (shipId != null ? !shipId.equals(ship.shipId) : ship.shipId != null) return false;
        return getShipState() == ship.getShipState();
    }

    @Override
    public int hashCode() {
        int result = shipId != null ? shipId.hashCode() : 0;
        result = 31 * result + getCurrentCargo();
        result = 31 * result + (getShipState() != null ? getShipState().hashCode() : 0);
        result = 31 * result + (isForUploading() ? 1 : 0);
        return result;
    }

    public enum ShipState {
        NEW, RUNNABLE, TERMINATED
    }
}
