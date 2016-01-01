package solarpost.station.station;

import solarpost.ship.ship.AbstractShip;

public class ScannerPostOffice extends PostOffice {
    public ScannerPostOffice(String name) {
        super(name);
    }

    @Override
    protected void afterTrade(AbstractShip ship) {
        super.afterTrade(ship);
        if(ship.needsScanner()){
            ship.installScanner();
        }
    }
}
