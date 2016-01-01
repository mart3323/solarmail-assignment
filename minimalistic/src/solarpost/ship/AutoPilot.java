package solarpost.ship;

import solarpost.misc.SolarMail;
import solarpost.route.Node;
import solarpost.ship.ship.AbstractShip;
import solarpost.station.station.ScannerPostOffice;

public class AutoPilot extends Thread{
    public static final int FLIGHT_TIME_MS = 15;
    public final String name;
    public final AbstractShip ship;
    private Node route;


    public AutoPilot(String name, AbstractShip ship, Node route) {
        this.name = name;
        this.ship = ship;
        this.route = route;
    }

    @Override
    public void run() {
        try {
            while(!interrupted()){
                this.ship.dockAt(this.route.postOffice, this::packageIsOnRoute);
                selectNextDestination();
                Thread.sleep(FLIGHT_TIME_MS);
            }
        } catch (InterruptedException ignored) {
        }
    }

    private void selectNextDestination() {
        if(this.ship.needsScanner()){
            do {
                this.route = this.route.next;
            } while(!(this.route.postOffice instanceof ScannerPostOffice));
        } else {
            this.route = this.route.next;
        }
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
