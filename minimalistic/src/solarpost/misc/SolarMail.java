package solarpost.misc;
import solarpost.interfaces.station.IPostOffice;
import jdk.nashorn.internal.ir.annotations.Immutable;
import solarpost.station.AbstractPostOffice;

/**
 * An immutable struct representing a single item of solar mail
 * <br>Properties:
 * <ol>
 * <li>this.source {@link AbstractPostOffice}
 * <li>this.target {@link AbstractPostOffice}
 * <li>this.weight <a href>int</a>
 * </ol>
 * <img src="https://starmadepedia.net/images/e/ec/Cargo_Space_2.png" />
 */
@Immutable
public class SolarMail {
    public final IPostOffice source;
    public final IPostOffice target;
    public final int weight;

    public SolarMail(IPostOffice source, IPostOffice target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }
}
