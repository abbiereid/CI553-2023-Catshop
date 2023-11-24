package clients.returns;

import middle.StockException;

/**
 * The Collection Controller
 * @author M A Smith (c) June 2014
 */

public class ReturnsController
{
  private ReturnsModel model = null;
  private ReturnsView view  = null;

  /**
   * Constructor
   * @param model The model 
   * @param view  The view from which the interaction came
   */
  public ReturnsController(ReturnsModel model, ReturnsView view )
  {
    this.view  = view;
    this.model = model;
  }

  /**
   * Collect interaction from view
   * @param orderNum The order collected
   */
  public void doSearch( String orderNum ) throws StockException { model.doSearch(orderNum);
  }

  public void doReturn(String orderNum) throws StockException { model.doSearch(orderNum);
  }
}


