package com.transfer.playlist.music.clients.spotify.exception;

import org.springframework.http.HttpStatusCode;

public class SpotifyAuthException extends RuntimeException {

    private final HttpStatusCode status;

    public SpotifyAuthException(String msg, HttpStatusCode status) {
        super(msg);
        this.status = status;
    }

    public HttpStatusCode getStatus() {
        return status;
    }
}
