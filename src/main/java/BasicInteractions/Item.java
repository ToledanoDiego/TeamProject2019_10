package BasicInteractions;

/**
 * <h1>The class that holds information about a menu item.</h1> Instances of 'Item' include items in
 * a users basket/order. This class does not hold all possible items as instances.
 * 
 * @author Lorenzo Menegotto and Diego Toledano
 * @version 1.3
 * @since 29-02-2019
 * 
 */
public class Item {

  private int itemID = 0;
  private String name, URL = null, description = null, category = null;
  private float price = 0;
  private int calories;
  private int quantity = 1;
  private String[] allergies;

  /**
   * Constructor for an 'Item' of the menu.
   * 
   * @param itemIDParameter the unique ID of the menu item.
   * @param nameParameter the name of the menu item.
   * @params
   * @param descriptionParameter the description of the menu item.
   * @param category
   * @param priceParameter the price of the menu item.
   * @param
   */

  public Item(int itemIDParameter, String category, String URL, String nameParameter,
      String descriptionParameter, float priceParameter, int caloriesParameter, String[] allergiesParameter) {
    this.itemID = itemIDParameter;
    this.name = nameParameter;
    this.description = descriptionParameter;
    this.price = priceParameter;
    this.calories = caloriesParameter;
    this.category = category;
    this.URL = URL;
    this.allergies = allergiesParameter;
  }

  /**
   * This initialises the 'quantity' value separately from the constructor.
   * 
   * @param itemQuantity the amount of this item being added to the basket/order.
   */
  public void setQuantity(int itemQuantity) {
    this.quantity = itemQuantity;
  }

  /**
   * Returns the ID of the item instance.
   * 
   * @return int the unique ID of the item instance.
   */

  public int getItemID() {
    return this.itemID;
  }

  /**
   * Returns the quantity of the item instance.
   * 
   * @return int the quantity of this item.
   */

  public int getQuantity() {
    return this.quantity;
  }

  /**
   * Returns the price of the current item
   * 
   * @return float the price of this item (if quantity was 1).
   */

  public float getPrice() {
    return this.price;
  }

  /**
   * Returns the name of this item.
   * 
   * @return String the name of this item.
   */

  public String getName() {
    return this.name;
  }

  /**
   * Returns the description of this item.
   * 
   * @return String the description of this item.
   */

  public String getDescription() {
    return this.description;
  }

  /**
   * Returns the calories of this item.
   * 
   * @return int the calories of this item
   */
  public int getCalories() {
    return this.calories;
  }

  /**
   * Returns the URL of the image of this item.
   *
   * @return String the URL of this image.
   */
  public String getImageURL() { 
    return this.URL; 
  }

  /**
   * Returns the allergies of this item.
   *
   * @return String array of the allergies.
   */
  public String[] getAllergies() { return  this.allergies; }
}
