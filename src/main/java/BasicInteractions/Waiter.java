package BasicInteractions;

import java.io.FileNotFoundException;

/**
 * <h1>The class that represents a waiter.</h1>
 *     
 * @author  Lorenzo Menegotto
 * @version 1.2
 * @since   28-02-2019
 * 
 */
public class Waiter {
  private String username = null;
  private static LogOnSystem logSys;
  private static DatabaseConnectionURL database;
  /**
   * This constructor creates a Waiter instance, but only if the user/pass is right.
   * @param username the account user name to be used for logging in.
   * @param password the account password to be used for logging in.
   * @throws IncorrectLoginDetails in the case that the user/password is incorrect.
   * @throws InvalidUrlException in the case that the url connection is broken.
   */
  public Waiter(String usernamePassed, String password) throws IncorrectLoginDetails, InvalidUrlException, FileNotFoundException {
    database = DatabaseConnectionURL.getInstance();
    logSys = new LogOnSystem();
    if (!logSys.logOn(usernamePassed,password)) {
      throw new IncorrectLoginDetails();
    }
    username = usernamePassed;
    try {
      logSys = new LogOnSystem();
      database = DatabaseConnectionURL.getInstance();
    } catch (InvalidUrlException e) {
      e.printStackTrace();
    }
  }
  /**
   * This allows the waiter to change their own password.
   * @param username the user name of the account being changed.
   * @param password the current password, to be replaced.
   * @param newPassword the password to replace the current one.
   * @return boolean true if the password was successfully changed.
   */
  
  public boolean changePassword(String username, String password, String newPassword) {
    return logSys.changePassword(username, password, newPassword);
  }
  /**
   * This calls a function in the DatabaseInteractions that will add an item to the menu.
   * @param itemID the itemID that's currently being added
   * @param name the title of the item being added
   * @param description the description of the contents of this item
   * @param price the price of the item being added
   * @param calories the calories of this item (if applicable)
   */
  
  public void addToMenu(int itemID, String categories, String URL, String name, String description, float price,
      int calories, boolean vegan, boolean gluten, boolean alcooh, boolean hot) {
    // TODO if the itemID (or 'name') then just modify rather than adapt.
    database.addRowToMenu(itemID, categories, URL, name, description, price, calories, vegan, gluten,
        alcooh, hot);
  }
  /**
   * This deletes an item from the current menu.
   * @param itemID the item that will be deleted.
   */
  
  public void deleteFromMenu(int itemID) {
    database.deleteRowFromMenu(itemID);
  }
  /**
   * This function confirms the order of an existing order.
   * @param orderID the unique ID of the order being confirmed.
   */
  
  public void confirmOrder(int orderID) {
    database.changeOrderStatus(orderID, Status.CONFIRMED);
  }
  /**
   * This function confirms the order of an existing order.
   * @param orderID the unique ID of the order being confirmed.
   */
  
  public void deliveredOrder(int orderID) {
    database.changeOrderStatus(orderID, Status.DELIVERED);
  }
}
