package SolarSystemMail.Station;

import SolarSystemMail.Exceptions.NotDockedException;
import SolarSystemMail.IMailOrderPackage;
import SolarSystemMail.IStarShip;

import java.util.HashSet;
import java.util.stream.Stream;

public interface ICargoHold {
    /**
     * Moves a package from this hold to the hold specified
     * @param ship the hold to move the package to
     * @param pckg the package to move
     * @throws NotDockedException if the requesting ship is not docked
     */
    void moveTo(ICargoHold ship, IMailOrderPackage pckg) throws NotDockedException;
    /** Returns a stream of all packages that are available for pickup */
    Stream<IMailOrderPackage> browsePackages();
}
