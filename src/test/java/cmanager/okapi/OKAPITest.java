package cmanager.okapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import cmanager.geo.Coordinate;
import cmanager.geo.Geocache;
import cmanager.okapi.helper.TestClient;
import cmanager.okapi.helper.TestClientCredentials;
import java.util.ArrayList;
import org.junit.Ignore;
import org.junit.Test;

/** Tests for the OKAPI-based methods. */
public class OKAPITest {

    /** Test the conversion of a username to an UUID with a not existing user. */
    @Test
    public void testUsernameToUUIDNotExisting() throws Exception {
        final String uuid = OKAPI.usernameToUUID("This.User.Does.Not.Exist");
        assertNull(uuid);
    }

    /** Test the conversion of a username to an UUID with a valid user. */
    @Test
    public void testUsernameToUUIDValid() throws Exception {
        final String uuid = OKAPI.usernameToUUID("cmanagerTestÄccount");
        assertEquals("a912cccd-1c60-11e7-8e90-86c6a7325f31", uuid);
    }

    /** Test the cache getter with an invalid OC code. */
    @Test
    public void testGetCacheNotExisting() throws Exception {
        final Geocache g = OKAPI.getCache("This.Cache.Does.Not.Exist");
        assertNull(g);
    }

    /** Test the cache getter with a cache having no GC waypoint set. */
    @Test
    public void testGetCacheWithoutGc() throws Exception {
        final Geocache g = OKAPI.getCache("OC827D");
        assertNotNull(g);
        assertEquals("auftanken", g.getName());
        assertTrue(g.getCoordinate().equals(new Coordinate(49.955717, 8.332967)));
        assertEquals("Tradi", g.getType().asNiceType()); // OKAPI has no "Drive-In" type.
        assertNull(g.getCodeGC());
        assertEquals((Double) 1.0, g.getDifficulty(), (Double) 0.0);
        assertEquals((Double) 2.0, g.getTerrain(), (Double) 0.0);
        assertTrue(g.getArchived());
    }

    /** Test the cache getter with a cache having a GC waypoint set. */
    @Test
    public void testGetCacheWithGc() throws Exception {
        final Geocache g = OKAPI.getCache("OC11ECF");
        assertNotNull(g);
        assertEquals("Gehüpft wie gesprungen", g.getName());
        assertTrue(g.getCoordinate().equals(new Coordinate(53.019517, 8.5344)));
        assertEquals("Tradi", g.getType().asNiceType());
        assertEquals("GC46PY8", g.getCodeGC());
        assertEquals((Double) 2.0, g.getDifficulty(), (Double) 0.0);
        assertEquals((Double) 1.5, g.getTerrain(), (Double) 0.0);
        assertTrue(g.getArchived());
    }

    /** Test the cache details getter. */
    @Test
    public void testCompleteCacheDetailsExample1() throws Exception {
        Geocache g = OKAPI.getCache("OC827D");
        assertNotNull(g);
        g = OKAPI.completeCacheDetails(g);
        assertEquals("Nano", g.getContainer().asGC());
        assertEquals("following", g.getOwner());
        assertEquals("", g.getListing_short());

        // Adopt once http://redmine.opencaching.de/issues/1045 has beend done.
        final String expected =
                "<p>ein kleiner Drive-in für zwischendurch<br /><br />\nStift mitbringen!</p>\n<p><em>&copy; <a href='https://www.opencaching.de/viewprofile.php?userid=150360'>following</a>, <a href='https://www.opencaching.de/viewcache.php?cacheid=136478'>Opencaching.de</a>, <a href='https://creativecommons.org/licenses/by-nc-nd/3.0/de/'>CC-BY-NC-ND</a>, Stand: ";
        final String listing = g.getListing().trim().substring(0, expected.length());
        assertEquals(expected.length(), listing.length());
        assertEquals(expected, listing);

        assertEquals("<magnetisch>", g.getHint());
    }

    /** Test the cache details getter. */
    @Test
    public void testCompleteCacheDetailsExample2() throws Exception {
        Geocache g = OKAPI.getCache("OC11ECF");
        assertTrue(g != null);
        g = OKAPI.completeCacheDetails(g);
        assertEquals("Micro", g.getContainer().asGC());
        assertEquals("Samsung1", g.getOwner());
        assertEquals("", g.getListing_short());

        // Adopt once http://redmine.opencaching.de/issues/1045 has beend done.
        final String expected =
                "<p><span>In Erinnerung an die schöne Zeit, die ich hier als Teenager mit Pferden in diesem schönen Gelände verbringen durfte:<br />\nEin kleiner Cache für unterwegs, hoffentlich auch eine kleine Herausforderung für euch ;).<br /><br />\nViel Spaß und Erfolg wünschen Samsung1 und Oreas1987.</span></p>\n<p><em>&copy; <a href='https://www.opencaching.de/viewprofile.php?userid=316615'>Samsung1</a>, <a href='https://www.opencaching.de/viewcache.php?cacheid=176512'>Opencaching.de</a>, <a href='https://creativecommons.org/licenses/by-nc-nd/3.0/de/'>CC-BY-NC-ND</a>, Stand: ";
        final String listing = g.getListing().trim().substring(0, expected.length());
        assertEquals(expected.length(), listing.length());
        assertEquals(expected, listing);

        assertEquals("", g.getHint());
    }

