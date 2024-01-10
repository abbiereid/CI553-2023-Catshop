package clients.returns;

import catalogue.Product;
import clients.Receipt;
import clients.backDoor.BackDoorModel;
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
  private HashMap<String, String> orderList = CashierModel.getOrderList();
  private StockReadWriter theStock = null;
  private String results = "";
  private static int numberOfReturns;


  public ReturnsModel(MiddleFactory mf) throws StockException, OrderException {
      theOrder = mf.makeOrderProcessing();
      theStock = mf.makeStockReadWriter();
  }

  public void doSearch(String orderNumber) throws StockException {
    String on = orderNumber.trim();


    if (orderList.containsKey(on)) {
      String pn = orderList.get(on);
      Product pr = theStock.getDetails(pn);
      results = "Order Number "+ on + ": " + pr.getDescription() + " , £" + pr.getPrice();
      theAction = "Results: ";
      theOutput = theAction;
    }  else {
      theAction = "Order #" + on + " not found in collected orders";
      theOutput = theAction;
    }

    setChanged();
    notifyObservers(theAction);
  }

  public void doReturn(String orderNumber) throws StockException {
    Receipt theReceipt = null;
    String on = orderNumber.trim();
    ArrayList<Receipt> receipts = CashierModel.getReceipts();

    for(Receipt receipt : receipts) {
      if (receipt.getOrderNumber().equals(on)) {
        theReceipt = receipt;
        break;
      }
    }

    ArrayList<String> productNumbers = theReceipt.getProductNumbers();
    double price = theReceipt.getPrice();

    CashierModel.minusTotalIncome((int) price);

    for (String pn : productNumbers) {
      theStock.addStock(pn, 1);
      setChanged();
    }

    numberOfReturns++;
    doClear();
  }

  public String getResults() {
    return results;
  }

  public static int getNumberOfReturns() { return numberOfReturns; }

  public void doClear() {
    results = "";
    setChanged(); notifyObservers(theAction);
  }

}

