package BasicInteractions;

/**
 * Contains for all data for each user in the login system
 *
 * @author Ben Griffiths
 * @version 1.0
 * @since 28-02-2019
 */
public class User {
    private String ip;
    private String username;
    private Roles role;

    /**
     * @param ip       of user in type String
     * @param username of user in type String
     * @param role     of user in type Role
     */
    public User(String ip, String username, Roles role) {
        this.ip = ip;
        this.username = username;
        this.role = role;
    }

    /**
     * Returns the ip adress of the user
     *
     * @return String ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * Returns the username of the user
     *
     * @return String username
     */
    public String getUsername() {
        return username;
    }


    /**
     * Returns the role of the user
     *
     * @return Role role
     */
    public Roles getRole() {
        return role;
    }
}
