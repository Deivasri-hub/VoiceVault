package com.voicebank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * VoiceBank Spring Boot Application Entry Point
 * This backend handles NLP parsing for voice commands.
 * All data storage is managed by the frontend via Local Storage.
 */
@SpringBootApplication
public class VoiceBankApplication {
    public static void main(String[] args) {
        SpringApplication.run(VoiceBankApplication.class, args);
    }
}
