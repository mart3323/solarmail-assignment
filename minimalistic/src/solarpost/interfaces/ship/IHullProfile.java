package solarpost.interfaces.ship;

import solarpost.interfaces.station.IPostOffice;

import java.util.function.Function;

/**
 * <p> As a blueprint for creating a ship, the HullProfile determines the fuel tank and cargo hold sizes
 * <br> as well as the scanners' wear pattern and fuel requirements
 * <br>
 * <br>
 * <img src="../../images/blueprint_diamondback_scout.png" />
 * <img src="../../images/blueprint_federal_dropship.png" />
 */
public interface IHullProfile {
    Function<IPostOffice, Integer> getScannerWearPattern();
    Function<IPostOffice, Integer> getFuelConsumptionPattern();

    int getMaxFuel();
    int getMaxScanner();
    int getCargoCapacity();
}
