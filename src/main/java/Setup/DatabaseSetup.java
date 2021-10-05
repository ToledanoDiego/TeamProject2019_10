package BasicInteractions;

import java.io.FileNotFoundException;

public class DatabaseSetup {

  public static void main(String[] args) {

    try {

      new DatabaseConnectionURL();
      DatabaseConnectionURL connection = DatabaseConnectionURL.getInstance();

      connection.dropTable("accounts");
      connection.createTable(
          "accounts (username varchar(50) primary key, hash varchar(70), salt varchar(70), role varchar(50), numberOfSeats varchar(255));");

      LogOnSystem system = new LogOnSystem();

      system.addUser("waiter1", "pass1", "Waiter");
      system.addUser("waiter2", "pass2", "Waiter");
      system.addUser("waiter3", "pass3", "Waiter");
      system.addUser("kitchen1", "pass1", "KitchenStaff");



      connection.dropTable("orders");
      connection.createTable(
          "orders (orderID int primary key, custID int, listOfItems varchar(255), seat int, time varchar(255), status varchar(255), price float, tip float, waiter varchar(50));");

      connection.dropTable("menu");
      connection.createTable(
          "menu (itemID int primary key, categories varchar(200), imageURL varchar(255), name varchar(200), description varchar(200), price float, calories int, peanut boolean, milk boolean, vegan boolean, gluten boolean, spicy boolean, alcohol boolean, hot boolean);");

      connection.insertIntoTableFromFile("menu", "Menu.txt");

      connection.closeConnection();

    } catch (InvalidUrlException e) {
      e.printStackTrace();
    } catch (InvalidUsername e) {
      e.printStackTrace();
    } catch (InvalidRole e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

  }

}
