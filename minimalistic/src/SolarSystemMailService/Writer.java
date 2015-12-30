package SolarSystemMailService;

import SolarSystemMailService.Station.PlanetaryStation;

import java.util.List;
import java.util.stream.Collectors;

public class Writer extends Thread{

    public static final int MILLISECONDS_PER_PACKAGE = 3;

    private final int total;
    private final PlanetaryStation station;

    public Writer(int packagesToCreate, PlanetaryStation station) {
        this.total = packagesToCreate;
        this.station = station;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < total; i++) {
                Thread.sleep(MILLISECONDS_PER_PACKAGE);
                createRandomPackage(station);
            }
            System.out.println("Writer complete");
        } catch (InterruptedException e) {
            System.err.println("Writer interrupted");
            e.printStackTrace();
        }
        super.run();
    }

    public static void createRandomPackage(PlanetaryStation station){
        station.composeMail(makeRandomDestination(station), makeRandomWeight());
    }

    private static int makeRandomWeight() {
        return 1+getRandomIntInRange(80);
    }

    private static int getRandomIntInRange(int max) {
        return (int)Math.floor(Math.random() * max);
    }

    private static PlanetaryStation makeRandomDestination(PlanetaryStation station) {
        final List<PlanetaryStation> options = PlanetaryStation.browse().collect(Collectors.toList());
        options.remove(station);
        return options.get(getRandomIntInRange(options.size()));
    }

}
