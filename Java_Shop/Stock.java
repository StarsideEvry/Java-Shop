package Java_Shop;

import java.util.Date;

public class Stock {
    int id;
    String name;
    double restock_cost; // restock cost for said item
    Boolean food;
    Date expiry_date;

    public Stock(int new_ID, String new_name, double new_restock, Date end_date) {
    id = new_ID;
    name = new_name;
    restock_cost = new_restock;
    food = true;
    expiry_date = end_date;
    }

    public Stock(int new_ID, String new_name, double new_restock) {
    id = new_ID;
    name = new_name;
    restock_cost = new_restock;
    food = false;
    expiry_date = null;
    }
}