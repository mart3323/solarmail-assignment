package solarpost.code.misc;

import solarpost.interfaces.misc.ICargoStorage;

import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class CargoStorage implements ICargoStorage {
    private final int capacity;
    private final Storage<SolarMail> items = new Storage<>();

    public CargoStorage(int i) {
        capacity = i;
    }
    public ReentrantReadWriteLock getLock() throws IllegalStateException {
        return this.items.getLock();
    }
    public Set<SolarMail> getItems() throws IllegalStateException {
        assertReadLock();
        return items.getItemsBy(solarMail -> true);
    }

    public boolean tryAdd(SolarMail mail) throws IllegalStateException {
        assertWriteLock();
        if (this.capacity >= this.getItems().stream().mapToInt(p -> p.weight).sum() + mail.weight) {
            this.items.add(mail);
            return true;
        }
        return false;
    }
    public void add(SolarMail mail) throws IllegalStateException, RuntimeException {
        if(!this.tryAdd(mail)){
            throw new RuntimeException("used .add but broke capacity (use tryAdd instead)");
        }
    }
    public void remove(SolarMail mail) throws IllegalStateException{
        assertWriteLock();
        this.items.remove(mail);
    }


    private void assertReadLock() {
        if(!currentThreadHasAtLeastReadLock()){
            throw new IllegalStateException("You must acquire a read or write lock for this action!");
        }
    }

    private void assertWriteLock() {
        if(!currentThreadHasWriteLock()){
            throw new IllegalStateException("You must acquire a write lock for this action!");
        }
    }

    private boolean currentThreadHasAtLeastReadLock() {
        return this.getLock().getReadHoldCount() > 0 || currentThreadHasWriteLock();
    }

    private boolean currentThreadHasWriteLock() {
        return this.getLock().isWriteLockedByCurrentThread();
    }
}
