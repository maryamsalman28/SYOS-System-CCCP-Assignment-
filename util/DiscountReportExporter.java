package util;

import java.io.IOException;
import java.util.List;
import model.Discount;

public class DiscountReportExporter extends ReportExporter {

    @Override
    protected void writeHeader() {
        try {
            writer.write("Discount ID,Discount Code,Description,Percentage,Start Date,End Date\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void writeData(List<?> data) {
        List<Discount> discounts = (List<Discount>) data;
        try {
            for (Discount discount : discounts) {
                writer.write(String.format("%d,%s,%s,%.2f%%,%s,%s\n",
                        discount.getDiscountId(), discount.getDiscountCode(),
                        discount.getDescription(), discount.getDiscountPercentage(),
                        discount.getStartDate(), discount.getEndDate()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
