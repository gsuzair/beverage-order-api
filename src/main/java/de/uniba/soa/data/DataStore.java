package de.uniba.soa.data;

import de.uniba.soa.model.Beverage;
import de.uniba.soa.model.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class DataStore {

    private static final DataStore instance = new DataStore();

    private final List<Beverage> beverages = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();

    private DataStore() {
        // Sample beverages
        beverages.add(new Beverage(1, "Coke", "bottle", 1.5, 20));
        beverages.add(new Beverage(2, "Pepsi", "bottle", 1.4, 15));
        beverages.add(new Beverage(3, "Water", "crate", 5.0, 10));
        beverages.add(new Beverage(4, "Orange Juice", "bottle", 2.0, 8));
    }

    public static DataStore getInstance() {
        return instance;
    }

    public List<Beverage> getBeverages() {
        return Collections.unmodifiableList(beverages);
    }

    public Beverage getBeverageById(Integer id) {
        return beverages.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public Order getOrderById(String id) {
        return orders.stream()
                .filter(o -> o.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    public void removeOrder(String id) {
        orders.removeIf(o -> o.getId().equals(id));
    }

    public int getNextBeverageId() {
        return beverages.stream()
                .mapToInt(Beverage::getId)
                .max()
                .orElse(0) + 1;
    }

    public void addBeverage(Beverage beverage) {
        beverages.add(beverage);
    }
}
