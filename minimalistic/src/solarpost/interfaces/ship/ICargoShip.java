package solarpost.interfaces.ship;

import solarpost.interfaces.misc.IStorage;
import solarpost.code.misc.CargoStorage;
import solarpost.code.misc.SolarMail;
import solarpost.code.station.AbstractPostOffice;
import solarpost.interfaces.station.IPostOffice;

import java.util.function.Predicate;

/**
 * A Cargo ship that has capabilities to land at Mail stations, get refueled and outfitted with new scanners
 * <p>The ship comes with no behaviour of it's own, it must be manually guided or an autopilot needs to be connected
 * <p>The exact type of ship depends on the {@link IHullProfile} the ship is instantiated with
 */
public interface ICargoShip {

    /**
     * Returns the {@link IStorage} that is this ships' cargo hold
     */
    CargoStorage getStorage();

    /**
     * Visits the Post office specified, picking up only packages matching filter
     * @param office {@link AbstractPostOffice} to land at
     * @param filter predicate to test cargo against before allowing it onboard
     */
    void dockAt(IPostOffice office, Predicate<SolarMail> filter);

    int getScannerDurability();
    int getFuel();

    /**
     * Restores the fuel tank to full capacity
     */
    void refuel();

    /**
     * Replaces the scanner with a brand new one, effectively resetting the scanner durability of this ship to full
     */
    void installScanner();

    /**
     * Launches this ship off the given PostStation,
     * consuming fuel and wearing the scanner according to this ship's Hull profile
     * @throws RuntimeException if the ship runs out of fuel or the scanner fails on takeoff
     */
    void launch(IPostOffice office) throws RuntimeException;
}
