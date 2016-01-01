package solarpost.station;

import solarpost.station.station.AbstractPostOffice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Writer extends Thread{
    AbstractPostOffice target;
    List<AbstractPostOffice> dests;

    public Writer(AbstractPostOffice target, ArrayList<AbstractPostOffice> dests) {
        this.target = target;
        this.dests = (ArrayList<AbstractPostOffice>)dests.clone();
        this.dests.remove(target);
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 25; i++) {
                target.addPackage(this.getRandomDest(), this.getRandomWeight());
                Thread.sleep(2);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getRandomWeight() {
        return (int)Math.floor(Math.random()*80);
    }

    public AbstractPostOffice getRandomDest() {
        return dests.get((int)Math.floor(dests.size()*Math.random()));
    }
}
