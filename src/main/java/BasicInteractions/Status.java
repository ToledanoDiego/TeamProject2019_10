package BasicInteractions;

/**
 * Documents the possible types of role privileges.
 * @author  Lorenzo Menegotto and Ben Griffiths
 * @version 1.0
 * @since   24-01-2019
 * 
 */

public enum Status {
  VOID("Void"),//Void is the default case of no privileges.
  SENT("Sent"),
  CONFIRMED("Confirmed"),
  COOKED("Cooked"),
  DELIVERED("Delivered"),
  PAID("Paid");
  
  private String stringOfStatus;
  
  /**
   * The constructor of the enum which links the string inputted
   * to the type, for the 'toString' method.
   * @param inputtedStringRepresentation the string version of the enum types.
   */
  Status(String inputtedStringRepresentation) {
    stringOfStatus = inputtedStringRepresentation;
  }
  
  /**
   * A way of returning the string corresponding to 'this' Status.
   * @return String the string linked to the object that called this function.
   */
  public String toString() {
    return stringOfStatus;
  }
  
  /**
   * This function returns a boolean which is true if the passed status is equal.
   * @param statusPassed the status that will be converted to a string.
   * @return boolean value to show equality (true if enums are equal).
   */
  public boolean equals(Status statusPassed) {
    return this == statusPassed;
  }

  /**
   * This function takes a string and returns the Status that corresponds to it
   * @param string the string of the 'Status' wanted.
   * @return Status the status of the string passed
   */
  public static Status fromString(String string) {
    for (Status s : Status.values()) {
      if (s.toString().equals(string)) {
        return s;
      }
    }
    return null;
  }
}
