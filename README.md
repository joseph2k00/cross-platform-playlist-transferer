# Cross-Platform Playlist Transferer

A Spring Boot service for transferring playlists between music streaming platforms. Currently in early development — Spotify OAuth integration is the first piece in place.

## Status

This project is a work in progress. At present it exposes a single endpoint that exchanges a Spotify authorization code for an access token, storing it in the HTTP session; the actual playlist-transfer logic and additional platform integrations are not yet implemented. A service for fetching a user's Spotify playlists is in progress but not yet wired up to a controller endpoint.

## Tech stack

- Java 25
- Spring Boot 4.1 (Web MVC, WebFlux, RestClient, Validation)
- Maven (via the included wrapper — no local Maven install required)

## Prerequisites

- JDK 25
- A Spotify application (from the [Spotify Developer Dashboard](https://developer.spotify.com/dashboard)) to obtain a client ID and secret

## Configuration

The app reads Spotify credentials from environment variables (see `src/main/resources/application.properties`):

```
SPOTIFY_CLIENT_ID=your-spotify-client-id
SPOTIFY_CLIENT_SECRET=your-spotify-client-secret
```

Set these in your shell or IDE run configuration before starting the app.

## Running locally

```bash
./mvnw spring-boot:run
```

The app starts on the default port (`8080`).

## Building

```bash
./mvnw clean package
```

## API

### `POST /spotify/get-access-token`

Exchanges a Spotify authorization code for an access token.

**Request body:**
```json
{
  "code": "authorization-code-from-spotify-redirect"
}
```

**Response:**
```json
{
  "access_token": "..."
}
```

**Error responses:**
| Status | Meaning |
|---|---|
| `400 Bad Request` | Spotify rejected the request (e.g. invalid or expired code) |
| `502 Bad Gateway` | Spotify returned a server-side error |
| `503 Service Unavailable` | Spotify was unreachable |

## Project structure

```
src/main/java/com/transfer/playlist/music/
├── MusicApplication.java
└── clients/
    └── spotify/
        ├── controller/   # REST endpoints
        ├── service/      # Business logic / Spotify API calls
        ├── dto/          # Request/response records
        └── exception/    # Spotify-specific error handling
```
