package de.uniba.soa;

import de.uniba.soa.data.DataStore;
import de.uniba.soa.model.Beverage;

import de.uniba.soa.model.dto.rest.BeverageDTO;
import de.uniba.soa.model.dto.rest.BeverageListResponse;
import de.uniba.soa.model.dto.rest.Pagination;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.stream.Collectors;

@Path("/beverages")
@Produces(MediaType.APPLICATION_JSON)
public class BeverageResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBeverages(@Context UriInfo uriInfo,
                                 @QueryParam("minPrice") Double minPrice,
                                 @QueryParam("maxPrice") Double maxPrice,
                                 @QueryParam("page") @DefaultValue("1") int page,
                                 @QueryParam("size") @DefaultValue("10") int size) {

        List<Beverage> all = DataStore.getInstance().getBeverages();
        List<Beverage> filtered = all.stream()
                .filter(b -> (minPrice == null || b.getPrice() >= minPrice) &&
                        (maxPrice == null || b.getPrice() <= maxPrice))
                .collect(Collectors.toList());

        int totalItems = filtered.size();
        int fromIndex = Math.min((page - 1) * size, totalItems);
        int toIndex = Math.min(fromIndex + size, totalItems);
        List<Beverage> paged = filtered.subList(fromIndex, toIndex);

        List<BeverageDTO> dtos = paged.stream().map(b -> {
            BeverageDTO dto = BeverageDTO.from(b);
            String beverageHref = uriInfo.getBaseUriBuilder()
                    .path("beverages")
                    .path(String.valueOf(dto.getId()))
                    .build()
                    .toString();
            dto.setHref(beverageHref);
            return dto;
        }).collect(Collectors.toList());

        BeverageListResponse response = new BeverageListResponse();
        response.setBeverages(dtos);
        int totalPages = (int) Math.ceil((double) totalItems / size);
        response.setPagination(new Pagination(page, size, totalItems, totalPages));
        response.setHref(uriInfo.getRequestUri().toString());

        return Response.ok(response).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addBeverage(BeverageDTO dto) {
        if (dto.getName() == null || dto.getType() == null || dto.getPrice() <= 0 || dto.getQuantity() < 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid beverage data").build();
        }

        int id = DataStore.getInstance().getNextBeverageId();
        Beverage beverage = new Beverage(id, dto.getName(), dto.getType(), dto.getPrice(), dto.getQuantity());
        DataStore.getInstance().addBeverage(beverage);

        return Response.status(Response.Status.CREATED).entity(beverage).build();
    }


    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBeverage(@PathParam("id") int id, BeverageDTO dto) {
        Beverage existing = DataStore.getInstance().getBeverageById(id);

        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Beverage with ID " + id + " not found").build();
        }

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getType() != null) existing.setType(dto.getType());
        if (dto.getPrice() > 0) existing.setPrice(dto.getPrice());
        if (dto.getQuantity() >= 0) existing.setQuantity(dto.getQuantity());

        return Response.ok(existing).build();
    }


}
