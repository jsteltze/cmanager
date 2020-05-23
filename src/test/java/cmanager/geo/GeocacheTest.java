package cmanager.geo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import cmanager.util.ObjectHelper;
import org.junit.Test;

public class GeocacheTest {

    @Test
    public void testConstructor() {

        new Geocache("OC1234", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");

        try {
            new Geocache(null, "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");
            fail("Exception expected");
        } catch (NullPointerException e) {
        }

        try {
            new Geocache("OC1234", null, new Coordinate(0, 0), 0.0, 0.0, "Tradi");
            fail("Exception expected");
        } catch (NullPointerException e) {
        }

        try {
            new Geocache("OC1234", "test", null, 0.0, 0.0, "Tradi");
            fail("Exception expected");
        } catch (NullPointerException e) {
        }

        try {
            new Geocache("OC1234", "test", new Coordinate(0, 0), null, 0.0, "Tradi");
            fail("Exception expected");
        } catch (NullPointerException e) {
        }

        try {
            new Geocache("OC1234", "test", new Coordinate(0, 0), 0.0, null, "Tradi");
            fail("Exception expected");
        } catch (NullPointerException e) {
        }

        try {
            new Geocache("OC1234", "test", new Coordinate(0, 0), 0.0, 0.0, null);
            fail("Exception expected");
        } catch (NullPointerException e) {
        }
    }

    @Test
    public void testDataInterpretation() {
        Geocache g;
        g = new Geocache("OC1234", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");
        assertTrue(g.isOC());
        assertFalse(g.isGC());
        assertFalse(g.hasVolatileStart());
        assertEquals("https://www.opencaching.de/OC1234", g.getURL());

        g = new Geocache("GC1234", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");
        assertTrue(g.isGC());
        assertFalse(g.isOC());
        assertFalse(g.hasVolatileStart());
        assertEquals("https://www.geocaching.com/geocache/GC1234", g.getURL());

        g = new Geocache("OC1234", "test", new Coordinate(0, 0), 0.0, 0.0, "Mystery");
        assertTrue(g.hasVolatileStart());
    }

    @Test
    public void testSerialize() {
        final Geocache g = new Geocache("OC1234", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");
        final Geocache g2 = ObjectHelper.copy(g);
        assertNotNull(g2);
    }
}
