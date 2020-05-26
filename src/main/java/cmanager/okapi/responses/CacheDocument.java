package cmanager.okapi.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Container for the basic properties of a geocache.
 *
 * @see <a href="https://www.opencaching.de/okapi/services/caches/geocache.html">OKAPI
 *     documentation</a>
 */
public class CacheDocument {

    /** Unique Opencaching code of the geocache. */
    private String code;

    /** Name of the geocache. */
    private String name;

    /**
     * Location of the cache in the "latitude|longitude" format with latitude and longitude being in
     * full degrees with a dot as a decimal point.
     */
    private String location;

    /** Cache type. */
    private String type;

    /**
     * Geocaching.com code (GC code) of the geocache or null if the cache is not listed on GC or the
     * GC code is unknown. This information is supplied by the cache owner and may be missing,
     * obsolete or otherwise incorrect.
     */
    @SerializedName("gc_code")
    private String gcCode;

    /** Difficulty rating of the cache. */
    private Double difficulty;

    /** Terrain rating of the cache. */
    private Double terrain;

    /** Cache status, Can be "Available", "Temporarily unavailable" or "Archived". */
    private String status;

    /**
     * Get the OC code.
     *
     * @return The OC code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Get the name.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the location string.
     *
     * @return The location string.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Get the type.
     *
     * @return The type.
     */
    public String getType() {
        return type;
    }

    /**
     * Get the GC code.
     *
     * @return The GC code.
     */
    public String getGcCode() {
        return gcCode;
    }

    /**
     * Get the difficulty rating.
     *
     * @return The difficulty rating.
     */
    public Double getDifficulty() {
        return difficulty;
    }

    /**
     * Get the terrain rating.
     *
     * @return The terrain rating.
     */
    public Double getTerrain() {
        return terrain;
    }

    /**
     * Get the status string.
     *
     * @return The status string.
     */
    public String getStatus() {
        return status;
    }
}
