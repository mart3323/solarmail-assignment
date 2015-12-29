package SolarSystemMail;

import SolarSystemMail.Station.IPlanetaryStation;

public interface IMailOrderPackage {
    /**
     * Get the Planetary station this package was sent from
     */
    IPlanetaryStation getReturnDestination();

    /**
     * Get the Planetary station this package needs to get to
     */
    IPlanetaryStation getDestination();

    /**
     * Get the weight of this package in kilograms
     */
    int getWeight();
}
