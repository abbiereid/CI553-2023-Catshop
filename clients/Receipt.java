package clients;

import java.util.ArrayList;

public class Receipt {
    private String email;
    private String orderNumber;
    private double price;
    private ArrayList<String> products;
    private ArrayList<String> productNumbers;

    public Receipt(String email, String orderNumber,double price,ArrayList<String> products,ArrayList<String> productNumbers){
        this.email = email;
        this.orderNumber = orderNumber;
        this.price = price;
        this.products = products;
        this.productNumbers = productNumbers;
    }

    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }

    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getOrderNumber() { return orderNumber; }

    public void setPrice(double price) { this.price = price; }
    public double getPrice() { return price; }

    public void setProducts(ArrayList<String> products) { this.products = products; }
    public ArrayList<String> getProducts() { return products; }

    public void setProductNo(ArrayList<String> productNumbers) { this.productNumbers = productNumbers; }
    public ArrayList<String> getProductNumbers() { return productNumbers; }

    public void printReceipt(){

        System.out.print("CUSTOMER RECEIPT \n ---------------------- \n" +
                "email : " + email + "\n" +
                "Order number : " + orderNumber + "\n" +
                "Total paid : £" + price + "\n" +
                "Products : " + products + "\n" +
                "Product Numbers : " + productNumbers);
    }
}
