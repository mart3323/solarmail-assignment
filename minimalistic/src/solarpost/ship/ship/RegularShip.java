package solarpost.ship.ship;

import solarpost.station.station.AbstractPostOffice;


public class RegularShip extends AbstractShip{
    @Override protected int getMaxScanner(){ return 100; }
    @Override protected int getScannerBuyThreshold(){ return 10; }
    @Override protected int getMaxFuel(){ return 100; }
    @Override protected int getScannerWear(AbstractPostOffice office) { return 4; }

    @Override
    protected int getFuelConsumption(AbstractPostOffice office) {
        switch (office.getTempClass()) {
            case Normal:
                return 20;
            default:
                return getMaxFuel()+1;
        }
    }


    @Override
    public boolean needsScanner() {
        return this.scannerDurability < getScannerBuyThreshold();
    }

}
