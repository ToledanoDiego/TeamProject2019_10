import java.util.ArrayList;

import src.main.java.BasicInteractions.DatabaseConnectionURL;
import src.main.java.BasicInteractions.InvalidUrlException;

/**
 * Personal testing, when tests need to be repeated often.
 * @author Lorenzo Menegotto
 * @version 1.0
 * @since 20-01-2019
 *
 */
public class TestClass {

  public static void main(String[] args) {
    //testing all of the authentication system:
    LogOnSystem logSys;
    try {
      logSys = new LogOnSystem();
      logSys.addUser("UserOne", "passwordOne", "Waiter");
      if (logSys.logOn("UserOne", "passwordOne")) {
        System.out.println("[TEST 1 SUCCESS] Successfully logged into UserOne.");
      } else {
        System.out.println("[TEST 1 FAILURE]");
      }
      logSys.addUser("UserTwo", "passwordTwo", "KitchenStaff");
      logSys.changePassword("UserTwo", "passwordTwo", "passwordThree");
      if (logSys.logOn("UserTwo", "passwordThree") && (!logSys.logOn("UserTwo", "passwordTwo"))) {
        System.out.println("[TEST 2 SUCCESS] Successfully logged into UserTwo, with updated password only.");
      } else {
        System.out.println("[TEST 2 FAILURE]");
      }
      
      try {
        logSys.addUser("!~@", "password", "Waiter");
      } catch (InvalidUsername e) {
        System.out.println("[TEST 3 SUCCESS] Does not accept username with special chars.");
      } catch (InvalidRole e) {
        e.printStackTrace();
      }
      
      try {
        logSys.addUser("UserFour", "password", "WrongInput");
      } catch (InvalidRole e) {
        System.out.println("[TEST 4 SUCCESS] Does not accept the role if the role doesn't work.");
      }
      if (!logSys.logOn("UserOne", "passwordWrong")) {
        System.out.println("[TEST 5 SUCCESS] Successfully can't login with incorrect password.");
      } else {
        System.out.println("[TEST 5 FAILURE]");
      }
    } catch(InvalidUsername | InvalidRole | InvalidUrlException e) {
      e.printStackTrace();
    }
    //Now testing all of the checkout system:
    try {
      Customer customerOne = new Customer();
      customerOne.chooseItem(7, 1);
      customerOne.chooseItem(3, 2);
      ArrayList<Integer> basketItems = customerOne.returnBasket();
      ArrayList<Integer> correctBasket = new ArrayList<Integer>();
      correctBasket.add(7);
      correctBasket.add(3);
      if (basketItems.equals(correctBasket)) {
        System.out.println("[TEST 6 SUCCESS] Basket of customer updates correctly");
      } else {
        System.out.println("[TEST 6 FAILURE]");
      }
      correctBasket.remove(1);
      customerOne.removeItem(3);
      basketItems = customerOne.returnBasket();
      if (basketItems.containsAll(correctBasket) && correctBasket.containsAll(basketItems)) {
        System.out.println("[TEST 7 SUCCESS] Customer can remove things from basket");
      } else {
        System.out.println("[TEST 7 FAILURE]");
      }
      customerOne.chooseItem(4,2);
      customerOne.chooseItem(7, 1);
      correctBasket.clear();
      correctBasket.add(7);
      correctBasket.add(4);
      basketItems = customerOne.returnBasket();
      if (basketItems.containsAll(correctBasket) && correctBasket.containsAll(basketItems)) {
        System.out.println("[TEST 8 SUCCESS] Added quantity does not duplicate list");
      } else {
        System.out.println("[TEST 8 FAILURE]");
      }
      customerOne.chooseItem(2, 1);
      customerOne.order(8); //Should be (7x2)+(4x2)+(2x1)
      customerOne.emptyBasket();
      System.out.println("[TEST 9 SUCCESS] Ran pushing of order '7x2','4x2','2x1'. Check if accurate on database.");
      Waiter waiterOne = new Waiter("UserOne","passwordOne");
      waiterOne.deleteFromMenu(5);
      waiterOne.addToMenu(8, "12 eggs", "Should be done in about a minute!", (float)49.99, 0);
      System.out.println("[TEST 10 SUCCESS] Pushed waiter changes! Check there's no 5 but there is 8 in database!");
    } catch (InvalidUrlException | IncorrectLoginDetails e) {
      e.printStackTrace();
    }
    try {
      DatabaseConnectionURL.getInstance().closeConnection();
    } catch (InvalidUrlException e1) {
      e1.printStackTrace();
    }
  }
}
