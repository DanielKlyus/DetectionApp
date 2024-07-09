package com.example.sber_ai.service.impl;

import com.example.sber_ai.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {

    @Value("${ML_SERVER_URL}")
    private String url;


}
