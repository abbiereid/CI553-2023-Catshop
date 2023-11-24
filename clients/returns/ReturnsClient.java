package clients.returns;

import middle.MiddleFactory;
import middle.Names;
import middle.RemoteMiddleFactory;

import javax.swing.*;

/**
 * The standalone Collection Client.
 * @author  Mike Smith University of Brighton
 * @version 2.0
 */


public class ReturnsClient
{
   public static void main (String args[])
   {
     String stockURL = args.length < 1     // URL of stock RW
                     ? Names.STOCK_RW      //  default  location
                     : args[0];            //  supplied location
     String orderURL = args.length < 2     // URL of order
                     ? Names.ORDER         //  default  location
                     : args[1];            //  supplied location
     
    RemoteMiddleFactory mrf = new RemoteMiddleFactory();
    mrf.setStockRWInfo( stockURL );
    mrf.setOrderInfo  ( orderURL );        //
    displayGUI(mrf);                       // Create GUI
  }
  
  private static void displayGUI(MiddleFactory mf)
  {     
    JFrame  window = new JFrame();
		     
    window.setTitle( "Returns Client (MVC RMI)");
    window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		    
	ReturnsModel model = new ReturnsModel(mf);
	ReturnsView view  = new ReturnsView( window, mf, 0, 0 );
	ReturnsController cont  = new ReturnsController( model, view );
	view.setController( cont );

	model.addObserver( view );       // Add observer to the model
	window.setVisible(true);         // Display Screen
  }
}
