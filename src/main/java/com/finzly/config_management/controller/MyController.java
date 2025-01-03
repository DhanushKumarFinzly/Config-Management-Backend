package com.finzly.config_management.controller;

import com.finzly.config_management.Repository.ApplicationRepo;
import com.finzly.config_management.model.Application;
import com.finzly.config_management.service.ConfigurationService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;

@RestController
@RequestMapping("/api")  // This maps all endpoints starting with /api
public class MyController {

    @Autowired
    ConfigurationService configurationService;

    @Autowired
    ApplicationRepo applicationRepo;


    static boolean isFirstCall=true;

    public static void disableSslVerification() throws NoSuchAlgorithmException, KeyManagementException {
        // Disable SSL verification
        TrustManager[] trustAllCertificates = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCertificates, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true); // Bypass hostname verification
    }

    @GetMapping("/compare-environments/{env1}/{env2}")
    public ResponseEntity<List<Map<String, Object>>> compareEnvironments(@PathVariable String env1,@PathVariable String env2) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        disableSslVerification();

        // Fetch all applications from the repository
        List<Application> applications = applicationRepo.findAll();
        List<Map<String, Object>> allComparisonResults = new ArrayList<>();
        Map<String, Object> combinedSource1 = new HashMap<>();
        Map<String, Object> combinedSource2 = new HashMap<>();


        for (Application app : applications) {
            String appName = app.getAppName();
            // Construct API URLs
            String api1Url = "https://finzly-eks-bankos-dev2-lb-396975821.us-east-2.elb.amazonaws.com/config/" + appName + "/" + env1 + "/master";
                Map<String, Object> api1CombinedSource = fetchAndCombineSources(api1Url,isFirstCall, appName, env1);
                 combinedSource1.putAll(api1CombinedSource);
        }
        isFirstCall=true;
        for (Application app : applications) {
            String appName = app.getAppName();
            String api2Url = "https://finzly-eks-bankos-dev-lb-1562127541.us-east-2.elb.amazonaws.com/config/" + appName + "/" + env2;
            Map<String, Object> api2CombinedSource = fetchAndCombineSources(api2Url,isFirstCall, appName, env2);
            combinedSource2.putAll(api2CombinedSource);


        }
             try {   // Compare environments
                 List<Map<String, Object>> comparisonResult = configurationService.envComparison(combinedSource1, combinedSource2);

                 // Collect results
                 allComparisonResults.addAll(comparisonResult);

            } catch (Exception e) {
                e.printStackTrace();
                // Log error and continue with next application

            }


        // Return all comparison results
        return ResponseEntity.ok(allComparisonResults);
    }

    private Map<String, Object> fetchAndCombineSources(String apiUrl,boolean isFirstCall,String application,String env) throws IOException {
        String username = "config"; // Replace with your username
        String password = "swaps123"; // Replace with your password
        Map<String, Object> combinedSource = new HashMap<>();

        // Fetch data from the API
        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestMethod("GET");

        System.setProperty("java.net.useSystemProxies", "true");
        String encodedAuth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        connection.setRequestProperty("Authorization", "Basic " + encodedAuth);

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        // Parse the JSON response
        JSONObject responseJson = new JSONObject(response.toString());
        JSONArray propertySources = responseJson.getJSONArray("propertySources");

        // Iterate over the propertySources and apply the conditions
        for (int i = 0; i < propertySources.length(); i++) {
            JSONObject propertySource = propertySources.getJSONObject(i);
            String sourceName = propertySource.getString("name");
                JSONObject source = propertySource.getJSONObject("source");
            Map<String, Object> sourceMap = source.toMap();


            if ((application+"-default").equals(sourceName)) {
                combinedSource.putAll(sourceMap);
            }

            // Add source3 only when name == "{application_name}-{envName}"
            if (isFirstCall && sourceName.equals("application"+env)) {
                combinedSource.putAll(sourceMap);
                isFirstCall=false;
            }
        }

        return combinedSource;
    }

}