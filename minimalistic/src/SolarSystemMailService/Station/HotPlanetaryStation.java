package SolarSystemMailService.Station;

public class HotPlanetaryStation extends PlanetaryStation {


    public HotPlanetaryStation(String name) {
        super(name);
    }

    @Override
    public TemperatureClass getTempClass() {
        return TemperatureClass.Hot;
    }

}
