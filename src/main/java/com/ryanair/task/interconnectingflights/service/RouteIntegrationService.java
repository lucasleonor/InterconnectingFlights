package com.ryanair.task.interconnectingflights.service;

import com.ryanair.task.interconnectingflights.dto.Route;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RouteIntegrationService extends Integration {

    public RouteIntegrationService(@Value("${ryanair.services.route}") final String routeUri) {
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

    public Set<String> getConnectingAirports(final String departure, final String arrival) {
        Set<Route> departureRoutes = findRoutes(departure, null);
        Set<Route> arrivalRoutes = findRoutes(null, arrival);

        Map<String, Route> allAirportsAvailableFromDeparture = departureRoutes.stream().collect(Collectors.toMap(Route::getAirportTo, route -> route));
        return arrivalRoutes.stream()
                .filter(route -> allAirportsAvailableFromDeparture.containsKey(route.getAirportFrom()))
                .map(Route::getAirportFrom)
                .collect(Collectors.toSet());

    }
}
