package com.ryanair.task.interconnectingflights.service;

import com.ryanair.task.interconnectingflights.dto.Route;
import com.ryanair.task.interconnectingflights.integration.RouteIntegration;
import com.ryanair.task.interconnectingflights.integration.ScheduleIntegration;
import net.bytebuddy.utility.RandomString;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Arrays;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@MockitoSettings
class InterconnectingFlightsServiceTest {

    @Mock
    private RouteIntegration routeIntegration;
    @Mock
    private ScheduleIntegration scheduleIntegration;
    private InterconnectingFlightsService service;

    @BeforeEach
    void setUp() {
        service = new InterconnectingFlightsService(routeIntegration, scheduleIntegration);
    }

    @Test
    public void getConnectingAirports() {
        String departure = RandomString.make();
        String arrival = RandomString.make();
        String connecting = RandomString.make();

        mockRoutes(departure, arrival, connecting);

        Set<String> connectingAirports = service.getConnectingAirports(departure, arrival);
        assertThat(connectingAirports, Matchers.containsInAnyOrder(connecting));
    }

    private void mockRoutes(final String departure, final String arrival, final String connecting) {
        when(routeIntegration.findRoutes(departure, null)).thenReturn(Sets.newSet(
                createRoute(departure, arrival),
                createRoute(departure, RandomString.make()),
                createRoute(departure, connecting)
        ));
        when(routeIntegration.findRoutes(null, arrival)).thenReturn(Sets.newSet(
                createRoute(departure, arrival),
                createRoute(RandomString.make(9), arrival),
                createRoute(connecting, arrival)
        ));
    }

    @NotNull
    private Route createRoute(final String departure, final String arrival) {
        return new Route(departure, arrival, null, "RYANAIR");
    }
}