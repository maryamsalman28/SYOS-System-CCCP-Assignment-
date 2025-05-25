package test;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestClient {
    public static void main(String[] args) {
        int numberOfClients = 10;

        ExecutorService executor = Executors.newFixedThreadPool(numberOfClients);

        for (int i = 0; i < numberOfClients; i++) {
            final int userId = 100 + i;
            executor.submit(() -> {
                try {
                    URL url = new URL("http://localhost:8080/generateReport");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    String payload = "userId=" + userId;
                    OutputStream os = conn.getOutputStream();
                    os.write(payload.getBytes());
                    os.flush();
                    os.close();

                    int responseCode = conn.getResponseCode();
                    System.out.println("User " + userId + " => Response Code: " + responseCode);

                    conn.disconnect();
                } catch (Exception e) {
                    System.err.println("Error for user " + userId + ": " + e.getMessage());
                }
            });
        }

        executor.shutdown();
    }
}

