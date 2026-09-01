# Cross-Platform Playlist Transferer

A Spring Boot service for transferring playlists between music streaming platforms. Currently in early development — Spotify OAuth integration is the first piece in place.

## Status

This project is a work in progress. At present it can exchange a Spotify authorization code for an access token (stored in the HTTP session) and list the connected user's Spotify playlists along with the full track listing (name, artist, ISRC) for each one; the actual cross-platform transfer logic and additional platform integrations are not yet implemented.

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

Exchanges a Spotify authorization code for an access token and stores it in the caller's HTTP session (`spotify_access_token`). The token itself is not returned in the response body — subsequent requests should reuse the session cookie.

**Request body:**
```json
{
  "code": "authorization-code-from-spotify-redirect"
}
```

**Response:**
```json
{
  "status": "success"
}
```

**Error responses:**
| Status | Meaning |
|---|---|
| `400 Bad Request` | Spotify rejected the request (e.g. invalid or expired code) |
| `502 Bad Gateway` | Spotify returned a server-side error |
| `503 Service Unavailable` | Spotify was unreachable |

### `GET /spotify/playlists`

Lists the playlists owned by the connected user, using the access token stored in the caller's session. Paginates through all of the user's playlists internally (filtering out playlists owned by other users, e.g. collaborative or followed playlists), and for each one paginates through and returns its full track listing.

**Response:**
```json
{
  "count": 1,
  "playlists": [
    {
      "id": "37i9dQZF1DXcBWIGoYBM5M",
      "name": "Today's Top Hits",
      "desc": "The hottest tracks right now.",
      "img_url": "https://i.scdn.co/image/...",
      "songs": [
        {
          "name": "Song Title",
          "artist": "Artist Name ",
          "isrc": "USRC17607839"
        }
      ]
    }
  ]
}
```

**Error responses:**
| Status | Meaning |
|---|---|
| `401 Unauthorized` | No Spotify access token in the session — call `/spotify/get-access-token` first |

## Project structure

```
src/main/java/com/transfer/playlist/music/
├── MusicApplication.java
└── clients/
    └── spotify/
        ├── controller/     # REST endpoints
        ├── service/        # Business logic / Spotify API calls
        ├── dto/             # Request/response records
        │   └── ClientResponses/  # Raw Spotify API response shapes
        └── exception/      # Spotify-specific error handling
```
