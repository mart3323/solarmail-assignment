package SolarSystemMail.Station;

import SolarSystemMail.Exceptions.NotDockedException;
import SolarSystemMail.IStarShip;

public interface ISellRadiationScanners {
    /**
     * Buys a new radiation scanner for the ship, mounting it automatically
     * @param ship the ship requesting a new scanner
     * @throws NotDockedException if the requesting ship is not docked
     */
    void buyRadiationScanner(IStarShip ship) throws NotDockedException;
}
