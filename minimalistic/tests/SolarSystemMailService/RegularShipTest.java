package SolarSystemMailService;

import SolarSystemMailService.Station.PlanetaryStation;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static SolarSystemMailService.Station.PlanetaryStation.TemperatureClass.Cold;
import static SolarSystemMailService.Station.PlanetaryStation.TemperatureClass.Hot;
import static SolarSystemMailService.Station.PlanetaryStation.TemperatureClass.Normal;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RegularShipTest {

    private Ship ship;
    private PlanetaryStation station = mock(PlanetaryStation.class);
    private PlanetaryStation hotStation = mock(PlanetaryStation.class);
    private PlanetaryStation scannerStation = mock(PlanetaryStation.class);
    private SolarMailPackage packageToRegularStation = new SolarMailPackage(6, this.scannerStation, this.station);
    private SolarMailPackage packageToHotStation = new SolarMailPackage(12, this.station, this.hotStation);

    @Rule
    public ExpectedException exp = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        this.ship = new RegularShip();
        when(this.station.getTempClass()).thenReturn(Normal);
        when(this.hotStation.getTempClass()).thenReturn(Hot);
        when(this.scannerStation.getTempClass()).thenReturn(Normal);
    }

    @Test
    public void testCanDeliver() throws Exception {
        packageToRegularStation = new SolarMailPackage(0, this.scannerStation, this.station);
        assertTrue(this.ship.canDeliver().test(packageToRegularStation));
        packageToHotStation = new SolarMailPackage(0, this.station, this.hotStation);
        assertFalse(this.ship.canDeliver().test(packageToHotStation));
    }

    @Test
    public void testAddAndRemoveCargoAndBrowse() throws Exception {
        Assume.assumeTrue(this.ship.browse().count() == 0);
        this.ship.addCargo(packageToHotStation);
        assertEquals(1, this.ship.browse().count());
        this.ship.addCargo(packageToRegularStation);
        assertEquals(2, this.ship.browse().count());
        this.ship.removeCargo(packageToHotStation);
        assertEquals(1, this.ship.browse().count());
    }

    @Test
    public void testRefuel() throws Exception {
        this.ship.launch(this.station);
        this.ship.launch(this.station);
        this.ship.launch(this.station);
        this.ship.launch(this.station);
        this.ship.launch(this.station);
        this.ship.refuel();
        this.ship.launch(this.station);
    }

    @Test
    public void testGetRemainingSpace() throws Exception {
        assertEquals(100, this.ship.getRemainingSpace());
        this.ship.addCargo(this.packageToHotStation);
        assertEquals(100 - 12, this.ship.getRemainingSpace());
        this.ship.addCargo(this.packageToRegularStation);
        assertEquals(100-12-6, this.ship.getRemainingSpace());
    }

    @Test
    public void testLaunch() throws Exception {
        this.ship.launch(this.station);
        this.ship.launch(this.station);
        this.ship.launch(this.station);
        this.ship.launch(this.station);
        this.ship.launch(this.station);
        exp.expect(RuntimeException.class);
        exp.expectMessage("Ship cannot launch, not enough fuel");
        this.ship.launch(this.station);
    }

    @Test
    public void testGetFuelCostToLaunch() throws Exception {
        assertEquals(20, this.ship.getFuelCostToLaunch(Normal));
        assertTrue(100 < this.ship.getFuelCostToLaunch(Hot));
        assertTrue(100 < this.ship.getFuelCostToLaunch(Cold));
    }

    @Test
    public void testScanner() throws Exception {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                this.ship.launch(this.station);
                assertFalse(this.ship.needsNewScanner());
            }
            this.ship.refuel();
        }
        for (int i = 0; i < 2; i++) {
            this.ship.launch(this.station);
            assertFalse(this.ship.needsNewScanner());
        }
        for (int i = 0; i < 3; i++) {
            this.ship.launch(this.station);
            assertTrue(this.ship.needsNewScanner());
        }
        this.ship.refuel();
        try {
            this.ship.launch(this.station);
            fail("Expected ship to throw an error because the scanner is broken");
        } catch(RuntimeException e){
            assertEquals("Ship cannot launch, the scanner would not survive", e.getMessage());
            this.ship.installNewScanner();
            assertFalse(this.ship.needsNewScanner());
            this.ship.launch(this.station);
        }
    }

}