package de.uniba.soa;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import jakarta.ws.rs.core.UriBuilder;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
        URI uri = UriBuilder.fromUri("http://localhost/").port(8080).build();
        ResourceConfig config = new ApplicationConfig();

        // Start the server but don't auto-start yet
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri, config, false);

        // Add static file support for Swagger UI
        String swaggerPath = Paths.get("src/main/webapp/swagger-ui/").toAbsolutePath().toString();
        StaticHttpHandler staticHandler = new StaticHttpHandler(swaggerPath);
        server.getServerConfiguration().addHttpHandler(staticHandler, "/swagger-ui");

        // Start the server
        server.start();
        System.out.println("Server started at http://localhost:8080/api/");
        System.out.println("Swagger UI available at http://localhost:8080/swagger-ui/");

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
    }
}
