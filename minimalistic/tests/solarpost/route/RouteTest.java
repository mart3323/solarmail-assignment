package solarpost.route;

import org.junit.Test;
import solarpost.station.AbstractPostOffice;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class RouteTest {

    final AbstractPostOffice p1 = mock(AbstractPostOffice.class);
    final AbstractPostOffice p2 = mock(AbstractPostOffice.class);
    final AbstractPostOffice p3 = mock(AbstractPostOffice.class);
    final AbstractPostOffice p4 = mock(AbstractPostOffice.class);
    final Node n1= Route.create(p1, p2, p3, p4);
    final Node n2 = n1.next;
    final Node n3 = n1.next.next;
    final Node n4 = n1.next.next.next;
    @Test
    public void testIsProperlyLinked() throws Exception {
        // n1-n4 are already defined in terms of .next, check if .last matches
        assertEquals(n1, n1);
        assertEquals(n4, n1.last);
        assertEquals(n3, n1.last.last);
        assertEquals(n2, n1.last.last.last);
        assertEquals(n1, n1.last.last.last.last);
    }
    @Test
    public void testHasCorrectOffices() throws Exception {
        assertEquals(p1, n1.postOffice);
        assertEquals(p2, n2.postOffice);
        assertEquals(p3, n3.postOffice);
        assertEquals(p4, n4.postOffice);
        assertEquals(p1, n1.postOffice);
    }
}