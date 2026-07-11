package com.techie.microservice.gateway.routes;

import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import javax.print.DocFlavor;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
public class Routes {

    @Bean
    public RouterFunction<ServerResponse> productServiceRoutes() {
        return route("product-service")
                .route(path("/api/product/**"), http())
                .before(uri("http://localhost:8080"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("productServiceCB",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> productServiceSwaggerRoutes() {
        RouterFunction<ServerResponse> build = route("product-service-swagger")
                .route(path("/aggregate/product-service/v3/api-docs"), http())
                .before(uri("http://localhost:8080"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("productServiceSwaggerCB",
                        URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api-docs"))
                .build();
        return build;
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoutes() {
        return route("order-service")
                .route(path("/api/order/**"), http())
                .before(uri("http://localhost:8081"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("orderServiceCB",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceSwaggerRoutes() {
        RouterFunction<ServerResponse> build = route("order-service-swagger")
                .route(path("/aggregate/order-service/v3/api-docs"), http())
                .before(uri("http://localhost:8081"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("orderServiceSwaggerCB",
                        URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api-docs"))
                .build();
        return build;
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoutes() {
        return route("inventory-service")
                .route(path("/api/inventory/**"), http())
                .before(uri("http://localhost:8082"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCB",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceSwaggerRoutes() {
        RouterFunction<ServerResponse> build = route("inventory-service-swagger")
                .route(path("/aggregate/inventory-service/v3/api-docs"), http())
                .before(uri("http://localhost:8082"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceSwaggerCB",
                        URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api-docs"))
                .build();
        return build;
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute(){
        return route("fallbackRoute")
                .GET("/fallbackRoute", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Service Unavailable, please try again later!"))
                .build();
    }
}