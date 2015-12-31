package SolarSystemMailService.Station;


import SolarSystemMailService.Ship.Ship;
import SolarSystemMailService.SolarMailPackage;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static SolarSystemMailService.Station.PlanetaryStation.TemperatureClass.Normal;

public class PlanetaryStation {


    /**
     * All actions that require a ship to dock must synchronize on this object.., this ensures that only one ship
     * can dock and be serviced at any one time
     */
    private final Object dockedShipSync = new Object();
    public final String name;

    protected final HashSet<SolarMailPackage> inbox = new HashSet<>();
    protected final HashSet<SolarMailPackage> outbox = new HashSet<>();
    private static HashSet<PlanetaryStation> stations = new HashSet<>();

    public static Stream<PlanetaryStation> browse() {
        return PlanetaryStation.stations.stream();
    }
    public Stream<SolarMailPackage> browseOutbox() {
        synchronized (this.outbox){
            return ((HashSet<SolarMailPackage>)this.outbox.clone()).stream();
        }
    }

    public PlanetaryStation(String name) {
        stations.add(this);
        this.name = name;
    }

    public enum TemperatureClass {
        /** over 400K */ Hot,
        /** 100-400K */ Normal,
        /** under 100K */ Cold;
    }

    public TemperatureClass getTempClass() { return Normal; }

    synchronized public void composeMail(PlanetaryStation destination, int weight){
        synchronized (this.outbox){
            this.outbox.add(new SolarMailPackage(weight, this, destination));
        }
    }

    /**
     * Performs the following actions
     * <ul>
     *     <li> Loads all packages sent to this planet off the ship
     *     <li> Loads as many new packages onto the ship as possible, ordered by...
     *     <ol>
     *         <li> The ship has the most (by weight) packages for the same destination
     *         <li> The package is the heaviest
     *     </ol>
     *     <li> Refuels the ship
     * @param ship the ship to dock and perform these actions on
     */
    synchronized public void dockAndTrade(Ship ship){
        synchronized (dockedShipSync){
            tradeWith(ship);
            ship.launch(this);
        }
    }

    /** Returns the total amount of packages received by this station */
    public int getReceivedPackagesAmount(){
        synchronized (this.inbox){
            return this.inbox.size();
        }
    }
    /** Returns the total amount of packages received by this station that match the given filter */
    synchronized public double getReceivedPackagesAmount(Predicate<SolarMailPackage> filter){
        return this.inbox.parallelStream().filter(filter).count();
    }
    /** Returns the total weight of all packages received by this station */
    synchronized public int getReceivedPackagesTotalWeight(){
        return this.inbox.stream().mapToInt(p -> p.weight).sum();
    }
    /** Returns the average weight of all packages received by this station.., 0 if none received */
    synchronized public double getReceivedPackagesAverageWeight(){
        return this.inbox.stream().mapToInt(p -> p.weight).average().orElseGet(() -> 0);
    }


    protected void tradeWith(Ship ship) {
        final List<SolarMailPackage> packagesToRemove = ship.browse()
                .filter(p -> p.destination == this)
                .collect(Collectors.toList());
        packagesToRemove.stream()
                .peek(this.inbox::add)
                .forEach(ship::removeCargo);

        final Map<PlanetaryStation, Integer> weightPerPlanet = ship.browse()
            .collect(Collectors.groupingBy(p -> p.destination, Collectors.summingInt(p -> p.weight)));

        // IMPROVEMENT: If speed becomes a concern, stop early
        weightPerPlanet.entrySet().stream()
            .sorted((o1, o2) -> o2.getValue() - o1.getValue())
            .map(Map.Entry::getKey)
            .forEachOrdered((planetaryStation) -> loadCargoForDestination(ship, planetaryStation));
        this.browseOutbox()
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
        synchronized (this.outbox) {
            this.outbox.parallelStream()
                    .filter(p -> p.destination == destination)
                    .filter(ship.canDeliver())
                    .sorted((p1, p2) -> p2.weight - p1.weight)
                    .forEach(p -> attemptLoadIntoShip(p, ship));
        }
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
