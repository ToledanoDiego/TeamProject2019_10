package servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.management.relation.Role;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BasicInteractions.*;

/**
 * Servlet which handles: - the user logging in - checking whether the user is
 * logged in
 *
 * @author Ben Griffiths
 * @since 27/03/2019
 */
@WebServlet(name = "login", urlPatterns = {"/login"})
public class Login extends HttpServlet {
    private ArrayList<User> users;

    /**
     * Initialising users array list to empty.
     */
    public Login() {
        users = new ArrayList<>();
    }

    private Boolean hasUser(String ip) {
        for (User user : users) {
            if (user.getIp().equals(ip)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the users with the same username.
     *
     * @param username String of the users.
     * @return Arraylist<User> of users.
     */
    public ArrayList<User> getUsersFromUsername(String username) {
        ArrayList<User> new_users = new ArrayList<>();
        for (User user : users) {
            if (user.getUsername() == username) {
                new_users.add(user);
            }
        }
        return new_users;
    }

    /**
     * Gets all the users with the same role.
     *
     * @param role Role role of the users.
     * @return Arraylist<User> of users.
     */
    public ArrayList<User> getUsersFromRole(Roles role) {
        ArrayList<User> new_users = new ArrayList<>();
        for (User user : users) {
            if (user.getRole() == role) {
                new_users.add(user);
            }
        }
        return new_users;
    }

    /**
     * Required method ran when a request is made to the servlet.
     *
     * @param req  HttpServletRequest required variable that handles the requests to
     *             the servlet.
     * @param resp HttpServletResponse required variable that handles the responses
     *             from the servlet.
     * @throws IOException thrown when connection to database fails.
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Servlet servlet = new Servlet(req, resp);

        // username passed by the web app when logging in
        String username = servlet.getParameter("username");
        // password passed by the web app when logging in
        String password = servlet.getParameter("password");
        // type passed by the server when checking if the user is logged in
        // acceptable inputs: loggedin (returns true or false based on whether the user
        // is logged in)
        String type = servlet.getParameter("type");
        // ip passed by the server when checking if the user is logged in
        // gets the users ip from the users servlet request
        String ip = servlet.getParameter("ip");

        Roles role = Roles.fromString(servlet.getParameter("role"));

        servlet.logRequest("login");
        try {
            DatabaseConnectionURL connection = DatabaseConnectionURL.getInstance();
            LogOnSystem logSys = new LogOnSystem();

            if (type == null) {
                if (logSys.logOn(username, password)) {
                    users.add(new User(servlet.getRemoteIp(), username, Roles.fromString(connection.returnRole(username))));
                    System.out.println(users.size() + users.toString());
                    servlet.write(connection.returnRole(username));
                }
            } else if (type.equals("loggedin") && ip != null) {
                servlet.write(hasUser(ip));
            } else if (type.equals("getusers")) {
                if (username != null) {
                    for (User user : getUsersFromUsername(username)) {
                        servlet.write(user.getIp() + ",");
                    }
                } else if (role != null) {
                    for (User user : getUsersFromRole(role)) {
                        servlet.write(user.getIp() + ",");
                    }
                } else {
                    for (User user : users) {
                        servlet.write(user.getIp() + ",");
                    }
                }
            } else if (type.equals("available_waiter")) {
                String min_user = "";
                int min_assignments = 100;
                int assignments;
                for (User user : users) {
                    if (user.getRole() == Roles.WAITER) {
                        assignments = connection.countWaiterAssignments(user.getUsername());
                        if (assignments < min_assignments) {
                            min_user = user.getUsername();
                            min_assignments = assignments;
                        }
                    }
                }
                servlet.write(min_user);
            }
        } catch (InvalidUrlException e) {
            e.printStackTrace();
        }

        servlet.push();
        servlet.close();
    }

}