package solarpost.code.misc;

import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class CargoStorageTest {

    private CargoStorage storage;
    private SolarMail p1;
    private SolarMail p2;
    private SolarMail p3;
    private SolarMail p4;

    @Rule
    public final ExpectedException exp = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        this.storage = new CargoStorage(80);
        this.p1 = new SolarMail(null, null, 1);
        this.p2 = new SolarMail(null, null, 10);
        this.p3 = new SolarMail(null, null, 50);
        this.p4 = new SolarMail(null, null, 70);
        this.storage.getLock().writeLock().lock();
    }

    @After
    public void tearDown() throws Exception {
        this.storage.getLock().writeLock().unlock();
    }

    @Test
    public void testReadLock() throws Exception {
        final CargoStorage unlockedStorage = new CargoStorage(80);
        exp.expect(RuntimeException.class);
        exp.expectMessage("You must acquire a read or write lock for this action!");
        unlockedStorage.getItems();
    }
    @Test
    public void testWriteLock() throws Exception {
        final CargoStorage unlockedStorage = new CargoStorage(80);
        exp.expectMessage("You must acquire a write lock for this action!");
        exp.expect(RuntimeException.class);
        unlockedStorage.add(this.p1);
    }

    @Test
    public void testTryAdd() throws Exception {
        assertTrue(this.storage.tryAdd(this.p4));
        assertFalse(this.storage.tryAdd(this.p3));
        assertEquals(1, this.storage.getItems().size());
    }

    @Test
    public void testAdd() throws Exception {
        this.storage.add(this.p4);
        exp.expect(RuntimeException.class);
        exp.expectMessage("used .add but broke capacity (use tryAdd instead)");
        this.storage.add(this.p3);
    }

    @Test
    public void testRemove() throws Exception {
        this.storage.add(this.p2);
        Assume.assumeTrue(this.storage.getItems().size() == 1);
        this.storage.remove(this.p2);
        assertEquals(0, this.storage.getItems().size());
    }

}