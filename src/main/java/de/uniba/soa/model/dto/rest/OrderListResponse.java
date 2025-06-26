package de.uniba.soa.model.dto.rest;

import java.util.List;

public class OrderListResponse {

    private Pagination pagination;
    private List<OrderDTO> orders;
    private String href;

    public OrderListResponse() {}

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public List<OrderDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderDTO> orders) {
        this.orders = orders;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
