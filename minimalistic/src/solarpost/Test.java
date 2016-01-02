package solarpost;

import solarpost.misc.CargoStorage;
import solarpost.route.Node;
import solarpost.route.Route;
import solarpost.ship.AutoPilot;
import solarpost.ship.ship.CargoShip;
import solarpost.ship.ship.HeatShieldedHull;
import solarpost.ship.ship.RegularHull;
import solarpost.station.Writer;
import solarpost.station.station.AbstractPostOffice;
import solarpost.station.station.HotPostOffice;
import solarpost.station.station.PostOffice;
import solarpost.station.station.ScannerPostOffice;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static solarpost.station.station.AbstractPostOffice.TempClass.Normal;

public class Test {

    public static Node everywhereRoute(ArrayList<AbstractPostOffice> stations){
        return Route.create(stations.toArray(new AbstractPostOffice[stations.size()]));
    }

    public static Node notHotRoute(ArrayList<AbstractPostOffice> stations){
        final List<AbstractPostOffice> offices = stations.stream().filter(p -> p.getTempClass() == Normal).collect(Collectors.toList());
        return Route.create(offices.toArray(new AbstractPostOffice[offices.size()]));
    }

    public static void main(String[] args) throws InterruptedException {
        ArrayList<AbstractPostOffice> stations = new ArrayList<>();
        stations.add(new PostOffice("Earth"));
        stations.add(new PostOffice("Moon (earth)"));
        stations.add(new PostOffice("Mars"));
        stations.add(new PostOffice("Io (Moon of Jupiter)"));
        stations.add(new PostOffice("Saturn"));
        stations.add(new PostOffice("Uranus"));
        stations.add(new PostOffice("Pluto"));
        stations.add(new ScannerPostOffice("Jupiter"));
        stations.add(new ScannerPostOffice("Neptune"));
        stations.add(new HotPostOffice("Mercury"));
        stations.add(new HotPostOffice("Venus"));

        final ExecutorService writerExecutor = Executors.newFixedThreadPool(30);
        final ExecutorService shipExecutor = Executors.newFixedThreadPool(30);

        ArrayList<AutoPilot> ships = new ArrayList<>();
        ships.add(new AutoPilot("r1", new CargoShip(new RegularHull()), notHotRoute(stations)));
        ships.add(new AutoPilot("r2", new CargoShip(new RegularHull()), notHotRoute(stations)));
        ships.add(new AutoPilot("r3", new CargoShip(new RegularHull()), notHotRoute(stations)));
        ships.add(new AutoPilot("r4", new CargoShip(new RegularHull()), notHotRoute(stations)));
        ships.add(new AutoPilot("r5", new CargoShip(new RegularHull()), notHotRoute(stations)));
        ships.add(new AutoPilot("r6", new CargoShip(new RegularHull()), notHotRoute(stations)));
        ships.add(new AutoPilot("r7", new CargoShip(new RegularHull()), notHotRoute(stations)));
        ships.add(new AutoPilot("r8", new CargoShip(new RegularHull()), notHotRoute(stations)));
        ships.add(new AutoPilot("r9", new CargoShip(new RegularHull()), notHotRoute(stations)));
        ships.add(new AutoPilot("r10", new CargoShip(new RegularHull()), notHotRoute(stations)));
        ships.add(new AutoPilot("r11", new CargoShip(new RegularHull()), notHotRoute(stations)));
        ships.add(new AutoPilot("r12", new CargoShip(new RegularHull()), notHotRoute(stations)));
        ships.add(new AutoPilot("r13", new CargoShip(new RegularHull()), notHotRoute(stations)));
        ships.add(new AutoPilot("r14", new CargoShip(new RegularHull()), notHotRoute(stations)));
        ships.add(new AutoPilot("r15", new CargoShip(new RegularHull()), notHotRoute(stations)));
        ships.add(new AutoPilot("S1", new CargoShip(new HeatShieldedHull()), everywhereRoute(stations)));
        ships.add(new AutoPilot("S2", new CargoShip(new HeatShieldedHull()), everywhereRoute(stations)));
        ships.add(new AutoPilot("S2", new CargoShip(new HeatShieldedHull()), everywhereRoute(stations)));
        ships.add(new AutoPilot("S3", new CargoShip(new HeatShieldedHull()), everywhereRoute(stations)));
        ships.add(new AutoPilot("S4", new CargoShip(new HeatShieldedHull()), everywhereRoute(stations)));
        ships.add(new AutoPilot("S5", new CargoShip(new HeatShieldedHull()), everywhereRoute(stations)));

        List<Writer> writers = stations.stream().map(station -> new Writer(station, stations)).collect(Collectors.toList());
        writers.forEach(writerExecutor::submit);
        ships.forEach(shipExecutor::submit);

        writerExecutor.shutdown();
        final boolean writersFinished = writerExecutor.awaitTermination(20, SECONDS);
        System.out.println(writersFinished ? "Writers done" : "Writers stopped prematurely");
        while(true){
            Thread.sleep(500);
            if (logWorldStatus(stations, ships)) break;
        }
        shipExecutor.shutdownNow();
        System.out.println("  Stations' mail statistics  ");
        stations.stream().forEach(AbstractPostOffice::DEBUG_log_items);
        System.out.println("  Ships' final statuses  ");
        ships.stream().forEach(autoPilot -> {
            final CargoStorage shipStorage = autoPilot.ship.getStorage();
            final CargoShip ship = autoPilot.ship;
            shipStorage.getLock().readLock().lock();
            System.out.println("Ship " + autoPilot.name + " fuel/scanner/packages " +
                    ship.getFuel() + "/" + ship.getScannerDurability() + "/" + ship.getStorage().getItems().size());
            shipStorage.getLock().readLock().unlock();
        });
    }

    /**
     * Logs the world status of packages (waiting, in transit, and delivered)
     * @return true if all packages are delivered (none are waiting or in cargo)
     */
    private static boolean logWorldStatus(ArrayList<AbstractPostOffice> stations, ArrayList<AutoPilot> ships) {
        // Yes, i don't lock the world.., but i check stations first..,
        // so if all stations are empty, and then after that all ships are empty,
        // then the packages must have been delivered, because packages can't move upstream or horizontally
        int delivered = 0;
        int waiting = 0;
        for (AbstractPostOffice station : stations) {
            final AbstractMap.SimpleEntry<Integer, Integer> entry = station.DEBUG_get_items();
            delivered += entry.getKey();
            waiting += entry.getValue();
        }
        int transit = 0;
        for (AutoPilot ship : ships) {
            final CargoStorage shipCargo = ship.ship.getStorage();
            shipCargo.getLock().readLock().lock();
            transit += shipCargo.getItems().size();
            shipCargo.getLock().readLock().unlock();
        }
        System.out.println(waiting+" → "+transit+" → "+delivered+" waiting → in transit → delivered");
        return transit + waiting == 0;
    }
}
