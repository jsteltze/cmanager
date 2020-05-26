package cmanager.okapi.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Container for the found status of a specific geocache.
 *
 * @see <a href="https://www.opencaching.de/okapi/services/caches/geocache.html">OKAPI
 *     documentation</a>
 */
public class FoundStatusDocument {

    /** Whether the user has already found this cache. */
    @SerializedName("is_found")
    private boolean found;

    /**
     * Check whether the user has already found this cache.
     *
     * @return Whether the user has already found this cache.
     */
    public boolean isFound() {
        return found;
    }
}
