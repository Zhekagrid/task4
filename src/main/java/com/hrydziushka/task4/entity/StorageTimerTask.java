package com.hrydziushka.task4.entity;

import java.util.TimerTask;

public class StorageTimerTask extends TimerTask
{
    @Override
    public void run() {
        Port port=Port.getInstance();
        port.checkCargo();
    }
}
