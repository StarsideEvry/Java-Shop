package Java_Shop;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class Fiscal {
    int id;
    Clerk seller;
    Date sale_made;
    ArrayList<Stock> items;
    ArrayList<Double> total_cost;
    Double money_given;

    public Fiscal(int ID_fiscal, Clerk seller_in, Date sale_of_fiscal, ArrayList<Stock> sold_items, ArrayList<Double> total_cost_fiscal, Double money_given_fiscal) {
        id = ID_fiscal;
        seller = seller_in;
        sale_made = sale_of_fiscal;
        items = sold_items;
        total_cost = total_cost_fiscal;
        money_given = money_given_fiscal;
    }

    public void saveReceipt() {
        File receipt_file = new File("Receipt" + id + ".txt");
        try {
            Double sale_price = 0.0;
            receipt_file.createNewFile();
            FileWriter writer = new FileWriter(receipt_file);
            writer.write("ID: " + id + "\r\n");
            writer.write("Seller: " + seller.name + " ID: " + seller.id + "\r\n");
            writer.write("Date: " + sale_made + "\r\n");
            if (items.size() == total_cost.size()) {
                for (int i = 0; i < items.size(); i++) {
                    writer.write("Item: " + items.get(i).name + " Cost: " + total_cost.get(i) + "\r\n");
                    sale_price += total_cost.get(i);
                }
            }
            writer.write("Given cash: " + money_given + "\r\n");
            writer.write("Change: " + (money_given - sale_price));
            writer.close();
        } catch (IOException Z) {
            System.out.println("Can't save the receipt");
        }
    }

    public void readReceipt() { //deserialize + read a file and print receipt
        
    }
}