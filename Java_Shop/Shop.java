package Java_Shop;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Date;
import java.util.ArrayList;

public class Shop {
    ArrayList<Clerk> clerks = new ArrayList<Clerk>();
    ArrayList<Stock> stock = new ArrayList<Stock>();
    ArrayList<Stock> sold = new ArrayList<Stock>();
    ArrayList<Fiscal> receipts = new ArrayList<Fiscal>(); 
    Integer[] active_clerks;//cashregisters
    Scanner inp = new Scanner(System.in);
    
    Integer total_number_receipts = 0; // receipt ID
    Double turnover = 0.00; // total earnings for the day from receipts/sold_stock
    Double restock_spending = 0.00;
    
    //HashMap<String, Double> prices = new HashMap<>(); // Dictionary to find the price of a single unique product
    
    Double market_price_food;
    Double market_price_non_food;
    Integer days_before_spoiling_discount;
    Double shelf_life_discount;
    Date current_date;

    int stock_ID = 1;
    int clerk_ID = 1;
    int fiscal_ID = 1;
    final long ms_in_day = 24 * 60 * 60 * 1000;

    //create a class constructor for the Shop Class
    public Shop(int registers, double food_price, double non_food_price, int days_before_spoil, double spoil_discount) {
        active_clerks = new Integer[registers];
        market_price_food = food_price;
        market_price_non_food = non_food_price;
        days_before_spoiling_discount = days_before_spoil;
        shelf_life_discount = spoil_discount;
        current_date = new Date();
    }

    public void printTotalSales() { // kalkulira razhodi za zaplati, dostavka na stoki, printira tova + prihodi
        System.out.println("Restock costs: " + restock_spending);
        Double wages = 0.00;
        for (Clerk clerk: clerks) {
            for (Integer register: active_clerks) {
                if (clerk.id == register) {
                    wages += clerk.salary * 12;
                    break;
                } 
            }
        }
        System.out.println("Salaries costs: " + wages);
        System.out.println("Sold Products: " + turnover);
        System.out.println("Total income for day: " + (turnover - wages - restock_spending));

    }

    public void addStock(int quantity, String name_stock, Double cost_of_stock, Boolean stock_is_food, Date expiry_date) { // DONE
        // za restockvane (incrementira stock spending) i dobavq producta v stock, ako go nqma v Hastable, dobavi nov entry v Hastable
        restock_spending += quantity * cost_of_stock;

        if (stock_is_food) {
            main.prices.put(name_stock, cost_of_stock * market_price_food);
        } else {
            main.prices.put(name_stock, cost_of_stock * market_price_non_food);
        }

        for (int i = 0; i < quantity; i++) {
            if (stock_is_food) {
                stock.add(new Stock(stock_ID, name_stock, cost_of_stock, expiry_date));
                stock_ID++;
                System.out.println("Added BEG");
            } else {
                stock.add(new Stock(stock_ID, name_stock, cost_of_stock));
                stock_ID++;
            }
        }

    }

    public void addClerk(String name_clerk, Double salary_clerk) { // DONE
        new Clerk(clerk_ID, name_clerk, salary_clerk);
        clerks.add(new Clerk(clerk_ID, name_clerk, salary_clerk));
        clerk_ID++;
    }

    public Boolean checkInRegister(int clerk_id, int register_num) { // DONE
        if(clerk_id < 1 || clerk_id >= clerk_ID) {
            System.out.println("INVALID CLERK ID! Try Again!");
            return false;
        }
        if (register_num < 0 || register_num >= active_clerks.length) {
            System.out.println("INVALID CASH REGISTER! Try Again!");
            return false;
        }
        active_clerks[register_num] = clerk_id;
        return true;
    }

