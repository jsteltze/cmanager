package cmanager.okapi.responses;

import cmanager.geo.Coordinate;
import cmanager.geo.Coordinate.UnparsableException;
import com.google.gson.annotations.SerializedName;

/**
 * Container for the home location of a specific user.
 *
 * @see <a href="https://www.opencaching.de/okapi/services/users/user.html">OKAPI documentation</a>
 */
public class HomeLocationDocument {

    /**
     * Home location of the user in the "latitude|longitude" format where latitude and longitude are
     * in full degrees with a dot as a decimal point. Will be null if no home location is given in
     * the user's Opencaching profile.
     */
    @SerializedName("home_location")
    private String homeLocation;

    /**
     * Get the home location of the user.
     *
     * @return The home location of the user or null if it is not set.
     */
    public String getHomeLocation() {
        return homeLocation;
    }

    /**
     * Get the home location of the user as a coordinate.
     *
     * @return The home location of the user or null if it is not set.
     * @throws UnparsableException The string cannot be parsed.
     */
    public Coordinate getHomeLocationAsCoordinate() throws UnparsableException {
        // Skip not set home locations.
        if (homeLocation == null) {
            return null;
        }

        // Split into latitude and longitude.
        // Abort if these two are not available.
        final String[] parts = homeLocation.split("\\|");
        if (parts.length != 2) {
            System.out.println("Could not split coordinate parts correctly.");
            throw new UnparsableException();
        }

        // Perform the conversion itself.
        return new Coordinate(parts[0], parts[1]);
    }
}
