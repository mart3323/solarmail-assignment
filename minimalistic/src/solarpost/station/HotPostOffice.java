package solarpost.station;

import static solarpost.interfaces.station.IPostOffice.TempClass.Hot;

/**
 * A variant of the regular post office stationed on a hot planet.., which limits the types of ships that can land here
 * @see PostOffice
 */
public class HotPostOffice extends PostOffice{
    public HotPostOffice(String name) {
        super(name);
    }

    @Override public TempClass getTempClass() { return Hot; }
}
