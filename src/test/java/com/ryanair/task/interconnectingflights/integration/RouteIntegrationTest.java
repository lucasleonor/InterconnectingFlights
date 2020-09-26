package com.ryanair.task.interconnectingflights.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryanair.task.interconnectingflights.dto.Route;
import net.bytebuddy.utility.RandomString;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RouteIntegrationTest {
    private static MockWebServer mockBackEnd;
    private RouteIntegration routeIntegration;
    private static ObjectMapper objectMapper;
    private String departure;
    private String arrival;
    private List<Route> expectedRoutes;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        objectMapper = new ObjectMapper();
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void setUp() {
        String baseUrl = String.format("http://localhost:%s",
                mockBackEnd.getPort());
        routeIntegration = new RouteIntegration(baseUrl);
        departure = RandomString.make(9);
        arrival = RandomString.make(9);
        expectedRoutes = new ArrayList<>();
    }

    @Test
    void findRoutes_matchesArrivalAndDeparture() throws JsonProcessingException, InterruptedException {
        assertFindRoutes(true, true);
    }

    @Test
    void findRoutes_matchesArrival() throws JsonProcessingException, InterruptedException {
        assertFindRoutes(false, true);
    }

    @Test
    void findRoutes_matchesDeparture() throws JsonProcessingException, InterruptedException {
        assertFindRoutes(true, false);
    }

    private void assertFindRoutes(final boolean expectDeparture, final boolean expectArrival) throws JsonProcessingException, InterruptedException {
        List<Route> response = createRoutes(expectDeparture, expectArrival);
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));

        Set<Route> actual = routeIntegration.findRoutes((expectDeparture) ? departure : null, (expectArrival) ? arrival : null);
        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod(), is("GET"));
        assertThat(recordedRequest.getPath(), is("/"));
        assertThat(actual, Matchers.containsInAnyOrder(expectedRoutes.toArray(new Route[0])));
    }

    @NotNull
    private List<Route> createRoutes(final boolean expectDeparture, final boolean expectArrival) {
        Route bothMatch = new Route(departure, arrival, null, "RYANAIR");
        Route onlyDepartureMatch = new Route(departure, RandomString.make(), null, "RYANAIR");
        Route onlyArrivalMatch = new Route(RandomString.make(), arrival, null, "RYANAIR");

        expectedRoutes.add(bothMatch);
        if (!expectDeparture) expectedRoutes.add(onlyArrivalMatch);
        if (!expectArrival) expectedRoutes.add(onlyDepartureMatch);

        return Arrays.asList(
                new Route(RandomString.make(), RandomString.make(), RandomString.make(), RandomString.make()),
                new Route(RandomString.make(), RandomString.make(), null, RandomString.make()),
                new Route(RandomString.make(), RandomString.make(), RandomString.make(), "RYANAIR"),
                onlyArrivalMatch,
                onlyDepartureMatch,
                bothMatch
        );
    }
}