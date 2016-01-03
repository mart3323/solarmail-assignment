package solarpost.station;

import solarpost.interfaces.station.IPostOffice;
import solarpost.misc.CargoStorage;
import solarpost.misc.SolarMail;
import solarpost.ship.CargoShip;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collector;

/**
 * A generic post office which can service only a single ship at a time, and has (nearly) infinite storages
 * for both outgoing and incoming mail
 */
public abstract class AbstractPostOffice implements IPostOffice {
    CargoStorage outbox = new CargoStorage(Integer.MAX_VALUE);
    CargoStorage inbox = new CargoStorage(Integer.MAX_VALUE);
    public final String name;

    public AbstractPostOffice(String name) {
        this.name = name;
    }

    @Override
    public void addPackage(IPostOffice target, int weight) {
        this.outbox.getLock().writeLock().lock();
        this.outbox.add(new SolarMail(this, target, weight));
        this.outbox.getLock().writeLock().unlock();
    }

    @Override
    synchronized public void dockTradeAndLaunch(CargoShip dockedShip, Predicate<SolarMail> filter){
        CargoStorage shipCargo = dockedShip.getStorage();

        beforeTrade(dockedShip);

            this.inbox.getLock().writeLock().lock();
                this.outbox.getLock().writeLock().lock();
                    shipCargo.getLock().writeLock().lock();
                        this.doTrade(shipCargo, filter);
                    shipCargo.getLock().writeLock().unlock();
                this.outbox.getLock().writeLock().unlock();
            this.inbox.getLock().writeLock().unlock();

        afterTrade(dockedShip);
        dockedShip.launch(this);
    }

    /**
     * Actions to perform on the ship before trading
     * <br>Anything that does not require cargo lock should be done here or {@link #afterTrade}
     */
    protected void beforeTrade(CargoShip ship){}

    /**
     * Actions to do on a docked ship during trading
     * <p>This method should contain as little as possible! Anything that does not require cargo-lock
     * should be done {@link #beforeTrade} or {@link #afterTrade}
     * @param ship the docked ship
     * @param filter the ship's inbound filter.., packages that fail this filter must not be loaded onboard the ship
     */
    protected abstract void doTrade(CargoStorage ship, Predicate<SolarMail> filter);

    /**
     * Actions to do on the ship after trading
     * <br>Anything that does not require cargo lock should be done here or {@link #beforeTrade}
     * @param ship the docked ship
     */
    protected void afterTrade(CargoShip ship){}

    @Override
    public <T,Q> T getInbox(Predicate<SolarMail> filter, Collector<? super SolarMail, Q, T> collector){
        return threadSafeRead(this.inbox).parallelStream().filter(filter).collect(collector);
    }
    @Override
    public <T,Q> T getOutbox(Predicate<SolarMail> filter, Collector<? super SolarMail, Q, T> collector){
        return threadSafeRead(this.outbox).parallelStream().filter(filter).collect(collector);
    }

    private Set<SolarMail> threadSafeRead(CargoStorage storage) {
        storage.getLock().readLock().lock();
        final Set<SolarMail> items = storage.getItems();
        storage.getLock().readLock().unlock();
        return items;
    }


}
