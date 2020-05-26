package cmanager.okapi.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Container for the details of a specific geocache.
 *
 * @see <a href="https://www.opencaching.de/okapi/services/caches/geocache.html">OKAPI
 *     documentation</a>
 */
public class CacheDetailsDocument {

    /**
     * Container for the geocache owner information.
     *
     * @see <a href="https://www.opencaching.de/okapi/services/users/user.html">OKAPI
     *     documentation</a>
     */
    private static class Owner {

        /** ID of the user. */
        String uuid;

        /** Username (login) of the user. */
        String username;

        /** URL of the user's Opencaching profile page. */
        @SerializedName("profile_url")
        String profileUrl;
    }

    /** String indicating th esize of the container. */
    String size2;

    /** User fields. */
    Owner owner;

    /** A plaintext string with a single line (very short) description of the cache. */
    String short_description;

    /** HTML string, description of the cache. Includes some attribution notice. */
    String description;

    /** Plain-text string, cache hints/spoilers. */
    String hint2;

    /** State if this cache requires a password in order to submit a "Found it" log entry. */
    @SerializedName("req_password")
    Boolean requiresPassword;

    /**
     * Get the name of the user.
     *
     * @return The name of the user.
     */
    public String getOwnerUsername() {
        return owner.username;
    }

    /**
     * Get the cache size.
     *
     * @return The cache size.
     */
    public String getSize() {
        return size2;
    }

    /**
     * Get the owner instance.
     *
     * @return The owner instance.
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * Get the short description.
     *
     * @return The short description.
     */
    public String getShortDescription() {
        return short_description;
    }

    /**
     * Get the (full) description.
     *
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the hint.
     *
     * @return The hint.
     */
    public String getHint() {
        return hint2;
    }

    /**
     * Get the password requirement status.
     *
     * @return Whether a password is required or not.
     */
    public Boolean doesRequirePassword() {
        return requiresPassword;
    }
}
