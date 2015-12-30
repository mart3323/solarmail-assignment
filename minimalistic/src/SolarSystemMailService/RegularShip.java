package SolarSystemMailService;

import SolarSystemMailService.Station.PlanetaryStation;
import SolarSystemMailService.Station.PlanetaryStation.TemperatureClass;
import com.sun.istack.internal.NotNull;
import net.jcip.annotations.ThreadSafe;

import java.util.HashSet;
import java.util.function.Predicate;
import java.util.stream.Stream;

@ThreadSafe
public class RegularShip implements Ship {

    private static final int FULL_PERCENT = 100;

    private static final int CARGO_CAPACITY = 100;
    private static final int FUEL_COST_TO_LAUNCH = 20;
    private static final int SCANNER_WEAR_ON_LAUNCH = 4;
    /** The durability threshold above which new scanners should not be bought! */
    private static final int SCANNER_PURCHASE_THRESHOLD = 10;

    private int fuel = FULL_PERCENT;
    private int scannerDurability = FULL_PERCENT;
    final HashSet<SolarMailPackage> cargo = new HashSet<>();

    @Override
    public Predicate<SolarMailPackage> canDeliver(){
        return p -> this.getFuelCostToLaunch(p.destination.getTempClass()) < FULL_PERCENT;
    }

    @Override
    public void removeCargo(@NotNull SolarMailPackage pckg) {
        synchronized (this.cargo){
            this.cargo.remove(pckg);
        }
    }

    @Override
    public void addCargo(@NotNull SolarMailPackage pckg) {
        synchronized (this.cargo){
            this.cargo.add(pckg);
        }
    }

    @Override
    public Stream<SolarMailPackage> browse() {
        synchronized (this.cargo) {
            return this.cargo.stream();
        }
    }

    @Override
    public void refuel() {
        this.fuel = FULL_PERCENT;
    }

    @Override
    public int getRemainingSpace() {
        synchronized (this.cargo) {
            return CARGO_CAPACITY - this.browse().mapToInt(p -> p.weight).sum();
        }
    }

    @Override
    public void launch(PlanetaryStation from) {
        final double fuelCost = this.getFuelCostToLaunch(from.getTempClass());
        if(this.fuel < fuelCost){
            throw new RuntimeException("Ship cannot launch, not enough fuel");
        }
        if(this.scannerDurability < SCANNER_WEAR_ON_LAUNCH){
            throw new RuntimeException("Ship cannot launch, the scanner would not survive");
        }
        this.fuel -= fuelCost;
        this.scannerDurability -= SCANNER_WEAR_ON_LAUNCH;
    }

    @Override
    public int getFuelCostToLaunch(TemperatureClass tempClass) {
        switch(tempClass){
            case Normal:
                return 20;
            default:
                return Integer.MAX_VALUE;
        }
    }

    @Override
    public void installNewScanner(){
        this.scannerDurability = FULL_PERCENT;
    }

    @Override
    public boolean needsNewScanner() {
        return this.scannerDurability < SCANNER_PURCHASE_THRESHOLD;
    }
}
