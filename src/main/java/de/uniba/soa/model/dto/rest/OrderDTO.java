package de.uniba.soa.model.dto.rest;

import de.uniba.soa.model.Order;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement
public class OrderDTO {
    private String id;
    private List<OrderItemDTO> items;
    private String status;

    public OrderDTO() {
    }

    public OrderDTO(String id, List<OrderItemDTO> items, String status) {
        this.id = id;
        this.items = items;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Convert from logic model to DTO
    public static OrderDTO fromOrder(Order order) {
        List<OrderItemDTO> dtoItems = order.getItems().stream()
                .map(OrderItemDTO::fromOrderItem)
                .collect(Collectors.toList());

        return new OrderDTO(order.getId(), dtoItems, order.getStatus());
    }
}
