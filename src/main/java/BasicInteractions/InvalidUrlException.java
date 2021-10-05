package BasicInteractions;

/**
 * <h1> Invalid URL Exception</h1>
 *     This exception is thrown if the URL is incorrect.
 *     
 * @see DatabaseConnectionURL
 * 
 */

public class InvalidUrlException extends Exception {
  private static final long serialVersionUID = 1L;
  /**
   * This constructor calls the superclass of Exception to construct if not given any parameters.
   */
  public InvalidUrlException() {
    super("Invalid url.");
  }

  /**
   * This returns an error message if needed
   * @return String a generic error message, for this error case.
   */
  public String getError() {
    return "Error: this URL is invalid.\nPlease check that it follows the correct format and that the database is online.";
  }
}
