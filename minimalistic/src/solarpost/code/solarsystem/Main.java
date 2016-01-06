package solarpost.code.solarsystem;

import solarpost.code.misc.CargoStorage;
import solarpost.code.route.Node;
import solarpost.code.route.Route;
import solarpost.code.ship.CargoShip;
import solarpost.code.ship.HeatShieldedHull;
import solarpost.code.ship.RegularHull;
import solarpost.code.station.AbstractPostOffice;
import solarpost.code.station.HotPostOffice;
import solarpost.code.station.PostOffice;
import solarpost.code.station.ScannerPostOffice;
import solarpost.interfaces.station.IPostOffice;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static solarpost.interfaces.station.IPostOffice.TempClass.Normal;

public class Main {

    public static final String RESET_LINE = "1%\r";

    public static final String SIMULATING_WORLD_HEADER = "Simulating world\n"+
                                                         "Unwritten     → Waiting      → In transit   → Delivered   ";
    public static final String SIMULATING_WORLD_FORMAT = "%12d → %12d → %12d → %12d ";
    public static final int WORLD_UPDATE_DELAY = 500;

    public static final String SHIP_STATISTICS_HEADER = "  Ships' final statuses  ";
    public static final String SHIP_STATISTICS_FORMAT = "Ship %3s %2d fuel, %2d scanner, %2d packages %n";

    public static final String STATION_STATISTICS_HEADER = "  Stations' mail statistics  ";
    public static final String STATION_STATISTICS_FORMAT = "%-20s has received %4d packages for a total weight of %5d and has %2d packages still awaiting delivery%n";

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


        ArrayList<Writer> writers = new ArrayList<>();
        writers.add(new Writer(stations));

        ArrayList<AutoPilot> ships = new ArrayList<>();
        for (int i = 1; i < 21; i++) {
            ships.add(new AutoPilot("r"+i, new CargoShip(new RegularHull()), notHotRoute(stations)));
        }
        for (int i = 1; i < 6; i++) {
            ships.add(new AutoPilot("S"+i, new CargoShip(new HeatShieldedHull()), everywhereRoute(stations)));
        }

        simulateWorld(stations, ships, writers);
        printStationStatistics(stations);
        printShipStatistics(ships);
    }

    /**
     * Prints out the statistics (fuel, scanner, cargo) of every ship in the given list
     *
     * <p> The output starts with a header, and then a formatted line for each ship
     * <br> The formats used are:
     * <pre>
     * {@link #SHIP_STATISTICS_HEADER}: {@value #SHIP_STATISTICS_HEADER}
     * {@link #SHIP_STATISTICS_FORMAT}: {@value #SHIP_STATISTICS_FORMAT}
     */
    private static void printShipStatistics(List<AutoPilot> ships) {
        System.out.println(SHIP_STATISTICS_HEADER);
        for (AutoPilot autoPilot : ships) {
            final CargoShip ship = autoPilot.ship;
            final CargoStorage shipStorage = ship.getStorage();

            shipStorage.getLock().readLock().lock();
            final int items = ship.getStorage().getItems().size();
            shipStorage.getLock().readLock().unlock();

            System.out.printf(SHIP_STATISTICS_FORMAT, autoPilot.name, ship.getFuel(), ship.getScannerDurability(), items);
        }
    }

    /**
     * Prints out the statistics of every station in the given list (name, inbox, inweight, outbox)
     *
     * <p> The output starts with a header, and then a formatted line for each station
     * <br> The formats used are:
     * <pre>
     * {@link #STATION_STATISTICS_HEADER}: {@value #STATION_STATISTICS_HEADER}
     * {@link #STATION_STATISTICS_FORMAT}: {@value #STATION_STATISTICS_FORMAT}
     */
    private static void printStationStatistics(List<AbstractPostOffice> stations) {
        System.out.println(STATION_STATISTICS_HEADER);
        for (AbstractPostOffice station : stations) {
            final Integer inWeight = station.getInbox(p -> true, Collectors.summingInt(p -> p.weight));
            final Integer count = station.getInbox(p -> true, Collectors.summingInt(p -> 1));
            final Integer outCount = station.getOutbox(p -> true, Collectors.summingInt(p -> 1));
            System.out.printf(STATION_STATISTICS_FORMAT, station.name, count, inWeight, outCount);
        }
    }

    /**
     * Simulates a world full of autopiloted ships, mail stations, and writers that produce new packages
     * <br> Blocks until interrupted, or until all writers have finished and all packages have been delivered
     *
     * <p> Before simulation, prints out a header
     * <br> During simulation, keeps updating a single line with updated data
     * every {@link #WORLD_UPDATE_DELAY} ({@value #WORLD_UPDATE_DELAY}) milliseconds
     * <br> After simulation, shuts down the ships and writers
     * @see #SIMULATING_WORLD_HEADER
     * @see #SIMULATING_WORLD_FORMAT
     * @see #WORLD_UPDATE_DELAY
     */
    private static void simulateWorld(List<AbstractPostOffice> stations, List<AutoPilot> ships, List<Writer> writers) throws InterruptedException {
        final ExecutorService shipExecutor = Executors.newFixedThreadPool(ships.size());
        final ExecutorService writerExecutor = Executors.newFixedThreadPool(writers.size());
        ships.forEach(shipExecutor::submit);
        writers.forEach(writerExecutor::submit);
        shipExecutor.shutdown();
        writerExecutor.shutdown();

        System.out.println(SIMULATING_WORLD_HEADER);
        while(!Thread.interrupted()){
            int notWritten = 0;
            for (Writer writer : writers) {
                notWritten += writer.getToWrite();
            }
            int delivered = 0;
            int waiting = 0;
            for (IPostOffice station : stations) {
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
            System.out.printf(SIMULATING_WORLD_FORMAT, notWritten, waiting, transit, delivered);
            Thread.sleep(WORLD_UPDATE_DELAY);
            if(writerExecutor.isTerminated() && notWritten+waiting+transit == 0){
                break;
            }
        }
        shipExecutor.shutdownNow();
        writerExecutor.shutdownNow();
        System.out.println();
    }

    private static Node everywhereRoute(ArrayList<? extends IPostOffice> stations){
        return Route.create(stations.toArray(new IPostOffice[stations.size()]));
    }

    private static Node notHotRoute(ArrayList<? extends IPostOffice> stations){
        final List<IPostOffice> offices = stations.stream().filter(p -> p.getTempClass() == Normal).collect(Collectors.toList());
        return Route.create(offices.toArray(new IPostOffice[offices.size()]));
    }
}
