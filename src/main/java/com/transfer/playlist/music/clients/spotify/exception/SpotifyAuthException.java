package com.transfer.playlist.music.clients.spotify.exception;

import org.springframework.http.HttpStatusCode;

public class SpotifyAuthException extends RuntimeException {

    private final HttpStatusCode status;

    public SpotifyAuthException(HttpStatusCode status) {
        super("Authentication failed");
        this.status = status;
    }

    public HttpStatusCode getStatus() {
        return status;
    }
}
