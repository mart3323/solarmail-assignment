package solarpost;

import solarpost.misc.CargoStorage;
import solarpost.route.Node;
import solarpost.route.Route;
import solarpost.ship.AutoPilot;
import solarpost.ship.ship.AbstractShip;
import solarpost.ship.ship.HeatShieldedShip;
import solarpost.ship.ship.RegularShip;
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
        AbstractPostOffice po1 = new PostOffice("Earth");
        AbstractPostOffice po2 = new PostOffice("Moon (earth)");
        AbstractPostOffice po3 = new PostOffice("Mars");
        AbstractPostOffice po4 = new PostOffice("Io (Moon of Jupiter)");
        AbstractPostOffice po5 = new PostOffice("Saturn");
        AbstractPostOffice po6 = new PostOffice("Uranus");
        AbstractPostOffice po7 = new PostOffice("Pluto");
        AbstractPostOffice poS1 = new ScannerPostOffice("Jupiter");
        AbstractPostOffice poS2 = new ScannerPostOffice("Neptune");
        AbstractPostOffice poH1 = new HotPostOffice("Mercury");
        AbstractPostOffice poH2 = new HotPostOffice("Venus");
        ArrayList<AbstractPostOffice> stations = new ArrayList<>();
        stations.add(po1);
        stations.add(po2);
        stations.add(po3);
        stations.add(po4);
        stations.add(po5);
        stations.add(po6);
        stations.add(po7);
        stations.add(poS1);
        stations.add(poS2);
        stations.add(poH1);
        stations.add(poH2);

        final ExecutorService writerExecutor = Executors.newFixedThreadPool(30);
        final ExecutorService shipExecutor = Executors.newFixedThreadPool(30);

        ArrayList<AutoPilot> ships = new ArrayList<>();
        ships.add(new AutoPilot("r1", new RegularShip(), notHotRoute(stations)));
        ships.add(new AutoPilot("r2", new RegularShip(), notHotRoute(stations)));
        ships.add(new AutoPilot("r3", new RegularShip(), notHotRoute(stations)));
        ships.add(new AutoPilot("r4", new RegularShip(), notHotRoute(stations)));
        ships.add(new AutoPilot("r5", new RegularShip(), notHotRoute(stations)));
        ships.add(new AutoPilot("r6", new RegularShip(), notHotRoute(stations)));
        ships.add(new AutoPilot("r7", new RegularShip(), notHotRoute(stations)));
        ships.add(new AutoPilot("r8", new RegularShip(), notHotRoute(stations)));
        ships.add(new AutoPilot("r9", new RegularShip(), notHotRoute(stations)));
        ships.add(new AutoPilot("r10", new RegularShip(), notHotRoute(stations)));
        ships.add(new AutoPilot("r11", new RegularShip(), notHotRoute(stations)));
        ships.add(new AutoPilot("r12", new RegularShip(), notHotRoute(stations)));
        ships.add(new AutoPilot("r13", new RegularShip(), notHotRoute(stations)));
        ships.add(new AutoPilot("r14", new RegularShip(), notHotRoute(stations)));
        ships.add(new AutoPilot("r15", new RegularShip(), notHotRoute(stations)));
        ships.add(new AutoPilot("S1", new HeatShieldedShip(), everywhereRoute(stations)));
        ships.add(new AutoPilot("S2", new HeatShieldedShip(), everywhereRoute(stations)));
        ships.add(new AutoPilot("S2", new HeatShieldedShip(), everywhereRoute(stations)));
        ships.add(new AutoPilot("S3", new HeatShieldedShip(), everywhereRoute(stations)));
        ships.add(new AutoPilot("S4", new HeatShieldedShip(), everywhereRoute(stations)));
        ships.add(new AutoPilot("S5", new HeatShieldedShip(), everywhereRoute(stations)));

        List<Writer> writers = stations.stream().map(station -> new Writer(station, stations)).collect(Collectors.toList());
        writers.forEach(writerExecutor::submit);
        ships.forEach(shipExecutor::submit);

        writerExecutor.shutdown();
        final boolean writersFinished = writerExecutor.awaitTermination(20, SECONDS);
        System.out.println(writersFinished ? "Writers done" : "Writers stopped prematurely");
        while(true){
            Thread.sleep(1000);
            int delivered = 0;
            int waiting = 0;
            for (AbstractPostOffice station : stations) {
                final AbstractMap.SimpleEntry<Integer, Integer> entry = station.DEBUG_get_items();
                delivered += entry.getKey();
                waiting += entry.getValue();
            }
            System.out.println(delivered+"/"+waiting+" packages delivered / packages waiting");
            if(waiting == 0){
                Thread.sleep(5000);
                break;
            }
        }
        shipExecutor.shutdownNow();
        stations.stream().forEach(AbstractPostOffice::DEBUG_log_items);
        ships.stream().forEach(autoPilot -> {
            final CargoStorage shipStorage = autoPilot.ship.getStorage();
            final AbstractShip ship = autoPilot.ship;
            shipStorage.getLock().readLock().lock();
            System.out.println("Ship " + autoPilot.name + " fuel/scanner/packages " +
                    ship.fuel + "/" + ship.scannerDurability + "/" + ship.getStorage().getItems().size());
            shipStorage.getLock().readLock().unlock();
        });
    }
}
