package org.kodluyoruz.milliyet_watchface.api.service;

import org.kodluyoruz.milliyet_watchface.api.model.DataList;
import org.kodluyoruz.milliyet_watchface.api.model.GitHubRepo;
import org.kodluyoruz.milliyet_watchface.api.model.SNODataClass;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SNOClient {

    @GET("/v2/stories/{id}")
    Call<SNODataClass> reposForUser(
            @Path("id") String id
    );

    @GET("/users/{user}/repos")
    Call<List<GitHubRepo>> reposForGitHub(
            @Path("user") String user
    );

    @GET("/v2/tags/1/stories")
    Call<DataList> reposForLastNews();
}
