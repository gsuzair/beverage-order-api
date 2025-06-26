package de.uniba.soa;

import de.uniba.soa.data.DataStore;
import de.uniba.soa.model.Beverage;
import de.uniba.soa.model.Order;
import de.uniba.soa.model.OrderItem;
import de.uniba.soa.model.dto.rest.OrderDTO;
import de.uniba.soa.model.dto.rest.OrderItemDTO;
import de.uniba.soa.model.dto.rest.OrderListResponse;
import de.uniba.soa.model.dto.rest.Pagination;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    private final DataStore dataStore = DataStore.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrders(@Context UriInfo uriInfo,
                              @QueryParam("page") @DefaultValue("1") int page,
                              @QueryParam("size") @DefaultValue("10") int size) {

        List<Order> allOrders = DataStore.getInstance().getOrders();

        int totalItems = allOrders.size();
        int fromIndex = Math.min((page - 1) * size, totalItems);
        int toIndex = Math.min(fromIndex + size, totalItems);
        List<Order> pagedOrders = allOrders.subList(fromIndex, toIndex);

        List<OrderDTO> dtos = pagedOrders.stream()
                .map(OrderDTO::fromOrder)
                .toList();

        OrderListResponse response = new OrderListResponse();
        response.setOrders(dtos);
        int totalPages = (int) Math.ceil((double) totalItems / size);
        response.setPagination(new Pagination(page, size, totalItems, totalPages));
        response.setHref(uriInfo.getRequestUri().toString());

        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    public Response getOrderById(@PathParam("id") String id) {
        Order order = dataStore.getOrderById(id);

        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Order with ID " + id + " not found.")
                    .build();
        }

        OrderDTO dto = OrderDTO.fromOrder(order);
        return Response.ok(dto).build();
    }

    @POST
    public Response createOrder(OrderDTO orderDTO) {
        if (orderDTO == null || orderDTO.getItems() == null || orderDTO.getItems().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Order must contain at least one item.").build();
        }

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            if (itemDTO.getBeverageId() == null || itemDTO.getBeverageId() < 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid beverage ID: " + itemDTO.getBeverageId()).build();
            }

            // Validate quantity
            if (itemDTO.getQuantity() == null || itemDTO.getQuantity() <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Quantity must be a positive number for beverage ID: " + itemDTO.getBeverageId()).build();
            }

            // Check if beverage exists
            Beverage beverage = DataStore.getInstance().getBeverageById(itemDTO.getBeverageId());
            if (beverage == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Beverage not found: " + itemDTO.getBeverageId()).build();
            }

            if (beverage.getQuantity() < itemDTO.getQuantity()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Not enough stock for beverage: " + beverage.getName()).build();
            }

            beverage.setQuantity(beverage.getQuantity() - itemDTO.getQuantity());

            orderItems.add(new OrderItem(itemDTO.getBeverageId(), itemDTO.getQuantity()));
        }

        String orderId = UUID.randomUUID().toString();
        Order order = new Order(orderId, orderItems, "SUBMITTED");
        DataStore.getInstance().addOrder(order);

        return Response.status(Response.Status.CREATED)
                .entity(OrderDTO.fromOrder(order))
                .build();
    }



    @PUT
    @Path("/{id}")
    public Response updateOrder(@PathParam("id") String id, OrderDTO updatedOrderDTO) {
        Order existingOrder = DataStore.getInstance().getOrderById(id);

        if (existingOrder == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Order not found").build();
        }

        if (!existingOrder.getStatus().equals("SUBMITTED")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Only SUBMITTED orders can be modified").build();
        }

        for (OrderItem oldItem : existingOrder.getItems()) {
            Beverage beverage = DataStore.getInstance().getBeverageById(oldItem.getBeverageId());
            if (beverage != null) {
                beverage.setQuantity(beverage.getQuantity() + oldItem.getQuantity());
            }
        }

        List<OrderItem> newItems = new ArrayList<>();
        for (OrderItemDTO itemDTO : updatedOrderDTO.getItems()) {
            Beverage beverage = DataStore.getInstance().getBeverageById(itemDTO.getBeverageId());

            if (beverage == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid beverage ID: " + itemDTO.getBeverageId()).build();
            }

            if (beverage.getQuantity() < itemDTO.getQuantity()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Not enough stock for: " + beverage.getName()).build();
            }

            beverage.setQuantity(beverage.getQuantity() - itemDTO.getQuantity());
            newItems.add(new OrderItem(itemDTO.getBeverageId(), itemDTO.getQuantity()));
        }

        existingOrder.setItems(newItems);
        return Response.ok(OrderDTO.fromOrder(existingOrder)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response cancelOrder(@PathParam("id") String id) {
        Order order = DataStore.getInstance().getOrderById(id);

        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Order not found").build();
        }

        if (!"SUBMITTED".equals(order.getStatus())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Only SUBMITTED orders can be cancelled").build();
        }

        for (OrderItem item : order.getItems()) {
            Beverage beverage = DataStore.getInstance().getBeverageById(item.getBeverageId());
            if (beverage != null) {
                beverage.setQuantity(beverage.getQuantity() + item.getQuantity());
            }
        }

        DataStore.getInstance().removeOrder(id);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}/process")
    public Response processOrder(@PathParam("id") String id) {
        Order order = DataStore.getInstance().getOrderById(id);

        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Order not found").build();
        }

        if (!"SUBMITTED".equals(order.getStatus())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Only SUBMITTED orders can be processed").build();
        }

        order.setStatus("PROCESSED");

        return Response.ok(order).build();
    }


}
