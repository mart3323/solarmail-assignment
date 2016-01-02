package interfaces.ship;

import solarpost.station.AbstractPostOffice;

import java.util.function.Function;

/**
 * <p> As a blueprint for creating a ship, the HullProfile determines the fuel tank and cargo hold sizes
 * <br> as well as the scanners' wear pattern and fuel requirements
 * <br>
 * <br>
 * <img src="diamondback_scout.png" />
 * <img src="federal_dropship.png" />
 */
public interface IHullProfile {
    Function<AbstractPostOffice, Integer> getScannerWearPattern();
    Function<AbstractPostOffice, Integer> getFuelConsumptionPattern();

    int getMaxFuel();
    int getMaxScanner();
    int getCargoCapacity();
}
