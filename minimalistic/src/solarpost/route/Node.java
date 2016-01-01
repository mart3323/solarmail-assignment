package solarpost.route;

import solarpost.station.station.AbstractPostOffice;

public class Node {
    public Node next;
    public Node last;
    public AbstractPostOffice postOffice;

    public Node(AbstractPostOffice postOffice) {
        this.postOffice = postOffice;
    }
}
