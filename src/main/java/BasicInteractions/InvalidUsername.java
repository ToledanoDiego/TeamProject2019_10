package BasicInteractions;

/**
 * <h1> Invalid User Name Exception</h1>
 *     This exception is thrown if the user name passed is non-unique, or perhaps if an invalid
 *     String is passed into the database (e.g. an empty string, or one that does not fit the
 *     user name checks.)
 *     
 * @see LogOnSystem#addUser()
 * 
 * @author  Lorenzo Menegotto
 * @version 1.0
 * @since   19-01-2019
 * 
 */
public class InvalidUsername extends Exception {
  private static final long serialVersionUID = 1L;
  /**
   * This constructor calls the superclass of Exception to construct if not given any parameters.
   */

  public InvalidUsername() {
    super("Invalid username.");
  }
  /**
   * This constructor calls the superclass of Exception to construct.
   * (Using the error message parameter as an explanatory description).
   * @param errorMessage This is the description of the reason for this exception
   */
  
  public InvalidUsername(String errorMessage) {
    super(errorMessage);
  }
}