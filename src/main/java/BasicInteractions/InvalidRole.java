
package BasicInteractions;
/**
 * <h1> Invalid Role Exception</h1>
 *     This exception is thrown if the role passed is not one of the possible roles from
 *     the 'Roles.java' enum class.
 * 
 * @author  Lorenzo Menegotto
 * @version 1.0
 * @since   19-01-2019
 * 
 */
public class InvalidRole extends Exception {
  private static final long serialVersionUID = 1L;
  /**
   * This constructor calls the superclass of Exception to construct if not given any parameters.
   */

  public InvalidRole() {
    super("Invalid role.");
  }
  /**
   * This constructor calls the superclass of Exception to construct.
   * (Using the error message parameter as an explanatory description).
   * @param errorMessage This is the description of the reason for this exception
   */
  
  public InvalidRole(String errorMessage) {
    super(errorMessage);
  }
}