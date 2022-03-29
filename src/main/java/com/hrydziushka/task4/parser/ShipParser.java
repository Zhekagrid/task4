package com.hrydziushka.task4.parser;

import com.hrydziushka.task4.entity.Ship;

import java.util.List;

public interface ShipParser {
    List<Ship> parseLinesToShips(List<String> lines);
}
