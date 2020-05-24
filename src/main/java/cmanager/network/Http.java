package cmanager.network;

import cmanager.global.Constants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Http {

    public static String get(String url) throws Exception {
        ConnectException connectException;

        int count = 0;
        do {
            try {
                return getInternal(url);
            } catch (ConnectException e) {
                connectException = e;
            }
        } while (++count < 3);

        throw connectException;
    }

    // HTTP GET request
    private static String getInternal(String url) throws UnexpectedStatusCode, IOException {
        final URL obj = new URL(url);
        final HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Optional default is GET.
        con.setRequestMethod("GET");

        // Add request header.
        con.setRequestProperty("User-Agent", Constants.HTTP_USER_AGENT);

        BufferedReader bufferedReader;
        try {
            bufferedReader =
                    new BufferedReader(
                            new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException exception) {
            bufferedReader =
                    new BufferedReader(
                            new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8));
        }

        String inputLine;
        final StringBuilder response = new StringBuilder();
        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();

        final int statusCode = con.getResponseCode();
        if (statusCode != 200) {
            throw new UnexpectedStatusCode(statusCode, response.toString());
        }

        return response.toString();
    }
}
