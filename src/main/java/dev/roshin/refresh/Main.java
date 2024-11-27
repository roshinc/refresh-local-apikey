package dev.roshin.refresh;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        // Load configuration from properties file
        Properties config = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            config.load(input);
        }

        // Get APP_TYPE from command-line arguments or default to 'EMPIRE'
        String APP_TYPE = args.length > 0 ? args[0] : "EMPIRE";
        logger.info("APP_TYPE: {}", APP_TYPE);

        // Initialize Gson instance
        Gson gson = new GsonBuilder().create();

        List<App> appList;

        // Create a shared HttpClient instance
        try (HttpClient client = HttpClient.newHttpClient()) {

            // Read URL from properties
            String configServiceUrl = config.getProperty("config.service.url") + APP_TYPE;

            // Prepare the initial POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(configServiceUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            // Send the initial POST request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("Initial POST response status code: {}", response.statusCode());

            // Check if the response is successful
            if (response.statusCode() != 200) {
                logger.error("Failed to retrieve app list. Exiting.");
                return;
            }

            // Parse the response to get the appList
            String responseBody = response.body();
            System.out.println("Initial POST response body: " + responseBody);

            // Define the type for deserialization
            Type responseType = new TypeToken<AppListResponse>() {
            }.getType();
            AppListResponse appListResponse = gson.fromJson(responseBody, responseType);
            appList = appListResponse.appList;

            if (appList == null || appList.isEmpty()) {
                logger.error("No apps found for APP_TYPE: {}", APP_TYPE);
                return;
            }

            // Print the list of apps to be processed
            logger.info("Apps to be processed:");
            for (App app : appList) {
                logger.info("- {}", app.getAppName());
            }

            // Basic auth credentials from properties
            String username = config.getProperty("auth.username");
            String password = config.getProperty("auth.password");
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String authHeaderValue = "Basic " + encodedAuth;

            // Multithreading: Process each app in its own thread
            appList.forEach(app -> {
                new Thread(() -> {
                    try {
                        processApp(app, APP_TYPE, client, gson, config, authHeaderValue);
                    } catch (Exception e) {
                        logger.error("Error processing app {}: {}", app.getAppName(), e.getMessage());
                        e.printStackTrace();
                    }
                }).start();
            });
        }

    }

    private static void processApp(App app, String APP_TYPE, HttpClient client, Gson gson, Properties config, String authHeaderValue) throws Exception {
        // Generate a unique imageId using appName and current timestamp
        String imageId = "LOCALTEST_" + app.getAppName() + "_" + System.currentTimeMillis();

        // Prepare the payload for steps 1 and 2
        AppPayload appPayload = new AppPayload(app.getAppName(), APP_TYPE, imageId);
        String jsonBody = gson.toJson(appPayload);

        // Step 1: Create Snapshot
        String createSnapshotUrl = config.getProperty("authutil.createSnapshot.url");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(createSnapshotUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", authHeaderValue)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        logger.info("[{}] Step 1 response: {}", app.getAppName(), response.body());

        // Step 2: Get Snapshot Data
        String getSnapshotDataUrl = config.getProperty("authutil.getSnapshotData.url");
        request = HttpRequest.newBuilder()
                .uri(URI.create(getSnapshotDataUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", authHeaderValue)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String rolesBase64 = response.body();
        logger.info("[{}] Step 2 response (rolesBase64): {}", app.getAppName(), rolesBase64);

        // Step 3: Get API Key for Local
        ApiKeyRequest apiKeyRequest = new ApiKeyRequest(app.getAppName(), imageId, APP_TYPE, rolesBase64);
        jsonBody = gson.toJson(apiKeyRequest);

        String apiKeyForLocalUrl = config.getProperty("authservice.apiKeyForLocal.url");
        request = HttpRequest.newBuilder()
                .uri(URI.create(apiKeyForLocalUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", authHeaderValue)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        logger.info("[{}] Step 3 response: {}", app.getAppName(), response.body());
    }
}


// Wrapper class for the response containing appList
class AppListResponse {
    List<App> appList;

    public AppListResponse() {
    }
}




