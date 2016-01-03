package solarpost.code.route;

import solarpost.interfaces.station.IPostOffice;

public class Route {
    /**
     * Construct a circular linked list of {@link Node}s in the order given, with the last connected to the first
     * @param stations stations to make {@link Node}s of
     * @return the first Node of the circular linked list
     */
    public static Node create(IPostOffice... stations){
        Node[] nodes = new Node[stations.length];
        for (int i = 0; i < stations.length; i++) {
            nodes[i] = new Node(stations[i]);
        }
        for (int i = 0; i < nodes.length - 1; i++) {
            nodes[i].next = nodes[i+1];
            nodes[i+1].last = nodes[i];
        }
        nodes[0].last = nodes[nodes.length-1];
        nodes[nodes.length-1].next = nodes[0];
        return nodes[0];
    }
}
