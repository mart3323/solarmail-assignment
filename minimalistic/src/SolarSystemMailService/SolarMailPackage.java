package SolarSystemMailService;

import SolarSystemMailService.Station.PlanetaryStation;
import jdk.nashorn.internal.ir.annotations.Immutable;

@Immutable
public class SolarMailPackage {
    public final int weight;
    public final PlanetaryStation source;
    public final PlanetaryStation destination;

    public SolarMailPackage(int weight, PlanetaryStation source, PlanetaryStation destination) {
        this.weight = weight;
        this.source = source;
        this.destination = destination;
    }
}
