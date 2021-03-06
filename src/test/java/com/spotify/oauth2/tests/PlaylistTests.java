package com.spotify.oauth2.tests;

import api.StatusCode;
import api.applicationApi.PlaylistApi;
import assertions.PlaylistAsserts;
import io.restassured.response.ValidatableResponse;
import models.error.Error;
import models.playlist.Playlist;

import utils.DataLoader;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PlaylistTests extends BaseTest{

    String expired_token = "11BQAzcXEIr2cqPwQ2v6ae3Y63T70ZCO9oSjAMPIqWGyv8OBiJqYBLkYY-jLXI7K8q8TI0JyTlM8fe2kR2UuIv4UxV5D8PTHO6T81XRFxoxwEgfxO_y5PfGFC91IuOoRkosBi7PtLN4RjZzkQ2rlpJJnm4XbUPVXVgoeffU6Tz44cDWUF0dvkH9blHoGGOAhWmzFk3DUXfnmFYjDOgwhXKuRb3G_GpxaBWxqPA6gHmLxJgzYgM";

    PlaylistAsserts playlistAsserts = new PlaylistAsserts();

    @Test(description = "Create new playlist")
    public void createPlaylist(){
        Playlist requestPlaylist = new Playlist("Playlist POJO", "Description to POJO playlist", false);

        Response response = PlaylistApi.post(requestPlaylist);

        playlistAsserts.assertStatusCode(response.statusCode(), StatusCode.CODE_201);
        playlistAsserts.assertPlaylistEqual(response.as(Playlist.class), requestPlaylist);
    }

    @Test(description = "Get playlist")
    public void getPlaylist(){
        Playlist requestPlaylist = new Playlist()
                .setName("Updated Playlist Rest")
                .setDescription("Updated description")
                .setPublic(false);

        Response response = PlaylistApi.get(DataLoader.getInstance().getPlaylistId());
        assertThat(response.statusCode(), equalTo(StatusCode.CODE_200.getCode()));

        Playlist responsePlaylist = response.as(Playlist.class);

        assertThat(responsePlaylist.getName(), equalTo(requestPlaylist.getName()));
        assertThat(responsePlaylist.getDescription(), equalTo(requestPlaylist.getDescription()));
        assertThat(responsePlaylist.getPublic(), equalTo(requestPlaylist.getPublic()));

    }

    @Test
    public void putPlaylistTest(){
        Playlist requestPlaylist = new Playlist()
                .setName("Updated Playlist Rest")
                .setDescription("Updated description")
                .setPublic(false);

        Response response = PlaylistApi.put(requestPlaylist, DataLoader.getInstance().getUpdatePlaylistId());
        playlistAsserts.assertStatusCode(response.statusCode(), StatusCode.CODE_200);
//        assertThat(response.statusCode(), equalTo(StatusCode.CODE_200.getCode()));
    }

    @Test
    public void createPlaylistWithEmptyName(){

        Playlist requestPlaylist = new Playlist()
                .setName("")
                .setDescription("Playlist with empty name description")
                .setPublic(false);

        Response response = PlaylistApi.post(requestPlaylist);
        assertThat(response.statusCode(), equalTo(StatusCode.CODE_400.getCode()));

        playlistAsserts.assertErrorResponse(response.as(Error.class), StatusCode.CODE_400);
    }

    @Test
    public void createPlaylistWithExpiredToken(){

        Playlist requestPlaylist = new Playlist();
        requestPlaylist.setName("Playlist with expired token");
        requestPlaylist.setDescription("Playlist with expired token description");
        requestPlaylist.setPublic(false);

        Response response = PlaylistApi.post(requestPlaylist, expired_token);
        assertThat(response.statusCode(), equalTo(StatusCode.CODE_401.getCode()));

        Error error = response.as(Error.class);

        assertThat(error.getError().getStatus(), equalTo(StatusCode.CODE_401.getCode()));
        assertThat(error.getError().getMessage(), equalTo(StatusCode.CODE_401.getMessage()));
    }
}
