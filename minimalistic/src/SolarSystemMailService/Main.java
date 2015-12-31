package SolarSystemMailService;

import SolarSystemMailService.Ship.HeatShieldedShip;
import SolarSystemMailService.Ship.RegularShip;
import SolarSystemMailService.Station.HotPlanetaryStation;
import SolarSystemMailService.Station.PlanetaryStation;
import SolarSystemMailService.Station.ScannerSellingPlanetaryStation;

import java.util.HashSet;
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
        IntStream.range(1,15).forEach(i -> ships.add(new Autopilot("s"+i, new RegularShip())));
        IntStream.range(1,5).forEach(i -> ships.add(new Autopilot("SS"+i, new HeatShieldedShip())));

        final ExecutorService writerExecutor = Executors.newFixedThreadPool(30);
        final ExecutorService shipExecutor = Executors.newFixedThreadPool(30);
        writers.forEach(writerExecutor::submit);
        ships.forEach(shipExecutor::submit);

        try {
            System.out.println("Waiting for writers to complete");
            writerExecutor.shutdown();
            writerExecutor.awaitTermination(10, SECONDS);
            System.out.println("All packages instantiated, now waiting for deliveries");

            int remainingPackages;
            do{
                Thread.sleep(100);
                int packagesOnShips = ships.stream().mapToInt(s -> (int) s.ship.browse().count()).sum();
                remainingPackages = stations.stream().mapToInt(s -> (int) s.browseOutbox().count()).sum()
                + packagesOnShips;
                synchronized (System.out){
                    System.out.println(remainingPackages+"+"+packagesOnShips+" packages remaining to deliver");
                }
            }while(remainingPackages > 0);
            shipExecutor.shutdownNow();
            System.out.println("Shutting down ships");
        } catch (InterruptedException ignored) { }

        System.out.println("Statistics");
        stations.stream().forEach(s -> {
            System.out.println("  Station "+s.name+" has received "+s.getReceivedPackagesAmount()+" packages"+
                    " and still needs "+s.browseOutbox().count()+" packages delivered");
        });
        final int deliveredPackages = stations.stream().mapToInt(PlanetaryStation::getReceivedPackagesAmount).sum();
        final int totalPackages = PACKAGES_PER_STATION * stations.size();

        System.out.println("Total number of delivered packages: "+deliveredPackages+"/"+ totalPackages);

        ships.forEach(Autopilot::interrupt);
    }


}
