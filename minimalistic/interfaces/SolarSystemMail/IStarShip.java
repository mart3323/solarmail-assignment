package SolarSystemMail;

import SolarSystemMail.Exceptions.InsufficientFuelException;
import SolarSystemMail.Station.IPlanetaryStation;
import sun.plugin.dom.exception.InvalidStateException;

public interface IStarShip {
    /**
     * Launch a ship from the station it is docked at
     * @throws InsufficientFuelException if the ship doesn't have enough fuel
     * @throws InvalidStateException if the ship is already in space
     */
    void launch() throws InsufficientFuelException, InvalidStateException;

    /**
     * Get the amount of fuel remaining
     * @return the amount of fuel in std darkmatter units
     */
    int getRemainingFuel();

    /**
     * Attempts to dock at the specified station, waiting in queue if necessary
     * @param station station to dock at
     */
    void dockAt(IPlanetaryStation station);
}
