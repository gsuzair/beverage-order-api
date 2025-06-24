package de.uniba.soa;

import de.uniba.soa.data.DataStore;
import de.uniba.soa.model.Beverage;
import de.uniba.soa.model.Order;
import de.uniba.soa.model.OrderItem;
import de.uniba.soa.model.dto.rest.OrderDTO;
import de.uniba.soa.model.dto.rest.OrderItemDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    private final DataStore dataStore = DataStore.getInstance();

    @GET
    public Response getOrders() {
        List<Order> orders = DataStore.getInstance().getOrders();

        List<OrderDTO> orderDTOs = orders.stream()
                .map(OrderDTO::fromOrder)
                .toList();

        return Response.ok(orderDTOs).build();
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
        List<OrderItem> orderItems = new ArrayList<>();

        // Validate and process each item
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            Beverage beverage = DataStore.getInstance().getBeverageById(itemDTO.getBeverageId());

            if (beverage == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid beverage ID: " + itemDTO.getBeverageId()).build();
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
                .entity(OrderDTO.fromOrder(order)).build();
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
