package com.thedeveloperworldisyours.hellorxjava.complex.zip;

import com.google.gson.JsonArray;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by javierg on 07/12/2016.
 */

public interface EventsService {
    @GET("users/{user}/events")
    Observable<JsonArray> listEvents(@Path("user") String user);
}
