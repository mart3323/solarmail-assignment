package solarpost.code.station;

import solarpost.code.misc.CargoStorage;
import solarpost.code.misc.SolarMail;
import solarpost.code.ship.CargoShip;

import java.util.function.Predicate;

/**
 * A variant of the regular post office that also sells scanners
 * <p> Any docking ship will receive a new scanner if their public .autoBuyScanner property is set to true
 */
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
