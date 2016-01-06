package solarpost.code.solarsystem;

import solarpost.interfaces.station.IPostOffice;

import java.util.ArrayList;
import java.util.List;

public class Writer extends Thread{
    public static final int MS_PER_PACKAGE = 3;
    private final List<IPostOffice> dests;
    private int toWrite = 2000;

    public Writer(ArrayList<? extends IPostOffice> dests) {
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
                Thread.sleep(MS_PER_PACKAGE);
                this.toWrite -= 1;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private IPostOffice getRandomSource() {
        return dests.get(randomIntInRange(dests.size()));
    }
    private int getRandomWeight() {
        return randomIntInRange(80);
    }

    private IPostOffice getRandomDest(IPostOffice source) {
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
