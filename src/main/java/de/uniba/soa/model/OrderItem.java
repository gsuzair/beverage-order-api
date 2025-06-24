package de.uniba.soa.model;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OrderItem {
    private Integer beverageId;
    private int quantity;

    public OrderItem() {}

    public OrderItem(Integer beverageId, int quantity) {
        this.beverageId = beverageId;
        this.quantity = quantity;
    }

    public Integer getBeverageId() { return beverageId; }
    public void setBeverageId(Integer beverageId) { this.beverageId = beverageId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
