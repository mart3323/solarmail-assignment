package SolarSystemMailService;

import SolarSystemMailService.Ship.HeatShieldedShip;
import SolarSystemMailService.Ship.RegularShip;
import SolarSystemMailService.Station.HotPlanetaryStation;
import SolarSystemMailService.Station.PlanetaryStation;
import SolarSystemMailService.Station.ScannerSellingPlanetaryStation;

import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Main {

    public static final int PACKAGES_PER_STATION = 150;

    public static void main(String[] args) {
        HashSet<PlanetaryStation> stations = new HashSet<>();
        stations.add(new HotPlanetaryStation("Mercury"));
        stations.add(new HotPlanetaryStation("Venus"));
        stations.add(new PlanetaryStation("Earth"));
        stations.add(new PlanetaryStation("Moon (earth)"));
        stations.add(new PlanetaryStation("Mars"));
        stations.add(new ScannerSellingPlanetaryStation("Jupiter"));
        stations.add(new PlanetaryStation("Io (Moon of Jupiter)"));
        stations.add(new PlanetaryStation("Saturn"));
        stations.add(new PlanetaryStation("Uranus"));
        stations.add(new ScannerSellingPlanetaryStation("Neptune"));
        stations.add(new PlanetaryStation("Pluto"));

        HashSet<Writer> writers = new HashSet<>();
        HashSet<Autopilot> ships = new HashSet<>();
        stations.forEach(station -> writers.add(new Writer(PACKAGES_PER_STATION, station)));
        IntStream.range(1,5).forEach(i -> ships.add(new Autopilot("SS"+i, new HeatShieldedShip())));
        IntStream.range(1,15).forEach(i -> ships.add(new Autopilot("s"+i, new RegularShip())));

        final ExecutorService writerExecutor = Executors.newFixedThreadPool(30);
        final ExecutorService shipExecutor = Executors.newFixedThreadPool(30);
        writers.forEach(writerExecutor::submit);
        ships.forEach(shipExecutor::submit);

        System.out.println(IntStream.range(0,20).mapToObj(a -> a).sorted((a, b) -> a - b).findFirst().get());
        try {
            System.out.println("Waiting for writers to complete");
            writerExecutor.shutdown();
            writerExecutor.awaitTermination(10, SECONDS);
            System.out.println("\nAll packages instantiated, now waiting for deliveries\n");

            int remainingPackages;
            new Scanner(System.in).nextLine();
            shipExecutor.shutdownNow();
            ships.stream().forEach(Thread::interrupt);
            System.out.println("Shutting down ships");
        } catch (InterruptedException ignored) { }

        System.out.println("Statistics");
        stations.stream().forEach(s -> {
            System.out.println("  Station "+s.name+" has received "+s.getReceivedPackagesAmount()+" packages"+
                    " and still needs "+s.browseOutbox().count()+" packages delivered");
        });
        final int deliveredPackages = stations.stream().mapToInt(PlanetaryStation::getReceivedPackagesAmount).sum();
        final int totalPackages = PACKAGES_PER_STATION * stations.size();
        System.out.println("Packages on ships");
        ships.stream().forEach(
                s -> {
                    System.out.println(s.ship.getClass().getSimpleName() + (s.ship.needsNewScanner() ? " Needs scanner" : ""));
                    s.ship.browse().forEach(p -> System.out.println("    "+p.source.name+" → "+p.destination.name));
                }
        );
        System.out.println("Packages on stations");
        stations.stream().forEach(
                s -> s.browseOutbox().forEach(p -> System.out.println("    " + p.source.name + " → " + p.destination.name))
        );

        System.out.println("Total number of delivered packages: "+deliveredPackages+"/"+ totalPackages);

        ships.forEach(Autopilot::interrupt);
    }


}
