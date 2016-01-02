package solarpost.solarsystem;

import solarpost.misc.CargoStorage;
import solarpost.route.Node;
import solarpost.route.Route;
import solarpost.ship.CargoShip;
import solarpost.ship.HeatShieldedHull;
import solarpost.ship.RegularHull;
import solarpost.station.AbstractPostOffice;
import solarpost.station.HotPostOffice;
import solarpost.station.PostOffice;
import solarpost.station.ScannerPostOffice;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static solarpost.station.AbstractPostOffice.TempClass.Normal;

public class Main {

    public static final String RESET_LINE = "1%\r";

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
        for (int i = 1; i < 21; i++) {
            ships.add(new AutoPilot("r"+i, new CargoShip(new RegularHull()), notHotRoute(stations)));
        }
        for (int i = 1; i < 6; i++) {
            ships.add(new AutoPilot("S"+i, new CargoShip(new HeatShieldedHull()), everywhereRoute(stations)));
        }
        final Writer writer = new Writer(stations);

        writer.start();
        ships.forEach(shipExecutor::submit);

        System.out.println("unwritten → waiting → in_transit → delivered");
        while(worldIsAlive(stations, ships, writer) || writer.isAlive()){
            Thread.sleep(500);
        }
        System.out.println();

        shipExecutor.shutdownNow();

        System.out.println("  Stations' mail statistics  ");
        stations.stream().forEach(station -> {
            final Integer inWeight = station.getInbox(p -> true, Collectors.summingInt(p -> p.weight));
            final Integer count = station.getInbox(p -> true, Collectors.summingInt(p -> 1));
            final Integer outCount = station.getOutbox(p -> true, Collectors.summingInt(p -> 1));
            System.out.printf("%-20s has received %4d packages for a total weight of %5d and has %2d packages still awaiting delivery%n",
                    station.name, count, inWeight, outCount);
        });

        System.out.println("  Ships' final statuses  ");
        ships.stream().forEach(autoPilot -> {
            final CargoShip ship = autoPilot.ship;
            final CargoStorage shipStorage = ship.getStorage();
            shipStorage.getLock().readLock().lock();
                final int items = ship.getStorage().getItems().size();
            shipStorage.getLock().readLock().unlock();
            System.out.printf("Ship %3s %2d fuel, %2d scanner, %2d packages %n",
                        autoPilot.name,
                        ship.getFuel(),
                        ship.getScannerDurability(),
                        items);
        });
    }

    /**
     * Logs the world status of packages (waiting, in transit, and delivered)
     * @return true if all packages are delivered (none are waiting or in cargo)
     */
    private static boolean worldIsAlive(ArrayList<AbstractPostOffice> stations, ArrayList<AutoPilot> ships, Writer writer) {
        // Yes, i don't lock the world.., but i check stations first..,
        // so if all stations are empty, and then after that all ships are empty,
        // then the packages must have been delivered, because packages can't move upstream or horizontally
        int delivered = 0;
        int waiting = 0;
        for (AbstractPostOffice station : stations) {
            delivered += station.getInbox(p -> true, Collectors.summingInt(p -> 1));
            waiting += station.getOutbox(p -> true, Collectors.summingInt(p -> 1));
        }
        int transit = 0;
        for (AutoPilot ship : ships) {
            final CargoStorage shipCargo = ship.ship.getStorage();
            shipCargo.getLock().readLock().lock();
            transit += shipCargo.getItems().size();
            shipCargo.getLock().readLock().unlock();
        }
        System.out.print(RESET_LINE);
        final int notWritten = writer.getToWrite();
        System.out.printf("%4d → %4d → %4d → %4d ", notWritten, waiting, transit, delivered);
        return notWritten + transit + waiting > 0;
    }
}
