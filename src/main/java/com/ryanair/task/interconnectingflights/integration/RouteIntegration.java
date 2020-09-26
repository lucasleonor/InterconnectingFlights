package com.ryanair.task.interconnectingflights.integration;

import com.ryanair.task.interconnectingflights.dto.Route;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RouteIntegration extends Integration {

    public RouteIntegration(@Value("${ryanair.services.route}") final String routeUri) {
        super(routeUri);
    }

    public Set<Route> findRoutes(String departureAirport, String arrivalAirport) {
        Set<Route> routes = clientGet(Route.class);
        //Client side filtering because API doesn't seem to support query filtering
        return routes.stream().filter(
                route -> (departureAirport == null || departureAirport.equals(route.getAirportFrom()))
                        && (arrivalAirport == null || arrivalAirport.equals(route.getAirportTo()))
                        && null == route.getConnectingAirport()
                        && "RYANAIR".equals(route.getOperator())
        ).collect(Collectors.toSet());
    }
}
