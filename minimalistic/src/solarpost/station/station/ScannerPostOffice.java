package solarpost.station.station;

import solarpost.misc.CargoStorage;
import solarpost.misc.SolarMail;
import solarpost.ship.ship.CargoShip;

import java.util.function.Predicate;

public class ScannerPostOffice extends PostOffice {
    public ScannerPostOffice(String name) {
        super(name);
    }

    @Override
    protected void doTrade(CargoStorage ship, Predicate<SolarMail> filter) {
        super.doTrade(ship, filter);
    }

    @Override
    protected void afterTrade(CargoShip ship) {
        super.afterTrade(ship);
        if(ship.autobuyScanner){
            ship.installScanner();
        }
    }
}
