package com.transfer.playlist.music.clients.spotify.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.transfer.playlist.music.clients.spotify.dto.GetUserPlaylistsResponse;
import com.transfer.playlist.music.clients.spotify.dto.PlaylistBasicDetails;
import com.transfer.playlist.music.clients.spotify.dto.ClientResponses.SpotifyPlaylistsApiResponse;
import com.transfer.playlist.music.clients.spotify.dto.ClientResponses.Playlists;

@Service
public class SpotifyApiService {

    private final RestClient restClient;
    private final static String SPOTIFY_API_BASE_URL = "https://api.spotify.com/v1";
    private final static String PLAYLISTS_URI = "/me/playlists";

    public SpotifyApiService(
        RestClient.Builder builder
    ) {
        this.restClient = builder
                .baseUrl(SPOTIFY_API_BASE_URL)
                .build();
    }

    public GetUserPlaylistsResponse getUserPlaylists(
        String accessToken
    ) {
        SpotifyPlaylistsApiResponse response = callSpotifyPlaylistsAPI(
            PLAYLISTS_URI,
            accessToken
        );

        List<PlaylistBasicDetails> list = new ArrayList<>();

        do {
            for (Playlists playlist: response.list()) {
                String imageURL = playlist.images().get(0).url();

                PlaylistBasicDetails pl = new PlaylistBasicDetails(
                    playlist.id(),
                    playlist.name(),
                    playlist.description(),
                    imageURL
                );
                list.add(pl);
            }
            
            if (response.next() == null) break;
            response = callSpotifyPlaylistsAPI(
                response.next(),
                accessToken
            );

        } while (true);

        return new GetUserPlaylistsResponse(
            response.total(),
            list
        );
    }

    private SpotifyPlaylistsApiResponse callSpotifyPlaylistsAPI(String uri, String accessToken) {
        return restClient.get()
            .uri(uri)
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .body(SpotifyPlaylistsApiResponse.class);
    }
}
