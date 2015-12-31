package SolarSystemMailService;

import SolarSystemMailService.Ship.HeatShieldedShip;
import SolarSystemMailService.Ship.Ship;
import SolarSystemMailService.Station.PlanetaryStation;
import SolarSystemMailService.Station.ScannerSellingPlanetaryStation;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public class Autopilot extends Thread{
    public static final int MILLISECONDS_OF_FLIGHT = 15;
    final Comparator<PlanetaryStation> hasMorePackagesToPickup() {
        return (s1, s2) -> (int)
                (s2.browseOutbox().filter(this.ship.canDeliver()).count() -
                        s1.browseOutbox().filter(this.ship.canDeliver()).count());
    }

    final String name;
    final Ship ship;
    public Autopilot(String name, Ship ship) {
        this.name = name;
        this.ship = ship;
    }

    @Override
    public void run() {
        try {
            while (!interrupted()) {
                final PlanetaryStation destination = this.pickNewDestination();
                if (destination != null && this.ship.canLandAt(destination)) {
                    destination.dockAndTrade(this.ship);
                }
                Thread.sleep(MILLISECONDS_OF_FLIGHT);
            }
            System.out.println(this.name+" stopped");
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Rocket \"" + name + "\" interrupted, stopping");
        }
    }

    private PlanetaryStation pickNewDestination() {
        if(this.ship instanceof HeatShieldedShip){
            System.out.print("");
        }
        synchronized (PlanetaryStation.class){
            return getWeightPerValidDestination().entrySet().stream()
                    .sorted((a, b) -> b.getValue() - a.getValue())
                    .map(Entry::getKey)
                    .filter(this.ship::canLandAt)
                    .findFirst()
                    .orElseGet(() ->
                                    PlanetaryStation.browse()
                                            .filter(this.ship::canLandAt)
                                            .peek(s -> System.out.print(s.name + "→"))
                                            .filter(s -> s.browseOutbox().filter(this.ship.canDeliver()).count() > 0)
                                            .peek(s -> System.out.print("←"))
                                            .sorted(hasMorePackagesToPickup())
                                            .findFirst() // Return

                                            .orElseGet(() ->
                                                            null
                                            )
                    );
        }
    }

    private Map<PlanetaryStation, Integer> getWeightPerValidDestination() {
        return this.ship.browse()
                .collect(
                        Collectors.groupingBy(
                            p -> p.destination,
                            Collectors.summingInt(p -> p.weight)
                        )
                );
    }
}
