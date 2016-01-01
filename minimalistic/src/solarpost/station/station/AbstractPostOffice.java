package solarpost.station.station;

import solarpost.misc.CargoStorage;
import solarpost.misc.SolarMail;
import solarpost.ship.ship.AbstractShip;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.function.Predicate;

public abstract class AbstractPostOffice {
    CargoStorage outbox = new CargoStorage(Integer.MAX_VALUE);
    CargoStorage inbox = new CargoStorage(Integer.MAX_VALUE);

    public void addPackage(AbstractPostOffice target, int weight) {
        this.outbox.getLock().writeLock().lock();
        this.outbox.add(new SolarMail(this, target, weight));
        this.outbox.getLock().writeLock().unlock();
    }

    public void DEBUG_log_items() {
        this.inbox.getLock().readLock().lock();
        this.outbox.getLock().readLock().lock();
        System.out.println("Station "+name+" has inbox/outbox "+this.inbox.getItems().size()+"/"+this.outbox.getItems().size());
        this.outbox.getLock().readLock().unlock();
        this.inbox.getLock().readLock().unlock();
    }
    /** Returns inbox, outbox pair */
    public HashMap.SimpleEntry<Integer, Integer> DEBUG_get_items(){
        this.inbox.getLock().readLock().lock();
        this.outbox.getLock().readLock().lock();
        final AbstractMap.SimpleEntry<Integer, Integer> simpleEntry = new HashMap.SimpleEntry(this.inbox.getItems().size(), this.outbox.getItems().size());
        this.outbox.getLock().readLock().unlock();
        this.inbox.getLock().readLock().unlock();
        return simpleEntry;
    }

    public enum TempClass { Hot, Normal, Cold;}

    public abstract TempClass getTempClass();

    public final String name;
    public AbstractPostOffice(String name) {
        this.name = name;
    }

    synchronized public void dockTradeAndLaunch(AbstractShip dockedShip, Predicate<SolarMail> filter){
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

    protected void beforeTrade(AbstractShip ship){}

    protected abstract void doTrade(CargoStorage ship, Predicate<SolarMail> filter);

    protected void afterTrade(AbstractShip ship){}

}
