package misc;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Created by mart3323 on 1/2/16.
 */
public class StorageTest {

    private Storage<Integer> storage;

    @Before
    public void setUp() throws Exception {
        this.storage = new Storage<Integer>();
        this.storage.add(5);
        this.storage.add(12);
        this.storage.add(-1);
    }

    @Test
    public void testGetItemsBy() throws Exception {
        assertEquals(setOf(5, 12), this.storage.getItemsBy(n -> n > 0));
    }

    private HashSet<Integer> setOf(Integer... ints) {
        return new HashSet<>(Arrays.asList(ints));
    }

    @Test
    public void testRemove() throws Exception {
        this.storage.remove(12);
        this.storage.remove(5);
        assertEquals(1, this.storage.getItemsBy(i -> true).size());
    }

    @Test
    public void testAdd() throws Exception {
        this.storage.add(0); // This should be added
        this.storage.add(5); // This is a duplicate and should be ignored
        assertEquals(4, this.storage.getItemsBy(i -> true).size());
    }

    @Test
    public void testGetLock() throws Exception {
        this.storage.getLock().writeLock().lock();
        final Object done = new Object();
        final Thread thread = new Thread() {
            public void run() {
                storage.getLock().writeLock().lock();
                storage.remove(5);
                storage.remove(12);
                storage.remove(-1);
                storage.getLock().writeLock().unlock();
                synchronized (done){ done.notify(); }
            }
        };
        thread.start();
        while(thread.getState() == Thread.State.RUNNABLE){ } // Wait for it to stop running (block or finish)
        assertEquals(Thread.State.WAITING, thread.getState());
        assertEquals(3, this.storage.getItemsBy(i -> true).size());

        this.storage.getLock().writeLock().unlock();
        synchronized (done) { done.wait(); }
        assertEquals(0, this.storage.getItemsBy(i -> true).size());
    }
}