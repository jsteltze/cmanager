package cmanager.okapi.responses;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

/**
 * Container for errors.
 *
 * @see <a href="https://www.opencaching.de/okapi/introduction.html#errors">OKAPI documentation</a>
 */
public class ErrorDocument {

    /**
     * Container for error messages.
     *
     * @see <a href="https://www.opencaching.de/okapi/introduction.html#errors">OKAPI
     *     documentation</a>
     */
    private static class Error {

        /** Description of the error. */
        @SerializedName("developer_message")
        String developerMessage;

        /**
         * A list of keywords which depicts the exception's position in the exception class
         * hierarchy.
         */
        @SerializedName("reason_stack")
        ArrayList<String> reasonStack;

        /** HTTP status code. */
        int status;

        /** Problematic parameter. */
        String parameter;

        /** The problem with the parameter. */
        @SerializedName("whats_wrong_about_it")
        String whatsWrongAboutIt;

        /** URL pointing to a more detailed description of the error. */
        @SerializedName("more_info")
        String moreInfo;
    }

    /** The error instance. */
    private Error error;

    /**
     * Get the description.
     *
     * @return The description.
     */
    public String getDeveloperMessage() {
        return error.developerMessage;
    }

    /**
     * Get the reason stack.
     *
     * @return The reason stack.
     */
    public ArrayList<String> getReasonStack() {
        return error.reasonStack;
    }

    /**
     * Get the HTTP status code.
     *
     * @return The status code.
     */
    public int getStatus() {
        return error.status;
    }

    /**
     * Get the offending parameter.
     *
     * @return The name of the offending parameter.
     */
    public String getParameter() {
        return error.parameter;
    }

    /**
     * Get some information on what is wrong about the parameter.
     *
     * @return What is wrong about the parameter?
     */
    public String getWhatsWrongAboutIt() {
        return error.whatsWrongAboutIt;
    }

    /**
     * Get the URL where to find more information on this error.
     *
     * @return The URL with more information on this problem.
     */
    public String getMoreInfo() {
        return error.moreInfo;
    }
}
