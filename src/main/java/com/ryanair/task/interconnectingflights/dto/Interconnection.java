package com.ryanair.task.interconnectingflights.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Interconnection implements Serializable {

    private Set<Flight> legs;

    public int getStops() {
        return legs == null || legs.isEmpty() ? 0 : legs.size() - 1;
    }
}
