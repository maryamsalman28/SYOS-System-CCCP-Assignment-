package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public abstract class ReportExporter {
    protected FileWriter writer;

    // Export report using the template method pattern
    public final void exportReport(List<?> data, String filePath) {
        try {
            writer = new FileWriter(filePath);
            writeHeader();
            writeData(data);
            writer.close();
            System.out.println("Report successfully exported to " + filePath);
        } catch (IOException e) {
            System.out.println("Failed to export the report.");
            e.printStackTrace();
        }
    }

    // Abstract methods to be implemented by subclasses
    protected abstract void writeHeader();
    protected abstract void writeData(List<?> data);
}
