package com.transfer.playlist.music.clients.youtube.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.transfer.playlist.music.clients.common.dto.PlaylistBasicDetails;
import com.transfer.playlist.music.clients.common.dto.PlaylistSong;
import com.transfer.playlist.music.clients.common.dto.UserPlaylistDTO;
import com.transfer.playlist.music.clients.youtube.dto.clientresponses.YoutubePlaylist;
import com.transfer.playlist.music.clients.youtube.dto.clientresponses.YoutubePlaylistItem;
import com.transfer.playlist.music.clients.youtube.dto.clientresponses.YoutubePlaylistItemsApiResponse;
import com.transfer.playlist.music.clients.youtube.dto.clientresponses.YoutubePlaylistsApiResponse;

@Service
public class YoutubeApiService {

    private final RestClient restClient;
    private final static String YOUTUBE_API_BASE_URL = "https://www.googleapis.com/youtube/v3";
    private final static String PLAYLISTS_URI = "/playlists";
    private final static String PLAYLIST_ITEMS_URI = "/playlistItems";
    private final static String SOURCE = "YouTube";
    public final static String YOUTUBE_ACCESS_TOKEN_SESSION_KEY = "youtube_access_token";

    public YoutubeApiService(
        RestClient.Builder builder
    ) {
        this.restClient = builder
            .baseUrl(YOUTUBE_API_BASE_URL)
            .build();
    }

    public UserPlaylistDTO getUserPlaylists(String accessToken) {
        YoutubePlaylistsApiResponse response = callYoutubePlaylistsAPI(null, accessToken);

        List<PlaylistBasicDetails> list = new ArrayList<>();

        do {
            for (YoutubePlaylist playlist: response.list()) {
                if (playlist == null || playlist.id() == null) continue;

                List<PlaylistSong> songs = getSongsInPlaylist(playlist.id(), accessToken);
                if (songs.isEmpty()) continue;

                list.add(
                    new PlaylistBasicDetails(
                        playlist.id(),
                        playlist.snippet() == null ? "" : playlist.snippet().title(),
                        playlist.snippet() == null ? "" : playlist.snippet().description(),
                        playlist.imageURL(),
                        songs
                    )
                );
            }

            if (response.next() == null) break;
            response = callYoutubePlaylistsAPI(response.next(), accessToken);

        } while (true);

        return new UserPlaylistDTO(
            list.size(),
            SOURCE,
            list
        );
    }

    private List<PlaylistSong> getSongsInPlaylist(String playlistId, String accessToken) {
        List<PlaylistSong> songs = new ArrayList<>();
        YoutubePlaylistItemsApiResponse response = callYoutubePlaylistItemsAPI(playlistId, null, accessToken);
        do {
            for (YoutubePlaylistItem item: response.items()) {
                if (item == null || item.snippet() == null) continue;
                if (item.snippet().resourceId() == null || item.snippet().resourceId().videoId() == null) continue;

                PlaylistSong song = new PlaylistSong(
                    item.snippet().songName(),
                    YouTubeMusicFilter.cleanArtist(item.snippet().artist()),
                    null
                );
                if (YouTubeMusicFilter.isJunkItem(song)) continue;

                songs.add(song);
            }
            if (response.next() == null) break;
            response = callYoutubePlaylistItemsAPI(playlistId, response.next(), accessToken);
        } while (true);
        return songs;
    }

    private YoutubePlaylistsApiResponse callYoutubePlaylistsAPI(String pageToken, String accessToken) {
        return restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(PLAYLISTS_URI)
                .queryParam("part", "snippet,contentDetails")
                .queryParam("mine", "true")
                .queryParam("maxResults", 50)
                .queryParamIfPresent("pageToken", java.util.Optional.ofNullable(pageToken))
                .build())
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .body(YoutubePlaylistsApiResponse.class);
    }

    private YoutubePlaylistItemsApiResponse callYoutubePlaylistItemsAPI(String playlistId, String pageToken, String accessToken) {
        return restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(PLAYLIST_ITEMS_URI)
                .queryParam("part", "snippet")
                .queryParam("playlistId", playlistId)
                .queryParam("maxResults", 50)
                .queryParamIfPresent("pageToken", java.util.Optional.ofNullable(pageToken))
                .build())
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .body(YoutubePlaylistItemsApiResponse.class);
    }
}
