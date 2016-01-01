package solarpost.ship.ship;

import solarpost.misc.SolarMail;
import solarpost.station.station.AbstractPostOffice;
import solarpost.station.station.PostOffice;

import static solarpost.station.station.AbstractPostOffice.TempClass.Hot;
import static solarpost.station.station.AbstractPostOffice.TempClass.Normal;

public class HeatShieldedShip extends AbstractShip{
    @Override protected int getMaxScanner() { return 100; }
    @Override protected int getScannerBuyThreshold() { return 10; }
    @Override protected int getMaxFuel() { return 100; }
    @Override protected int getScannerWear(AbstractPostOffice office) { return 4; }

    @Override
    public boolean needsScanner() {
        return this.scannerDurability < getScannerBuyThreshold();
    }

    @Override
    protected int getFuelConsumption(AbstractPostOffice office) {
        switch (office.getTempClass()) {
            case Hot:
                return 50;
            case Normal:
                return 25;
            default:
                return getMaxFuel()+1;
        }
    }

    @Override
    public boolean canDeliver(SolarMail pckg) {
        return pckg.source.getTempClass() == Hot || pckg.target.getTempClass() == Hot;
    }
}
