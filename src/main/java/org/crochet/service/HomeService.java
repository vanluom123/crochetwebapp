package org.crochet.service;

import org.crochet.payload.response.HomeResponse;

import java.util.concurrent.CompletableFuture;

public interface HomeService {
    CompletableFuture<HomeResponse> getHomesAsync();
}
