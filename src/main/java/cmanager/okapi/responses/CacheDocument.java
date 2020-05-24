package cmanager.okapi.responses;

import com.google.gson.annotations.SerializedName;

public class CacheDocument {

    private String code;
    private String name;
    private String location;
    private String type;

    @SerializedName("gc_code")
    private String gcCode;

    private Double difficulty;
    private Double terrain;
    private String status;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public String getGcCode() {
        return gcCode;
    }

    public Double getDifficulty() {
        return difficulty;
    }

    public Double getTerrain() {
        return terrain;
    }

    public String getStatus() {
        return status;
    }
}
