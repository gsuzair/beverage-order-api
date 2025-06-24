package de.uniba.soa.model;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Beverage {
    private Integer id;
    private String name;
    private String type; // e.g. "bottle" or "crate"
    private double price;
    private int quantity;

    public Beverage() {}

    public Beverage(Integer id, String name, String type, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
    }

    // 🧠 Here's what you need:
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
