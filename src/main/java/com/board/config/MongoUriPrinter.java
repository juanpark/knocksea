package com.board.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MongoUriPrinter {

    @Value("${MONGODB_URI}")
    private String mongoUri;

    @PostConstruct
    public void printMongoUri() {
        System.out.println("✅ MongoDB URI Loaded from .env: " + mongoUri);
    }
}