package BasicInteractions;

/**
 * This class handles SQL queries for the server to ease the load on it and for simplicity.
 *
 * @author Sorin and Diego and Lorenzo and Ben Griffiths
 * @version 3.6
 * @since 27/03/2019
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class DatabaseConnectionURL {
    private static String database = "";
    private static Connection connection;
    private static BasicInteractions.DatabaseConnectionURL instance = null;

    /**
     * Private Constructor for a DatabaseConnection object.
     */
    DatabaseConnectionURL() {
    }

    /**
     * Singleton "getter" which calls constructor if necessary.
     *
     * @throws InvalidUrlException a custom exception which handles URLs that do not follow the
     *                             expected format.
     */
    public static BasicInteractions.DatabaseConnectionURL getInstance() throws InvalidUrlException {
        if (instance == null) {
            instance = new BasicInteractions.DatabaseConnectionURL();
            database = System.getenv("DATABASE_URL");
            try {
                makeConnection();
            } catch (SQLException e) {
                throw new InvalidUrlException();
            }
        }
        return instance;
    }

    /**
     * Closes the connection when done.
     */
    @SuppressWarnings("unlikely-arg-type")
    public void closeConnection() {
        if (!connection.equals("NULL")) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Closes the previous connection and reopens it with a new URL.
     *
     * @param url the new url for the connection.
     */
    @SuppressWarnings("unlikely-arg-type")
    public static void setURL(String url) {
        if (!connection.equals("NULL")) {
            try {
                connection.close();
                database = url;
                makeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * Attempt to establish a connection to the database given to the constructor.
     *
     * @throws SQLException an exception that provides information on a database access error or other
     * SQL errors.
     */
    private static void makeConnection() throws SQLException {
        System.out.println("Attempting connection...");
        try {
            connection =
                    DriverManager.getConnection(split(database), username(database), password(database));
        } catch (SQLException e) {

        }
        if (connection == null) {
            throw new SQLException();
        } else {
            System.out.println("Connection successful!\nYou may now use this database.\n");
        }
    }

    /**
     * Prepare the URL so it is easier to connect to the database.
     *
     * @param url used to connect to the database
     */

    private static String split(String url) {
        return ("jdbc:postgresql://" + url.substring(url.indexOf("@") + 1, url.indexOf(":", 30)) + ":"
                + url.substring(url.indexOf(":", 30) + 1, url.lastIndexOf("/")) + "/"
                + url.substring(url.lastIndexOf("/") + 1));
    }

    /**
     * Drop an existing table in the database.
     *
     * @param name the name of the table to be dropped.
     */
    public void dropTable(String name) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS " + name);
            statement.close();
            System.out.println("Table " + name + " has been dropped.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a new table to the database.
     *
     * @param tableDescription the name of the table as well as a list of columns. Must be in the
     *                         following format:"table_name ( [column] [type], [column] [type] ..., PRIMARY KEY
     *                         ([column(s)] ); ".
     */
    public void createTable(String tableDescription) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE " + tableDescription);
            statement.close();
            System.out.println("Table has been successfully created");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Drop an existing view in the database.
     *
     * @param name the name of the view to be dropped.
     */
    public void dropView(String name) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("DROP VIEW IF EXISTS " + name);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a new view to the database.
     *
     * @param viewDescription name of the view as well as a set of conditions to follow. Must be in
     *                        the following format:" view_name AS [condition] WHERE [condition] ".
     */
    public void createView(String viewDescription) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("CREATE VIEW " + "'" + viewDescription + "'");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a user from the users table in the database.
     *
     * @param username the username associated with the user.
     */
    public void deleteUser(String username) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM accounts WHERE username = " + "'" + username + "'" + " ;");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add an account to the accounts table in the database.
     *
     * @param username the username associated with the user.
     * @param role     the role of the user.
     * @param salt     the salt value associated with the user's hash, aids in encryption.
     * @param hash     the user's hash value associated with their password.
     */
    public void addAccount(String username, String role, String salt, String hash) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO accounts VALUES (" + "'" + username + "'" + "," + "'" + hash
                    + "'" + "," + "'" + salt + "'" + "," + "'" + role + "'" + ");");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return all usernames in the database.
     *
     * @return an ArrayList containing all the usernames currently in the users table.
     */
    public ArrayList<String> returnAllUsernames() {
        ArrayList<String> usernames = new ArrayList<String>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultset = statement.executeQuery("SELECT username FROM accounts");
            while (resultset.next()) {
                usernames.add(resultset.getString(1));
            }
            statement.close();
            resultset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usernames;
    }

    /**
     * Execute a query in the format SELECT x FROM y.
     *
     * @param query the query to be executed.
     * @return a resultset containing the result of the query.
     */
    public ResultSet executeSelectQuery(String query) {
        ResultSet resultset = null;
        try {
            Statement statement = connection.createStatement();
            resultset = statement.executeQuery(query);
            statement.close();
            resultset.close();
        } catch (SQLException e) {
            System.out.println("Something has gone wrong.\n");
            e.printStackTrace();
        }
        return resultset;

    }

    /**
     * Add an entry to the orders table.
     *
     * @param orderID     the ID associated with the order.
     * @param customerIP  IP associated with the customer who placed the order.
     * @param listOfItems a list of items that the customer has ordered.
     * @param totalPrice  the total price of the items ordered by the customer.
     * @param totalTip    the tip paid by the customer to members of staff.
     * @param tableNumber the number for the table the customer is sat at.
     * @param date        the date when the order was placed.
     * @param status      the current status of the order.
     */
    public void addRowToOrders(int orderID, String customerIP, String listOfItems, float totalPrice,
                               float totalTip, int tableNumber, String date, String status, String comment) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO Orders VALUES ('" + orderID + "', '" + listOfItems + "', '"
                    + tableNumber + "', '" + date + "', '"
                    + status + "', '" + totalPrice + "', '"
                    + totalTip + "', '' , '" + comment + "', '" + customerIP + "');");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns all the allergies which are true in the database.
     *
     * @param rs result set of the order get request from the database.
     * @return string array of each allergy which is true inside the datsbase.
     */
    public static String[] getAllergies(ResultSet rs) {
        ArrayList<String> out = new ArrayList<>();
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 8; i < 15; i++) {
                if (rs.getString(i) != null && rs.getString(i).equals("t")) {
                    if (rsmd.getColumnCount() >= i) {
                        out.add(rsmd.getColumnName(i));
                    }
                }
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return out.toArray(new String[0]);
    }

    /**
     * Take a unique itemID and return an accurate Item instance.
     *
     * @return Item the correctly formatted 'Item' instance (with a default quantity of 1).
     */
    public ArrayList<Item> returnItems() {
        ArrayList<Item> Menu = new ArrayList<Item>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultset = statement.executeQuery("Select * FROM Menu");
            while (resultset.next()) {
                Item item = createItem(resultset);
                Menu.add(item);
            }
            statement.close();
            resultset.close();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        return Menu;
    }

    /**
     * reutrns an item from the database via its item id.
     *
     * @param itemid int of the item.
     * @return item of type Item.
     */
    public Item returnItem(int itemid) {
        Item item = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultset = statement.executeQuery("Select * FROM Menu WHERE itemid = " + itemid + ";");
            if (resultset.next()) {
                item = createItem(resultset);
            }
            statement.close();
            resultset.close();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        return item;
    }

    /**
     * Return the String stored as 'role' of the row of this user.
     *
     * @param username the user name who's role will be returned.
     * @return String the role of this account.
     */
    public String returnRole(String username) {
        String rolePulled = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultset = statement.executeQuery(
                    "SELECT role FROM accounts WHERE username = " + "'" + username + "'" + " ;");
            if (resultset.next()) {
                rolePulled = resultset.getString(1);
            }
            statement.close();
            resultset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rolePulled;
    }

    /**
     * Return the 16 byte array stored as 'hash' of the row of this user.
     *
     * @param username the user name who's hash will be returned.
     * @return String the hash used for that username's encryption. (flag: ",")
     */
    public String returnHash(String username) {
        String hashPulled = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultset = statement.executeQuery(
                    "Select hash FROM accounts WHERE username = " + "'" + username + "'" + " ;");
            if (resultset.next()) {
                hashPulled = resultset.getString(1);
            }
            statement.close();
            resultset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashPulled;

    }

    /**
     * Return the 16 byte array salt stored for this user.
     *
     * @param username the user name who's salt will be returned.
     * @return byte[] the salt used for that username's encryption.
     */
    public String returnSalt(String username) {
        String result = "";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultset = statement.executeQuery(
                    "Select salt FROM accounts WHERE username = " + "'" + username + "'" + " ;");
            if (resultset.next()) {
                result = resultset.getString(1);
            }
            statement.close();
            resultset.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the current status of the order (of orderID passed).
     *
     * @return String the status of this order.
     * @orderID the orderID who's status is being checked
     */
    public String returnStatus(int orderID) {
        String result = "";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultset = statement.executeQuery(
                    "Select status FROM orders WHERE orderID = " + "'" + orderID + "'" + " ;");
            if (resultset.next()) {
                result = resultset.getString(1);
            }
            statement.close();
            resultset.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This function adds an item/row into the 'menu' table. If the itemID is already in the table, it
     * is overwritten.
     *
     * @param itemID      the unique ID of the item being added.
     * @param name        the name of the item being added.
     * @param description the description of the item being added.
     * @param price       the price of the item being added.
     * @param calories    the calories of the item being added.
     * @param vegan       true if the item is vegan
     * @param gluten      true if the item contain gluten.
     * @param alcohol     true if you can get drunk while drinking it.
     * @param hot         true if the item is not cold
     */
    public void addRowToMenu(int itemID, String URL, String categories, String name,
                             String description, float price, int calories, boolean vegan, boolean gluten, boolean alcohol,
                             boolean hot) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultset = statement
                    .executeQuery("SELECT itemID FROM menu WHERE itemID =" + itemID);
            if (resultset.next()) {
                statement.execute("DELETE FROM menu WHERE itemID = " + itemID);
            }
            statement.execute("INSERT INTO menu VALUES( " + "'" + itemID + "'" + ","
                    + "'" + categories + "'" + "," + "'" + URL + "'" + "," + "'" + name + "'" + "," + "'"
                    + description + "'" + "," + "'" + price + "'" + "," + "'"
                    + calories + "'" + "," + "'" + vegan + "'" + "," + "'"
                    + gluten + "'" + "," + "'" + alcohol + "'" + "," + "'"
                    + hot + "'" + " );");
            statement.close();
            resultset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add row to the menu table in the database via a single formatted string.
     *
     * @param values formatted string to add to the database.
     */
    public void addRowToMenuBySingleString(String values) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO menu VALUES(" + values + ");");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function deletes an item from the 'menu' table in the database.
     *
     * @param itemID the itemID of the item being deleted.
     */

    public void deleteRowFromMenu(int itemID) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM menu WHERE itemID = " + itemID);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * This changes the order status in the database.
     *
     * @param orderID      the unique ID of the order being changed.
     * @param statusPulled the status that the order is being changed into.
     */
    public void changeOrderStatus(int orderID, Status statusPulled) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("UPDATE orders SET status = '" + statusPulled.toString() + "' WHERE orderID = "
                    + orderID);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populate a table with the contents of a file.
     *
     * @param table the name of the table in which to insert data.
     * @param file  the name of the file to be used.
     * @return
     */
    public int insertIntoTableFromFile(String table, String file) {
        BufferedReader bufferedreader = null;
        int numRows = 0;
        try {
            Statement st = connection.createStatement();
            String sCurrentLine;
            String[] brokenLine;
            String composedLine = "";
            bufferedreader = new BufferedReader(new FileReader(file));
            while ((sCurrentLine = bufferedreader.readLine()) != null) {
                brokenLine = sCurrentLine.split(",");
                composedLine = "INSERT INTO " + table + " VALUES (";
                int i;
                for (i = 0; i < brokenLine.length - 1; i++) {
                    composedLine += "'" + brokenLine[i] + "',";
                }
                composedLine += "'" + brokenLine[i] + "')";
                numRows = st.executeUpdate(composedLine);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedreader != null)
                    bufferedreader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return numRows;
    }

    /**
     * This function select the password from the url so it is easier to connect to the database
     *
     * @param url used to connect to the database
     */
    private static String password(String url) {
        return url.substring(url.indexOf(":", 12) + 1, url.indexOf("@"));
    }

    /**
     * This function select the username from the url so it is easier to connect to the database
     *
     * @param url used to connect to the database
     */
    private static String username(String url) {
        return url.substring(url.indexOf("//") + 2, url.indexOf(":", 15));
    }

    /**
     * Take every elements in the database that match certain filter option and add them to a list.
     * eg. gives every vegan food as a list
     *
     * @param filter, the filter option you want. eg. vegan
     * @return filteredMenu, list of the item
     */
    public ArrayList<Item> filterMenu(String filter) {
        ArrayList<Item> filteredMenu = new ArrayList<Item>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultset = statement.executeQuery("Select * FROM Menu WHERE " + filter + "=t ;");
            if (resultset.next()) {
                Item item = createItem(resultset);
            }
            statement.close();
            resultset.close();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        return filteredMenu;
    }

    private static Item createItem(ResultSet rs) throws SQLException {
        return new Item(Integer.parseInt(rs.getString(1)), rs.getString(2),
                rs.getString(3), rs.getString(4), rs.getString(5),
                Float.parseFloat(rs.getString(6)), Integer.parseInt(rs.getString(7)),
                getAllergies(rs));
    }

    /**
     * Take every elements in the database that match certain category and add them to a list. eg.
     * gives every drinks as a list
     *
     * @param categories, the filter category you want. eg. drinks
     * @return filteredMenu, list of the item
     */
    public static ArrayList<Item> getCategories(String categories) {
        ArrayList<Item> filteredMenu = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultset = statement.executeQuery("SELECT * FROM menu WHERE categories='" + categories + "';");
            while (resultset.next()) {
                filteredMenu.add(createItem(resultset));
            }
            statement.close();
            resultset.close();
        } catch (SQLException | NumberFormatException | NullPointerException e) {
            e.printStackTrace();
        }
        return filteredMenu;

    }

    /**
     * This function returns the size of the menu by counting each try in the table.
     *
     * @return int the amount of menu items.
     */
    public static int getMenuSize() {
        int count = -1;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultset = statement
                    .executeQuery("Select COUNT(*) FROM Menu;");
            if (resultset.next()) {
                count = Integer.parseInt(resultset.getString(1));
            }
            statement.close();
            resultset.close();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        return count;
    }

    /* This function returns the price in the database, linked to the orderID passed.
     * @param orderID the order who's price will be returned
     * @return float the value which is the price of the order in question.
     */
    public float returnPrice(int orderID) {
        float pricePulled = 0;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultset = statement
                    .executeQuery("Select * FROM orders WHERE orderid = " + orderID + " ;");
            if (resultset.next()) {
                pricePulled = Float.parseFloat(resultset.getString(7));
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        return pricePulled;
    }

    /* This function returns the table's which are Delivered but not yet paid.
     *
     * @return ArrayList<Integer> an array list with the integers of table numbers that aren't paid.
     */
//    public static ArrayList<Integer> getOrdersNotPaid() {
//        ArrayList<Order> tablesArrayList = new ArrayList<Order>();
//        Order tablesBeingAdded;
//        try {
//            Statement statement = connection.createStatement();
//            ResultSet resultset = statement.executeQuery("Select seat FROM orders WHERE status = 'Cooked';");
//            while (resultset.next()) {
//                tablesBeingAdded = Integer.parseInt(resultset.getString(1));
//                tablesArrayList.add(orderBeingAdded);
//            }
//            statement.close();
//            resultset.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
//        return tablesArrayList;
//    }

    /**
     * This function returns all orders that are in the database,
     * in an Arraylist form (after creating all the orders as 'Order' objects).
     *
     * @return ArrayList<Order> all orders in the database.
     */
    public static ArrayList<Order> getOrders() {
        ArrayList<Order> orders = new ArrayList<Order>();
        Order order;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultset = statement
                    .executeQuery("Select orderid FROM orders;");
            while (resultset.next()) {
                order = instance.returnOrder(Integer.parseInt(resultset.getString(1)));
                orders.add(order);
            }
            statement.close();
            resultset.close();
        } catch (SQLException | NumberFormatException | NullPointerException e) {
            e.printStackTrace();
        }
        return orders;
    }

    /**
     * Returns the order and it's details, of the order with the inputted orderID.
     * Returns null if the ID isn't found.
     *
     * @param orderID the ID of the order wanted.
     * @return Order the order linked to the orderID.
     */
    public Order returnOrder(int orderID) {
        Order orderPulled = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultset = statement
                    .executeQuery("Select * FROM orders WHERE orderid = " + orderID + " ;");
            if (resultset.next()) {
                orderPulled = new Order(Integer.parseInt(resultset.getString(1)),
                        resultset.getString(10),
                        resultset.getString(2),
                        Integer.parseInt(resultset.getString(3)),
                        resultset.getString(4),
                        Status.fromString(resultset.getString(5)),
                        Float.parseFloat(resultset.getString(6)),
                        Float.parseFloat(resultset.getString(7)),
                        resultset.getString(9));
            }
            statement.close();
            resultset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return orderPulled;
    }

    /**
     * Deletes the order from the table, of the order with passed orderID.
     *
     * @param orderID the orderID of the order being deleted.
     */
    public static void deleteOrder(int orderID) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM orders WHERE orderid =" + orderID + ";");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Take every elements in the database that match certain category and add them to a list. eg.
     * gives every drinks as a list
     *
     * @param calories, calories you want to have
     * @return filteredMenu, list of the item
     */
    public ArrayList<Item> getCalories(int calories) {
        ArrayList<Item> filteredMenu = new ArrayList<Item>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultset = statement.executeQuery(
                    "SELECT * FROM menu WHERE calories <= " + calories + " ;");
            if (resultset.next()) {
                Item item = createItem(resultset);
                filteredMenu.add(item);
            }
            statement.close();
            resultset.close();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        return filteredMenu;

    }

    /**
     * Assigns the waiter to an order inside the database.
     *
     * @param orderID  int id of the order.
     * @param username String of the waiter.
     */
    public void assignWaiter(int orderID, String username) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE orders SET waiter = '" + username + "'  WHERE orderid = " + orderID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int countWaiterAssignments(String username) {
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("Select COUNT(*) FROM orders WHERE waiter = '" + username + "';");
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Look for every tables in the restaurant, if the table has no waiter assign to it,
     * it assign to this table the waiter with to the lowest number of table assign to him.
     */
    public void assignTables() {

        // ArrayList<Integer> waitersTables = new ArrayList<Integer>();
        int numberOfTables;
        ArrayList<Integer> listOfTables = new ArrayList<Integer>();

        try {
            Statement statement = connection.createStatement();

            ResultSet resultset =
                    statement.executeQuery("SELECT COUNT(*) FROM accounts WHERE role = waiter ;");
            resultset = statement.executeQuery("SELECT seat, COUNT(seat) DISTINCT seat FROM orders;");
            numberOfTables = Integer.parseInt(resultset.getString(2));

            while (resultset.next()) {
                listOfTables.add(Integer.parseInt(resultset.getString(1)));
            }

            for (int i = 0; i < numberOfTables; i++) {
                if (!(isAssign(listOfTables.get(i)))) {
                    resultset = statement.executeQuery(
                            "SELECT username, min(numberOfSeats) FROM accounts where roles = waiter;");
                    String waiterToAdd = resultset.getString(1);

                    resultset = statement.executeQuery("UPDATE orders SET Waiter = '" + waiterToAdd
                            + "' WHERE seat = " + i + ";");
                }
            }

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
    }


    /**
     * Look if the table has a waiter assign to it.
     *
     * @param tableNumber, it's the number of the table.
     * @return boolean, true if the table has a waiter assign to it.
     */
    private boolean isAssign(int tableNumber) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultset = statement.executeQuery(
                "SELECT waiter FROM orders WHERE seat = " + tableNumber + " ;");
        return (resultset.getString(1)) != null;
    }

    /**
     * Changes the table number of an order in the database.
     *
     * @param orderID     the order being changed.
     * @param tableNumber the new table number of the order.
     */
    public void changeSeat(int orderID, int tableNumber) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("UPDATE orders SET seat = " + tableNumber + " WHERE orderID = " + orderID);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get customer orders based on the order ip.
     *
     * @param custip String ip adress of the customer.
     * @return ArrayList<Interger> of order ids.
     */
    public ArrayList<Integer> returnCustomerOrders(String custip) {
        try {
            ArrayList<Integer> out = new ArrayList<>();
            Statement statement = connection.createStatement();
            ResultSet resultset = statement.executeQuery(
                    "SELECT orderid FROM orders WHERE custip = '" + custip + "';");
            while (resultset.next()) {
                out.add(Integer.parseInt(resultset.getString(1)));
            }
            return out;
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
}