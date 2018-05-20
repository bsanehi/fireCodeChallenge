package com.fire.baz.firecodechallenge.network;

import android.support.v7.app.AppCompatActivity;

import com.fire.baz.firecodechallenge.utilities.Constants;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.schedulers.Schedulers;

public class ServerRequests extends AppCompatActivity {

    public static Retrofit_Interface getRetrofit() {

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(rxAdapter)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(Retrofit_Interface.class);
    }
}