package clients.returns;

import middle.MiddleFactory;
import middle.OrderException;
import middle.OrderProcessing;
import middle.StockException;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ReturnsView implements Observer {
    private static final String SEARCH = "Search";
    private static final String RETURN = "Return";

    private static final int H = 300;       // Height of window pixels
    private static final int W = 400;       // Width  of window pixels

    private final JLabel theAction = new JLabel();
    private final JTextField theInput = new JTextField();
    private final JTextArea theOutput = new JTextArea();
    private final JScrollPane theSP = new JScrollPane();
    private final JButton theBtSearch = new JButton(SEARCH);
    private final JButton theBtReturn = new JButton(RETURN);

    private OrderProcessing theOrder = null;
    private ReturnsController cont = null;

    public ReturnsView(RootPaneContainer rpc, MiddleFactory mf, int x, int y) {
        try {
            theOrder = mf.makeOrderProcessing();
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        Container cp = rpc.getContentPane();
        Container rootWindow = (Container) rpc;
        cp.setLayout(null);
        rootWindow.setSize(W, H);
        rootWindow.setLocation(x, y);

        Font f = new Font("Monospaced", Font.PLAIN, 12);

        theBtSearch.setBounds(16, 25, 80, 40);
        theBtSearch.addActionListener(
                e -> {
                    try {
                        cont.doSearch(theInput.getText());
                    } catch (StockException ex) {
                        throw new RuntimeException(ex);
                    }
                });
        cp.add(theBtSearch);

        theBtReturn.setBounds(16, 75,80,40);
        theBtReturn.addActionListener(
                e -> {
                    try {
                        cont.doReturn(theInput.getText());
                    } catch (StockException ex) {
                        throw new RuntimeException(ex);
                    }
                });
        cp.add(theBtReturn);

        theAction.setBounds(110, 25, 270, 20);
        theAction.setText("");
        cp.add(theAction);

        theInput.setBounds(110, 50, 270, 40);
        theInput.setText("");
        cp.add(theInput);

        theSP.setBounds(110, 100, 270, 160);
        theOutput.setText("");
        theOutput.setFont(f);
        cp.add(theSP);
        theSP.getViewport().add(theOutput);
        rootWindow.setVisible(true);
        theInput.requestFocus();
    }

    public void setController(ReturnsController c) {
        cont = c;
    }

    private String listOfOrders(List<Integer> orders) {
        StringBuilder res = new StringBuilder();
        for (int i : orders) {
            res.append(" ").append(i);
        }
        return res.toString();
    }
    @Override
    public void update(Observable modelC, Object arg) {
        ReturnsModel model = (ReturnsModel) modelC;
        String message = (String) arg;
        theAction.setText(message);
        theInput.requestFocus();
        theOutput.setText(model.getResults());
    }

}
