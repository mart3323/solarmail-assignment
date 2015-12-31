package SolarSystemMailService;

import SolarSystemMailService.Ship.Ship;
import SolarSystemMailService.Station.PlanetaryStation;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public class Autopilot extends Thread{
    public static final int MILLISECONDS_OF_FLIGHT = 15;
    final Comparator<PlanetaryStation> hasMorePackagesToPickup = (s1, s2) -> (int)
            (s1.browseOutbox().filter(this.ship.canDeliver()).count() -
            s2.browseOutbox().filter(this.ship.canDeliver()).count());

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
                    final PlanetaryStation destination = this.pickNewDestination();
                    if(destination != null){
                        System.out.print(this.name+"("+this.ship.browse().count()+"|"+this.ship.browse().filter(p -> p.destination == destination).count()+") ");
                        destination.dockAndTrade(this.ship);
                        Thread.sleep(MILLISECONDS_OF_FLIGHT);
                    } else {
                        System.out.println("+");
                    }
            }
        } catch (InterruptedException e) {
            System.err.println("Rocket \""+name+"\" interrupted, stopping");
        }
    }

    private PlanetaryStation pickNewDestination() {
            System.out.print("→");
            return getWeightPerValidDestination()
                    .entrySet()
                    .stream()
                    .sorted((a, b) -> b.getValue() - a.getValue())
                    .filter(e -> e.getValue() > 0)
                    .map(Entry::getKey)
                    .findFirst() // Return
                    .orElseGet(() -> {
                                System.out.print("↓");
                                return PlanetaryStation.browse()
                                        .filter(this.ship::canLandAt)
                                        .sorted(hasMorePackagesToPickup)
                                        .findFirst() // Return
                                        .orElseGet(() ->{
                                            System.out.print("←");
                                            return null;
                                        });
                            }
                    );
    }

    private Map<PlanetaryStation, Integer> getWeightPerValidDestination() {
        return this.ship.browse().collect(
                Collectors.groupingBy(
                        p -> p.destination,
                        Collectors.summingInt(p -> p.weight)
                )
        );
    }
}
