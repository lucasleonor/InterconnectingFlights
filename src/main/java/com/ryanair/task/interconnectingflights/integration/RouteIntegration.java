package com.ryanair.task.interconnectingflights.integration;

import com.ryanair.task.interconnectingflights.dto.Route;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RouteIntegration {

    private static final String ROUTE_URL = "https://services-api.ryanair.com/locate/3/routes";
    private final WebClient WEB_CLIENT = WebClient.create(ROUTE_URL);

    public Set<Route> findRoutes(String departureAirport, String arrivalAirport) {
        Set<Route> routes = fetchRoutes();
        //Client side filtering because API doesn't seem to support query filtering
        return routes.stream().filter(
                route -> (departureAirport == null || departureAirport.equals(route.getAirportFrom()))
                        && (arrivalAirport == null || arrivalAirport.equals(route.getAirportTo()))
                        && null == route.getConnectingAirport()
                        && "Ryanair".equals(route.getOperator())
        ).collect(Collectors.toSet());
    }


    private Set<Route> fetchRoutes() {
        WebClient.ResponseSpec retrieve = WEB_CLIENT.get().retrieve();
        return retrieve.bodyToFlux(Route.class).toStream().collect(Collectors.toSet());
    }
}
