package solarpost.ship;

import solarpost.route.Node;
import solarpost.ship.ship.AbstractShip;

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
                this.route.postOffice.dockTradeAndLaunch(this.ship);
                this.route = this.route.next;
                Thread.sleep(FLIGHT_TIME_MS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
