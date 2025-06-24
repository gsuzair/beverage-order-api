package de.uniba.soa;

import de.uniba.soa.data.DataStore;
import de.uniba.soa.model.Beverage;

import de.uniba.soa.model.dto.rest.BeverageDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/beverages")
@Produces(MediaType.APPLICATION_JSON)
public class BeverageResource {

    @GET
    public Response getBeverages(
            @QueryParam("minPrice") Double minPrice,
            @QueryParam("maxPrice") Double maxPrice,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        List<Beverage> allBeverages = DataStore.getInstance().getBeverages();
        if (minPrice != null) {
            allBeverages = allBeverages.stream()
                    .filter(b -> b.getPrice() >= minPrice)
                    .toList();
        }

        if (maxPrice != null) {
            allBeverages = allBeverages.stream()
                    .filter(b -> b.getPrice() <= maxPrice)
                    .toList();
        }

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, allBeverages.size());
        if (fromIndex >= allBeverages.size()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Page number out of range").build();
        }

        List<Beverage> paginated = allBeverages.subList(fromIndex, toIndex);
        return Response.ok(paginated).build();
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
