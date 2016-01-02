package solarpost.solarsystem;

import solarpost.misc.SolarMail;
import solarpost.route.Node;
import solarpost.ship.CargoShip;
import solarpost.station.ScannerPostOffice;

public class AutoPilot extends Thread{
    public static final int FLIGHT_TIME_MS = 15;
    public static final int SCANNER_CHANGE_THRESHOLD = 10;
    public final String name;
    public final CargoShip ship;
    private Node route;


    public AutoPilot(String name, CargoShip ship, Node route) {
        this.name = name;
        this.ship = ship;
        this.route = route;
    }

    @Override
    public void run() {
        try {
            while(!interrupted()){
                this.ship.autobuyScanner = needScanner();
                this.ship.dockAt(this.route.postOffice, this::packageIsOnRoute);
                selectNextDestination();
                Thread.sleep(FLIGHT_TIME_MS);
            }
        } catch (InterruptedException ignored) {
        }
    }

    private void selectNextDestination() {
        if(needScanner()){
            do {
                this.route = this.route.next;
            } while(!(this.route.postOffice instanceof ScannerPostOffice));
        } else {
            this.route = this.route.next;
        }
    }

    private boolean needScanner() {
        return this.ship.getScannerDurability() < SCANNER_CHANGE_THRESHOLD;
    }

    private boolean packageIsOnRoute(SolarMail pckg){
        Node first = this.route;
        Node node = this.route;
        do{
            if(node.postOffice == pckg.target){
                return true;
            }
            node = node.next;
        }while(node != first);
        return false;
    }

}
