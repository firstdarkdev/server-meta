package dev.firstdark.servermeta.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.firstdark.servermeta.MetaServer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author HypherionSA
 * Helper Class for working with Files and Data
 */
public class FileUtils {

    public static final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    public static String getJson(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        if (!url.endsWith(".sha1")) {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return response.body().string();
                }
            } catch (IOException e) {
                MetaServer.LOGGER.error("Failed to read minecraft meta", e);
            }
        }
        return "";
    }

    public static long getFileSizeFromURL(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Get the content length from the Content-Length header
                long contentLength = connection.getContentLengthLong();
                connection.disconnect();
                return contentLength;
            } else {
                // Handle other HTTP response codes
                MetaServer.LOGGER.error("HTTP request failed with response code: {} - {}", responseCode, url);
                connection.disconnect();
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getSha1(String urlll) {
        String i = null;
        try {
            URL url = new URL(urlll);
            BufferedReader read = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            i = read.readLine();
            read.close();
        } catch (Exception ignored) {}
        return i == null ? "Unknown" : i;
    }

}
