package SolarSystemMailService.Station;

import SolarSystemMailService.Ship;

public class HotPlanetaryStation extends PlanetaryStation {
    @Override
    public TemperatureClass getTempClass() {
        return TemperatureClass.Hot;
    }

}
