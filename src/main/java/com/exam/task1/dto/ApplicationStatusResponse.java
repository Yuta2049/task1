package com.exam.task1.dto;

import jakarta.annotation.Nullable;

import java.time.Duration;

public sealed interface ApplicationStatusResponse {
    record Failure(@Nullable Duration lastRequestTime, int retriesCount) implements ApplicationStatusResponse {}
    record Success(String id, String status) implements ApplicationStatusResponse {}
}
