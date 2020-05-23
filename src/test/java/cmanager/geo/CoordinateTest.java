package cmanager.geo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import cmanager.geo.Coordinate.UnparsableException;
import org.junit.Test;

public class CoordinateTest {

    @Test
    public void test() {
        final Coordinate c1 = new Coordinate("1.23", "4.56");
        assertEquals(1.23, c1.getLat(), 0.0);
        assertEquals(4.56, c1.getLon(), 0.0);

        final Coordinate c2 = new Coordinate(1.23, 4.56);
        assertEquals(1.23, c2.getLat(), 0.0);
        assertEquals(4.56, c2.getLon(), 0.0);
        assertEquals("1.23, 4.56", c2.toString());

        assertTrue(c1.equals(c2));
        assertEquals(0, c1.distanceHaversine(c2), 0);

        final Coordinate c3 = new Coordinate(1.23, 4.567);
        assertFalse(c1.equals(c3));
        assertEquals(778.1851, c1.distanceHaversine(c3), 0.00009);
        assertEquals(778.185, c1.distanceHaversineRounded(c3), 0);
    }

    @Test
    public void testDistance() {
        final Coordinate c1 = new Coordinate(53.09780, 8.74908);
        final Coordinate c2 = new Coordinate(53.05735, 8.59148);
        assertEquals(11448.0325, c1.distanceHaversine(c2), 0.0009);
    }

    private void parse(String string, double lat, double lon) {
        try {
            final Coordinate c = new Coordinate(string);
            assertEquals(lat, c.getLat(), 0.0);
            assertEquals(lon, c.getLon(), 0.00009);
        } catch (UnparsableException e) {
            fail(e.getStackTrace().toString());
        }
    }

    private void parse(String string) {
        parse(string, 53.1073, 8.12945);
    }

    @Test
    public void testParsing() {
        parse(" N 53° 06.438' E 008° 07.767' (WGS84)");
        parse("  N53° 06.438' E 008° 07.767' (WGS84)");
        parse("N 53°06.438' E 008° 07.767' (WGS84)");
        parse("N 53 06.438' E 008° 07.767'");
        parse("N 53 06.438 E 008° 07.767' (WGS84)");
        parse("N 53 06.438 E 008 07.767' (WGS84)");
        parse("N 53 06.438E008 07.767' (WGS84)");
        parse("N 53 06E008 07' (WGS84)", 53.1, 8.1166);
        parse("    N 53° 06.438' E 008° 07.767' ");
    }
}
