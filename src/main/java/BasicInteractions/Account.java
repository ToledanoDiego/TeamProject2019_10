package BasicInteractions;

/**
 * <h1>The interface that represents a class that abstracts an account type.</h1>
 *     
 * @author  Lorenzo Menegotto
 * @version 1.0
 * @since   23-01-2019
 * 
 */
public interface Account {
  public boolean changePassword(String username, String password, String newPassword);
}
