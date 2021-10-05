package BasicInteractions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.util.Calendar;

/**
 * <h1>The checking out basket of the customer, before the order is confirmed.</h1>
 *
 * @author Lorenzo Menegotto and Ben Griffiths
 * @version 1.0
 * @since 23-01-2019
 */
public class CheckOutBasket {
    private ArrayList<Item> listOfItem = new ArrayList<Item>();
    private DatabaseConnectionURL database;
    private float totalPrice = 0;

    /**
     * Constructor for initialising a checkout basket object.
     *
     * @throws InvalidUrlException in the case that the database connection is null.
     */
    public CheckOutBasket() throws InvalidUrlException, FileNotFoundException {
        database = DatabaseConnectionURL.getInstance();
    }

    /**
     * Takes an itemId and pulls the details necessary as an instance of 'Item' and
     * saves this Item in this basket's list.
     *
     * @param itemID       the unique ID of the item being put into the basket.
     */
    public void addItem(int itemID) {
        //check to ensure that if there's duplication, the quantity changes:
//        for (int itemChecked = 0; itemChecked < listOfItem.size(); itemChecked++) {
//            if (listOfItem.get(itemChecked).getItemID() == itemID) {
//                int currentQuantity = listOfItem.get(itemChecked).getQuantity();
//                listOfItem.get(itemChecked).setQuantity(currentQuantity + itemQuantityParameter);
//                totalPrice = listOfItem.get(itemChecked).getPrice() * itemQuantityParameter;
//                return;
//            }
//        }
        Item itemBeingAdded = database.returnItems().get(itemID - 1);
//        itemBeingAdded.setQuantity(1);
        listOfItem.add(itemBeingAdded);
    }

    /**
     * Searches the list for the item, and it'll be removed.
     *
     * @param itemID the unique ID of the item being removed from the basket.
     */

    public void removeItem(int itemIDParameter) {
        for (int itemChecked = 0; itemChecked < listOfItem.size(); itemChecked++) {
            if (listOfItem.get(itemChecked).getItemID() == itemIDParameter) {
                listOfItem.remove(itemChecked);
                return;
            }
        }
    }

    /**
     * Creates an order with the items currently in this basket,
     * including the customerID which ordered.
     *
     * @return Order the Order object of the order created.
     * @throws InvalidUrlException in the case that the url is incorrect.
     */

    public Order createOrder(String customerIP, int tableNumberPassed, int orderID, float totalTipPassed, String comment) throws InvalidUrlException, FileNotFoundException {
        String listOfItemString = "";
        for (Item item : listOfItem) {
            listOfItemString += item.getItemID();
            listOfItemString += "|";
        }
        System.out.println(listOfItemString);
        String date = new SimpleDateFormat("ss:mm:HH:dd:MM:yyyy").format(Calendar.getInstance().getTime());
        Order order = new Order(orderID, customerIP, listOfItemString, tableNumberPassed,
                date, Status.SENT, this.totalPrice, totalTipPassed, comment);
        order.pushOrder();
        return order;
    }

    /**
     * Empties the current basket to reset.
     */

    public void empty() {
        totalPrice = 0;
        listOfItem.clear();
    }

    /**
     * Returns the size of the current basket.
     */

    public int size() {
        return listOfItem.size();
    }

    /**
     * Returns the item id at the specific position passed.
     *
     * @param itemCounter the position of the item id wanted.
     * @return Integer the item id of the item wanted.
     */

    public Integer returnItemId(int itemCounter) {
        return listOfItem.get(itemCounter).getItemID();
    }
}
