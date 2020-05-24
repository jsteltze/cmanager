package cmanager.okapi.responses;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class LogSubmissionDocument {

    private Boolean success;
    private String message;

    @SerializedName("log_uuid")
    private String logUuid;

    @SerializedName("log_uuids")
    private List<String> logUuids;

    public Boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getLogUuid() {
        return logUuid;
    }
}
