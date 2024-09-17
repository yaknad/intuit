package com.example.intuit.player_service.approach1.exceptions;

public class PlayerNotFoundException extends RuntimeException {

    public PlayerNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
