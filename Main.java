// File: Main.java
import dao.BillDAO;
import dao.DiscountDAO;
import dao.ItemDAO;
import dao.MultiStoreDAO;
import dao.ReportDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.Bill;
import model.Discount;
import model.Item;
import model.MultiStore;
import model.ShelfItem;
import service.DiscountCalculator;
import service.DiscountService;
import service.FlatDiscountStrategy;
import service.ItemService;
import service.MultiStoreService;
import service.PercentageDiscountStrategy;
import service.ReportServiceTest;
import service.TieredDiscountStrategy;
import service.UserService;
import util.DatabaseConnection;
import util.ReportExporter;
import util.ReportFactory;


public class Main {
    public static void main(String[] args) throws SQLException {
        // DAO and Service Initialization
        ItemDAO itemDAO = new ItemDAO();
        BillDAO billDAO = new BillDAO();
        MultiStoreDAO multiStoreDAO = new MultiStoreDAO();
        DiscountDAO discountDAO = new DiscountDAO();
        ReportDAO reportDAO = new ReportDAO();

        System.out.println("Testing database connection...");
        itemDAO.testConnection();

        ItemService itemService = new ItemService();
        MultiStoreService multiStoreService = new MultiStoreService();
        DiscountService discountService = new DiscountService();
        UserService userService = new UserService();
        ReportServiceTest reportService = new ReportServiceTest();
        Scanner scanner = new Scanner(System.in);
        

        while (true) {
            System.out.println("\n--- SYOS STORE ---");
            System.out.println("1.  Process customer purchase and generate bill");
            System.out.println("\n--- Manage items ---");
            System.out.println("2.  Add item");
            System.out.println("3.  View items");
            System.out.println("4.  Reorder batch items");
            System.out.println("5.  Delete item");
            System.out.println("6.  Manage stores");
            System.out.println("7.  Manage discounts");
            System.out.println("\n--- Reports ---");
            System.out.println("8.  Generate daily sales report");
            System.out.println("9.  Generate reorder report (Low stock)");
            System.out.println("10. View inventory stock levels");
            System.out.println("11. Generate shelf items report");
            System.out.println("12. Generate batch-wise stock levels report");
            System.out.println("13. Generate complete bill history report");
            System.out.println("\n--- Export reports ---");
            System.out.println("14. Export inventory report");
            System.out.println("15. Export reorder report");
            System.out.println("\n--- User management ---");
            System.out.println("16. Register user");
            System.out.println("\n--- Online sales ---");
            System.out.println("17. View online inventory");
            System.out.println("\n--- Restocking ---");
            System.out.println("18. Restock shelves");
            System.out.println("19. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); 

                                                switch (choice) {
                                                    case 1: // process customer purchase and generate bill
                                                    List<Item> billItems = new ArrayList<>();
                                                    System.out.print("Enter the number of items in the bill: ");
                                                    int itemCount = scanner.nextInt();
                                                
                                                    double totalAmount = 0.0;
                                                
                                                    // get item and calc total amount 
                                                    for (int i = 0; i < itemCount; i++) {
                                                        System.out.print("Enter item code: ");
                                                        String billItemCode = scanner.next();
                                                        
                                                        System.out.print("Enter quantity: ");
                                                        int itemQuantity = scanner.nextInt();
                                                        while (itemQuantity <= 0) {
                                                            System.out.print("Quantity must be positive. Re-enter: ");
                                                            itemQuantity = scanner.nextInt();
                                                        }
                                                
                                                        Item billItem = itemService.getItemByCode(billItemCode);
                                                        if (billItem != null) {
                                                            double itemTotalPrice = billItem.getPrice() * itemQuantity;
                                                            totalAmount += itemTotalPrice;
                                                
                                                            // add item to the bill
                                                            billItem = new Item.Builder()
                                                                    .itemId(billItem.getItemId())
                                                                    .itemCode(billItem.getItemCode())
                                                                    .itemName(billItem.getItemName())
                                                                    .quantity(itemQuantity)
                                                                    .price(billItem.getPrice())
                                                                    .expiryDate(billItem.getExpiryDate())
                                                                    .dateOfPurchase(billItem.getDateOfPurchase())
                                                                    .build();
                                                            billItems.add(billItem);
                                                
                                                            System.out.printf("Added item: %s | Quantity: %d | Total price: %.2f\n",
                                                                    billItem.getItemName(), itemQuantity, itemTotalPrice);
                                                        } else {
                                                            System.out.println("Invalid item ID. Skipping item.");
                                                        }
                                                    }
                                                
                                                    System.out.printf("\nFull price: %.2f\n", totalAmount);
                                                
                                                    // discount options
                                                    System.out.println("\n--- Discount strategies ---");
                                                    System.out.println("0. No discount");
                                                    System.out.println("1. Flat discount");
                                                    System.out.println("2. Percentage discount");
                                                    System.out.println("3. Tiered discount");
                                                    System.out.print("Choose a discount strategy: ");
                                                    int discountType = scanner.nextInt();
                                                
                                                    DiscountCalculator discountCalculator = new DiscountCalculator();
                                                    double discount = 0.0;
                                                
                                                    switch (discountType) {
                                                        case 0: // no discount
                                                            System.out.println("No discount applied.");
                                                            break;
                                                        case 1: // flat discount
                                                            System.out.print("Enter flat discount amount: ");
                                                            double flatDiscount = scanner.nextDouble();
                                                            while (flatDiscount < 0 || flatDiscount > totalAmount) {
                                                                System.out.print("Flat discount must be between 0 and " + totalAmount + ". Re-enter: ");
                                                                flatDiscount = scanner.nextDouble();
                                                            }
                                                            discountCalculator.setDiscountStrategy(new FlatDiscountStrategy(flatDiscount));
                                                            discount = discountCalculator.calculate(totalAmount);
                                                            break;
                                                        case 2: // percentage discount
                                                            System.out.print("Enter discount percentage: ");
                                                            double percentage = scanner.nextDouble();
                                                            while (percentage < 0 || percentage > 100) {
                                                                System.out.print("Percentage must be between 0 and 100. Re-enter: ");
                                                                percentage = scanner.nextDouble();
                                                            }
                                                            discountCalculator.setDiscountStrategy(new PercentageDiscountStrategy(percentage));
                                                            discount = discountCalculator.calculate(totalAmount);
                                                            break;
                                                        case 3: // tiered discount
                                                            discountCalculator.setDiscountStrategy(new TieredDiscountStrategy());
                                                            discount = discountCalculator.calculate(totalAmount);
                                                            break;
                                                        default:
                                                            System.out.println("Invalid discount choice. No discount applied.");
                                                    }
                                                
                                                    double finalAmount = totalAmount - discount;
                                                    System.out.printf("Discount applied: %.2f\n", discount);
                                                    System.out.printf("Final amount: %.2f\n", finalAmount);
                                                
                                                    // payment details
                                                    System.out.print("Enter cash tendered: ");
                                                    double cashTendered = scanner.nextDouble();
                                                    while (cashTendered < finalAmount) {
                                                        System.out.print("Cash tendered must be at least " + finalAmount + ". Re-enter: ");
                                                        cashTendered = scanner.nextDouble();
                                                    }
                                                    double changeDue = cashTendered - finalAmount;
                                                
                                                    // builder pattern to create bill
                                                    Bill bill = new Bill.Builder()
                                                            .billDate(LocalDate.now().toString())
                                                            .totalAmount(totalAmount)
                                                            .discount(discount)
                                                            .finalAmount(finalAmount)
                                                            .cashTendered(cashTendered)
                                                            .changeDue(changeDue)
                                                            .items(billItems)
                                                            .build();
                                                
                                                    // pass connection only for this case 
                                                    try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
                                                        if (conn == null || conn.isClosed()) {
                                                            System.out.println("Database connection failed.");
                                                            return;
                                                        }
                                                    
                                                        conn.setAutoCommit(false);
                                                    
                                                        try {
                                                            billDAO.addBill(bill, conn);  // pass connection
                                                    
                                                           
                                                            
                                                    
                                                            conn.commit();
                                                            System.out.println("Transaction committed successfully.");
                                                        } catch (SQLException e) {
                                                            conn.rollback();
                                                            System.out.println("Transaction rolled back due to error.");
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    
                                                    
                                                    
                                                    
                                                    
                                                    
                                                
                                                    // display the bill
                                                    System.out.println("\n--- Bill receipt ---");
                                                    System.out.printf("Bill date: %s\n", bill.getBillDate());
                                                    System.out.printf("Bill serial number: %d\n", bill.getBillId());
                                                    System.out.println("\nItems purchased:");
                                                    for (Item currentItem : billItems) {
                                                        double itemTotal = currentItem.getPrice() * currentItem.getQuantity();
                                                        System.out.printf("Item name: %s | Quantity: %d | Total price: %.2f\n",
                                                                currentItem.getItemName(), currentItem.getQuantity(), itemTotal);
                                                    }
                                                    System.out.printf("\nFull price: %.2f\n", totalAmount);
                                                    System.out.printf("Discount applied: %.2f\n", discount);
                                                    System.out.printf("Final amount: %.2f\n", finalAmount);
                                                    System.out.printf("Cash tendered: %.2f\n", cashTendered);
                                                    System.out.printf("Change due: %.2f\n", changeDue);
                                                    break;
                                                

                                                    case 2: // add item 
                                                    System.out.print("Enter Item Code: ");
                                                    String itemCode = scanner.nextLine();
                                                
                                                    System.out.print("Enter Item Name: ");
                                                    String itemName = scanner.nextLine();
                                                
                                                    System.out.print("Enter Quantity in Store: ");
                                                    int quantityInStore = scanner.nextInt();
                                                    while (quantityInStore <= 0) {
                                                        System.out.print("Quantity must be positive. Re-enter Quantity: ");
                                                        quantityInStore = scanner.nextInt();
                                                    }
                                                
                                                    System.out.print("Enter Price: ");
                                                    double price = scanner.nextDouble();
                                                    while (price <= 0) {
                                                        System.out.print("Price must be positive. Re-enter Price: ");
                                                        price = scanner.nextDouble();
                                                    }
                                                    scanner.nextLine(); // Consume newline
                                                
                                                    System.out.print("Enter Date of Purchase (YYYY-MM-DD): ");
                                                    String dateOfPurchase = scanner.nextLine();
                                                
                                                    System.out.print("Enter Expiry Date (YYYY-MM-DD): ");
                                                    String expiryDate = scanner.nextLine();
                                                
                                                    System.out.print("Enter Sales Type (e.g., in-store, online): ");
                                                    String salesType = scanner.nextLine();
                                                
                                                    // batch number removed (it will be auto-generated in StockBatches)
                                                    Item item = new Item.Builder()
                                                        .itemCode(itemCode)
                                                        .itemName(itemName)
                                                        .quantity(quantityInStore)
                                                        .price(price)
                                                        .dateOfPurchase(dateOfPurchase)
                                                        .expiryDate(expiryDate.isEmpty() ? null : expiryDate)
                                                        .salesType(salesType.isEmpty() ? "in-store" : salesType)
                                                        .build();
                                                
                                                    itemService.addItem(item);
                                                    break;
                                                
                                    
                                                    case 3: // view items
                                                    itemService.displayAllItems();
                                                    break;
                                    
                                                    case 4: // reorder New Batch for an Existing Item
    System.out.print("Enter Item Code to Reorder: ");
    String updateItemCode = scanner.nextLine();

    // fetch item details
    Item existingItem = itemService.getItemByCode(updateItemCode);
    if (existingItem == null) {
        System.out.println("Item with code '" + updateItemCode + "' not found.");
        break;
    }

    System.out.print("Enter Quantity for New Batch: ");
    int newBatchQuantity = scanner.nextInt();
    while (newBatchQuantity <= 0) {
        System.out.print("Quantity must be positive. Re-enter: ");
        newBatchQuantity = scanner.nextInt();
    }
    scanner.nextLine(); 

    System.out.print("Enter New Batch Purchase Date (YYYY-MM-DD): ");
    String newBatchPurchaseDate = scanner.nextLine(); 

    System.out.print("Enter New Batch Expiry Date (YYYY-MM-DD): ");
    String newBatchExpiryDate = scanner.nextLine();  

    // generate new batch automatically
    String newBatchNumber = "BATCH-" + System.currentTimeMillis();

    // call the service to update stock and add a new batch
    boolean batchAdded = itemService.reorderNewBatch(updateItemCode, newBatchQuantity, newBatchNumber, newBatchPurchaseDate, newBatchExpiryDate);

    if (batchAdded) {
        System.out.println("New batch added successfully.");
    } else {
        System.out.println("Failed to add new batch.");
    }
    break;

                                                
                                                
                                                
                                                case 5: // delete item 
                                                    System.out.print("Enter Item Code to Delete: ");
                                                    String deleteItemCode = scanner.nextLine();
                                                
                                                    // fetch item by code
                                                    Item itemToDelete = itemService.getItemByCode(deleteItemCode);
                                                    if (itemToDelete == null) {
                                                        System.out.println("Item with code '" + deleteItemCode + "' not found.");
                                                        break;
                                                    }
                                                
                                                    itemService.deleteItem(deleteItemCode); 
                                                    break;
                                                
                                                    
                                    
                                                    case 6: // manage stores 
                                                    System.out.println("\n--- Manage Stores ---");
                                                    System.out.println("1. Add Store");
                                                    System.out.println("2. View All Stores");
                                                    System.out.print("Choose an option: ");
                                                    int storeChoice = scanner.nextInt();
                                                    scanner.nextLine(); 
                               
                                                    if (storeChoice == 1) {
                                                        System.out.print("Enter Store Name: ");
                                                        String storeName = scanner.nextLine();
                                                        System.out.print("Enter Location: ");
                                                        String location = scanner.nextLine();
                                                        System.out.print("Enter Manager's Name: ");
                                                        String manager = scanner.nextLine();
                                                        System.out.print("Enter Contact Info: ");
                                                        String contactInfo = scanner.nextLine();
                               
                                                        MultiStore store = new MultiStore();
                                                        store.setStoreName(storeName);
                                                        store.setLocation(location);
                                                        store.setManager(manager);
                                                        store.setContactInfo(contactInfo);
                               
                                                        multiStoreService.addStore(store);
                                                    } else if (storeChoice == 2) {
                                                        List<MultiStore> stores = multiStoreService.getAllStores();
                                                        System.out.println("\n--- All Stores ---");
                                                        for (MultiStore store : stores) {
                                                            System.out.printf("ID: %d | Name: %s | Location: %s | Manager: %s | Contact: %s\n",
                                                                store.getStoreId(), store.getStoreName(), store.getLocation(), store.getManager(), store.getContactInfo());
                                                        }
                                                    } else {
                                                        System.out.println("Invalid choice.");
                                                    }
                                                    break;
                                    
                                                    case 7: // manage discounts 
                                                    System.out.println("\n--- Manage Discounts ---");
                                                    System.out.println("1. Add Discount");
                                                    System.out.println("2. View All Discounts");
                                                    System.out.print("Choose an option: ");
                                                    int discountChoice = scanner.nextInt();
                                                    scanner.nextLine(); 
                               
                                                    if (discountChoice == 1) {
                                                        System.out.print("Enter Discount Code: ");
                                                        String discountCode = scanner.nextLine();
                                                        System.out.print("Enter Description: ");
                                                        String description = scanner.nextLine();
                                                        System.out.print("Enter Discount Percentage: ");
                                                        double percentage = scanner.nextDouble();
                                                        scanner.nextLine(); 
                                                        System.out.print("Enter Start Date (YYYY-MM-DD): ");
                                                        String startDateInput = scanner.nextLine();
                                                        LocalDate startDate = LocalDate.parse(startDateInput);
                                                        System.out.print("Enter End Date (YYYY-MM-DD): ");
                                                        String endDateInput = scanner.nextLine();
                                                        LocalDate endDate = LocalDate.parse(endDateInput);
                               
                                                        Discount storeDiscount = new Discount();
                                                        storeDiscount.setDiscountCode(discountCode);
                                                        storeDiscount.setDescription(description);
                                                        storeDiscount.setDiscountPercentage(percentage);
                                                        storeDiscount.setStartDate(startDate);
                                                        storeDiscount.setEndDate(endDate);
                               
                                                        discountService.addDiscount(storeDiscount);
                                                    } else if (discountChoice == 2) {
                                                        List<Discount> discounts = discountService.getAllDiscounts();
                                                        System.out.println("\n--- All Discounts ---");
                                                        for (Discount storeDiscount : discounts) {
                                                            System.out.printf("ID: %d | Code: %s | Description: %s | Percentage: %.2f%% | Start Date: %s | End Date: %s\n",
                                                            storeDiscount.getDiscountId(), storeDiscount.getDiscountCode(), storeDiscount.getDescription(),
                                                            storeDiscount.getDiscountPercentage(), storeDiscount.getStartDate(), storeDiscount.getEndDate());
                                                        }
                                                    } else {
                                                        System.out.println("Invalid choice.");
                                                    }
                                                    break;
                                    
                                                    case 8: // generate dailysales report
                                                    System.out.print("Enter date for sales report (YYYY-MM-DD): ");
                                                    String date = scanner.nextLine();
                                               
                                                    // retrieve in-store sales
                                                    System.out.println("\n--- In-Store Sales Report for " + date + " ---");
                                                    List<Bill> inStoreSales = billDAO.getBillsByTypeAndDate("in-store", date);
                                                    if (inStoreSales.isEmpty()) {
                                                        System.out.println("No in-store sales found for the given date.");
                                                    } else {
                                                        for (Bill storeBill : inStoreSales) {
                                                            System.out.printf("Bill ID: %d | Total Amount: %.2f | Discount: %.2f | Final Amount: %.2f\n",
                                                            storeBill.getBillId(), storeBill.getTotalAmount(), storeBill.getDiscount(), storeBill.getFinalAmount());
                                                        }
                                                    }
                                               
                                                    // retrieve online sales
                                                    System.out.println("\n--- Online Sales Report for " + date + " ---");
                                                    List<Bill> onlineSales = billDAO.getBillsByTypeAndDate("online", date);
                                                    if (onlineSales.isEmpty()) {
                                                        System.out.println("No online sales found for the given date.");
                                                    } else {
                                                        for (Bill storeBill : onlineSales) {
                                                            System.out.printf("Bill ID: %d | Total Amount: %.2f | Discount: %.2f | Final Amount: %.2f\n",
                                                            storeBill.getBillId(), storeBill.getTotalAmount(), storeBill.getDiscount(), storeBill.getFinalAmount());
                                                        }
                                                    }
                                                    break;
                                    
                                                    case 9: // generate reorder report (low stock)
                                                    System.out.print("\nEnter reorder level threshold: ");
                                                    int reorderLevel = scanner.nextInt();
                                                    scanner.nextLine(); 
                               
                                                    System.out.println("\n--- Reorder Report (Low Stock) ---");
                                                    List<Item> lowStockItems = itemService.getLowStockItems(reorderLevel);
                                                    if (lowStockItems.isEmpty()) {
                                                        System.out.println("No items below the reorder level.");
                                                    } else {
                                                        for (Item lowStockItem : lowStockItems) {
                                                            System.out.printf("ID: %d | Code: %s | Name: %s | Quantity: %d\n",
                                                                lowStockItem.getItemId(), lowStockItem.getItemCode(), lowStockItem.getItemName(), lowStockItem.getQuantity());
                                                        }
                                                    }
                                                    break;
                                    
                                                    case 10: // view inventory stock level
                                                    System.out.println("\n--- Inventory Stock Levels ---");
                                                    List<Item> inventory = itemService.getInventoryStockLevels();
                                                    if (inventory.isEmpty()) {
                                                        System.out.println("No inventory found.");
                                                    } else {
                                                        for (Item invItem : inventory) {
                                                            System.out.printf("ID: %d | Code: %s | Name: %s | Quantity: %d\n",
                                                                invItem.getItemId(), invItem.getItemCode(), invItem.getItemName(), invItem.getQuantity());
                                                        }
                                                    }
                                                    break;
                                    
                                    
                                    
                                                    case 11: // generate items on shelf report
                                                    System.out.println("\n--- Items on Shelf Report ---");
                                                
                                                    List<ShelfItem> shelfItems = reportDAO.getShelfItems(); 
                                                    if (shelfItems.isEmpty()) {
                                                        System.out.println("No items are currently on the shelves.");
                                                    } else {
                                                        System.out.printf("%-10s %-20s %-10s\n", "Shelf ID", "Item Name", "Quantity");
                                                        System.out.println("----------------------------------------------");
                                                        for (ShelfItem shelfItem : shelfItems) {  
                                                            System.out.printf("%-10d %-20s %-10d\n",
                                                                    shelfItem.getShelfId(), shelfItem.getItemName(), shelfItem.getQuantityOnShelf());
                                                        }
                                                    }
                                                    break;

// Case 12: Generate Batchwise Stock Report
case 12: // Generate Batch-Wise Stock Report
    reportService.generateBatchWiseStockReport();
    break;



                                                


                                    
        case 13: // generate complete bill history report
        System.out.println("\n--- Complete Bill History Report ---");
        ((ReportServiceTest) reportService).generateBillHistoryReport();
        break;

case 14: // export inventory report
System.out.print("Enter file path to save the inventory report: ");
String inventoryFilePath = scanner.nextLine();
List<Item> inventoryReport = itemService.getInventoryStockLevels();
ReportExporter inventoryExporter = ReportFactory.getReportExporter("inventory");
inventoryExporter.exportReport(inventoryReport, inventoryFilePath);
break;

                        
                                        case 15: // export reorder report 
                                        System.out.print("Enter file path to save the reorder report: ");
                                        String reorderFilePath = scanner.nextLine();
                                        System.out.print("Enter reorder level threshold: ");
                                        int reorderThreshold = scanner.nextInt();
                                        scanner.nextLine(); 
                                        List<Item> reorderItems = itemService.getLowStockItems(reorderThreshold);
                                        ReportExporter reorderExporter = ReportFactory.getReportExporter("reorder");
                                        reorderExporter.exportReport(reorderItems, reorderFilePath);
                                        break;
                        
                                        

                                        case 16: // register user
                                        System.out.println("\n--- Register User ---");
                                    
                                        System.out.print("Enter Full Name: ");
                                        String name = scanner.nextLine();
                                    
                                        System.out.print("Enter Email: ");
                                        String email = scanner.nextLine();
                                    
                                        System.out.print("Enter Password: ");
                                        String password = scanner.nextLine();
                                    
                                        System.out.print("Enter Phone Number: ");
                                        String phone = scanner.nextLine();
                                    
                                        System.out.print("Enter Address: ");
                                        String address = scanner.nextLine();
                                    
                                        System.out.print("Enter Role (e.g., Admin, Manager, Customer): ");
                                        String role = scanner.nextLine();
                                    
                                        boolean userRegistered = userService.registerUser(name, email, password, phone, address, role);
                                    
                                        if (userRegistered) {
                                            System.out.println("User registered successfully.");
                                        } else {
                                            System.out.println("Failed to register user. Email may already exist.");
                                        }
                                        break;
                                    

            

                case 17: // view online inventory
                System.out.println("\n--- Online Inventory ---");
                List<Item> onlineInventory = itemService.getOnlineInventory();
                if (onlineInventory.isEmpty()) {
                    System.out.println("No online inventory available.");
                } else {
                    for (Item onlineItem : onlineInventory) {
                        System.out.printf("ID: %d | Code: %s | Name: %s | Quantity: %d | Price: %.2f\n",
                                onlineItem.getItemId(), onlineItem.getItemCode(), onlineItem.getItemName(),
                                onlineItem.getQuantity(), onlineItem.getPrice());
                    }
                }
                break;

                case 18: // restock shelves
    System.out.println("Attempting to restock shelves...");

    try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
        if (conn == null || conn.isClosed()) {
            System.out.println("Database connection failed.");
            return;
        } else {
            System.out.println("Database connected successfully.");
        }

        boolean success = itemDAO.restockShelves(conn);
        if (success) {
            System.out.println("Shelves successfully restocked.");
        } else {
            System.out.println("Restocking may not have been necessary or failed.");
        }
    } catch (SQLException e) {
        System.out.println("Database connection error.");
        e.printStackTrace();
    }
    break;

            

            


                case 19: // exit
                    System.out.println("Exiting...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}





