package solarpost.route;

import solarpost.station.station.AbstractPostOffice;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Route {
    public static Node create(AbstractPostOffice... stations){
        Node[] nodes = new Node[stations.length];
        for (int i = 0; i < stations.length; i++) {
            nodes[i] = new Node(stations[i]);
        }
        for (int i = 0; i < nodes.length - 1; i++) {
            Node node = nodes[i];
            nodes[i].next = nodes[i+1];
            nodes[i+1].last = nodes[i];
        }
        nodes[0].last = nodes[nodes.length-1];
        nodes[nodes.length-1].next = nodes[0];
        return nodes[0];
    }
}
