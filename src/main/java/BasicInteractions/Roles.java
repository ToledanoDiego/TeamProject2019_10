package BasicInteractions;

/**
 * Documents the possible types of role privileges.
 * @author  Lorenzo Menegotto
 * @version 1.0
 * @since   19-01-2019
 * 
 */

public enum Roles {
  VOID("Void"), //Void is the default case of no privileges.
  WAITER("Waiter"),
  KITCHEN_STAFF("KitchenStaff"),
  ADMIN("Admin");
  
  private String stringOfRole;
  
  /**
   * The constructor of the enum which links the string inputted
   * to the type, for the 'toString' method.
   * @param inputtedStringRepresentation the string version of the enum types.
   */
  Roles(String inputtedStringRepresentation) {
    stringOfRole = inputtedStringRepresentation;
  }
  
  /**
   * A way of returning the string corresponding to 'this' Status.
   * @return String the string linked to the object that called this function.
   */
  public String toString() {
    return stringOfRole;
  }  
  /**
   * This function returns a boolean which is true if the passed role is equal.
   * @return boolean value to show equality (true if enums are equal).
   */

  public static Roles fromString(String string) {
    for (Roles r : Roles.values()) {
      if (r.toString().equals(string)) {
        return r;
      }
    }
    return null;
  }

  /**
   * This function returns a boolean which is true if the passed status is equal.
   * @param statusPassed the role that will be converted to a string.
   * @return boolean value to show equality (true if enums are equal).
   */
  public boolean equals(Roles rolePassed) {
    return this == rolePassed;
  }
}
