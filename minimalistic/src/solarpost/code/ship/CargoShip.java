package solarpost.code.ship;

import solarpost.interfaces.ship.ICargoShip;
import solarpost.interfaces.ship.IHullProfile;
import solarpost.code.misc.CargoStorage;
import solarpost.code.misc.SolarMail;
import solarpost.interfaces.station.IPostOffice;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A general description of a cargo ship's behaviour
 * <p>This class does not represent any specific ship..,
 * rather it must be instantiated with a specific {@link IHullProfile}
 *
 * @see RegularHull
 * @see HeatShieldedHull
 */
public class CargoShip implements ICargoShip{
    private final CargoStorage storage;
    public boolean autobuyScanner = false;
    private int fuel;
    private int scanner;
    private final int MAX_FUEL;
    private final int MAX_SCANNER;
    private final Function<IPostOffice, Integer> scannerWear;
    private final Function<IPostOffice, Integer> fuelConsumption;

    /**
     * Builds a new ship according to the specified Hull profile
     */
    public CargoShip(IHullProfile hull) {
        this.MAX_FUEL = hull.getMaxFuel();
        this.MAX_SCANNER = hull.getMaxScanner();
        this.scannerWear = hull.getScannerWearPattern();
        this.fuelConsumption = hull.getFuelConsumptionPattern();
        this.fuel = MAX_FUEL;
        this.scanner = MAX_SCANNER;
        this.storage = new CargoStorage(hull.getCargoCapacity());
    }

    public void dockAt(IPostOffice office, Predicate<SolarMail> filter){
        office.dockTradeAndLaunch(this, filter);
    }

    public CargoStorage getStorage(){
        return this.storage;
    }

    public void refuel() { this.fuel = MAX_FUEL; }
    public void installScanner() { this.scanner = this.MAX_SCANNER; }

    public int getScannerDurability() { return this.scanner; }
    public int getFuel() { return this.fuel; }


    public void launch(IPostOffice office) {
        this.fuel -= this.fuelConsumption.apply(office);
        this.scanner -= this.scannerWear.apply(office);
        if(this.fuel < 0){
            System.err.println("Rocket crashed (out of fuel)");
            throw new RuntimeException("Not enough fuel to launch");
        }
        if(this.scanner < 0){
            System.err.println("Rocket stranded (scanner broken)");
            throw new RuntimeException("Scanner would not survive launch");
        }
    }

}
