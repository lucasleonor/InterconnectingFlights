package com.ryanair.task.interconnectingflights.dto;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class InterconnectionTest {

    @Test
    void getStops() {
        assertThat("when legs is null getStops should return 0 ", new Interconnection().getStops(), is(0));
        assertThat("when legs is empty getStops should return 0 ", new Interconnection(Collections.emptySet()).getStops(), is(0));
        assertThat("when legs is not empty getStops should the number of connections",
                new Interconnection(Set.of(new Flight())).getStops(), is(0));
    }
}