package cmanager.okapi.responses;

/**
 * Container for the name of a specific user.
 *
 * @see <a href="https://www.opencaching.de/okapi/services/users/user.html">OKAPI documentation</a>
 */
public class UsernameDocument {

    /** Username (login) of the user. */
    private String username;

    /**
     * Get the name of the user.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }
}
