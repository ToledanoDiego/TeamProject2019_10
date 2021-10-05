package BasicInteractions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.FileNotFoundException;

/**
 * <h1>The class that represents a order placed by a customer.</h1>
 *
 * @author Lorenzo Menegotto and Ben Griffiths
 * @version 1.0
 * @since 23-01-2019
 */
public class Order {
    private int orderID, tableNumber;
    private static ArrayList<Order> allOrderObjects = new ArrayList<Order>();
    private ArrayList<Item> listOfItem = new ArrayList<Item>();
    private float totalPrice = 0, totalTip = 0;
    private String date, customerIP, comment;
    private Status status;

    /**
     * Constructor to create an order.
     *
     * @param orderID           the ID of the order being created
     * @param customerIPpassed  the IP of the customer who ordered it
     * @param listOfItemPassed  the items in the order, with a flag of '|' to seperate them.
     * @param tableNumberPassed the table which links to this order.
     * @param date              the date/time of the order in question, as a string.
     * @param status            the current status of the order being made.
     * @param totalPricePassed  the price of the order.
     * @param totalTipPassed    the tip that the customer would give the order.
     */

    public Order(int orderID, String customerIPpassed, String listOfItemPassed,
                 int tableNumberPassed, String date, Status status,
                 float totalPricePassed, float totalTipPassed, String comment) {
        this.orderID = orderID;
        try {
            DatabaseConnectionURL connection = DatabaseConnectionURL.getInstance();
            String[] listOfItemsAsString = listOfItemPassed.split("\\|");
            for (String itemAsString : listOfItemsAsString) {
                if (!itemAsString.equals("")) {
                    Item item = connection.returnItems().get(Integer.parseInt(itemAsString) - 1);
                    if (item != null) {
                        Item contains = containsItem(listOfItem, item.getItemID());
                        if (contains == null) {
                            this.listOfItem.add(item);
                        } else {
                            contains.setQuantity(contains.getQuantity() + 1);
                        }
                    }
                }
            }
        } catch (InvalidUrlException e) {
            e.printStackTrace();
        }
        this.totalPrice = totalPricePassed;
        this.totalTip = totalTipPassed;
        this.tableNumber = tableNumberPassed;
        this.customerIP = customerIPpassed;
        this.date = date;
        this.status = status;
        allOrderObjects.add(this);
        this.comment = comment;
    }


    /**
     * Checks whether an item already exists in the list based on its item id.
     * @param items ArrayList of items
     * @param id of item
     * @return item
     */
    private static Item containsItem(ArrayList<Item> items, int id) {
        for (Item item : items) {
            if (item.getItemID() == id)
                return item;
        }
        return null;
    }

    /**
     * Returns the array list of all order objects referenced.
     *
     * @return the array list of all order objects referenced.
     */

    public static ArrayList<Order> getOrders() {
        return allOrderObjects;
    }

    /**
     * Returns the private ID of this particular order.
     *
     * @return the unique ID of this order.
     */

    public int getID() {
        return orderID;
    }

    /**
     * Pushes this order to the database, through the DatabaseInteractions class.
     *
     * @throws InvalidUrlException in the case that the database connection is invalid.
     */

    public void pushOrder() throws InvalidUrlException, FileNotFoundException {
        DatabaseConnectionURL database;
        database = DatabaseConnectionURL.getInstance();
        String listOfItemString = "";
        for (Item item : listOfItem) {
            for (int i = 0; i < item.getQuantity(); i++) {
                listOfItemString += Integer.toString(item.getItemID());
                listOfItemString += "|";
            }
        }
        database.addRowToOrders(orderID, customerIP, listOfItemString, totalPrice, totalTip, tableNumber, date, status.toString(), comment);
    }

    /**
     * Pulls the status of this order (not from the database but from the object instance.
     *
     * @return Status the status that this order is on.
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * sets the status instance of this object instance as the parameter.
     *
     * @param status the status that this object is now on.
     */

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * This function takes a string input and returns the correct Status enum that it
     * corresponds to (assuming the string is correctly formatted).
     *
     * @param roleAsString the string variable that should contain a role name.
     * @return Status the corresponding role to the param (Or Status.VOID if none).
     */

    private static Status resolve(String statusAsString) {
        for (Status statusChecked : Status.values()) {
            if (statusChecked.toString().equals(statusAsString)) {
                return statusChecked;
            }
        }
        return Status.VOID;
    }

    /**
     * Returns the items that are inside this order object.
     *
     * @return the arrayList of 'Item' objects stored in "listOfItem".
     */

    public ArrayList<Item> getItems() {
        return this.listOfItem;
    }

    /**
     * Returns the date made, of this particular order.
     *
     * @return the date of this order, formatted as a string.
     */

    public String getDate() {
        return this.date;
    }

    /**
     * Sets the tablenumber linked to this order, in the case it changes or
     * needs to be reinitialized.
     *
     * @param tableNumber the table number linked to the order.
     */
    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    /**
     * Gets theh table number of the order
     * @return Integer table number of the order
     */
    public int getTableNumber() {
        return this.tableNumber;
    }

    /**
     * Gets the comments of the order
     * @return String comment
     */
    public String getComment() {
        return this.comment;
    }
}
