package BasicInteractions;

import java.util.ArrayList;
import java.io.FileNotFoundException;

/**
 * <h1>The class that represents a customer.</h1>
 * This has capabilities to add/remove items to it's basket
 * as well as to turn the basket into an official order.
 *     
 * @author  Lorenzo Menegotto
 * @version 1.0
 * @since   23-01-2019
 * 
 */
public class Customer {
  private CheckOutBasket basket = new CheckOutBasket();
  private DatabaseConnectionURL connection = DatabaseConnectionURL.getInstance();
  private int customerID; 
  private static int totalCustomers = 0;
  private int tableNumber;
  
  /**
   * Constructor to give customer a unique customerID
   * @throws InvalidUrlException in the case that the database isn't functional
   */
  public Customer() throws InvalidUrlException, FileNotFoundException {
    totalCustomers++;
    customerID = totalCustomers;
    basket = new CheckOutBasket();
  }
  
  /**
   * Calling the function in this.basket to add the item to the basket.
   * @param itemID the unique ID of the item being added.
   * @param itemQuantity the quantity of the item being added.
   */
//  public void chooseItem(int itemID, int itemQuantity) {
//    basket.addItem(itemID, itemQuantity);
//  }
  
  /**
   * Calling the function in this.basket to remove the item from the basket.
   * @param itemID the unique ID of the item being removed.
   */
  public void removeItem(int itemID) {
    basket.removeItem(itemID);
  }
  
  /**
   * Setter for the tablenumber that the customer is on!
   * @param numberPassed the number of the table the customer is on.
   */
  public void setTable(int numberPassed) {
    this.tableNumber = numberPassed;
  }
  
  /**
   * Calling the appropriate functions in this.basket, to send off an order
   * and also reset the basket (since it's items have 'moved' to the order)
   * @param orderID the ID of the order being pushed.
   * @param totalTipPassed the tip added ontop of the price of the order.
   * @return Order the object of 'Order' that the order created
   * @throws InvalidUrlException in the case that the database connection is null
   * @throws PaymentFailedException in the case that the payment was unsuccessful.
   */
//  public Order order(int orderID, float totalTipPassed) throws InvalidUrlException,
//                                                                                FileNotFoundException,
//                                                                                PaymentFailedException {
//    this.pay(orderID, totalTipPassed);
//    return basket.createOrder(customerID, this.tableNumber, orderID, totalTipPassed);
//  }
  
  /**
   * Customer payment method, to update database.
   * Actual paying occurs in javascript, this just updates the rest of system.
   */
//  private void paid(int orderID) {
//    changeOrderStatus(orderID, Status.PAID);
//    //If wanted, the order could be moved to a 'log' table here, rather than deleted:
//    deleteOrder(orderID);
//  }
  
  /**
   * Calling this function empties the basket of the customer after a basket
   * has become an order.
   */
  public void emptyBasket() {
    basket.empty();
  }
  
  /**
   * Returning the item id's present inside the current checkout basket
   * @return 
   */
  public ArrayList<Integer> returnBasket() {
    ArrayList<Integer> basketItemIdList = new ArrayList<Integer>();
    for (int itemCounter = 0; itemCounter < basket.size(); itemCounter++) {
      basketItemIdList.add(basket.returnItemId(itemCounter));
    }
    return basketItemIdList;
  }
}
