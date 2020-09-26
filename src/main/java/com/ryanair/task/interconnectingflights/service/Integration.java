package com.ryanair.task.interconnectingflights.service;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Integration {
    private final WebClient webClient;

    public Integration(String url) {
        webClient = WebClient.create(url);
    }

    protected <T> Set<T> clientGet(Class<T> clazz) {
        return clientGet(clazz, null);
    }

    protected <T> Set<T> clientGet(Class<T> clazz, Function<UriBuilder, URI> uri) {
        WebClient.RequestHeadersUriSpec<?> get = webClient.get();
        if (uri != null) get.uri(uri);
        return get.retrieve().bodyToFlux(clazz).toStream().collect(Collectors.toSet());
    }
}
