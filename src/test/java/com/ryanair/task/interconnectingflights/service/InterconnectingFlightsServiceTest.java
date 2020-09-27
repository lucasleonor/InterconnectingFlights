package com.ryanair.task.interconnectingflights.service;

import com.ryanair.task.interconnectingflights.dto.Flight;
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
import static org.mockito.Mockito.doReturn;

@MockitoSettings
class InterconnectingFlightsServiceTest {

    @Mock
    private RouteIntegrationService routeIntegrationService;
    @Mock
    private ScheduleIntegrationService scheduleIntegrationService;
    private InterconnectingFlightsService service;
    private String departure;
    private String connection;
    private String arrival;

    @BeforeEach
    void setUp() {
        service = new InterconnectingFlightsService(routeIntegrationService, scheduleIntegrationService);
        departure = RandomString.make();
        connection = RandomString.make();
        arrival = RandomString.make();
    }

    @Test
    void findFlights() {
        doReturn(Set.of(connection)).when(routeIntegrationService).getConnectingAirports(departure, arrival);
        LocalDateTime departureDateTime = randomDateTime();
        LocalDateTime arrivalDateTime = randomDateTime();
        Flight directFlight = new Flight(departure, arrival, departureDateTime, arrivalDateTime);
        doReturn(Set.of(
                directFlight
        )).when(scheduleIntegrationService).findAllFlights(departure, arrival, departureDateTime, arrivalDateTime);
        Flight flight1 = new Flight(departure, connection, departureDateTime, departureDateTime.plusHours(1));
        doReturn(Set.of(
                flight1,
                new Flight(departure, connection, departureDateTime.plusHours(2), departureDateTime.plusHours(5))
        )).when(scheduleIntegrationService).findAllFlights(departure, connection, departureDateTime, arrivalDateTime);
        Flight flight2 = new Flight(departure, connection, departureDateTime.plusHours(4), departureDateTime.plusHours(5));
        doReturn(Set.of(
                new Flight(departure, connection, departureDateTime, departureDateTime.plusHours(2)),
                flight2
        )).when(scheduleIntegrationService).findAllFlights(connection, arrival, departureDateTime, arrivalDateTime);
        Set<Interconnection> flights = service.findFlights(departure, arrival, departureDateTime, arrivalDateTime);
        assertThat(flights, containsInAnyOrder(
                new Interconnection(Set.of(directFlight)),
                new Interconnection(Set.of(flight1, flight2))
        ));
    }

    @Test
    void matchFlights() {
        LocalDateTime baseDateTime = randomDateTime();
        Flight departureToConnection = new Flight(departure, connection, baseDateTime, baseDateTime.plusHours(3));
        Set<Flight> flightsDepartureToConnection = Set.of(
                new Flight(departure, connection, baseDateTime, baseDateTime.plusHours(5)),
                departureToConnection
        );
        Flight connectionToArrival = new Flight(connection, arrival, baseDateTime.plusHours(5), baseDateTime.plusHours(10));
        Set<Flight> flightsConnectionToArrival = Set.of(
                connectionToArrival,
                new Flight(connection, arrival, baseDateTime.plusHours(3), baseDateTime.plusHours(6))
        );

        Set<Interconnection> returnedInterconnections = service.matchFlights(flightsDepartureToConnection, flightsConnectionToArrival);

        assertThat(returnedInterconnections, containsInAnyOrder(new Interconnection(Set.of(departureToConnection, connectionToArrival))));
    }

    private LocalDateTime randomDateTime() {
        Random random = new Random();
        return LocalDateTime.of(random.nextInt(3000), random.nextInt(11) + 1, random.nextInt(27) + 1, random.nextInt(24), random.nextInt(60));
    }
}