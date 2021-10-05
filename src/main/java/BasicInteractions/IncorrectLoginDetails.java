package BasicInteractions;

/**
 * <h1>Incorrect Login details exception</h1>
 *     This exception is thrown if the object being created has an invalid user/pass combination.
 *     This exception is used rather than an 'if' check to prevent unauthenticated instances of a
 *     'Waiter','KitchenStaff' or 'Admin' from being a possibility.
 * 
 * @author  Lorenzo Menegotto
 * @version 1.0
 * @since   23-01-2019
 * 
 */
public class IncorrectLoginDetails extends Exception {
  private static final long serialVersionUID = 1L;
  /**
   * This constructor calls the superclass of Exception to construct if not given any parameters.
   */

  public IncorrectLoginDetails() {
    super("Invalid username/password combination.");
  }
  /**
   * This constructor calls the superclass of Exception to construct.
   * (Using the error message parameter as an explanatory description).
   * @param errorMessage This is the description of the reason for this exception
   */
  
  public IncorrectLoginDetails(String errorMessage) {
    super(errorMessage);
  }
}