package de.uniba.soa.model.dto.rest;

import de.uniba.soa.model.OrderItem;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OrderItemDTO {
    private Integer beverageId;
    private int quantity;

    public OrderItemDTO() {
    }

    public OrderItemDTO(Integer beverageId, int quantity) {
        this.beverageId = beverageId;
        this.quantity = quantity;
    }

    public Integer getBeverageId() {
        return beverageId;
    }

    public void setBeverageId(Integer beverageId) {
        this.beverageId = beverageId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static OrderItemDTO fromOrderItem(OrderItem item) {
        return new OrderItemDTO(item.getBeverageId(), item.getQuantity());
    }
}