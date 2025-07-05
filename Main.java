import java.util.Scanner;
import java.util.*;
interface Shippable {
    String getName();
    double getWeight();
}
// main product
class Product {
    String name;
    double price;
    int quantity;
    // int isExpire;
    // int isShipping;
    Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}
class ExpirableProduct extends Product {
    boolean expired;
    ExpirableProduct(String name, double price, int quantity, boolean expired) {
        super(name, price, quantity);
        this.expired = expired;
    }
}
class ShippableProduct extends Product implements Shippable {
    double weight;

    ShippableProduct(String name, double price, int quantity, double weight) {
        super(name, price, quantity);
        this.weight = weight;
    }
    public String getName() {
       return name;
    }
    public double getWeight() {
       return weight;
    }
}

class ExpirableShippableProduct extends ExpirableProduct implements Shippable {
    double weight;
    ExpirableShippableProduct(String name, double price, int quantity, boolean expired, double weight) {
        super(name, price, quantity, expired);
        this.weight = weight;
    }
    public String getName() { 
      return name; 
    }
    public double getWeight() { 
      return weight; 
    }
}
class CartItem {
    Product product;
    int quantity;
    CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}
class Cart {
    List<CartItem> items = new ArrayList<>();
    void add(Product product, int quantity) throws Exception {
        if (quantity > product.quantity) throw new Exception("Not enough stock");
        items.add(new CartItem(product, quantity));
    }

    boolean isEmpty() {
       if(items.isEmpty()){
        return true;
       } else{
        return false;
       }
      }
}
class Customer {
    double balance;
    Customer(double balance) { 
      this.balance = balance; 
    }
}
class ShippingService {
    static void ship(List<Shippable> items) {
        System.out.println("** Shipment notice **");
        double totalWeight = 0;
        for (Shippable item : items) {
            System.out.println(item.getName());
            totalWeight += item.getWeight();
        }
        System.out.printf("Total package weight %.1fkg\n", totalWeight);
    }
}
class CheckoutService {
    static void checkout(Customer customer, Cart cart) throws Exception {
        if (cart.isEmpty()) throw new Exception("Cart is empty!");
        double subtotal = 0;
        double shipping = 0;
        List<Shippable> toShip = new ArrayList<>();
        for (CartItem item : cart.items) {
            if (item.product instanceof ExpirableProduct) {
                ExpirableProduct exp = (ExpirableProduct) item.product;
                if (exp.expired) throw new Exception(item.product.name + " expired");
            }
            if (item.quantity > item.product.quantity) throw new Exception(item.product.name + " out of stock");
            subtotal += item.product.price * item.quantity;
            item.product.quantity -= item.quantity;

            if (item.product instanceof Shippable) {
                for (int i = 0; i < item.quantity; i++) {
                    toShip.add((Shippable) item.product);
                }
            }
        }

        if (!toShip.isEmpty()) shipping = 30;
        double total = subtotal + shipping;
        if (customer.balance < total) throw new Exception("Not balance");

        customer.balance -= total;

        if (!toShip.isEmpty()) ShippingService.ship(toShip);

        System.out.println("** Checkout receipt **");
        for (CartItem item : cart.items) {
            System.out.printf("%dx %s %.0f\n", item.quantity, item.product.name, item.product.price * item.quantity);
        }
        System.out.println("--------------------------------------");
        System.out.printf("Subtotal %.0f\n", subtotal);
        System.out.printf("Shipping %.0f\n", shipping);
        System.out.printf("amount %.0f\n", total);
        System.out.printf("balance %.0f\n", customer.balance);
    }
}
public class Main {
    public static void main(String[] args) throws Exception {
        ExpirableShippableProduct cheese = new ExpirableShippableProduct("Cheese", 100, 5, false, 0.2);
        // ExpirableShippableProduct cheese = new ExpirableShippableProduct("Cheese", 100, 5, true, 1.1);
        ExpirableShippableProduct biscuits = new ExpirableShippableProduct("Biscuits", 150, 3, false, 0.7);
        Product scratchCard = new Product("Scratch Card", 50, 10);
        ShippableProduct tv = new ShippableProduct("TV", 1000, 2, 10);

        Customer customer = new Customer(2000);
        Cart cart = new Cart();
        cart.add(cheese, 2);
        // cart.add(cheese, 8);
        cart.add(biscuits, 1);
        cart.add(scratchCard, 1);
        cart.add(tv, 1);

        CheckoutService.checkout(customer, cart);
    }
}

