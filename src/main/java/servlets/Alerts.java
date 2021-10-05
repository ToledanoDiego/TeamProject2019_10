package servlets;

import BasicInteractions.Alert;
import BasicInteractions.Roles;
import BasicInteractions.Status;
import BasicInteractions.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Alert servlet which handles alerts and interations with the alert system and the ui.
 *
 * @author Ben Griffiths
 * @since 27/03/2019
 */
@WebServlet(name = "alerts", urlPatterns = {"/alerts"})
public class Alerts extends HttpServlet {
    private ArrayList<Alert> alerts;

    /**
     * Handles all get requests from the webpage.
     *
     * @param req  Request made by the user.
     * @param resp Reesponse from the server
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Servlet servlet = new Servlet(req, resp);
        Roles role = Roles.fromString(servlet.getParameter("role"));
        String username = servlet.getParameter("username");
        String message = servlet.getParameter("message");

        servlet.logRequest("Alerts");
        if (message != null) {
            Alert a = new Alert(message);
            if (role != null) {
                for (String ip : getUsersFromRole(servlet, role)) {
                    a.addUser(ip);
                }
            } else if (username != null) {
                for (String ip : getUsersFromUsername(servlet, username)) {
                    a.addUser(ip);
                }
            } else {
                for (String ip : getUsers(servlet)) {
                    a.addUser(ip);
                }
            }
            alerts.add(a);
        } else {
            Alert alert = getAlert(servlet.getRemoteIp());
            if (alert != null) {
                servlet.write(alert.getMessage());
                alert.removeUser(servlet.getRemoteIp());
            }
        }
        servlet.push();
        servlet.close();
    }

    /**
     * Constructor which creates an array list of orders.
     */
    public Alerts() {
        alerts = new ArrayList<>();
    }

    /**
     * Gets all the users from the login system.
     *
     * @param servlet Servlet class for the current get request.
     * @return Sting ip addresses of users.
     * @throws IOException
     */
    private ArrayList<String> getUsers(Servlet servlet) throws IOException {
        String response = servlet.makeGetRequest("/login?type=getusers");
        return new ArrayList<>(Arrays.asList(response.split(",")));
    }

    /**
     * Gets all the users from the login system with the same role.
     *
     * @param servlet Servlet class for the current get request.
     * @param role    Role of the user
     * @return String ip addresses of the users.
     * @throws IOException
     */
    private ArrayList<String> getUsersFromRole(Servlet servlet, Roles role) throws IOException {
        String response = servlet.makeGetRequest("/login?type=getusers&role=" + role.toString());
        return new ArrayList<>(Arrays.asList(response.split(",")));
    }

    /**
     * Gets the ip address of the user via their username.
     *
     * @param servlet  Servlet class for the current get request.
     * @param username String username of the user.
     * @return Arraylist String of user ip addresses
     * @throws IOException
     */
    private ArrayList<String> getUsersFromUsername(Servlet servlet, String username) throws IOException {
        String response = servlet.makeGetRequest("/login?type=getusers&username=" + username);
        return new ArrayList<>(Arrays.asList(response.split(",")));
    }

    /**
     * Get the alert which contain the user with a certain ip.
     *
     * @param ip of the user
     * @return Alert
     */
    public Alert getAlert(String ip) {
        for (Alert alert : alerts) {
            if (alert.containsUser(ip)) {
                return alert;
            }
        }
        return null;
    }
}
