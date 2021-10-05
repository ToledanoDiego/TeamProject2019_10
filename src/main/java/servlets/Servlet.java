package servlets;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Servlet facade making the use of servlets easier.
 *
 * @author Ben Griffiths
 * @since 27/03/2019
 */
public class Servlet {
    private HttpServletRequest req;
    private HttpServletResponse resp;
    private ServletOutputStream out;
    private ArrayList<String> parameters;

    /**
     * Creation of the servlet.
     *
     * @param req  HttpServletRequest from the doGet method.
     * @param resp HttpServletResponse from the doGet method.
     */
    public Servlet(HttpServletRequest req, HttpServletResponse resp) {
        this.req = req;
        this.resp = resp;
        resp.setContentType("text/html");
        try {
            this.out = this.resp.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.parameters = new ArrayList<>();
    }

    /**
     * Creation of the servlet without HttpServletResponse.
     *
     * @param req HttpServletRequest from the doGet method.
     */
    public Servlet(HttpServletRequest req) {
        this.req = req;
        this.parameters = new ArrayList<>();
    }

    /**
     * Returns the ip address of the user
     *
     * @return String ip address of the user
     */
    String getRemoteIp() {
        String ipAddress = req.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = req.getRemoteAddr();
        }
        return ipAddress;
    }

    /**
     * Log each request from the user based on parameters passed
     * to the servlet and user ip address.
     *
     * @param name String name of the current servlet.
     */
    void logRequest(String name) {
        String ipAddress = getRemoteIp();
        StringBuilder out = new StringBuilder(ipAddress + ":" + name + "?");
        for (String param : parameters) {
            String[] parameters2 = this.getParameters(param);
            if (parameters2 != null) {
                out.append(param).append("=");
                for (int j = 0; j < parameters2.length; j++) {
                    String param2 = parameters2[j];
                    if (j != 0) out.append(",");
                    out.append(param2);
                }
                out.append(";");
            }
        }
        System.out.println(out);
    }


    /**
     * Get parameter given in the url.
     *
     * @param parameter String name of the parameter given.
     * @return String value from the url or null if it doesnt exist.
     */
    String getParameter(String parameter) {
        if (getParameters(parameter) == null) {
            return null;
        }
        return getParameters(parameter)[0];
    }

    /**
     * Get all parameters under a certain name in the url.
     *
     * @param parameter String name of the parameters given.
     * @return String[] array of the values from the url.
     */
    private String[] getParameters(String parameter) {
        if (!parameters.contains(parameter)) {
            parameters.add(parameter);
        }
        return req.getParameterValues(parameter);
    }

    /**
     * Write a html string to the page buffer.
     *
     * @param value String to be written.
     * @throws IOException thrown when the value cannot be written.
     */
    void write(String value) throws IOException {
        out.write(value.getBytes());
    }

    /**
     * Convert the int to a string and write the html string to the page buffer.
     *
     * @param value int to be written.
     * @throws IOException thrown when the value cannot be written.
     */
    void write(int value) throws IOException {
        out.write(Integer.toString(value).getBytes());
    }

    /**
     * Convert the float to a string and write the html string to the page buffer.
     *
     * @param value int to be written.
     * @throws IOException thrown when the value cannot be written.
     */
    void write(float value) throws IOException {
        out.write(Float.toString(value).getBytes());
    }

    /**
     * Convert the boolean to a string and write the html string to the page buffer.
     *
     * @param value Boolean to be written.
     * @throws IOException when write fails.
     */
    void write(Boolean value) throws IOException {
        out.write(Boolean.toString(value).getBytes());
    }

    /**
     * Push all items in the page buffer to the page.
     */
    void push() {
        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the page buffer, required to be ran at the end of the servlet.
     */
    void close() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Makes a get request to a servlet on the server.
     *
     * @param path String url path to the servlet.
     * @return String response from the server.
     * @throws IOException
     */
    public String makeGetRequest(String path) throws IOException {
        StringBuffer url = req.getRequestURL();
        String uri = req.getRequestURI();

        String host = url.substring(0, url.indexOf(uri));

        URL obj = new URL(host + path);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            System.out.println("Request:" + path);
            return null;
        }
    }

    /**
     * Request to be ran by secure servlets in order to check whether the user
     * is already logged in.
     *
     * @return Boolean true if the user is logged in and vice versa and null if
     * the login request fails.
     * @throws IOException thrown when the connection to the servlet fails.
     */
    public Boolean isLoggedIn() throws IOException {
        return Boolean.parseBoolean(makeGetRequest("/login?type=loggedin&ip=" + getRemoteIp()));
    }
}
