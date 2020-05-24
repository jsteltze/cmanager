package cmanager.okapi.responses;

import com.google.gson.annotations.SerializedName;

public class CacheDetailsDocument {

    private static class Owner {

        String uuid;
        String username;

        @SerializedName("profile_url")
        String profileUrl;
    }

    String size2;
    Owner owner;
    String short_description;
    String description;
    String hint2;

    @SerializedName("req_password")
    Boolean requiresPassword;

    public String getOwnerUuid() {
        return owner.uuid;
    }

    public String getOwnerUsername() {
        return owner.username;
    }

    public String getOwnerProfileUrl() {
        return owner.profileUrl;
    }

    public String getSize2() {
        return size2;
    }

    public Owner getOwner() {
        return owner;
    }

    public String getShort_description() {
        return short_description;
    }

    public String getDescription() {
        return description;
    }

    public String getHint2() {
        return hint2;
    }

    public Boolean doesRequirePassword() {
        return requiresPassword;
    }
}
