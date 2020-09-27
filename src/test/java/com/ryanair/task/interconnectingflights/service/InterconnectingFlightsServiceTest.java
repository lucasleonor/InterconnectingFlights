package com.ryanair.task.interconnectingflights.service;

import com.ryanair.task.interconnectingflights.dto.Connection;
import com.ryanair.task.interconnectingflights.dto.Interconnection;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

@MockitoSettings
class InterconnectingFlightsServiceTest {

    @Mock
    private RouteIntegrationService routeIntegrationService;
    @Mock
    private ScheduleIntegrationService scheduleIntegrationService;
    private InterconnectingFlightsService service;

    @BeforeEach
    void setUp() {
        service = new InterconnectingFlightsService(routeIntegrationService, scheduleIntegrationService);
    }

    @Test
    void findFlights() {
    }

    @Test
    void matchFlights() {
        String departure = RandomString.make();
        String connection = RandomString.make();
        String arrival = RandomString.make();
        LocalDateTime baseDateTime = randomDateTime();
        Connection departureToConnection = new Connection(departure, connection, baseDateTime, baseDateTime.plusHours(3));
        Set<Connection> flightsDepartureToConnection = Set.of(
                new Connection(departure, connection, baseDateTime, baseDateTime.plusHours(5)),
                departureToConnection
        );
        Connection connectionToArrival = new Connection(connection, arrival, baseDateTime.plusHours(5), baseDateTime.plusHours(10));
        Set<Connection> flightsConnectionToArrival = Set.of(
                connectionToArrival,
                new Connection(connection, arrival, baseDateTime.plusHours(3), baseDateTime.plusHours(6))
        );

        Set<Interconnection> returnedInterconnections = service.matchFlights(flightsDepartureToConnection, flightsConnectionToArrival);

        assertThat(returnedInterconnections, containsInAnyOrder(new Interconnection(Set.of(departureToConnection, connectionToArrival))));
    }

    private LocalDateTime randomDateTime() {
        Random random = new Random();
        return LocalDateTime.of(random.nextInt(3000), random.nextInt(11) + 1, random.nextInt(27) + 1, random.nextInt(24), random.nextInt(60));
    }
}