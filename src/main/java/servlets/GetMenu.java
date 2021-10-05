package servlets;

import javax.servlet.http.*;
import java.io.IOException;
import java.io.FileNotFoundException;
import javax.servlet.annotation.WebServlet;
import java.lang.NumberFormatException;
import java.util.ArrayList;

import BasicInteractions.*;

/**
 * Servlet which handles:
 * - retrieving item details based on item id and type of detail (price)
 * - retrieving number of items in menu table
 *
 * @author Ben Griffiths
 * @since 27/03/2019
 *
 */
@WebServlet(
        name = "getmenu",
        urlPatterns = {"/getmenu"}
)
public class GetMenu extends HttpServlet {

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
        Item item = null;
        String type;
        String category;
        String create;
        String remove;
        int id = -1;

        //id passed by the web app when requesting items from the server
        //any input accepted nothing returned when invalid
        if (servlet.getParameter("id") != null) {
            id = Integer.parseInt(servlet.getParameter("id"));
        }
        //type passed by the web app when requesting types of information from each item
        //acceptable inputs:
        //      name  (returns the name of the item with id ?)
        //      price (returns the price of the item with id ?)
        //      desc  (returns the description of the item with id ?)
        //      count (returns the number of items in the menu)
        type = servlet.getParameter("type");
        category = servlet.getParameter("category");
        create = servlet.getParameter("create");
        remove = servlet.getParameter("remove");
        if (category != null) {
            category = Character.toUpperCase(category.charAt(0)) + category.substring(1);
        }
        servlet.logRequest("getmenu");

        try {
            DatabaseConnectionURL connection = DatabaseConnectionURL.getInstance();
            if (id != -1) {
                if (category == null) {
                    item = connection.returnItem(id);
                } else {
                    item = connection.getCategories(category).get(id);
                }
                if (item != null) {
                    if (type.equals("name")) {
                        servlet.write(item.getName());
                    } else if (type.equals("price")) {
                        servlet.write(item.getPrice());
                    } else if (type.equals("desc")) {
                        servlet.write(item.getDescription());
                    } else if (type.equals("id")) {
                        servlet.write(item.getItemID());
                    } else if (type.equals("image")) {
                        servlet.write(item.getImageURL());
                    } else if (type.equals("calories")) {
                        servlet.write(item.getCalories());
                    } else if (type.equals("allergies")) {
                        servlet.write(String.join(", ", item.getAllergies()));
                    }
                }
            } else if (create != null) {
                connection.addRowToMenuBySingleString(create);
            } else if (remove != null) {
                connection.deleteRowFromMenu(Integer.parseInt(remove));
            } else {
                if (type.equals("count")) {
                    if (category == null) {
                        servlet.write(connection.getMenuSize());
                    } else {
                        servlet.write(connection.getCategories(category).size());
                    }
                }
            }
        } catch (InvalidUrlException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        servlet.push();
        servlet.close();
    }
}