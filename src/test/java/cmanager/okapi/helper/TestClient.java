package cmanager.okapi.helper;

import cmanager.network.ApacheHTTP;
import cmanager.network.UnexpectedStatusCode;
import cmanager.okapi.OKAPI;
import cmanager.okapi.OKAPI.RequestAuthorizationCallbackI;
import cmanager.okapi.OKAPI.TokenProviderI;
import com.github.scribejava.core.model.OAuth1AccessToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class TestClient implements TokenProviderI {

    private final ApacheHTTP http = new ApacheHTTP();
    private OAuth1AccessToken token = null;

    public boolean login() throws UnexpectedStatusCode, IOException {
        final List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("email", TestClientCredentials.USERNAME));
        nvps.add(new BasicNameValuePair("password", TestClientCredentials.PASSWORD));
        nvps.add(new BasicNameValuePair("action", "login"));
        http.post("https://www.opencaching.de/login.php", nvps);

        final String response = http.get("https://www.opencaching.de/index.php").getBody();
        return response.contains(TestClientCredentials.USERNAME);
    }

    private String extractParameter(final String url, final String parameter) {
        final String parameters = url.split("\\?")[1];
        final String[] pairs = parameters.split("&");
        for (final String pair : pairs) {
            final String[] split = pair.split("=");
            if (split[0].equals(parameter)) {
                return split[1];
            }
        }
        return "";
    }

    public OAuth1AccessToken requestToken()
            throws IOException, InterruptedException, ExecutionException {
        final OAuth1AccessToken token =
                OKAPI.requestAuthorization(
                        new RequestAuthorizationCallbackI() {
                            private String pin = null;

                            @Override
                            public void redirectUrlToUser(String authUrl) {
                                final String oauth_token = extractParameter(authUrl, "oauth_token");
                                final String url =
                                        "https://www.opencaching.de/okapi/apps/authorize?interactivity=minimal&oauth_token="
                                                + oauth_token;

                                // Open the authorization page.
                                String response = null;
                                try {
                                    response = http.get(url).getBody();
                                } catch (IOException e) {
                                }

                                // If this application has not been authorized before, we will get a
                                // form where we have to accept that the application will have
                                // access to our account.
                                // Previously this assumed that the application has been registered
                                // manually beforehand, which in fact should nearly always be the
                                // case. So we are sending the required POST request here manually
                                // if needed.
                                if (response.contains(
                                        "<form id='authform' method='POST' class='form'>")) {
                                    final List<NameValuePair> parameters = new ArrayList<>(3);
                                    parameters.add(
                                            new BasicNameValuePair("interactivity", "minimal"));
                                    parameters.add(
                                            new BasicNameValuePair("oauth_token", oauth_token));
                                    parameters.add(
                                            new BasicNameValuePair(
                                                    "authorization_result", "granted"));
                                    try {
                                        response = http.post(url, parameters).getBody();
                                    } catch (IOException | UnexpectedStatusCode e) {
                                    }
                                }

                                // Retrieve the PIN value from the webpage.
                                final Matcher matcher =
                                        Pattern.compile("<div class=\\'pin\\'>(\\d*)<\\/div>")
                                                .matcher(response);
                                matcher.find();
                                pin = matcher.group(1);
                            }

                            @Override
                            public String getPin() {
                                return pin;
                            }
                        });

        this.token = token;
        return token;
    }

    @Override
    public OAuth1AccessToken getOkapiToken() {
        return token;
    }
}
