package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BasicInteractions.CheckOutBasket;
import BasicInteractions.DatabaseConnectionURL;
import BasicInteractions.InvalidUrlException;
import BasicInteractions.Item;
import BasicInteractions.Order;
import BasicInteractions.Status;


/**
 * Servlet which handles:
 * - storing orders
 * - updating stored orders
 * - retrieving order details based on order id and type of detail (date)
 * - retrieving number of orders in orders table
 * - creating orders
 * - pushing orders to the database
 *
 * @author Ben Griffiths
 * @since 27/03/2019
 */
@WebServlet(name = "getorders", urlPatterns = {"/getorders"})
public class GetOrders extends HttpServlet {
    private static final long serialVersionUID = 8879758916920759913L;
    private static ArrayList<Order> orders;
    private static ArrayList<String> updatedUsers;

    /**
     * Runs the update function when initialised.
     */
    public GetOrders() {
        updatedUsers = new ArrayList<>();
        update();
    }

    /**
     * Retrieves all orders from the database and stores it in an array.
     */
    private void update() {
        try {
            DatabaseConnectionURL connection = DatabaseConnectionURL.getInstance();
            orders = connection.getOrders();
            updatedUsers.clear();
        } catch (InvalidUrlException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns all the orders which have the confirmed status.
     *
     * @return ArrayList of orders.
     */
    private ArrayList<Order> filterConfirmed() {
        ArrayList<Order> newArray = new ArrayList<Order>();
        for (Order order : orders) {
            if (order.getStatus() == Status.CONFIRMED) {
                newArray.add(order);
            }
        }
        return newArray;
    }

    /**
     * Finds the smallest id available inside the array. Empty indices aren't
     * removed but just set to null.
     *
     * @return int index of smallest empty index.
     */
    private int getNextID() {
        int id = 0;
        Set<Integer> hash_Set = new HashSet<Integer>();
        for (Order order : orders) {
            if (order.getID() != id++) {
                hash_Set.add(order.getID());
            }
        }
        while (true) {
            if (!hash_Set.contains(id++)) {
                return id - 1;
            }
        }
    }

    /**
     * Required method ran when a request is made to the servlet.
     *
     * @param req  HttpServletRequest required variable that handles the requests to the servlet.
     * @param resp HttpServletResponse required variable that handles the responses from the servlet.
     * @throws IOException thrown when connection to database fails.
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Servlet servlet = new Servlet(req, resp);
        int id = -1;
        String type;
        String filter;
        int tableno = -1;
        float tip = -1;
        String comment;
        String[] new_items = new String[0];

        //type passed by the web app when requesting types of information from each order
        //acceptable inputs: items (gives the list of item names in the order)
        type = servlet.getParameter("type");

        //items padded bt rhe web app when creating orders
        if (servlet.getParameter("items") != null) {
            new_items = servlet.getParameter("items").split(",");
        }

        if (servlet.getParameter("tip") != null) {
            tip = Float.parseFloat(servlet.getParameter("tip"));
        }

        if (servlet.getParameter("tableno") != null) {
            tableno = Integer.parseInt(servlet.getParameter("tableno"));
        }

        comment = servlet.getParameter("comment");

        filter = servlet.getParameter("filter");

        servlet.logRequest("GetOrders");

        if (type != null && type.equals("create")) {
            createOrder(new_items, tip, comment, servlet);
        } else if (type == null && id == -1) {
            getCustomerOrderStatus(servlet);
        } else if (type.equals("pay")) {
            payCustomerOrder(servlet);
        } else if (servlet.isLoggedIn()) {
            //id passed by the web app when requesting orders from the server
            //any input accepted nothing returned when invalid
            if (servlet.getParameter("id") != null) {
                try {
                    id = Integer.parseInt(servlet.getParameter("id"));
                } catch (NumberFormatException e) {
                    System.out.println("Failed to parse int: " + servlet.getParameter("id"));
                }
            }
            if (type != null && (type.equals("itemnames") || type.equals("date") || type.equals("id") ||
                    type.equals("status") || type.equals("table") || type.equals("comment"))) {
                getOrderInfo(servlet, id, type, filter);
            } else if (id != -1) {
                editOrder(id, type, filter, tableno);
            } else {
                if (type != null && type.equals("count")) {
                    if (filter != null && filter.equals("Confirmed")) {
                        servlet.write(filterConfirmed().size());
                    } else {
                        servlet.write(orders.size());
                    }
                } else if (type != null && type.equals("update")) {
                    update();
                } else if (type != null && type.equals("needsupdate")) {
                    servlet.write(!updatedUsers.contains(servlet.getRemoteIp()));
                }
            }
        }


        servlet.push();
        servlet.close();
    }

    /**
     * @param servlet Servlet class for the current get request.
     * @param id      int ide of the order
     * @param type    String type of info to be retuned.
     *                - itemnames
     *                - date
     *                - id
     *                - status
     *                - table
     *                - comment
     * @param filter  String filter items to be returned
     *                -Confirmed
     * @throws IOException
     */
    private void getOrderInfo(Servlet servlet, int id, String type, String filter) throws IOException {
        Order order;
        if (filter != null && filter.equals("Confirmed")) {
            order = filterConfirmed().get(id);
        } else {
            order = orders.get(id);
        }
        if (order != null) {
            ArrayList<Item> items = order.getItems();
            updatedUsers.add(servlet.getRemoteIp());
            if (items != null) {
                if (type.equals("itemnames")) {
                    for (Item item : items) {
                        servlet.write(item.getName() + " x" + item.getQuantity() + ",");
                    }
                } else if (type.equals("date")) {
                    servlet.write(order.getDate());
                } else if (type.equals("id")) {
                    servlet.write(order.getID());
                } else if (type.equals("status")) {
                    servlet.write(order.getStatus().toString());
                } else if (type.equals("table")) {
                    servlet.write(order.getTableNumber());
                } else if (type.equals("comment")) {
                    servlet.write(order.getComment());
                }
            }
        }
    }

    /**
     * Set parameters of the order based on the order id.
     *
     * @param id      of the Order
     * @param type    String type of parameter to edit
     *                - accept
     *                - delivered
     *                - cooked
     *                - paid
     *                - cancel
     * @param filter  String filter items to be edited.
     *                - Confirmed
     * @param tableno int table number of the order
     */
    private void editOrder(int id, String type, String filter, int tableno) {
        Order order;
        try {
            if (filter != null && filter.equals("Confirmed")) {
                order = filterConfirmed().get(id);
            } else {
                order = orders.get(id);
            }
            DatabaseConnectionURL connection = DatabaseConnectionURL.getInstance();
            if (type.equals("accept")) {
                updatedUsers.clear();
                order.setTableNumber(tableno);
                connection.changeSeat(order.getID(), tableno);
                order.setStatus(Status.CONFIRMED);
                connection.changeOrderStatus(order.getID(), Status.CONFIRMED);
            } else if (type.equals("delivered")) {
                updatedUsers.clear();
                order.setStatus(Status.DELIVERED);
                connection.changeOrderStatus(order.getID(), Status.DELIVERED);
            } else if (type.equals("cooked")) {
                updatedUsers.clear();
                order.setStatus(Status.COOKED);
                connection.changeOrderStatus(order.getID(), Status.COOKED);
            } else if (type.equals("paid")) {
                updatedUsers.clear();
                order.setStatus(Status.PAID);
                connection.changeOrderStatus(order.getID(), Status.PAID);
            } else if (type.equals("cancel")) {
                updatedUsers.clear();
                orders.remove(id);
                connection.deleteOrder(order.getID());
            }
        } catch (InvalidUrlException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an order.
     *
     * @param new_items String array of items to be added to the order.
     * @param tip       float tip from the customer for the order.
     * @param comment   Sting comment from the customer to the staff.
     * @param servlet   Servlet class for the current get request.
     */
    private void createOrder(String[] new_items, float tip, String comment, Servlet servlet) {
        try {
            CheckOutBasket basket = new CheckOutBasket();
            int num;
            for (String strNum : new_items) {
                num = Integer.parseInt(strNum);
                basket.addItem(num);
            }
            int id = getNextID();
            basket.createOrder(servlet.getRemoteIp(), 0, id, tip, comment);
            DatabaseConnectionURL connection = DatabaseConnectionURL.getInstance();
            connection.assignWaiter(id, servlet.makeGetRequest("/login?type=available_waiter"));
            update();
        } catch (InvalidUrlException | java.io.IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the status of the customers order.
     *
     * @param servlet Servlet class for the current get request.
     * @throws IOException
     */
    private void getCustomerOrderStatus(Servlet servlet) throws IOException {
        try {
            DatabaseConnectionURL connection = DatabaseConnectionURL.getInstance();
            ArrayList<Integer> orderids = connection.returnCustomerOrders(servlet.getRemoteIp());
            if (orderids.size() > 0) {
                servlet.write(connection.returnOrder(orderids.get(0)).getStatus().toString());
            }
        } catch (InvalidUrlException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pays for the customers order.
     *
     * @param servlet Servlet class for the current get request.
     * @throws IOException
     */
    private void payCustomerOrder(Servlet servlet) throws IOException {
        try {
            DatabaseConnectionURL connection = DatabaseConnectionURL.getInstance();
            ArrayList<Integer> orderids = connection.returnCustomerOrders(servlet.getRemoteIp());
            if (orderids.size() > 0) {
                connection.changeOrderStatus(orderids.get(0), Status.PAID);
                updatedUsers.clear();
                for (Order order : orders) {
                    if (order.getID() == orderids.get(0)) {
                        orders.remove(order);
                        break;
                    }
                }
                connection.deleteOrder(orderids.get(0));
            }
        } catch (InvalidUrlException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}
