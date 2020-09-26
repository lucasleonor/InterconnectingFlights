package com.ryanair.task.interconnectingflights.service;

import com.ryanair.task.interconnectingflights.dto.Route;
import net.bytebuddy.utility.RandomString;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

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
}