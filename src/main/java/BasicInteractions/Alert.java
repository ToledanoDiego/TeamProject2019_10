package BasicInteractions;

import java.util.ArrayList;

/**
 * Contains information for each alert made by the alert system.
 *
 * @author Ben Griffiths
 * @since 27/0/2019
 */
public class Alert {
    private String message;
    private ArrayList<String> users;

    /**
     * Constructor for the alert.
     *
     * @param message to be sent in the alert.
     */
    public Alert(String message) {
        this.message = message;
        users = new ArrayList<>();
    }

    /**
     * Adds a user to receive this alert.
     *
     * @param ip String ip adresses of the user
     */
    public void addUser(String ip) {
        users.add(ip);
    }

    /**
     * Check if the user is already added to this request.
     *
     * @param ip String ip address of the user.
     * @return Boolean if the user is contained inside the alert.
     */
    public Boolean containsUser(String ip) {
        return users.contains(ip);
    }

    /**
     * REmoves the user from the alert, usually done after the user has received the alert.
     *
     * @param ip address of the user to remove from the alert.
     */
    public void removeUser(String ip) {
        users.remove(ip);
    }

    /**
     * Gets the message inside the alert.
     *
     * @return String message.
     */
    public String getMessage() {
        return message;
    }

}
