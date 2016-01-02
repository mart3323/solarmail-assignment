package solarpost.ship.ship;

import solarpost.station.station.AbstractPostOffice;

import java.util.function.Function;

public interface IHullProfile {
    Function<AbstractPostOffice, Integer> getScannerWearPattern();
    Function<AbstractPostOffice, Integer> getFuelConsumptionPattern();

    int getMaxFuel();
    int getMaxScanner();
    int getCargoCapacity();
}
