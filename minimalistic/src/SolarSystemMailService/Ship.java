package SolarSystemMailService;

import SolarSystemMailService.Station.PlanetaryStation;

import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Ship {

    Predicate<SolarMailPackage> canDeliver();

    void removeCargo(SolarMailPackage pckg);
    void addCargo(SolarMailPackage pckg);
    Stream<SolarMailPackage> browse();
    void refuel();

    int getRemainingSpace();

    void launch(PlanetaryStation from);

    int getFuelCostToLaunch(PlanetaryStation.TemperatureClass tempClass);

    void installNewScanner();

    boolean needsNewScanner();
}
