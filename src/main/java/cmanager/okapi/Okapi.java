package cmanager.okapi;

import cmanager.MalFormedException;
import cmanager.geo.Coordinate;
import cmanager.geo.Geocache;
import cmanager.geo.GeocacheLog;
import cmanager.global.Constants;
import cmanager.gui.ExceptionPanel;
import cmanager.network.ApacheHttp;
import cmanager.network.ApacheHttp.HttpResponse;
import cmanager.network.UnexpectedStatusCode;
import cmanager.okapi.responses.CacheDetailsDocument;
import cmanager.okapi.responses.CacheDocument;
import cmanager.okapi.responses.CachesAroundDocument;
import cmanager.okapi.responses.ErrorDocument;
import cmanager.okapi.responses.FoundStatusDocument;
import cmanager.okapi.responses.LogSubmissionDocument;
import cmanager.okapi.responses.UnexpectedLogStatus;
import cmanager.okapi.responses.UsernameDocument;
import cmanager.okapi.responses.UuidDocument;
import cmanager.xml.Element;
import cmanager.xml.Parser;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Okapi {

    private static final String CONSUMER_API_KEY = ConsumerKeys.get_CONSUMER_API_KEY();
    private static final String CONSUMER_SECRET_KEY = ConsumerKeys.get_CONSUMER_SECRET_KEY();
    private static final String BASE_URL = Constants.OKAPI_SERVICE_BASE;

    private static final ApacheHttp httpClient = new ApacheHttp();

    public static String usernameToUuid(String username) throws Exception {
        final String url =
                BASE_URL
                        + "/users/by_username"
                        + "?consumer_key="
                        + CONSUMER_API_KEY
                        + "&username="
                        + URLEncoder.encode(username, "UTF-8")
                        + "&fields=uuid";

        final HttpResponse response = httpClient.get(url);
        final String http = response.getBody();

        if (response.getStatusCode() != 200) {
            final ErrorDocument okapiError = new Gson().fromJson(http, ErrorDocument.class);
            if (okapiError.getParameter().equals("username")) {
                return null;
            } else {
                throw new UnexpectedStatusCode(response.getStatusCode(), http);
            }
        }

        final UuidDocument document = new Gson().fromJson(http, UuidDocument.class);
        return document.getUuid();
    }

    public static Geocache getCache(String code) throws Exception {
        final String url =
                BASE_URL
                        + "/caches/geocache"
                        + "?consumer_key="
                        + CONSUMER_API_KEY
                        + "&cache_code="
                        + code
                        + "&fields="
                        + URLEncoder.encode(
                                "code|name|location|type|gc_code|difficulty|terrain|status",
                                "UTF-8");

        final HttpResponse response = httpClient.get(url);
        final String http = response.getBody();

        if (response.getStatusCode() != 200) {
            final ErrorDocument okapiError = new Gson().fromJson(http, ErrorDocument.class);
            if (okapiError.getParameter().equals("cache_code")) {
                return null;
            } else {
                throw new UnexpectedStatusCode(response.getStatusCode(), http);
            }
        }

        final CacheDocument document = new Gson().fromJson(http, CacheDocument.class);
        if (document == null) {
            return null;
        }

        Coordinate coordinate = null;
        if (document.getLocation() != null) {
            final String[] parts = document.getLocation().split("\\|");
            coordinate = new Coordinate(parts[0], parts[1]);
        }

        final Geocache geocache =
                new Geocache(
                        code,
                        document.getName(),
                        coordinate,
                        document.getDifficulty(),
                        document.getTerrain(),
                        document.getType());
        geocache.setCodeGC(document.getGcCode());

        final String status = document.getStatus();
        if (status != null) {
            switch (status) {
                case "Archived":
                    geocache.setAvailable(false);
                    geocache.setArchived(true);
                    break;
                case "Temporarily unavailable":
                    geocache.setAvailable(false);
                    geocache.setArchived(false);
                    break;
                case "Available":
                    geocache.setAvailable(true);
                    geocache.setArchived(false);
                    break;
            }
        }

        return geocache;
    }

    public static Geocache getCacheBuffered(String code, List<Geocache> okapiRuntimeCache)
            throws Exception {
        synchronized (okapiRuntimeCache) {
            final int index = Collections.binarySearch(okapiRuntimeCache, code);
            if (index >= 0) {
                return okapiRuntimeCache.get(index);
            }
        }

        final Geocache geocache = getCache(code);
        if (geocache != null) {
            synchronized (okapiRuntimeCache) {
                okapiRuntimeCache.add(geocache);
                okapiRuntimeCache.sort(Comparator.comparing(Geocache::getCode));
            }
        }
        return geocache;
    }

    public static Geocache completeCacheDetails(Geocache geocache) throws Exception {
        final String url =
                BASE_URL
                        + "/caches/geocache"
                        + "?consumer_key="
                        + CONSUMER_API_KEY
                        + "&cache_code="
                        + geocache.getCode()
                        + "&fields="
                        + URLEncoder.encode(
                                "size2|short_description|description|owner|hint2|req_passwd",
                                "UTF-8");

        final HttpResponse response = httpClient.get(url);
        final String http = response.getBody();

        if (response.getStatusCode() != 200) {
            final ErrorDocument okapiError = new Gson().fromJson(http, ErrorDocument.class);
            if (okapiError.getParameter().equals("cache_code")) {
                return null;
            } else {
                throw new UnexpectedStatusCode(response.getStatusCode(), http);
            }
        }

        final CacheDetailsDocument document = new Gson().fromJson(http, CacheDetailsDocument.class);

        geocache.setContainer(document.getSize2());
        geocache.setListingShort(document.getShort_description());
        geocache.setListing(document.getDescription());
        geocache.setOwner(document.getOwnerUsername());
        geocache.setHint(document.getHint2());
        geocache.setRequiresPassword(document.doesRequirePassword());

        return geocache;
    }

    public interface RequestAuthorizationCallbackI {
        void redirectUrlToUser(String authUrl);

        String getPin();
    }

    private static OAuth10aService getOAuthService() {
        return new ServiceBuilder(CONSUMER_API_KEY)
                .apiSecret(CONSUMER_SECRET_KEY)
                .build(new OAuth());
    }

    public static OAuth1AccessToken requestAuthorization(RequestAuthorizationCallbackI callback)
            throws IOException, InterruptedException, ExecutionException {
        // Step One: Create the OAuthService object
        final OAuth10aService service = getOAuthService();

        // Step Two: Get the request token
        final OAuth1RequestToken requestToken = service.getRequestToken();

        // Step Three: Making the user validate your request token
        final String authUrl = service.getAuthorizationUrl(requestToken);
        callback.redirectUrlToUser(authUrl);

        final String pin = callback.getPin();
        if (pin == null) {
            return null;
        }

        // Step Four: Get the access Token
        return service.getAccessToken(requestToken, pin);
    }

    public interface TokenProviderI {
        OAuth1AccessToken getOkapiToken();
    }

    private static String authedHttpGet(final TokenProviderI tokenProvider, final String url)
            throws InterruptedException, ExecutionException, IOException {
        final OAuth10aService service = getOAuthService();
        final OAuthRequest request = new OAuthRequest(Verb.GET, url);
        service.signRequest(tokenProvider.getOkapiToken(), request); // the access token from step 4
        final Response response = service.execute(request);
        return response.getBody();
    }

    public static List<Geocache> getCachesAround(
            TokenProviderI tokenProvider,
            String excludeUuid,
            Geocache geocache,
            double searchRadius,
            List<Geocache> okapiRuntimeCache)
            throws Exception {
        final Coordinate coordinate = geocache.getCoordinate();
        return getCachesAround(
                tokenProvider,
                excludeUuid,
                coordinate.getLatitude(),
                coordinate.getLongitude(),
                searchRadius,
                okapiRuntimeCache);
    }

    public static List<Geocache> getCachesAround(
            TokenProviderI tokenProvider,
            String excludeUuid,
            Double latitude,
            Double longitude,
            Double searchRadius,
            List<Geocache> okapiCacheDetailsCache)
            throws Exception {
        final boolean useOAuth = tokenProvider != null && excludeUuid != null;
        final String url =
                BASE_URL
                        + "/caches/search/nearest"
                        + "?consumer_key="
                        + CONSUMER_API_KEY
                        + "&center="
                        + URLEncoder.encode(
                                latitude.toString() + "|" + longitude.toString(), "UTF-8")
                        + "&radius="
                        + searchRadius.toString()
                        + "&status="
                        + URLEncoder.encode("Available|Temporarily unavailable|Archived", "UTF-8")
                        + "&limit=500"
                        + (useOAuth ? "&ignored_status=notignored_only" : "")
                        + (useOAuth ? "&not_found_by=" + excludeUuid : "");

        String http;
        if (useOAuth) {
            http = authedHttpGet(tokenProvider, url);
        } else {
            final HttpResponse response = httpClient.get(url);
            http = response.getBody();

            if (response.getStatusCode() != 200) {
                throw new UnexpectedStatusCode(response.getStatusCode(), http);
            }
        }

        final CachesAroundDocument document = new Gson().fromJson(http, CachesAroundDocument.class);
        if (document == null) {
            return null;
        }

        final List<Geocache> caches = new ArrayList<>();
        for (final String code : document.getResults()) {
            try {
                final Geocache geocache = getCacheBuffered(code, okapiCacheDetailsCache);
                if (geocache != null) {
                    caches.add(geocache);
                }
            } catch (MalFormedException exception) {
                ExceptionPanel.display(exception);
            }
        }
        return caches;
    }

    public static void updateFoundStatus(TokenProviderI tokenProvider, Geocache oc)
            throws IOException, InterruptedException, ExecutionException {
        if (tokenProvider == null) {
            return;
        }

        final String url =
                BASE_URL
                        + "/caches/geocache"
                        + "?consumer_key="
                        + CONSUMER_API_KEY
                        + "&cache_code="
                        + oc.getCode()
                        + "&fields=is_found";
        final String http = authedHttpGet(tokenProvider, url);

        final FoundStatusDocument document = new Gson().fromJson(http, FoundStatusDocument.class);
        oc.setIsFound(document.isFound());
    }

    public static String getUuid(TokenProviderI tokenProvider)
            throws IOException, InterruptedException, ExecutionException {
        final String url = BASE_URL + "/users/user" + "?fields=uuid";
        final String http = authedHttpGet(tokenProvider, url);

        final UuidDocument document = new Gson().fromJson(http, UuidDocument.class);
        if (document == null) {
            return null;
        }
        return document.getUuid();
    }

    public static String getUsername(TokenProviderI tokenProvider)
            throws IOException, InterruptedException, ExecutionException {
        final String url = BASE_URL + "/users/user" + "?fields=username";
        final String http = authedHttpGet(tokenProvider, url);

        final UsernameDocument document = new Gson().fromJson(http, UsernameDocument.class);
        if (document == null) {
            return null;
        }
        return document.getUsername();
    }

    public static void postLog(TokenProviderI tp, Geocache cache, GeocacheLog log)
            throws InterruptedException, ExecutionException, IOException, UnexpectedLogStatus {
        String url =
                BASE_URL
                        + "/logs/submit"
                        + "?format=json"
                        + "&cache_code="
                        + URLEncoder.encode(cache.getCode(), "UTF-8")
                        + "&logtype="
                        + URLEncoder.encode(log.getOkapiType(cache), "UTF-8")
                        + "&comment="
                        + URLEncoder.encode(log.getText(), "UTF-8")
                        + "&when="
                        + URLEncoder.encode(log.getDateStrIso8601NoTime(), "UTF-8");

        /*if (cache.doesRequirePassword()) {
            url += "&password=" + URLEncoder.encode(log.getPassword(), "UTF-8");
        }*/

        // System.out.println(url);

        final String response = authedHttpGet(tp, url);

        // TODO: We might want to handle another problem here as well, although this should not be
        // this common.
        // Currently this is done by catching the NPE below
        // {"error":{"developer_message":"Parameter 'logtype' has invalid value: 'Webcam Photo
        // Taken' in not a valid logtype
        // code.","reason_stack":["bad_request","invalid_parameter"],"status":400,"parameter":"logtype","whats_wrong_about_it":"'Webcam Photo Taken' in not a valid logtype code.","more_info":"https:\/\/www.opencaching.de\/okapi\/introduction.html#errors"}}

        // Retrieve the response document.
        final LogSubmissionDocument document =
                new Gson().fromJson(response, LogSubmissionDocument.class);

        // The document itself is null.
        if (document == null) {
            throw new NullPointerException(
                    "Problems with handling posted log. Response document is null.");
        }

        // Check success status.
        try {
            if (!document.isSuccess()) {
                throw new UnexpectedLogStatus(document.getMessage());
            }
        } catch (NullPointerException exception) {
            // When the OKAPI reports a request error, this will lead to a NPE.
            System.out.println(response);
            throw new NullPointerException(
                    "Could not submit log. Please open an issue for this and add the terminal output to it.");
        }
    }

    public static Coordinate getHomeCoordinates(TokenProviderI tp)
            throws MalFormedException, IOException, InterruptedException, ExecutionException {
        final String uuid = getUuid(tp);

        final String url =
                BASE_URL
                        + "/users/user"
                        + "?format=xmlmap2"
                        + "&fields=home_location"
                        + "&user_uuid="
                        + uuid;
        final String http = authedHttpGet(tp, url);

        // <object><string key="home_location">53.047117|9.608</string></object>
        final Element root = Parser.parse(http);
        for (final Element element : root.getChild("object").getChildren()) {
            if (element.attrIs("key", "home_location")) {
                final String[] parts = element.getUnescapedBody().split("\\|");
                return new Coordinate(parts[0], parts[1]);
            }
        }

        return null;
    }
}
