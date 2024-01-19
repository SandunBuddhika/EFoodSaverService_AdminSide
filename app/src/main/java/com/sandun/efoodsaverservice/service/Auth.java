package com.sandun.efoodsaverservice.service;

import com.google.gson.JsonObject;
import com.sandun.efoodsaverservice.dto.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Auth {
    @POST("auth")
    Call<JsonObject> auth(@Body User user);
    @POST("insert")
    Call<JsonObject> insert(@Body User user);
    @POST("verification")
    Call<JsonObject> verification(@Body JsonObject user);
    @POST("resendVerificationCode")
    Call<JsonObject> resendVerificationCode(@Body JsonObject user);
}
