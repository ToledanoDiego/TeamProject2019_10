package BasicInteractions;

import java.io.FileNotFoundException;

/**
 * <h1>The class that represents any member of staff in the kitchen.</h1>
 *
 * @author Lorenzo Menegotto
 * @version 1.0
 * @since 23-01-2019
 */
public class KitchenStaff {
    private String username = null;
    private static LogOnSystem logSys;
    private static DatabaseConnectionURL database;

    /**
     * This constructor creates a KitchenStaff instance, but only if authenticated.
     *
     * @param username the account user name to be used for logging in.
     * @param password the account password to be used for logging in.
     * @throws IncorrectLoginDetails in the case that the user/password is incorrect.
     * @throws InvalidUrlException   in the case that the database url is incorrect.
     */
    public KitchenStaff(String usernamePassed, String password) throws IncorrectLoginDetails, InvalidUrlException, FileNotFoundException {
        logSys = new LogOnSystem();
        if (!logSys.logOn(usernamePassed, password)) {
            throw new IncorrectLoginDetails();
        }
        username = usernamePassed;
        try {
            logSys = new LogOnSystem();
            database = DatabaseConnectionURL.getInstance();
        } catch (InvalidUrlException e) {
            e.printStackTrace();
        }
    }

    /**
     * This allows the kitchen staff to change their own password.
     *
     * @param username    the user name of the account being changed.
     * @param password    the current password, to be replaced.
     * @param newPassword the password to replace the current one.
     * @return boolean true if the password was successfully changed.
     */

    public boolean changePassword(String username, String password, String newPassword) {
        return logSys.changePassword(username, password, newPassword);
    }

    /**
     * This function confirms the order has been cooked.
     *
     * @param orderID the unique ID of the order that's been cooked.
     */

    public void cookedOrder(int orderID) {
        database.changeOrderStatus(orderID, Status.COOKED);
    }
}
