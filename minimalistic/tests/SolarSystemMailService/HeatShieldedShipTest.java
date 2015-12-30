package SolarSystemMailService;

import SolarSystemMailService.Ship.HeatShieldedShip;
import SolarSystemMailService.Station.HotPlanetaryStation;
import SolarSystemMailService.Station.PlanetaryStation;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class HeatShieldedShipTest {

    private HeatShieldedShip ship;
    private HotPlanetaryStation hotStation = new HotPlanetaryStation("test");
    private PlanetaryStation station = new PlanetaryStation("test");

    @Before
    public void setUp() throws Exception {
        this.ship = new HeatShieldedShip();
    }

    @Rule
    public ExpectedException exp = ExpectedException.none();

    @Test
    public void testCARGO_CAPACITY() throws Exception {
        ship.addCargo(new SolarMailPackage(70, null, null));
        assertEquals(10, ship.getRemainingSpace());
    }

    @Test
    public void testHotStationFuelCost() throws Exception {
        this.ship.launch(hotStation);
        this.ship.launch(hotStation);
        exp.expect(RuntimeException.class);
        exp.expectMessage("Ship cannot launch, not enough fuel");
        this.ship.launch(hotStation);
    }
    @Test
    public void testNormalStationFuelCost() throws Exception {
        for (int i = 0; i < 4; i++) {
            this.ship.launch(station);
        }
        exp.expect(RuntimeException.class);
        exp.expectMessage("Ship cannot launch, not enough fuel");
        this.ship.launch(station);
    }
}