package solarpost.code.station;


import solarpost.code.misc.CargoStorage;
import solarpost.code.ship.CargoShip;
import solarpost.code.misc.SolarMail;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static solarpost.interfaces.station.IPostOffice.TempClass.Normal;

public class PostOffice extends AbstractPostOffice{

    public PostOffice(String name) {
        super(name);
    }

    @Override public TempClass getTempClass() { return Normal; }

    @Override
    protected void doTrade(CargoStorage ship, Predicate<SolarMail> filter) {
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
                                        .filter(filter)
                                        .sorted((p1, p2) -> p2.weight - p1.weight)
                                        .forEachOrdered(p -> tryLoadOntoShip(ship, p))
                );
        // Load any other packages that fit
        this.outbox.getItems().stream()
                .filter(filter)
                .forEach(p -> tryLoadOntoShip(ship, p));
    }


    @Override
    protected void afterTrade(CargoShip ship) {
        ship.refuel();
    }

    private void tryLoadOntoShip(CargoStorage ship, SolarMail mail) {
        if(ship.tryAdd(mail)){
            this.outbox.remove(mail);
        }
    }
}
