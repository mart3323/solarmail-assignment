package solarpost.station;

import static solarpost.station.AbstractPostOffice.TempClass.Hot;

public class HotPostOffice extends PostOffice{
    public HotPostOffice(String name) {
        super(name);
    }

    @Override public TempClass getTempClass() { return Hot; }
}
