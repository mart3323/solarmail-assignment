package SolarSystemMail.Station;

import SolarSystemMail.IMailOrderPackage;

import java.util.HashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface IPostalService extends ICargoHold{
    /** Returns the number of packages that have been sent to this station */
    int getNumReceivedPackages();
    /** Returns the number of packages that have been sent to this station that match the given predicate */
    int getNumReceivedPackages(Predicate<IMailOrderPackage> filter);
    /** Returns the total weight of all packages that have been sent to this station */
    int getTotalWeightOfPackagesReceived();
    /** Returns the average weight of all packages that have been sent to this station */
    int getAverageWeightOfPackagesReceived();
    /** Returns a mapping from all planetary stations that have sent mail here, to the number of packages sent*/
    HashMap<IPlanetaryStation, Integer> getNumberOfPackagesReceivedByPlanet();
}
