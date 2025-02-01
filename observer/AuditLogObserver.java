package observer;

import java.io.FileWriter;
import java.io.IOException;

public class AuditLogObserver implements Observer {

    private final String logFilePath;

    public AuditLogObserver(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    @Override
    public void update(String message) {
        try (FileWriter writer = new FileWriter(logFilePath, true)) {
            writer.write(message + "\n");
            System.out.println("Logged message: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
