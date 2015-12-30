package SolarSystemMailService.Station;

import SolarSystemMailService.Ship.Ship;

public class ScannerSellingPlanetaryStation extends PlanetaryStation{


    public ScannerSellingPlanetaryStation(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     * <li> If the ship's scanner is sufficiently damaged, buys and installs a new one
     */
    @Override
    public synchronized void dockAndTrade(Ship ship) {
        super.dockAndTrade(ship);
    }

    @Override
    protected void tradeWith(Ship ship) {
        super.tradeWith(ship);
        if(ship.needsNewScanner()){
            ship.installNewScanner();
        }
    }
}
