package com.example.sber_ai.service;

import com.example.sber_ai.model.response.ServerResponse;

import java.util.Optional;

public interface ImageService {
    Optional<ServerResponse> sendImage(String fileName);
}
