package SolarSystemMailService.Ship;

import SolarSystemMailService.SolarMailPackage;
import SolarSystemMailService.Station.PlanetaryStation;
import SolarSystemMailService.Station.PlanetaryStation.TemperatureClass;

import java.util.function.Predicate;

import static SolarSystemMailService.Station.PlanetaryStation.TemperatureClass.Hot;

public class HeatShieldedShip extends RegularShip{

    @Override
    protected int getCargoCapacity(){ return 80;}

    @Override
    public int getFuelCostToLaunch(TemperatureClass tempClass) {
        switch(tempClass){
            case Hot:
                return 50;
            case Normal:
                return 25;
            default:
                return Integer.MAX_VALUE;
        }
    }

    @Override
    public Predicate<SolarMailPackage> canDeliver(){
        return p -> p.destination.getTempClass() == Hot || p.source.getTempClass() == Hot;
    }

    @Override
    public boolean canLandAt(PlanetaryStation station) {
        return station.getTempClass() == Hot;
    }
}
