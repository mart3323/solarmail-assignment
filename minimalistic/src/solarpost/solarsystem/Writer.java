package solarpost.solarsystem;

import solarpost.interfaces.station.IPostOffice;
import solarpost.station.AbstractPostOffice;

import java.util.ArrayList;
import java.util.List;

public class Writer extends Thread{
    List<IPostOffice> dests;
    private int toWrite = 2000;

    public Writer(ArrayList<IPostOffice> dests) {
        this.dests = new ArrayList<>(dests);
    }


    @Override
    public void run() {
        try {
            while(this.toWrite > 0) {
                IPostOffice source = getRandomSource();
                IPostOffice destination = getRandomDest(source);
                int weight = getRandomWeight();
                source.addPackage(destination, weight);
                Thread.sleep(3);
                this.toWrite -= 1;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public IPostOffice getRandomSource() {
        return dests.get(randomIntInRange(dests.size()));
    }
    public int getRandomWeight() {
        return randomIntInRange(80);
    }

    public IPostOffice getRandomDest(IPostOffice source) {
        dests.remove(source);
        final IPostOffice target = this.getRandomSource();
        dests.add(source);
        return target;
    }

    private int randomIntInRange(int range) {
        return (int) Math.floor(range * Math.random());
    }

    public int getToWrite() {
        return this.toWrite;
    }
}
