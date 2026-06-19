package Java_Shop;

import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class main {
    /*
     * create and initialize shop_object
     * create shop, addStock, addClerks, addDictionary items, da gledame belejki
     * end of day procedure(command)
     */

    // public Shop(int registers, double food_price, double non_food_price, int
    // days_before_spoil, double spoil_discount)
    static Scanner inp = new Scanner(System.in);
    public static HashMap<String, Double> prices = new HashMap<>();
    public static void main(String[] args) {
        
        int registers = -1;
        do {
            System.out.println("Please insert POSITIVE number of REGISTERS in Shop: ");
            String uInp = inp.nextLine();
            try {
                registers = Integer.parseInt(uInp);
            } catch (NumberFormatException nfe) {
                System.out.println("Not a NUMBER, try AGAIN!");
            }
        } while(registers <= 0);

        Double food_price = 0.00;
        do {
            System.out.println("Please insert food MARKUP(1.2 = 20% markup): ");
            String uInp = inp.nextLine();
            try {
                food_price = Double.parseDouble(uInp);
            } catch (NumberFormatException nfe) {
                System.out.println("Not a NUMBER, try AGAIN!");
            }
        } while (food_price < 1);

        Double non_food_price = 0.00;
        do {
            System.out.println("Please insert NON food MARKUP(1.2 = 20% markup): ");
            String uInp = inp.nextLine();
            try {
                non_food_price = Double.parseDouble(uInp);
            } catch (NumberFormatException nfe) {
                System.out.println("Not a NUMBER, try AGAIN!");
            }
        } while (non_food_price < 1);

        int days_before_spoil = -1;
        do {
            System.out.println("Please insert POSITIVE number of days before spoil to apply discount in Shop: ");
            String uInp = inp.nextLine();
            try {
                days_before_spoil = Integer.parseInt(uInp);
            } catch (NumberFormatException nfe) {
                System.out.println("Not a NUMBER, try AGAIN!");
            }
        } while(days_before_spoil <= 0);

        Double spoil_discount = 0.00;
        do {
            System.out.println("Please insert spoil discount(0.8 = 20% discount): ");
            String uInp = inp.nextLine();
            try {
                spoil_discount = Double.parseDouble(uInp);
            } catch (NumberFormatException nfe) {
                System.out.println("Not a NUMBER, try AGAIN!");
            }
        } while (spoil_discount >= 1 || spoil_discount <= 0);
        //Shop shop = new Shop(registers, days_before_spoil, registers, days_before_spoil, days_before_spoil);
        Shop shop = new Shop(registers, food_price, non_food_price, days_before_spoil, spoil_discount);

        int menu_choice = -1;
        do {
            System.out.println("Menu");
            System.out.println("1. Items");
            System.out.println("2. Clerks");
            System.out.println("3. Create Basket");
            System.out.println("0. Exit Shop");
            String uInp = inp.nextLine();
            try {
                menu_choice = Integer.parseInt(uInp);
            } catch (NumberFormatException nfe) {
                System.out.println("Not a NUMBER, try AGAIN!");
            } 
            switch (menu_choice) {
                case 1:
                    menuItems(shop);
                    break;
                case 2:
                    menuClerks(shop);
                    break;
                case 3:
                    menuBasket(shop);
                case 0: 
                    break;

                default:
                    break;
            }
        } while (menu_choice != 0);
    }

    private static void menuItems(Shop shop) {
        int menu_items = -1;
        System.out.println("Items Menu");
        System.out.println("1. Add Stock");
        System.out.println("2. Remove Stock");
        System.out.println("3. Return");
        String uInp = inp.nextLine();
        try {
            menu_items = Integer.parseInt(uInp);
        } catch (NumberFormatException nfe) {
            System.out.println("Not a NUMBER, try AGAIN!");
        }
        switch (menu_items) {
            case 1:
                int quantity = askUserNumber("Enter quantity for Item!");
                String name = askUser("Name of Product!");
                Double price = askUserDouble("Base price of product!");
                Boolean is_food = askUser("Is this a food product or NOT?").toLowerCase().startsWith("y");
                Date expiry_date = null;
                if (is_food) {
                    expiry_date = askUserDate("When is the expiration date (DD/MM/YYYY): ");
                }
                shop.addStock(quantity, name, price, is_food, expiry_date);
                break;
            case 2:
                shop.printItems();
                int id = (askUserNumber("Which item should be REMOVED?"));
                shop.stock.removeIf(s -> s.id == id);
                break;
            case 3:
                break;
            default:
                System.out.println("INVALID DECISION");
                break;
        }
    }

    private static void menuClerks(Shop shop) {
        System.out.println("Clerks Menu");
        System.out.println("0. Clock in Clerk");
        System.out.println("1. Add Clerk");
        System.out.println("2. Remove Clerk");
        System.out.println("3. Return back");
        int menu_clerks = askUserNumber("");
        switch (menu_clerks) {
            case 0:
                Integer ID = askUserNumber("Clerk ID to clock IN!");
                if (ID > 0 || shop.clerk_ID <= ID) {
                    Integer register_number = askUserNumber("Which register do you clock in to?");
                    shop.active_clerks[register_number] = ID;
                } else {
                    System.out.println("WRONG ID!");
                }
                break;
            case 1:
                String name = askUser("Name of Clerk!");
                Double salary = askUserDouble("Salary of clerk!");
                shop.addClerk(name, salary);
                break;
            case 2:
                shop.printClerks();
                int id = (askUserNumber("Which Clerk should be REMOVED?"));
                shop.clerks.removeIf(s -> s.id == id);
                break;
            case 3:
                break;
            default:
                break;
        }
    }

    private static void menuBasket(Shop shop) {
        ArrayList<String> items_to_buy = new ArrayList<String>();
        do {
            System.out.println("Basket Menu");
            prices.forEach((k,v) -> { System.out.println(k + " costing " + v + " each.");}); // k = name // v = value/price
            String name = askUser("Which item do you wish to buy?");
            int number;
            if (prices.containsKey(name)) {
                number = askUserNumber("How many would you like to buy?");
                if (number <= 0) {
                    System.out.println("Please insert a number bigger than 0!");
                    continue;                   
                }
            } else {
                System.out.println("We do NOT offer that item!");
                continue;
            }
            for (int i = 0; i < number; i++) {
                items_to_buy.add(name);
            }
            Boolean continue_to_add = askUser("Do you wish to add more products? y/n").toLowerCase().startsWith("y");
            if (continue_to_add) {
                continue;
            } else {
                int register_number;
                do {
                    register_number = askUserNumber("Which register would you like to go to?");
                    if (register_number < 0 || shop.active_clerks.length <= register_number) {
                        System.out.println("Invalid register");
                        continue;
                    } else {
                        break;
                    }
                } while(true);
                Double money_in_possession = askUserDouble("How much money do you have?");
                shop.makeSale(register_number, items_to_buy, money_in_possession);
                break;
            }
        } while(true);
    }

    static String askUser(String prompt) {
        System.out.println(prompt);
        return inp.nextLine();
    }

    static Integer askUserNumber(String prompt) {
        do {
            try {
                return Integer.parseInt(askUser(prompt));
            } catch (NumberFormatException nfe) {
                System.out.println("NOT a Number! Try Again!");
            }
        } while (true);
    }

    static Double askUserDouble(String prompt) {
        do {
            try {
                return Double.parseDouble(askUser(prompt));
            } catch (NumberFormatException nfe) {
                System.out.println("NOT a Number! Try Again!");
            }
        } while (true);
    }

    static Date askUserDate(String prompt) {
        do {
            try {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.parse(askUser(prompt));
            } catch (ParseException e) {
                System.out.println("Not a DATE, try AGAIN!");
            }
        } while (true);
    }
}