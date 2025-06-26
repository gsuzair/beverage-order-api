package de.uniba.soa;

import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;
import jakarta.ws.rs.ApplicationPath;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class ApplicationConfig extends ResourceConfig  {

    public ApplicationConfig() {
        packages("de.uniba.soa");
    }
}
