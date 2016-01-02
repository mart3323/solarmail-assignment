package solarpost.ship;

import interfaces.ship.IHullProfile;
import org.junit.Before;
import org.junit.Test;
import solarpost.misc.SolarMail;
import solarpost.station.AbstractPostOffice;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static solarpost.station.AbstractPostOffice.TempClass.Normal;

public class CargoShipTest {

    private CargoShip ship;
    private AbstractPostOffice station;
    private AbstractPostOffice station2;

    @Before
    public void setUp() throws Exception {
        this.ship = new CargoShip(new IHullProfile() {
            @Override public Function<AbstractPostOffice, Integer> getScannerWearPattern() { return i -> i.getTempClass().ordinal(); }
            @Override public Function<AbstractPostOffice, Integer> getFuelConsumptionPattern() { return i -> i.getTempClass().ordinal()*2; }
            @Override public int getMaxFuel() { return 10; }
            @Override public int getMaxScanner() { return 10; }
            @Override public int getCargoCapacity() { return 10; }
        });
        this.station = mock(AbstractPostOffice.class);
        when(station.getTempClass()).thenReturn(Normal);
        this.station2 = mock(AbstractPostOffice.class);
    }

    @Test
    public void testDockAt() throws Exception {
        final Predicate<SolarMail> acceptAll = i -> true;
        ship.dockAt(station, acceptAll);
        verify(station).dockTradeAndLaunch(ship, acceptAll);
    }

    @Test
    public void testGetStorage() throws Exception {
        ship.getStorage().getLock().writeLock().lock();
        ship.getStorage().add(new SolarMail(station, station2, 10));
        assertFalse(ship.getStorage().tryAdd(new SolarMail(station, station2, 1)));
        ship.getStorage().getLock().writeLock().unlock();
    }

    @Test
    public void testRefuel() throws Exception {
        ship.launch(station);
        assertEquals(10 - Normal.ordinal() * 2, this.ship.getFuel());
        assertEquals(10 - Normal.ordinal(), this.ship.getScannerDurability());
        ship.refuel();
        assertEquals(10, this.ship.getFuel());
    }

    @Test
    public void testInstallScanner() throws Exception {
        ship.launch(station);
        assertEquals(10 - Normal.ordinal() * 2, this.ship.getFuel());
        assertEquals(10 - Normal.ordinal(), this.ship.getScannerDurability());
        ship.installScanner();
        assertEquals(10, this.ship.getScannerDurability());
    }

    @Test
    public void testGetScannerDurability() throws Exception {
        ship.launch(station);
        assertEquals(10 - Normal.ordinal() * 2, this.ship.getFuel());
        assertEquals(10 - Normal.ordinal(), this.ship.getScannerDurability());
    }

    @Test
    public void testGetFuel() throws Exception {
        ship.launch(station);
        assertEquals(10-Normal.ordinal()*2, this.ship.getFuel());
        assertEquals(10-Normal.ordinal(), this.ship.getScannerDurability());
    }

    @Test
    public void testLaunch() throws Exception {
        ship.launch(station);
        assertEquals(10 - Normal.ordinal() * 2, this.ship.getFuel());
        assertEquals(10 - Normal.ordinal(), this.ship.getScannerDurability());
    }
}