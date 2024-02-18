package com.exam.task1.service;

import com.exam.task1.dto.ApplicationStatusResponse;

public interface Handler {
    ApplicationStatusResponse performOperation(String id);
}
