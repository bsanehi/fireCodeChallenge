package com.fire.baz.firecodechallenge.network;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface Retrofit_Interface {

    @GET("data/2.5/weather")
    Observable<Response<ResponseBody>> getWeather(@Query("units") String units, @Query("lat") double latitude, @Query("lon") double longitude, @Query("appid") String apiKey);

   // @GET("data/2.5/weather")
   // Observable<Response<WeatherData>> getWeather2(@Query("units") String units, @Query("lat") double latitude, @Query("lon") double longitude, @Query("appid") String apiKey);

}