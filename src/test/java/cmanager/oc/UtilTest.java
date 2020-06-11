package cmanager.oc;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import cmanager.CacheListModel;
import cmanager.geo.Coordinate;
import cmanager.geo.Geocache;

public class UtilTest {
    
    @Test
    public void findCacheOnOc() throws Throwable {
        /*
         * This unit test is more of an entry point for debugging than it is for
         * automated tests.
         */
        
        AtomicBoolean ab = new AtomicBoolean(false);
        
        Geocache GC1QKNW = new Geocache("GC1QKNW", "Rund um Schloss Burgk", new Coordinate("N51°00.213 E013°39.941"), 1.0, 2.0, "Multi");
        Geocache GC1PMYR = new Geocache("GC1PMYR", "Tharandter Wald - Das Bellmanns Los", new Coordinate("N50°57.744 E013°34.788"), 2.0, 2.5, "Tradi");
        Geocache GC297DD = new Geocache("GC297DD", "Pack die Badehose ein", new Coordinate("N50°56.966 E013°30.523"), 1.5, 3.0, "Multi");
        
        CacheListModel clm = new CacheListModel();
        clm.addCaches(Arrays.asList(GC1QKNW, GC1PMYR, GC297DD));
        
        Util.findOnOc(ab, clm, null, null, null, null);
        
        /*
         * Expected OC-Caches:
         *   GC1QKNW <-> OCF919
         *   GC1PMYR <-> OC976B
         *   GC297DD <-> OCA87B
         */
    }
}
