package cmanager.okapi.responses;

import com.google.gson.annotations.SerializedName;

public class FoundStatusDocument {

    @SerializedName("is_found")
    private boolean found;

    public boolean isFound() {
        return found;
    }
}
