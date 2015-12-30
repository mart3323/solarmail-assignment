package SolarSystemMailService.Station;


import SolarSystemMailService.Ship;
import SolarSystemMailService.SolarMailPackage;

import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static SolarSystemMailService.Station.PlanetaryStation.TemperatureClass.Normal;

public class PlanetaryStation {

    public enum TemperatureClass {
        /** over 400K */ Hot,
        /** 100-400K */ Normal,
        /** under 100K */ Cold;
    }

    /**
     * All actions that require a ship to dock must synchronize on this object.., this ensures that only one ship
     * can dock and be serviced at any one time
     */
    private final Object dockedShipSync = new Object();

    public TemperatureClass getTempClass() { return Normal; }

    public final HashSet<SolarMailPackage> inbox = new HashSet<>();
    public final HashSet<SolarMailPackage> outbox = new HashSet<>();

    synchronized public void dockAndTrade(Ship ship){
        synchronized (dockedShipSync){
            tradeWith(ship);
            ship.launch(this);
        }
    }

    protected void tradeWith(Ship ship) {
        ship.browse()
            .filter(p -> p.destination == this)
            .peek(ship::removeCargo)
            .forEach(this.inbox::add);

        final Map<PlanetaryStation, Integer> weightPerPlanet = ship.browse()
            .collect(Collectors.groupingBy(p -> p.destination, Collectors.summingInt(p -> p.weight)));

        // IMPROVEMENT: If speed becomes a concern, stop early
        weightPerPlanet.entrySet().stream()
            .sorted((o1, o2) -> o2.getValue() - o1.getValue())
            .map(Map.Entry::getKey)
            .forEachOrdered((planetaryStation) -> loadCargoForDestination(ship, planetaryStation));
        ((HashSet<SolarMailPackage>)outbox.clone()).stream()
            .filter(ship.canDeliver())
            .forEach(p -> attemptLoadIntoShip(p, ship));
        ship.refuel();
    }

    /**
     * Given a target destination, and a ship.., load packages for that destination onto the ship
     * <br>Start with the heaviest so that we fill the space more efficiently
     * <p>Further improvement would be possible, but this is good enough while still being simple
     * @param ship the docked ship to load packages onto
     * @param destination the destination for which to load packages
     */
    private void loadCargoForDestination(Ship ship, PlanetaryStation destination) {
        this.outbox.parallelStream()
                .filter(p -> p.destination == destination)
                .sorted((p1, p2) -> p2.weight - p1.weight)
                .forEach(p -> attemptLoadIntoShip(p, ship));
    }

    /**
     * Returns a consumer that takes a package and loads it into the specified ship
     */
    private void attemptLoadIntoShip(SolarMailPackage pckg, Ship ship) {
        synchronized (this.outbox){
            if (ship.getRemainingSpace() >= pckg.weight) {
                ship.addCargo(pckg);
                this.outbox.remove(pckg);
            }
        }
    }
}
