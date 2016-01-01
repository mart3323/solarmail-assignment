package solarpost.misc;
import jdk.nashorn.internal.ir.annotations.Immutable;
import solarpost.station.station.AbstractPostOffice;
import solarpost.station.station.PostOffice;

@Immutable
public class SolarMail {
    public final AbstractPostOffice source;
    public final AbstractPostOffice target;
    public final int weight;

    public SolarMail(AbstractPostOffice source, AbstractPostOffice target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }
}
