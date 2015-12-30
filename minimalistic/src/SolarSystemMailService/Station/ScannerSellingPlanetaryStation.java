package SolarSystemMailService.Station;

import SolarSystemMailService.Ship;

public class ScannerSellingPlanetaryStation extends PlanetaryStation{
    @Override
    protected void tradeWith(Ship ship) {
        super.tradeWith(ship);
        if(ship.needsNewScanner()){
            ship.installNewScanner();
        }
    }
}
