package solarpost.interfaces.misc;

import solarpost.code.misc.SolarMail;

import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A specific version of IStorage for storing {@link SolarMail}s, with a weight capacity limit
 * <br>Unlike a plain {@link IStorage}, this requires you to manually lock before accessing to avoid oversights
 */
public interface ICargoStorage {

    /**
     * Returns the Lock in use by this CargoStorage - this only needs to be called if multiple actions have to be synchronized
     * <br>(for example read-then-remove)
     */
    ReentrantReadWriteLock getLock() throws IllegalStateException;

    /**
     * Get (a clone of) the set of items in this storage
     * @return A cloned set of all the items in this storage
     * @throws IllegalStateException if the read lock hasn't been manually acquired
     */
    Set<SolarMail> getItems() throws IllegalStateException;

    /**
     * Attempts to add a {@link SolarMail} to this storage
     * @return true if there was enough space and the mail was added, false if there wasn't enough space
     * @throws IllegalStateException if the write lock hasn't been manually acquired
     */
    boolean tryAdd(SolarMail mail) throws IllegalStateException;

    /**
     * Adds a {@link SolarMail} to this storage
     * <br> Only use this method if you know for sure that there is enough space!
     * @throws IllegalStateException if the write lock hasn't been manually acquired
     * @throws RuntimeException if there wasn't enough space to add the item
     */
    void add(SolarMail mail) throws IllegalStateException, RuntimeException;

    /**
     * Removes a {@link SolarMail} from this storage
     * @throws IllegalStateException if the wrote lock hasn't been manually acquired
     */
    void remove(SolarMail mail) throws IllegalStateException;

}
