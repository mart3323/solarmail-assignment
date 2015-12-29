package SolarSystemMail.Station;

import SolarSystemMail.Exceptions.NotDockedException;
import SolarSystemMail.IStarShip;

public interface IPlanetaryStation {
    /**
     * Restores fuel to the ship, given that the ship is currently docked
     * @param ship the requesting ship
     * @throws NotDockedException if the requesting ship is not docked
     */
    void refuel(IStarShip ship) throws NotDockedException;

    /**
     * Returns the size of the current queue, including any currently docked ships
     * This data can be requested over radio by anyone
     * @return the size of the queue and docked ship together (f ex. one docked plus one waiting is a queuesize of 2)
     */
    int getWaitingQueueSize();

    /**
     * Enters the queue to dock at the first opportunity
     * <br>NB! This is a blocking action!
     * @return the Cargo hold of the station to trade with
     */
    ICargoHold enterDockingQueue();
}
