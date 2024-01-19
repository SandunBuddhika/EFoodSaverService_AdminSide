package com.sandun.efoodsaverservice.service;

import com.google.gson.JsonObject;
import com.sandun.efoodsaverservice.dto.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Test {
    @POST("test")
    @Headers("Content-Type: application/json")
    Call<JsonObject> test(@Body User req);
}
