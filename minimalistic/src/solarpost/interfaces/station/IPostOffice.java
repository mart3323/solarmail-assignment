package solarpost.interfaces.station;

import solarpost.misc.SolarMail;
import solarpost.ship.CargoShip;

import java.util.function.Predicate;
import java.util.stream.Collector;

/**
 * A Post office has an inbox and an outbox of {@link SolarMail}
 * <p>postal ships can request to dock here to have packages dropped off and pick up new ones<br>
 * (done automatically by the post office)
 * <p>Note that there is no permanent docking here, ships are only allowed to dock for the time it takes to service them!
 */
public interface IPostOffice {

    /**
     * Creates a new package in this stations' outbox (the source is implicitly this station)
     * @param target the destination for the package
     * @param weight the weight of the package
     */
    void addPackage(IPostOffice target, int weight);

    /**
     * Returns the temp class of this station (this can usually be assumed to never change)
     */
    TempClass getTempClass();

    /**
     * Docks a given ship, and trades with it, but never puts packages on the ship that don't match the filter
     * @param dockedShip the ship to dock
     * @param filter a filter for incoming packages.., packages not matching this filter will never be loaded
     */
    void dockTradeAndLaunch(CargoShip dockedShip, Predicate<SolarMail> filter);

    /**
     * Iterates over the inbox and collects some statistical data
     * <p> For example, to get the average weight of all packages you would do
     * <br>{@code getInbox(p -> true, Collectors.averagingInt(p -> p.weight))}
     * @param filter filter to apply to the packages first
     * @param collector collector to collect the packages into a single item of statistics
     * @param <T> Intermediary type
     * @param <Q> Final statistical type
     * @return the result of collecting all packages that match the filter
     */
    <T,Q> T getInbox(Predicate<SolarMail> filter, Collector<? super SolarMail, Q, T> collector);

    /**
     * same as {@link #getInbox(Predicate, Collector)}, but for outgoing packages
     * @see #getInbox(Predicate, Collector)
     */
    <T,Q> T getOutbox(Predicate<SolarMail> filter, Collector<? super SolarMail, Q, T> collector);

    enum TempClass {
        Hot,
        Normal,
        Cold }
}
