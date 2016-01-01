package solarpost.ship.ship;

import solarpost.misc.CargoStorage;
import solarpost.misc.SolarMail;
import solarpost.station.station.AbstractPostOffice;
import solarpost.station.station.PostOffice;

public abstract class AbstractShip {
    protected final CargoStorage storage = new CargoStorage(80);

    public int fuel;
    public int scannerDurability;

    protected abstract int getMaxScanner();
    protected abstract int getScannerBuyThreshold();
    protected abstract int getMaxFuel();
    public abstract boolean needsScanner();

    public void dockAt(PostOffice office){
        office.dockTradeAndLaunch(this);
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
    }


    public void installScanner() {
        this.scannerDurability = this.getMaxScanner();
    }

    protected abstract int getScannerWear(AbstractPostOffice office);
    protected abstract int getFuelConsumption(AbstractPostOffice office);
    public abstract boolean canDeliver(SolarMail pckg);
}
