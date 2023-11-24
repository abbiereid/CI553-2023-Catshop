package clients.returns;

import catalogue.Product;
import clients.cashier.CashierModel;
import debug.DEBUG;
import middle.*;

import java.util.*;

/**
 * Implements the Model of the collection client
 * @author  Mike Smith University of Brighton
 * @version 1.0
 */

public class ReturnsModel extends Observable {
  private String theAction = "";
  private String theOutput = "";
  private OrderProcessing theOrder = null;
  private HashMap<String, String> orderList = CashierModel.getOrderList(); // New list to store collected orders
  private StockReadWriter theStock = null;
  private String results = "";

  public ReturnsModel(MiddleFactory mf) {
    try {
      theOrder = mf.makeOrderProcessing();
      theStock = mf.makeStockReadWriter();
    } catch (Exception e) {
      DEBUG.error("%s\n%s", "CollectModel.constructor\n%s", e.getMessage());
    }
  }

  public void doSearch(String orderNumber) throws StockException {
    String on = orderNumber.trim();


    if (orderList.containsKey(on)) {
      // Order found in collected orders
      String pn = orderList.get(on);
      Product pr = theStock.getDetails(pn);
      results = "Order Number "+ on + ": " + pr.getDescription() + " , £" + pr.getPrice();
      theAction = "Results: ";
      theOutput = theAction;
    }  else {
      // Order not found in collected orders
      theAction = "Order #" + on + " not found in collected orders";
      theOutput = theAction;
    }

    setChanged();
    notifyObservers(theAction);
  }

  public void doReturn(String orderNumber) throws StockException {
    String on = orderNumber.trim();


  }


  public String getResults() {
    return results;
  }

}

