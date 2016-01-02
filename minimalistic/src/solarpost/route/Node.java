package solarpost.route;

import solarpost.station.AbstractPostOffice;

/**
 * This is essentially a linked list node, but specifically for {@link AbstractPostOffice}s,
 * and without convenience methods, just public fields
 */
public class Node {
    public Node next;
    public Node last;
    public AbstractPostOffice postOffice;

    public Node(AbstractPostOffice postOffice) {
        this.postOffice = postOffice;
    }
}
