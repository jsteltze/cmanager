package cmanager.oc;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import cmanager.CacheListModel;
import cmanager.geo.Coordinate;
import cmanager.geo.Geocache;

public class UtilTest {
    
    /*
     * TODO weitere testen: OC976B OCA87B
     */
    
    @Test
    public void findCacheOnOc() throws Throwable {
        
        // OCF919 <-> GC1QKNW
        AtomicBoolean ab = new AtomicBoolean(false);
        
        CacheListModel clm = new CacheListModel();
        clm.addCaches(Arrays.asList(new Geocache("GC1QKNW", "Rund um Schloss Burgk", new Coordinate("N51°00.213 E013°39.941"), 1.0, 2.0, "Multi")));
        
        Util.findOnOc(ab, clm, null, null, null, null);
    }
}
