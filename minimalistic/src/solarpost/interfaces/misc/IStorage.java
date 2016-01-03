package solarpost.interfaces.misc;

import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

/**
 * A simplified generic {@link Set} with a {@link ReentrantReadWriteLock} for thread safety
 * @param <T> the type of items to store
 */

public interface IStorage<T> {
    /**
     * Returns a subset of the items in storage, based on the filter provided
     * <br>Blocks if the storage is currently locked
     * @param filter predicate to filter the items by
     * @return the subset of items that match the predicate
     */
    Set<T> getItemsBy(Predicate<T> filter);

    /**
     * Returns the Lock in use by this set - this only needs to be called if multiple actions have to be synchronized
     * <br>(for example read-then-remove)
     */
    ReentrantReadWriteLock getLock();

    /**
     * Thread-safely adds an item to this set
     */
    void remove(T item);

    /**
     * Thread-safely removes an item from this set
     */
    void add(T item);
}
