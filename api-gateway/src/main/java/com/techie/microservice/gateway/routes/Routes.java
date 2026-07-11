package com.techie.microservice.gateway.routes;

import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
public class Routes {

    @Bean
    public RouterFunction<ServerResponse> productServiceRoutes() {
        return GatewayRouterFunctions.route("product-service")
                .route(path("/api/product/**"), http())
                .before(uri("http://localhost:8080"))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> productServiceSwaggerRoutes() {
        RouterFunction<ServerResponse> build = GatewayRouterFunctions.route("product-service-swagger")
                .route(path("/aggregate/product-service/v3/api-docs"), http())
                .before(uri("http://localhost:8080"))
                .filter(setPath("/api-docs"))
                .build();
        return build;
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoutes() {
        return GatewayRouterFunctions.route("order-service")
                .route(path("/api/order/**"), http())
                .before(uri("http://localhost:8081"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceSwaggerRoutes() {
        RouterFunction<ServerResponse> build = GatewayRouterFunctions.route("order-service-swagger")
                .route(path("/aggregate/order-service/v3/api-docs"), http())
                .before(uri("http://localhost:8081"))
                .filter(setPath("/api-docs"))
                .build();
        return build;
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoutes() {
        return GatewayRouterFunctions.route("inventory-service")
                .route(path("/api/inventory/**"), http())
                .before(uri("http://localhost:8082"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceSwaggerRoutes() {
        RouterFunction<ServerResponse> build = GatewayRouterFunctions.route("inventory-service-swagger")
                .route(path("/aggregate/inventory-service/v3/api-docs"), http())
                .before(uri("http://localhost:8082"))
                .filter(setPath("/api-docs"))
                .build();
        return build;
    }
}