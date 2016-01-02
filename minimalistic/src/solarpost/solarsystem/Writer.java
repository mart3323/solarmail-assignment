package solarpost.solarsystem;

import solarpost.station.AbstractPostOffice;

import java.util.ArrayList;
import java.util.List;

public class Writer extends Thread{
    List<AbstractPostOffice> dests;
    private int toWrite = 2000;

    public Writer(ArrayList<AbstractPostOffice> dests) {
        this.dests = (ArrayList<AbstractPostOffice>)dests.clone();
    }


    @Override
    public void run() {
        try {
            while(this.toWrite > 0) {
                AbstractPostOffice source = getRandomSource();
                AbstractPostOffice destination = getRandomDest(source);
                int weight = getRandomWeight();
                source.addPackage(destination, weight);
                Thread.sleep(3);
                this.toWrite -= 1;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public AbstractPostOffice getRandomSource() {
        return dests.get((int)Math.floor(dests.size()*Math.random()));
    }
    public int getRandomWeight() {
        return (int)Math.floor(Math.random()*80);
    }

    public AbstractPostOffice getRandomDest(AbstractPostOffice source) {
        dests.remove(source);
        final AbstractPostOffice target = dests.get((int) Math.floor(dests.size() * Math.random()));
        dests.add(source);
        return target;
    }

    public int getToWrite() {
        return this.toWrite;
    }
}
