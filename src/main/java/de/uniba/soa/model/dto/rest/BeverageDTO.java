package de.uniba.soa.model.dto.rest;

import de.uniba.soa.model.Beverage;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BeverageDTO {
    private Integer id;
    private String name;
    private double price;
    private int quantity;
    private String type;
    private String href;

    public BeverageDTO() {
    }

    public BeverageDTO(Integer id, String name, double price, int quantity, String type) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.type = type;
    }

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

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public static BeverageDTO from(Beverage beverage) {
        BeverageDTO dto = new BeverageDTO();
        dto.setId(beverage.getId());
        dto.setName(beverage.getName());
        dto.setType(beverage.getType());
        dto.setPrice(beverage.getPrice());
        dto.setQuantity(beverage.getQuantity());
        return dto;
    }
}