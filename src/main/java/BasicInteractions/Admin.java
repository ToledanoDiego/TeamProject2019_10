package BasicInteractions;
import java.io.FileNotFoundException;
/**
 * <h1>The class that represents an administrator, with maximum privileges.</h1>
 *     
 * @author  Lorenzo Menegotto
 * @version 1.0
 * @since   23-01-2019
 * 
 */
public class Admin {
  private String username = null;
  private static LogOnSystem logSys;
  /**
   * This constructor creates an Admin instance, but only if authenticated.
   * @param username the account user name to be used for logging in.
   * @param password the account password to be used for logging in.
   * @throws IncorrectLoginDetails in the case that the user/password is incorrect.
   * @throws InvalidUrlException in the case that the url connection is incorrect.
   */
  
  public Admin(String usernamePassed, String password) throws IncorrectLoginDetails, InvalidUrlException, FileNotFoundException {
    logSys = new LogOnSystem();
    if (!logSys.logOn(usernamePassed,password)) {
      throw new IncorrectLoginDetails();
    }
    this.username = usernamePassed;
  }
  /**
   * This allows the admin to change their own password.
   * @param username the user name of the account being changed.
   * @param password the current password, to be replaced.
   * @param newPassword the password to replace the current one.
   * @return boolean true if the password was successfully changed.
   */
  
  public boolean changePassword(String username, String password, String newPassword) {
    return logSys.changePassword(username, password, newPassword);
  }
}
