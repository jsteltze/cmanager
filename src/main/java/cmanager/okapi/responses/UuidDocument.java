package cmanager.okapi.responses;

/**
 * Container for the UUID of a specific user.
 *
 * @see <a href="https://www.opencaching.de/okapi/services/users/user.html">OKAPI documentation</a>
 */
public class UuidDocument {

    /** ID of the user. */
    private String uuid;

    /**
     * Get the ID of the user.
     *
     * @return The ID of the user.
     */
    public String getUuid() {
        return uuid;
    }
}
