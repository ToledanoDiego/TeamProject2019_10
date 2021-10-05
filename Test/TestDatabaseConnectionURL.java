public class TestDatabaseConnectionURL {



  public static void main(String[] args) {

    try {

      DatabaseConnectionURL connection = new DatabaseConnectionURL().getInstance();

      connection.dropTable("accounts");

      connection.createTable(

          "accounts (username varchar(50), hash varchar(70), salt varchar(70), role varchar(50), PRIMARY KEY(username) );");

      connection.dropTable("orders");

      connection.createTable(

          "orders (orderID int primary key, custID int, listOfItems varchar(255), seat int, time varchar(255), status varchar(255), price float, tip float );");

      connection.dropTable("menu");

      connection.createTable(

          "menu (itemID int primary key,categories varchar(200), name varchar(200), description varchar(200), price float, calories int);");

      connection.insertIntoTableFromFile("menu", "Menu.txt");


      connection.closeConnection();
    } catch (InvalidUrlException e) {

      e.printStackTrace();

    }



  }

}
