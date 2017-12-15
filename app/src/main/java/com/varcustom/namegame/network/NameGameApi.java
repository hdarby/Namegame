package com.varcustom.namegame.network;

import com.varcustom.namegame.model.Person;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NameGameApi {

    @GET("/api/v1.0/profiles")
    Call<List<Person>> getPersonProfiles();
}
