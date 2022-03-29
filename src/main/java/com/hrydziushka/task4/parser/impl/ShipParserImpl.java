package com.hrydziushka.task4.parser.impl;

import com.hrydziushka.task4.entity.Ship;
import com.hrydziushka.task4.parser.ShipParser;

import java.util.ArrayList;
import java.util.List;

public class ShipParserImpl implements ShipParser {
    private static final String DELIMITER = ";";

    @Override
    public List<Ship> parseLinesToShips(List<String> lines) {
        List<Ship> ships = new ArrayList<>();
        lines.forEach(x -> {
            String[] shipOptions = x.split(DELIMITER);
            String id = shipOptions[0];
            boolean forUploading = Boolean.parseBoolean(shipOptions[1]);
            ships.add(new Ship(id, forUploading));
        });
        return ships;
    }
}
