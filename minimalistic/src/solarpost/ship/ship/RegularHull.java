package solarpost.ship.ship;

import solarpost.station.station.AbstractPostOffice;

import java.util.function.Function;


public class RegularHull implements IHullProfile {

    @Override public int getMaxFuel() { return 100; }
    @Override public int getMaxScanner() { return 100; }
    @Override public int getCargoCapacity() { return 100; }

    @Override
    public Function<AbstractPostOffice, Integer> getScannerWearPattern() {
        return p -> 4;
    }

    @Override
    public Function<AbstractPostOffice, Integer> getFuelConsumptionPattern() {
        return p -> {
            switch (p.getTempClass()) {
                case Normal:
                    return 20;
                default:
                    return getMaxFuel()+1;
            }
        };
    }

}
