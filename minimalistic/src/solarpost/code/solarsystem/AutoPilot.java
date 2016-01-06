package solarpost.code.solarsystem;

import solarpost.code.misc.CargoStorage;
import solarpost.code.misc.SolarMail;
import solarpost.code.route.Node;
import solarpost.code.ship.CargoShip;
import solarpost.code.station.ScannerPostOffice;
import solarpost.interfaces.station.IPostOffice;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * An autopilot, instantiated with a ship and a Route (Node), keeps travelling the route forever,
 * <br>stopping at each station and skipping to the next ScannerSellingStation when the scanner durability goes below 10 (2.5 uses)
 * <br><img src=../../images/EDH-PlanetaryLanding.jpg />
 */
public class AutoPilot extends Thread{
    private static final int FLIGHT_TIME_MS = 15;
    private static final int SCANNER_CHANGE_THRESHOLD = 10;
    public final String name;
    public final CargoShip ship;
    private Node route;

    public AutoPilot(String name, CargoShip ship, Node route) {
        this.name = name;
        this.ship = ship;
        this.route = route;
    }

    @Override
    public void run() {
        try {
            while(!interrupted()){
                this.ship.autobuyScanner = needScanner();
                this.ship.dockAt(this.route.postOffice, this::packageIsOnRoute);
                selectNextDestination();
                Thread.sleep(FLIGHT_TIME_MS);
            }
        } catch (InterruptedException ignored) {
        }
    }

    private void selectNextDestination() {
        final Optional<IPostOffice> bestDestination = findBestDestination();
        Predicate<IPostOffice> predicate;
        if (bestDestination.isPresent()) {
            predicate = n -> n == bestDestination.get();
        } else if (needScanner()){
            predicate = n -> n instanceof ScannerPostOffice;
        } else {
            predicate = n -> true;
        }

        do {
            this.route = this.route.next;
        } while (!predicate.test(this.route.postOffice));
    }

    private Optional<IPostOffice> findBestDestination() {
        Map<IPostOffice, Integer> weightByDestination;
        final CargoStorage storage;

        storage = this.ship.getStorage();
        storage.getLock().readLock().lock();
        weightByDestination = storage.getItems().parallelStream()
                .collect(Collectors.groupingBy(p -> p.target, Collectors.summingInt(p -> p.weight)));
        storage.getLock().readLock().unlock();
        if(this.needScanner()){
            final Set<IPostOffice> toRemove = weightByDestination.keySet().stream()
                    .filter(station -> !(station instanceof ScannerPostOffice)).collect(Collectors.toSet());
            toRemove.forEach(weightByDestination::remove);
        }
        return weightByDestination.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .map(Map.Entry::getKey)
                .findFirst();
    }

    private boolean needScanner() {
        return this.ship.getScannerDurability() < SCANNER_CHANGE_THRESHOLD;
    }

    private boolean packageIsOnRoute(SolarMail pckg){
        Node first = this.route;
        Node node = this.route;
        do{
            if(node.postOffice == pckg.target){
                return true;
            }
            node = node.next;
        }while(node != first);
        return false;
    }

}
