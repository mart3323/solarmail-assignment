package SolarSystemMailService;

import SolarSystemMailService.Ship.Ship;
import SolarSystemMailService.Station.PlanetaryStation;
import SolarSystemMailService.Station.ScannerSellingPlanetaryStation;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Autopilot extends Thread{
    public static final int MILLISECONDS_OF_FLIGHT = 15;
    String name;
    Ship ship;

    public Autopilot(String name, Ship ship) {
        this.name = name;
        this.ship = ship;
    }

    @Override
    public void run() {
        try {
            while(true){
                Thread.sleep(MILLISECONDS_OF_FLIGHT);
                this.pickNewDestination().dockAndTrade(this.ship);
                //System.out.println("Launching with "+this.ship.browse().count()+" cargo");
            }
        } catch (InterruptedException e) {
            System.err.println("Rocket \""+name+"\" interrupted, stopping");
        }
    }

    private PlanetaryStation pickNewDestination() {
        final boolean needsNewScanner = this.ship.needsNewScanner();
        final Map<PlanetaryStation, Integer> weightPerValidDestination = this.ship.browse()
            .filter(p -> !needsNewScanner || p.destination instanceof ScannerSellingPlanetaryStation)
            .collect(
                Collectors.groupingBy(
                    p -> p.destination,
                    Collectors.summingInt(p -> p.weight)
                )
            );
        final Optional<PlanetaryStation> chosenDestination = weightPerValidDestination
                                                                        .entrySet()
                                                                        .stream()
                                                                        .sorted((a, b) -> a.getValue() - b.getValue())
                                                                        .map(Entry::getKey)
                                                                        .findFirst();
        if(chosenDestination.isPresent()){
            return chosenDestination.get();
        } else {
            return PlanetaryStation.browse().filter(
                    station -> this.ship.canLandAt(station) && !needsNewScanner || station instanceof ScannerSellingPlanetaryStation
            ).findAny().get();
        }
    }
}
