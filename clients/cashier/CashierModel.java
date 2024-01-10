package clients.cashier;

import catalogue.Basket;
import catalogue.Product;
import clients.Receipt;
import debug.DEBUG;
import middle.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.HashMap;

/**
 * Implements the Model of the cashier client
 * @author  Mike Smith University of Brighton
 * @version 1.0
 */
public class CashierModel extends Observable {
  private enum State {process, checked}

  private State theState = State.process;   // Current state
  private Product theProduct = null;            // Current product
  private Basket theBasket = null;            // Bought items

  private String pn = "";                      // Product being processed

  private StockReadWriter theStock = null;
  private OrderProcessing theOrder = null;
  private static HashMap<String, String> orderList = new HashMap<>();
  private static int totalIncome = 0;
  private static String customerEmail = "";

  private static ArrayList<Receipt> receipts = new ArrayList<>();
  ArrayList<String> productNumbers = new ArrayList<>();
  /**
   * Construct the model of the Cashier
   *
   * @param mf The factory to create the connection objects
   */

  public CashierModel(MiddleFactory mf) {
    try                                           // 
    {
      theStock = mf.makeStockReadWriter();        // Database access
      theOrder = mf.makeOrderProcessing();        // Process order
    } catch (Exception e) {
      DEBUG.error("CashierModel.constructor\n%s", e.getMessage());
    }
    theState = State.process;                  // Current state
  }

  /**
   * Get the Basket of products
   *
   * @return basket
   */
  public Basket getBasket() {
    return theBasket;
  }

  /**
   * Check if the product is in Stock
   *
   * @param productNum The product number
   */
  public void doCheck(String productNum) {
    String theAction = "";
    theState = State.process;                  // State process
    pn = productNum.trim();                    // Product no.
    int amount = 1;                         //  & quantity
    try {
      if (theStock.exists(pn))              // Stock Exists?
      {                                         // T
        Product pr = theStock.getDetails(pn);   //  Get details      //COULD USE THIS FOR RETURNS TO GET THE DATA, COPY THIS CODE, TAKE PN FROM HASHMAP.
        if (pr.getQuantity() >= amount)       //  In stock?
        {                                       //  T
          theAction =                           //   Display 
                  String.format("%s : %7.2f (%2d) ", //
                          pr.getDescription(),              //    description
                          pr.getPrice(),                    //    price
                          pr.getQuantity());               //    quantity
          theProduct = pr;                      //   Remember prod.
          theProduct.setQuantity(amount);     //    & quantity
          theState = State.checked;             //   OK await BUY 
        } else {                                //  F
          theAction =                           //   Not in Stock
                  pr.getDescription() + " not in stock";
        }
      } else {                                  // F Stock exists
        theAction =                             //  Unknown
                "Unknown product number " + pn;       //  product no.
      }
    } catch (StockException e) {
      DEBUG.error("%s\n%s",
              "CashierModel.doCheck", e.getMessage());
      theAction = e.getMessage();
    }
    setChanged();
    notifyObservers(theAction);
  }

  /**
   * Buy the product
   */
  public void doBuy() {
    String theAction = "";
    int amount = CashierView.getQuantity();
    theProduct.setQuantity(amount);
    try {
      if (theState != State.checked)          // Not checked
      {                                         //  with customer
        theAction = "Check if OK with customer first";
      } else {
        if (CashierView.getEmail() != null) {
              boolean  stockBought = theStock.buyStock(                    //  however
                        theProduct.getProductNum(),         //  may fail
                        theProduct.getQuantity());
                productNumbers.add(theProduct.getProductNum());
            if (stockBought)                      // Stock bought
            {                                       // T
                makeBasketIfReq();                    //  new Basket ?
                theBasket.add(theProduct);          //  Add to bought
                int price = (int) theProduct.getPrice();
                totalIncome = totalIncome + price;
                theAction = "Purchased " +            //    details
                        theProduct.getDescription();  //
            } else {                                // F
                theAction = "!!! Not in stock";       //  Now no stock
            }
        } else {
          theAction = "Please take Customer's Email for their Receipt";
        }
      }
      } catch(StockException e){
        DEBUG.error("%s\n%s",
                "CashierModel.doBuy", e.getMessage());
        theAction = e.getMessage();
      }
      theState = State.process;                   // All Done
      setChanged();
      notifyObservers(theAction);
    }


  /**
   * Customer pays for the contents of the basket
   */
  public void doBought() {
    String theAction = "";
    try {
      if (theBasket != null &&
              !theBasket.isEmpty())            // items > 1
      {                                       // T
        theOrder.newOrder(theBasket);       //  Process order
        int on = theBasket.getOrderNum();
        orderList.put(String.valueOf(on), pn);
        customerEmail = CashierView.getEmail();
        generateReceipt(customerEmail,String.valueOf(on));
        theBasket = null;                     //  reset
      }                                       //
      theAction = "Next customer";            // New Customer
      theState = State.process;               // All Done
      theBasket = null;
    } catch (OrderException e) {
      DEBUG.error("%s\n%s",
              "CashierModel.doCancel", e.getMessage());
      theAction = e.getMessage();
    } catch (StockException e) {
        throw new RuntimeException(e);
    }
      theBasket = null;
    setChanged();
    notifyObservers(theAction); // Notify
    productNumbers.clear();
} /**
   * ask for update of view callled at start of day
   * or after system reset
   */
  public void askForUpdate() {
    setChanged();
    notifyObservers("Welcome");
  }

  /**
   * make a Basket when required
   */
  private void makeBasketIfReq() {
    if (theBasket == null) {
      try {
        int uon = theOrder.uniqueNumber();     // Unique order num.
        theBasket = makeBasket();                //  basket list
        theBasket.setOrderNum(uon);            // Add an order number
      } catch (OrderException e) {
        DEBUG.error("Comms failure\n" +
                "CashierModel.makeBasket()\n%s", e.getMessage());
      }
    }
  }

  /**
   * return an instance of a new Basket
   *
   * @return an instance of a new Basket
   */
  protected Basket makeBasket() {
    return new Basket();
  }


  public static HashMap<String, String> getOrderList() {
    return orderList;
  }

  public static int getTotalIncome() {
    return totalIncome;
  }

  public static void minusTotalIncome(int amount) {  totalIncome = totalIncome - amount; }

  public void generateReceipt(String email, String orderNumber) throws StockException {
    Product pr;
    double price = 0;
    ArrayList<String> products = new ArrayList<>();

  for (String pn : productNumbers) {
      pr = theStock.getDetails(pn);
      products.add(pr.getDescription());
      price = price + pr.getPrice();
  }

    Receipt theReceipt = new Receipt(email,orderNumber,price,products,productNumbers);

    theReceipt.printReceipt();

    receipts.add(theReceipt);

  }

  public static ArrayList<Receipt> getReceipts() {
    return receipts;
  }

  public void doCancel() {
    String theAction = "Next Customer";
    theBasket.clear();
    setChanged(); notifyObservers(theAction);
  }

}