package cmanager.okapi.responses;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class ErrorDocument {

    private static class Error {

        @SerializedName("developer_message")
        String developerMessage;

        @SerializedName("reason_stack")
        ArrayList<String> reasonStack;

        int status;
        String parameter;

        @SerializedName("whats_wrong_about_it")
        String whatsWrongAboutIt;

        @SerializedName("more_info")
        String moreInfo;
    }

    private Error error;

    public String getDeveloperMessage() {
        return error.developerMessage;
    }

    public ArrayList<String> getReasonStack() {
        return error.reasonStack;
    }

    public int getStatus() {
        return error.status;
    }

    public String getParameter() {
        return error.parameter;
    }

    public String getWhatsWrongAboutIt() {
        return error.whatsWrongAboutIt;
    }

    public String getMoreInfo() {
        return error.moreInfo;
    }
}
