package solarpost.code.misc;

import solarpost.interfaces.misc.IStorage;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Storage<T> implements IStorage<T> {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final HashSet<T> items = new HashSet<>();

    public Set<T> getItemsBy(Predicate<T> filter){
        this.lock.readLock().lock();
        final Set<T> items = this.items.parallelStream().filter(filter).collect(Collectors.toSet());
        this.lock.readLock().unlock();
        return items;
    }

    public void remove(T item){
        this.lock.writeLock().lock();
        this.items.remove(item);
        this.lock.writeLock().unlock();
    }

    public void add(T item){
        this.lock.writeLock().lock();
        this.items.add(item);
        this.lock.writeLock().unlock();
    }

    public ReentrantReadWriteLock getLock() {
        return lock;
    }
}
