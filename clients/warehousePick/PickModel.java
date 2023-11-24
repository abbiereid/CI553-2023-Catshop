package clients.warehousePick;

import catalogue.Basket;
import debug.DEBUG;
import middle.MiddleFactory;
import middle.OrderException;
import middle.OrderProcessing;
import middle.StockReadWriter;

import java.util.Observable;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implements the Model of the warehouse pick client
 * @author  Mike Smith University of Brighton
 * @version 1.0
 */
public class PickModel extends Observable
{
  private AtomicReference<Basket> theBasket = new AtomicReference<>(); 

  private StockReadWriter theStock   = null;
  private OrderProcessing theOrder   = null;
  private String          theAction  = "";
  
  private StateOf         worker   = new StateOf();

  private  int  timer = 60;
  private static int totalPicked = 0;
  private static int latepicked = 0;
  private boolean lateOrder = false;

  /*
   * Construct the model of the warehouse pick client
   * @param mf The factory to create the connection objects
   */
  public PickModel(MiddleFactory mf)
  {
    try                                     // 
    {      
      theStock = mf.makeStockReadWriter();  // Database access
      theOrder = mf.makeOrderProcessing();  // Process order
    } catch ( Exception e )
    {
      DEBUG.error("CustomerModel.constructor\n%s", e.getMessage() );
    }

    theBasket.set( null );                  // Initial Basket
    // Start a background check to see when a new order can be picked
    new Thread( () -> checkForNewOrder() ).start();
    new Thread( () -> setTimer() ).start();

  }
  
  
  /**
   * Semaphore used to only allow 1 order
   * to be picked at once by this person
   */
  class StateOf
  {
    private boolean held = false;
    
    /**
     * Claim exclusive access
     * @return true if claimed else false
     */
    public synchronized boolean claim()   // Semaphore
    {
      return held ? false : (held = true);
    }
    
    /**
     * Free the lock
     */
    public synchronized void free()     //
    {
      assert held;
      held = false;
    }

  }
  
  /**
   * Method run in a separate thread to check if there
   * is a new order waiting to be picked and we have
   * nothing to do.
   */
  private void checkForNewOrder()
  {
    while ( true )
    {
      try
      {
        boolean isFree = worker.claim();     // Are we free
        if ( isFree )                        // T
        {                                    //
          Basket sb = 
            theOrder.getOrderToPick();       //  Order 
          if ( sb != null )                  //  Order to pick
          {                                  //  T
            theBasket.set(sb);               //   Working on
            theAction = "Order to pick";     //   what to do
            timer = 60;
          } else {                           //  F
            worker.free();                   //  Free
            theAction = "";                  // 
          }
        }
          setChanged(); notifyObservers(theAction);
          Thread.sleep(2000);
      } catch ( Exception e )
      {
        DEBUG.error("%s\n%s",                // Eek!
           "BackGroundCheck.run()\n%s",
           e.getMessage() );
      }
    }
  }
  
  
  /**
   * Return the Basket of products that are to be picked
   * @return the basket
   */
  public Basket getBasket()
  {
    return theBasket.get();
  }

  /**
   * Process a picked Order
   */
  public void doPick()
  {
    String theAction = "";
    try
    {
      Basket basket =  theBasket.get();       // Basket being picked
      if ( basket != null )                   // T
      {
        theBasket.set( null );                //  Picked
        int no = basket.getOrderNum();        //  Order no
        theOrder.informOrderPicked( no );     //  Tell system
        theAction = "";                       //  Inform picker
        worker.free();                        //  Can pick some more
        totalPicked++;
        System.out.println(totalPicked);
        lateOrder = false;

      } else if (timer == 0) {
        lateOrder = true;
      } else if (lateOrder) {
        latepicked++;
        System.out.println(latepicked);
      }
      else {                                // F
              theAction = "No order to pick";//   Not picked order
      }
      setChanged(); notifyObservers(theAction);
    }
    catch ( OrderException e )                // Error
    {                                         //  Of course
      DEBUG.error( "PickModel.doPick()\n%s\n",//  should not
                            e.getMessage() ); //  happen
    }
    setChanged(); notifyObservers(theAction);
  }

  public void setTimer() {
    timer = 60;
    while (timer > 0) {
      try {
        Thread.sleep(1000);
        timer--;
        setChanged();
        notifyObservers(theAction);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }


  public int getTimer() {
    return timer;
  }

  public static int getTotalPicked() {
    return totalPicked;
  }

  public static int getLatePicked() {
    return latepicked;
  }
}




