package solarpost.station;

import solarpost.misc.CargoStorage;
import solarpost.misc.SolarMail;
import solarpost.ship.CargoShip;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collector;

public abstract class AbstractPostOffice {
    CargoStorage outbox = new CargoStorage(Integer.MAX_VALUE);
    CargoStorage inbox = new CargoStorage(Integer.MAX_VALUE);

    public void addPackage(AbstractPostOffice target, int weight) {
        this.outbox.getLock().writeLock().lock();
        this.outbox.add(new SolarMail(this, target, weight));
        this.outbox.getLock().writeLock().unlock();
    }

    public enum TempClass { Hot, Normal, Cold;}

    public abstract TempClass getTempClass();

    public final String name;
    public AbstractPostOffice(String name) {
        this.name = name;
    }

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

    protected void beforeTrade(CargoShip ship){}
    protected abstract void doTrade(CargoStorage ship, Predicate<SolarMail> filter);
    protected void afterTrade(CargoShip ship){}

    public <T,Q> T getInbox(Predicate<SolarMail> filter, Collector<? super SolarMail, Q, T> collector){
        return threadSafeRead(this.inbox).parallelStream().filter(filter).collect(collector);
    }
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
