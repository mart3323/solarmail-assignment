package SolarSystemMailService.Station;

import SolarSystemMailService.Ship;
import SolarSystemMailService.SolarMailPackage;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static SolarSystemMailService.Station.PlanetaryStation.TemperatureClass.Normal;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PlanetaryStationTest {

    PlanetaryStation station = new PlanetaryStation();
    PlanetaryStation station2 = new PlanetaryStation();
    PlanetaryStation station3 = new PlanetaryStation();
    Ship ship = mock(Ship.class);
    private SolarMailPackage[] shipPackages = new SolarMailPackage[]{
            new SolarMailPackage(12, station2, station),
            new SolarMailPackage(1, station, station2),
            new SolarMailPackage(6, station3, station),
            new SolarMailPackage(3, station3, station2)};
    private SolarMailPackage[] stationPackages = new SolarMailPackage[]{
            new SolarMailPackage(3, station, station3),
            new SolarMailPackage(2, station, station3),
            new SolarMailPackage(4, station, station2),
            new SolarMailPackage(16, station, station2),
            new SolarMailPackage(8, station, station2),
            new SolarMailPackage(70, station, station2),
    };

    @Before
    public void setUp() throws Exception {
        when(ship.browse()).thenAnswer(invocation -> Stream.of(shipPackages));
        when(ship.canDeliver()).thenReturn((p) -> true);
        when(ship.getRemainingSpace()).thenReturn(50);
        Collections.addAll(this.station.outbox, stationPackages);
    }

    @Test
    public void testGetTempClass() throws Exception {
        assertEquals(Normal, this.station.getTempClass());
    }

    @Test
    public void testDockAndTrade() throws Exception {
        this.station.dockAndTrade(this.ship);
        verify(ship, atLeastOnce()).browse();
        verify(ship).removeCargo(shipPackages[0]);
        verify(ship).removeCargo(shipPackages[2]);
        verify(ship).addCargo(stationPackages[3]);
        verify(ship).addCargo(stationPackages[4]);
        verify(ship).addCargo(stationPackages[2]);
        verify(ship).addCargo(stationPackages[0]);
        verify(ship).addCargo(stationPackages[1]);
        verify(ship, never()).addCargo(stationPackages[5]);
        verify(ship).refuel();
    }
}