    public Boolean makeSale(int sale_register_num, ArrayList<String> items_to_buy, Double given_money) { // DONE
        // proverka pari(proverka za otstypki), proverka kolichestvo, throw exception if missing certain item else receipt
        if (sale_register_num < 0 || sale_register_num >= active_clerks.length) {
            System.out.println("INVALID CASH REGISTER! Try Again!");
            return false;
        }

        Integer current_seller_ID = active_clerks[sale_register_num];
        Clerk current_seller = null;

        if (current_seller_ID != null) {
            for (Clerk seller : clerks) {
                if (seller.id == current_seller_ID) {
                    current_seller = seller;
                }
            }
            if (current_seller == null) {
                System.out.println("NO CLERK MATCHING ID! Try Again!");
                return false;
            }
        } else {
            System.out.println("NO CLERK AT REGISTER! Try Another Register!");
            return false;
        }
        
        Double sale_cost = 0.00;
        ArrayList<Stock> scanned_items = new ArrayList<Stock>();
        ArrayList<Double> prices_sold_stocks = new ArrayList<Double>();
        for (String item:items_to_buy) {
            Boolean have_item_left = false;
            for (Stock product:stock) {
                if (product.name.equals(item)) {
                    System.out.print(product.id);
                    if (product.expiry_date != null) {
                        System.out.print(" Best By Date: ");
                        System.out.println(product.expiry_date);
                    }
                    have_item_left = true;
                }
            }
            if (have_item_left) {
                Stock chosen_item = takeChoice(item);
                scanned_items.add(chosen_item);
                if (chosen_item.expiry_date == null) {
                    prices_sold_stocks.add(main.prices.get(item));
                    sale_cost += prices_sold_stocks.get(prices_sold_stocks.size()-1);
                } else if (chosen_item.expiry_date.getTime() / ms_in_day - days_before_spoiling_discount <= current_date.getTime() / ms_in_day) {
                    prices_sold_stocks.add(main.prices.get(item) * shelf_life_discount);
                    sale_cost += prices_sold_stocks.get(prices_sold_stocks.size()-1);
                } else {
                    prices_sold_stocks.add(main.prices.get(item));
                    sale_cost += prices_sold_stocks.get(prices_sold_stocks.size()-1);
                }
            } else {
                System.out.println("We only have 2 pieces of bread left, where did you find the 3rd bread");
                return false;
            }
        }

        if (sale_cost <= given_money) {
            //take money, return left over cash, remove stock from our inventory & print a fiscal for purchase
            Double change = given_money - sale_cost;
            turnover += sale_cost;
            for (Stock item: scanned_items) {
                stock.remove(item);
            }
            
            Fiscal current_receipt = new Fiscal(fiscal_ID, current_seller, current_date, scanned_items, prices_sold_stocks, given_money);
            receipts.add(current_receipt); 
            fiscal_ID++;
            current_receipt.saveReceipt();
            System.out.println("Sale finished, return " + change + ", have a nice day !");
        } else {
            System.out.println("Not enough money to make purchase!");
        }
        return true;
    }

    public Stock takeChoice(String item_name) { // DONE
        int chosen_ID = -1;
        do{
            String choice = inp.nextLine();
            try{
                chosen_ID = Integer.parseInt(choice);
            } catch(NumberFormatException nfe) {
                System.out.println("You didn't type in an INTEGER, TRY AGAIN!");
            }
            for (Stock product:stock) {
                if (product.id == chosen_ID) {
                    if (product.name.equals(item_name)) {
                        return product;
                    }
                }
            }
            System.out.println("ID is of INCORRECT Product");
        } while(chosen_ID == -1);     
        return null;
    }

    public void printItems() {
        System.out.println("ID\t Name\t Price\t Expiry Date\t");
        for (Stock stock: stock) {
            System.out.println(stock.id + "\t" + stock.name + "\t" + main.prices.get(stock.name) + "\t" + stock.expiry_date);
        }
    }

    public void printClerks() {
        System.out.println("ID\t Name\t Salary\t");
        for (Clerk clerk: clerks) {
            System.out.println(clerk.id + "\t" + clerk.name + "\t" + clerk.salary);
        }
    }
}