package com.transfer.playlist.music.clients.youtube.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.transfer.playlist.music.clients.common.dto.PlaylistBasicDetails;
import com.transfer.playlist.music.clients.common.dto.PlaylistSong;
import com.transfer.playlist.music.clients.common.dto.UserPlaylistDTO;
import com.transfer.playlist.music.clients.youtube.dto.clientrequests.AddItemsToPlaylistRequest;
import com.transfer.playlist.music.clients.youtube.dto.clientrequests.CreatePlaylistRequest;
import com.transfer.playlist.music.clients.youtube.dto.clientresponses.YoutubeCreatePlaylistResponse;
import com.transfer.playlist.music.clients.youtube.dto.clientresponses.YoutubePlaylist;
import com.transfer.playlist.music.clients.youtube.dto.clientresponses.YoutubePlaylistItem;
import com.transfer.playlist.music.clients.youtube.dto.clientresponses.YoutubePlaylistItemsApiResponse;
import com.transfer.playlist.music.clients.youtube.dto.clientresponses.YoutubePlaylistsApiResponse;
import com.transfer.playlist.music.clients.youtube.dto.clientresponses.YoutubeSearchResponse;

@Service
public class YoutubeApiService {

    private final RestClient restClient;
    private final static String YOUTUBE_API_BASE_URL = "https://www.googleapis.com/youtube/v3";
    private final static String PLAYLISTS_URI = "/playlists";
    private final static String PLAYLIST_ITEMS_URI = "/playlistItems";
    private final static String SEARCH_URI = "/search";
    private final static String SOURCE = "YouTube";

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

    public void createPlaylistAndAddSongs(
        String accessToken,
        UserPlaylistDTO request
    ) {
        for (PlaylistBasicDetails playlist: request.playlists()) {
            String playlistId = createPlaylist(playlist.name(), playlist.desc(), accessToken);
            addSongsToPlaylist(playlistId, playlist.songs(), accessToken);
        }
    }

    public String createPlaylist(
        String name,
        String description,
        String accessToken
    ) {
        CreatePlaylistRequest requestBody = CreatePlaylistRequest.of(name, description);

        YoutubeCreatePlaylistResponse response = restClient.post()
            .uri(uriBuilder -> uriBuilder
                .path(PLAYLISTS_URI)
                .queryParam("part", "snippet,status")
                .build())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + accessToken)
            .body(requestBody)
            .retrieve()
            .body(YoutubeCreatePlaylistResponse.class);

        return response.id();
    }

    public void addSongsToPlaylist(
        String playlistId,
        List<PlaylistSong> songs,
        String accessToken
    ) {
        for (PlaylistSong song: songs) {
            String videoId = getVideoId(song, accessToken);
            if (videoId == null) continue;

            AddItemsToPlaylistRequest requestBody = AddItemsToPlaylistRequest.of(playlistId, videoId);

            restClient.post()
                .uri(uriBuilder -> uriBuilder
                    .path(PLAYLIST_ITEMS_URI)
                    .queryParam("part", "snippet")
                    .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(requestBody)
                .retrieve()
                .toBodilessEntity();
        }
    }

    private String getVideoId(PlaylistSong song, String accessToken) {
        String name = song.name() == null ? "" : song.name();
        String artist = song.artist() == null ? "" : song.artist();

        YoutubeSearchResponse response = restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(SEARCH_URI)
                .queryParam("part", "snippet")
                .queryParam("type", "video")
                .queryParam("maxResults", 1)
                .queryParam("q", name + " " + artist)
                .build())
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .body(YoutubeSearchResponse.class);

        if (response == null || response.items() == null || response.items().isEmpty()) {
            return null;
        }
        var item = response.items().getFirst();
        return item.id() == null ? null : item.id().videoId();
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
