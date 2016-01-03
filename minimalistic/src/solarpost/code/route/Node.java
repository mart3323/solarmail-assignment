package solarpost.code.route;

import solarpost.code.station.AbstractPostOffice;
import solarpost.interfaces.station.IPostOffice;

/**
 * This is essentially a linked list node, but specifically for {@link AbstractPostOffice}s,
 * and without convenience methods, just public fields
 */
public class Node {
    public Node next;
    public Node last;
    public IPostOffice postOffice;

    public Node(IPostOffice postOffice) {
        this.postOffice = postOffice;
    }
}
