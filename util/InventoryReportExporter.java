package util;

import java.io.IOException;
import java.util.List;
import model.Item;

public class InventoryReportExporter extends ReportExporter {

    @Override
    protected void writeHeader() {
        try {
            writer.write("Item ID,Item Code,Item Name,Quantity\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void writeData(List<?> data) {
        List<Item> items = (List<Item>) data;
        try {
            for (Item item : items) {
                writer.write(String.format("%d,%s,%s,%d\n",
                        item.getItemId(), item.getItemCode(), item.getItemName(), item.getQuantity()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
