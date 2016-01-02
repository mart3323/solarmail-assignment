package solarpost.ship;

import interfaces.ship.ICargoShip;
import interfaces.ship.IHullProfile;
import solarpost.misc.CargoStorage;
import solarpost.misc.SolarMail;
import solarpost.station.AbstractPostOffice;

import java.util.function.Function;
import java.util.function.Predicate;

public class CargoShip implements ICargoShip{
    protected final CargoStorage storage;
    public boolean autobuyScanner = false;
    private int fuel;
    private int scanner;
    private final int MAX_FUEL;
    private final int MAX_SCANNER;
    private final Function<AbstractPostOffice, Integer> scannerWear;
    private final Function<AbstractPostOffice, Integer> fuelConsumption;

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

    public void dockAt(AbstractPostOffice office, Predicate<SolarMail> filter){
        office.dockTradeAndLaunch(this, filter);
    }

    public CargoStorage getStorage(){
        return this.storage;
    }

    public void refuel() { this.fuel = MAX_FUEL; }
    public void installScanner() { this.scanner = this.MAX_SCANNER; }

    public int getScannerDurability() { return this.scanner; }
    public int getFuel() { return this.fuel; }


    public void launch(AbstractPostOffice office) {
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
