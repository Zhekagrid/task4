package com.hrydziushka.task4.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Port {
    private static final Logger logger = LogManager.getLogger();
    private static final int NUMBER_OF_DOCKS = 3;
    private static final int MAX_CAPACITY = 500;
    private static final int HALF_CAPACITY = MAX_CAPACITY / 2;
    private static final double FILL_FACTOR_TO_INCREASE_CARGO = 0.2;
    private static final double FILL_FACTOR_TO_REDUCTION_CARGO = 0.8;
    private static final int TIMER_DELAY = 0;
    private static final int TIMER_INTERVAL = 1200;
    private static Port instance;
    private static Lock lock = new ReentrantLock();
    private static AtomicBoolean create = new AtomicBoolean(false);
    private ArrayDeque<Dock> availableDocks;
    private ArrayDeque<Condition> waitForDockDeque;
    private AtomicInteger currentCargo;
    private Timer timer;
    private Lock cargoLock = new ReentrantLock();
    private Condition waitForUpload = cargoLock.newCondition();
    private Condition waitForUnload = cargoLock.newCondition();

    private Port() {
        currentCargo = new AtomicInteger(HALF_CAPACITY);
        waitForDockDeque = new ArrayDeque<>();
        availableDocks = new ArrayDeque<>();

        for (int i = 0; i < NUMBER_OF_DOCKS; i++) {
            availableDocks.add(new Dock());
        }

        timer = new Timer();
        timer.schedule(new StorageTimerTask(), TIMER_DELAY, TIMER_INTERVAL);
    }

    public static Port getInstance() {
        if (!create.get()) {
            try {
                lock.lock();
                if (instance == null) {
                    instance = new Port();
                    create.set(true);
                }
            } finally {
                lock.unlock();
            }
        }

        return instance;
    }

    public Timer getTimer() {
        return timer;
    }

    public void checkCargo() {
        try {
            cargoLock.lock();
            logger.info("Check cargo in port currentCargo: " + currentCargo.get());
            if (currentCargo.getAndAdd(0) >= MAX_CAPACITY * FILL_FACTOR_TO_REDUCTION_CARGO) {
                currentCargo.getAndSet(HALF_CAPACITY);
                logger.info("Cargo in port updated with \"train\" new cargo: " + currentCargo.get());
                waitForUnload.signal();
            } else if (currentCargo.getAndAdd(0) <= MAX_CAPACITY * FILL_FACTOR_TO_INCREASE_CARGO) {
                currentCargo.getAndSet(HALF_CAPACITY);
                logger.info("Cargo in port updated with \"train\" new cargo: " + currentCargo.get());
                waitForUpload.signal();
            }

        } finally {
            cargoLock.unlock();
        }

    }

    public void addCargo(int cargo) {
        try {
            cargoLock.lock();
            if (currentCargo.get() + cargo > MAX_CAPACITY) {
                logger.info("The storage facility is full, waiting for cargo to be loaded onto another ship or \"train\"");
                waitForUnload.await();
            }
            currentCargo.getAndAdd(cargo);
            waitForUpload.signal();
            logger.info("the port has received new cargo: " + cargo + " Total cargo in port: " + currentCargo.getAndAdd(0));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            cargoLock.unlock();
        }

    }

    public void removeCargo(int cargo) {
        try {
            cargoLock.lock();
            if (currentCargo.get() - cargo <= 0) {
                logger.info("storage area is empty, waiting for another ship or \"train\" to unload its cargo");

                waitForUpload.await();
            }
            currentCargo.getAndAdd(-cargo);
            waitForUnload.signal();
            logger.info("The port has shipped cargo: " + cargo + " Total cargo in port: " + currentCargo.getAndAdd(0));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            cargoLock.unlock();
        }

    }

    public Dock getDock() {
        Dock dock = null;
        lock.lock();
        try {
            if (availableDocks.isEmpty()) {
                Condition condition = lock.newCondition();
                waitForDockDeque.add(condition);
                logger.info("All docks are busy. Free dock waiting ");
                condition.await();

            }
            dock = availableDocks.remove();
            logger.info("The dock, dockId " + dock.getId() + ",   is given to the ship");
        } catch (InterruptedException e) {
            logger.error(Thread.currentThread().getName() + "was interrupted.", e);
            Thread.currentThread().interrupt();

        } finally {
            lock.unlock();
        }

        return dock;
    }

    public void releaseDock(Dock dock) {
        lock.lock();
        try {
            availableDocks.add(dock);
            Condition condition = waitForDockDeque.pollFirst();
            if (condition != null) {
                condition.signal();
            }
            logger.info("The dock, dockId " + dock.getId() + ", has been released");
        } finally {
            lock.unlock();
        }

    }


}
