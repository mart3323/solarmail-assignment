package solarpost.station.station;

import solarpost.misc.CargoStorage;
import solarpost.misc.SolarMail;
import solarpost.ship.ship.AbstractShip;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPostOffice {
    CargoStorage outbox = new CargoStorage(Integer.MAX_VALUE);
    CargoStorage inbox = new CargoStorage(Integer.MAX_VALUE);

    public void addPackage(AbstractPostOffice target, int weight) {
        this.outbox.getLock().writeLock().lock();
        this.outbox.add(new SolarMail(this, target, weight));
        this.outbox.getLock().writeLock().unlock();
    }

    public void DEBUG_log_items() {
        this.outbox.getLock().readLock().lock();
        this.inbox.getLock().readLock().lock();
        System.out.println("Station "+name+" has inbox/outbox "+this.inbox.getItems().size()+"/"+this.outbox.getItems().size());
        this.inbox.getLock().readLock().unlock();
        this.outbox.getLock().readLock().unlock();
    }
    /** Returns inbox, outbox pair */
    public HashMap.SimpleEntry<Integer, Integer> DEBUG_get_items(){
        this.outbox.getLock().readLock().lock();
        this.inbox.getLock().readLock().lock();
        final AbstractMap.SimpleEntry<Integer, Integer> simpleEntry = new HashMap.SimpleEntry(this.inbox.getItems().size(), this.outbox.getItems().size());
        this.inbox.getLock().readLock().unlock();
        this.outbox.getLock().readLock().unlock();
        return simpleEntry;
    }

    public enum TempClass { Hot, Normal, Cold;}

    public abstract TempClass getTempClass();

    public final String name;
    public AbstractPostOffice(String name) {
        this.name = name;
    }

    synchronized public void dockTradeAndLaunch(AbstractShip dockedShip){
        CargoStorage shipCargo = dockedShip.getStorage();

        beforeTrade(dockedShip);

        shipCargo.getLock().writeLock().lock();
            this.inbox.getLock().writeLock().lock();
                this.outbox.getLock().writeLock().lock();
                    this.doTrade(shipCargo);
                this.outbox.getLock().writeLock().unlock();
            this.inbox.getLock().writeLock().unlock();
        shipCargo.getLock().writeLock().unlock();

        afterTrade(dockedShip);
        dockedShip.launch(this);
    }

    protected void beforeTrade(AbstractShip ship){}
    protected abstract void doTrade(CargoStorage ship);
    protected void afterTrade(AbstractShip ship){}

}
