package com.transfer.playlist.music.clients.spotify.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice(basePackages = "com.transfer.playlist.music.clients.spotify")
public class SpotifyExceptionHandler {

    @ExceptionHandler(SpotifyAuthException.class)
    public ResponseEntity<Map<String, String>> handleSpotifyAuthException(SpotifyAuthException ex) {
        HttpStatus status = ex.getStatus().is4xxClientError()
            ? HttpStatus.BAD_REQUEST
            : HttpStatus.BAD_GATEWAY;

        return ResponseEntity.status(status)
            .body(Map.of("error", "spotify_auth_failed", "message", ex.getMessage()));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Map<String, String>> handleResourceAccessException(ResourceAccessException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Map.of("error", "spotify_unreachable", "message", "Unable to reach Spotify"));
    }
}
