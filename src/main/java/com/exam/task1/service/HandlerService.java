package com.exam.task1.service;

import com.exam.task1.dto.ApplicationStatusResponse;
import com.exam.task1.client.Client;
import com.exam.task1.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class HandlerService implements Handler {

    private static int retriesCount = 0;

    @Autowired
    private final Client client;


    public HandlerService(Client client) {
        this.client = client;
    }

    @Override
    public ApplicationStatusResponse performOperation(String id) {

        CompletableFuture<?> future1 = CompletableFuture
                .supplyAsync(() -> client.getApplicationStatus1(id));

        CompletableFuture<?> future2 = CompletableFuture
                .supplyAsync(() -> client.getApplicationStatus2(id));

        return CompletableFuture.anyOf(future1, future2)
                .orTimeout(15, TimeUnit.SECONDS)
                .exceptionally(t -> new ApplicationStatusResponse.Failure(Duration.of(1, ChronoUnit.SECONDS), retriesCount))
                .thenApply(this::getApplicationStatusResponse)
                .join();
    }

    private ApplicationStatusResponse getApplicationStatusResponse(Object response) {
        if (response instanceof Response.Success) {
            return new ApplicationStatusResponse.Success(((Response.Success) response).applicationId(),
                    ((Response.Success) response).applicationStatus());
        } else if (response instanceof Response.RetryAfter) {
            // Непонятно, почему "время последнего запроса, завершившегося ошибкой (опциональное)" это Duration
            return new ApplicationStatusResponse.Failure(((Response.RetryAfter) response).delay(), retriesCount);
        } else {
            return new ApplicationStatusResponse.Failure(Duration.of(1, ChronoUnit.SECONDS), retriesCount);
        }
    }
}
