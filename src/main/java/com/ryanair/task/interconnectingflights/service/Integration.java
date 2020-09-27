package com.ryanair.task.interconnectingflights.service;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.function.Function;

public abstract class Integration {
    private final WebClient webClient;

    public Integration(String url) {
        webClient = WebClient.create(url);
    }

    protected WebClient.ResponseSpec get(final Function<UriBuilder, URI> uri) {
        WebClient.RequestHeadersUriSpec<?> get = webClient.get();
        if (uri != null) get.uri(uri);
        return get.retrieve();
    }
}
