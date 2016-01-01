package solarpost.station.station;

import static solarpost.station.station.AbstractPostOffice.TempClass.Hot;

public class HotPostOffice extends PostOffice{
    public HotPostOffice(String name) {
        super(name);
    }

    @Override public TempClass getTempClass() { return Hot; }
}
