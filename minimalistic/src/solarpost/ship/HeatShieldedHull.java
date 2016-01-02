package solarpost.ship;

import interfaces.ship.IHullProfile;
import solarpost.station.AbstractPostOffice;

import java.util.function.Function;

public class HeatShieldedHull implements IHullProfile {

    @Override public int getMaxFuel() { return 100; }
    @Override public int getMaxScanner() { return 100; }
    @Override public int getCargoCapacity() { return 80; }

    @Override
    public Function<AbstractPostOffice, Integer> getScannerWearPattern() {
        return p -> 4;
    }

    @Override
    public Function<AbstractPostOffice, Integer> getFuelConsumptionPattern() {
        return p -> {
            switch (p.getTempClass()) {
                case Hot:
                    return 50;
                case Normal:
                    return 25;
                default:
                    return getMaxFuel()+1;
            }
        };
    }
}
