package util;

public class ReportFactory {
    public static ReportExporter getReportExporter(String reportType) {
        switch (reportType.toLowerCase()) {
            case "inventory":
                return new InventoryReportExporter();
            case "reorder":
                return new ReorderReportExporter();
            case "discount":
                return new DiscountReportExporter();
            default:
                throw new IllegalArgumentException("Invalid report type: " + reportType);
        }
    }
}
