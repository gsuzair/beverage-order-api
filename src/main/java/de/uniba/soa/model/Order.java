package de.uniba.soa.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class Order {
    private String id;
    private List<OrderItem> items;
    private String status; // SUBMITTED or PROCESSED

    public Order() {}

    public Order(String id, List<OrderItem> items, String status) {
        this.id = id;
        this.items = items;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
