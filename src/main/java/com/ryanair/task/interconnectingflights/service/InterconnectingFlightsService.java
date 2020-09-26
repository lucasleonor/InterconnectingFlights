package com.ryanair.task.interconnectingflights.service;

import com.ryanair.task.interconnectingflights.dto.Interconnection;
import com.ryanair.task.interconnectingflights.dto.Route;
import com.ryanair.task.interconnectingflights.integration.RouteIntegration;
import com.ryanair.task.interconnectingflights.integration.ScheduleIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InterconnectingFlightsService {

    private final RouteIntegration routeIntegration;
    private final ScheduleIntegration scheduleIntegration;

    @Autowired
    public InterconnectingFlightsService(final RouteIntegration routeIntegration, final ScheduleIntegration scheduleIntegration) {
        this.routeIntegration = routeIntegration;
        this.scheduleIntegration = scheduleIntegration;
    }

    public Set<Interconnection> findFlights(final String departure, final String arrival,
                                            final LocalDateTime departureDateTime, final LocalDateTime arrivalDateTime) {
        Set<String> connectingAirports = getConnectingAirports(departure, arrival);
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    protected Set<String> getConnectingAirports(final String departure, final String arrival) {
        Set<Route> departureRoutes = routeIntegration.findRoutes(departure, null);
        Set<Route> arrivalRoutes = routeIntegration.findRoutes(null, arrival);

        Map<String, Route> allAirportsAvailableFromDeparture = departureRoutes.stream().collect(Collectors.toMap(Route::getAirportTo, route -> route));
        return arrivalRoutes.stream()
                .filter(route -> allAirportsAvailableFromDeparture.containsKey(route.getAirportFrom()))
                .map(Route::getAirportFrom)
                .collect(Collectors.toSet());

    }
}