    /** The test client instance to use. */
    private TestClient tc = null;

    /** Test that the client login works. */
    @Test
    public void testTestClientIsOkay() throws Exception {
        tc = new TestClient();
        final boolean loggedIn = tc.login();
        assertTrue(loggedIn);
    }

    /** Test that retrieving the OKAPI token works. */
    @Test
    public void testTestClientRequestToken() throws Exception {
        if (tc == null) {
            System.out.println("TestClient is unintialized. Initializing...");
            testTestClientIsOkay();
        }

        assertNotNull(tc.requestToken());
    }

    /** Make sure that the test client is logged in. Request the token if this is not the case. */
    private void assureTestClientIsLoggedIn() throws Exception {
        if (tc == null || tc.getOkapiToken() == null) {
            System.out.println("OKAPI token is unintialized. Fetching...");
            testTestClientRequestToken();
        }
    }

    /**
     * Test getting the caches around a given position. This does not use a user filter and just
     * checks that at least 3 matches are present.
     */
    @Test
    public void testGetCachesAroundBasic() throws Exception {
        assureTestClientIsLoggedIn();

        final ArrayList<Geocache> caches =
                OKAPI.getCachesAround(
                        null, null, 53.01952, 008.53440, 1.0, new ArrayList<Geocache>());
        assertTrue(caches.size() >= 3);
    }

    /**
     * Test getting the caches around a given position. This does not use a user filter, but checks
     * that one cache is present.
     */
    @Test
    public void testGetCachesAroundWithoutUserFilter() throws Exception {
        assureTestClientIsLoggedIn();

        final ArrayList<Geocache> caches =
                OKAPI.getCachesAround(
                        null, null, 00.21667, 000.61667, 1.0, new ArrayList<Geocache>());
        assertTrue(caches.size() >= 1);

        boolean containsCache = false;
        for (final Geocache g : caches) {
            if (g.toString()
                    .equals(
                            "1.0/5.0 OC13A45 (Tradi) -- 0.216667, 0.616667 -- cmanager TEST cache")) {
                containsCache = true;
                break;
            }
        }
        assertTrue(containsCache);
    }

    /**
     * Test getting the caches around a given position. This uses a user filter and checks that one
     * cache is present.
     */
    @Test
    public void testGetCachesAroundWithUserFilter() throws Exception {
        assureTestClientIsLoggedIn();

        final ArrayList<Geocache> caches =
                OKAPI.getCachesAround(
                        tc, OKAPI.getUUID(tc), 00.21667, 000.61667, 1.0, new ArrayList<Geocache>());

        boolean containsCache = false;
        for (final Geocache g : caches) {
            if (g.toString()
                    .equals(
                            "1.0/5.0 OC13A45 (Tradi) -- 0.216667, 0.616667 -- cmanager TEST cache")) {
                containsCache = true;
                break;
            }
        }
        assertTrue(containsCache);
        // The old version does not work as the used account has not logged this cache.
        // assertFalse(containsCache);
    }

    /**
     * Test logging the specified test cache.
     *
     * <p>This has been disabled to avoid spamming the production site.
     */
    @Test
    @Ignore
    public void testUpdateFoundStatusSuccess() throws Exception {
        assureTestClientIsLoggedIn();

        final Geocache g = new Geocache("OC13A45", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");
        assertNull(g.getIsFound());

        OKAPI.updateFoundStatus(tc, g);
        assertTrue(g.getIsFound());
    }

    /**
     * Test logging the specified test cache.
     *
     * <p>This has been disabled to avoid spamming the production site.
     */
    @Test
    @Ignore
    public void testUpdateFoundStatusFailure() throws Exception {
        assureTestClientIsLoggedIn();

        final Geocache g = new Geocache("OC0BEF", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");
        assertNull(g.getIsFound());

        // Logging should not be successful. TODO: Why?
        OKAPI.updateFoundStatus(tc, g);
        assertFalse(g.getIsFound());
    }

    /** Test getting the UUID of the test user. */
    @Test
    public void testGetUUID() throws Exception {
        assureTestClientIsLoggedIn();
        assertEquals("23fe0d6a-9cf3-11ea-8df9-d516ed642eb6", OKAPI.getUUID(tc));
    }

    /** Test getting the user name of the test user. */
    @Test
    public void testGetUsername() throws Exception {
        assureTestClientIsLoggedIn();
        assertEquals(TestClientCredentials.USERNAME, OKAPI.getUsername(tc));
    }
}
