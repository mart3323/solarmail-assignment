package solarpost.station.station;


import solarpost.misc.CargoStorage;
import solarpost.ship.ship.AbstractShip;
import solarpost.misc.SolarMail;

import java.util.Map;
import java.util.stream.Collectors;

import static solarpost.station.station.AbstractPostOffice.TempClass.Hot;
import static solarpost.station.station.AbstractPostOffice.TempClass.Normal;

public class PostOffice extends AbstractPostOffice{

    public PostOffice(String name) {
        super(name);
    }

    @Override public TempClass getTempClass() { return Normal; }

    @Override
    protected void doTrade(CargoStorage ship) {
        // Remove packages for this station
        ship.getItems().stream()
                .filter(p -> p.target == this)
                .peek(ship::remove)
                .forEach(this.inbox::add);
        // Load packages to existing destinations
        ship.getItems().stream()
                .collect(Collectors.groupingBy(p -> p.target, Collectors.summingInt(p -> p.weight)))
                .entrySet().stream()
                .sorted((o1, o2) -> o2.getValue() - o1.getValue())
                .map(Map.Entry::getKey)
                .forEachOrdered(station ->
                                this.outbox.getItems().stream()
                                        .filter(p -> p.target == station)
                                        .sorted((p1, p2) -> p2.weight - p1.weight)
                                        .forEachOrdered(p -> tryLoadOntoShip(ship, p))
                );
        // Load any other packages that fit
        this.outbox.getItems().forEach(p -> tryLoadOntoShip(ship, p));
    }


    @Override
    protected void afterTrade(AbstractShip ship) {
        ship.refuel();
    }

    private void tryLoadOntoShip(CargoStorage ship, SolarMail mail) {
        if(ship.tryAdd(mail)){
            this.outbox.remove(mail);
        }
    }
}
