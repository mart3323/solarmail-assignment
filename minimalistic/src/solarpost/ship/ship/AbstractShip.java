package solarpost.ship.ship;

import solarpost.misc.CargoStorage;
import solarpost.misc.SolarMail;
import solarpost.station.station.AbstractPostOffice;

import java.util.function.Predicate;

public abstract class AbstractShip {
    protected final CargoStorage storage = new CargoStorage(80);

    public int fuel = getMaxFuel();
    public int scannerDurability = getMaxScanner();

    protected abstract int getMaxScanner();
    protected abstract int getScannerBuyThreshold();
    protected abstract int getMaxFuel();
    public abstract boolean needsScanner();

    public void dockAt(AbstractPostOffice office, Predicate<SolarMail> filter){
        office.dockTradeAndLaunch(this, filter);
    }

    public CargoStorage getStorage(){
        return this.storage;
    }

    public void refuel() {
        this.fuel = this.getMaxFuel();
    }

    public void launch(AbstractPostOffice office) {
        this.fuel -= getFuelConsumption(office);
        this.scannerDurability -= getScannerWear(office);
        if(this.fuel < 0){
            System.err.println("Rocket crashed");
            throw new RuntimeException("Not enough fuel to launch");
        }
        if(this.scannerDurability < 0){
            System.err.println("Rocket crashed");
            throw new RuntimeException("Scanner would not survive launch");
        }
    }


    public void installScanner() {
        this.scannerDurability = this.getMaxScanner();
    }

    protected abstract int getScannerWear(AbstractPostOffice office);
    protected abstract int getFuelConsumption(AbstractPostOffice office);
}
